package net.smartcosmos.extension.tenant.rest.resource;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.junit.*;
import org.junit.runner.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.web.servlet.MvcResult;

import net.smartcosmos.extension.tenant.TenantPersistenceConfig;
import net.smartcosmos.extension.tenant.TenantPersistenceTestApplication;
import net.smartcosmos.extension.tenant.dao.TenantDao;
import net.smartcosmos.extension.tenant.dto.tenant.CreateTenantResponse;
import net.smartcosmos.extension.tenant.dto.user.CreateOrUpdateUserResponse;
import net.smartcosmos.extension.tenant.rest.dto.tenant.RestCreateTenantRequest;
import net.smartcosmos.extension.tenant.util.UuidUtil;

import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.startsWith;
import static org.mockito.Matchers.anyObject;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.asyncDispatch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.request;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Unit Testing sample for creating Tenants.
 */
@SuppressWarnings("Duplicates")
@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = { TenantPersistenceTestApplication.class,
                                            TenantPersistenceConfig.class })
public class CreateTenantResourceTest extends AbstractTestResource {

    @Autowired
    protected TenantDao tenantDao;

    @After
    public void tearDown() throws Exception {
        reset(tenantDao);
    }

    /**
     * Test that creating a Tenant is successful.
     *
     * @throws Exception
     */
    @Test
    public void thatCreateTenantSucceeds() throws Exception {

        final String name = "example.com";
        final String username = "spam@example.com";

        final String expectedTenantUrn = "urn:tenant:uuid:" + UuidUtil.getNewUuid()
            .toString();

        final String expectedUserUrn = "urn:user:uuid:" + UuidUtil.getNewUuid()
            .toString();

        List<String> adminRoles = new ArrayList<>();
        adminRoles.add("Admin");

        CreateOrUpdateUserResponse createOrUpdateUserResponse = CreateOrUpdateUserResponse
            .builder()
            .urn(expectedUserUrn)
            .username(username)
            .emailAddress(username)
            .active(true)
            .roles(adminRoles)
            .build();

        CreateTenantResponse createTenantResponse = CreateTenantResponse
            .builder()
            .urn(expectedTenantUrn)
            .name(name)
            .active(true)
            .admin(createOrUpdateUserResponse)
            .build();

        when(tenantDao.createTenant(anyObject())).thenReturn(Optional.ofNullable(createTenantResponse));

        MvcResult mvcResult = this.mockMvc.perform(
            post("/tenants").content(this.json(RestCreateTenantRequest.builder()
                                                   .name(name)
                                                   .username(username)
                                                   .build()))
                .contentType(contentType))
            .andExpect(status().isOk())
            .andExpect(request().asyncStarted())
            .andReturn();

        this.mockMvc.perform(asyncDispatch(mvcResult))
            .andExpect(status().isCreated())
            .andExpect(content().contentType(contentType))
            .andExpect(jsonPath("$.urn", startsWith("urn:tenant:uuid")))
            .andExpect(jsonPath("$.name", is(name)))
            .andExpect(jsonPath("$.admin.emailAddress", is(username)))
            .andExpect(jsonPath("$.admin.username", is(username)))
            .andExpect(jsonPath("$.admin.urn", startsWith("urn:user:uuid")));

    }
}
