package net.smartcosmos.extension.tenant.rest.resource.tenant;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

import org.apache.commons.lang.RandomStringUtils;
import org.junit.*;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.web.servlet.MvcResult;

import net.smartcosmos.cluster.userdetails.util.UuidUtil;
import net.smartcosmos.extension.tenant.rest.resource.AbstractTestResource;
import net.smartcosmos.test.security.WithMockSmartCosmosUser;
import net.smartcosmos.usermanagement.DevKitUserManagementService;
import net.smartcosmos.usermanagement.tenant.dto.CreateTenantResponse;
import net.smartcosmos.usermanagement.tenant.dto.RestCreateTenantRequest;
import net.smartcosmos.usermanagement.tenant.persistence.TenantDao;
import net.smartcosmos.usermanagement.user.dto.CreateUserResponse;

import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.startsWith;
import static org.mockito.Matchers.anyObject;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.asyncDispatch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.request;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Unit Testing sample for creating Tenants.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = { DevKitUserManagementService.class })
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
    @WithMockSmartCosmosUser
    public void thatCreateTenantWithUserSucceeds() throws Exception {

        final String name = "example.com";
        final String username = "spam@example.com";
        final String password = RandomStringUtils.randomAlphanumeric(8);

        final String expectedTenantUrn = "urn:tenant:uuid:" + UuidUtil.getNewUuid()
            .toString();

        final String expectedUserUrn = "urn:user:uuid:" + UuidUtil.getNewUuid()
            .toString();

        Set<String> adminRoles = new HashSet<>();
        adminRoles.add("Admin");

        CreateUserResponse createUserResponse = CreateUserResponse.builder()
            .urn(expectedUserUrn)
            .username(username)
            .password(password)
            .roles(adminRoles)
            .tenantUrn(expectedTenantUrn)
            .build();

        CreateTenantResponse createTenantResponse = CreateTenantResponse.builder()
            .urn(expectedTenantUrn)
            .admin(createUserResponse)
            .build();

        when(tenantDao.createTenant(anyObject())).thenReturn(Optional.ofNullable(createTenantResponse));

        RestCreateTenantRequest request = RestCreateTenantRequest.builder()
            .name(name)
            .username(username)
            .build();

        MvcResult mvcResult = this.mockMvc.perform(
            post("/tenants").content(this.json(request))
                .contentType(contentType))
            .andExpect(status().isOk())
            .andExpect(request().asyncStarted())
            .andReturn();

        this.mockMvc.perform(asyncDispatch(mvcResult))
            .andExpect(status().isCreated())
            .andExpect(content().contentType(contentType))
            .andExpect(jsonPath("$.urn", startsWith("urn:tenant:uuid")))
            .andExpect(jsonPath("$.name").doesNotExist())
            .andExpect(jsonPath("$.admin").isMap())
            .andExpect(jsonPath("$.admin.emailAddress").doesNotExist())
            .andExpect(jsonPath("$.admin.username", is(username)))
            .andExpect(jsonPath("$.admin.password", is(password)))
            .andExpect(jsonPath("$.admin.roles").isArray())
            .andExpect(jsonPath("$.admin.tenantUrn", startsWith("urn:tenant:uuid")))
            .andExpect(jsonPath("$.admin.urn", startsWith("urn:user:uuid")));

    }

    @Test
    public void thatCreateTenantUnAuthenticatedSucceeds() throws Exception {

        final String name = "example.com";
        final String username = "spam@example.com";
        final String password = RandomStringUtils.randomAlphanumeric(8);

        final String expectedTenantUrn = "urn:tenant:uuid:" + UuidUtil.getNewUuid()
            .toString();

        final String expectedUserUrn = "urn:user:uuid:" + UuidUtil.getNewUuid()
            .toString();

        Set<String> adminRoles = new HashSet<>();
        adminRoles.add("Admin");

        CreateUserResponse createUserResponse = CreateUserResponse.builder()
            .urn(expectedUserUrn)
            .username(username)
            .password(password)
            .roles(adminRoles)
            .tenantUrn(expectedTenantUrn)
            .build();

        CreateTenantResponse createTenantResponse = CreateTenantResponse.builder()
            .urn(expectedTenantUrn)
            .admin(createUserResponse)
            .build();

        when(tenantDao.createTenant(anyObject())).thenReturn(Optional.ofNullable(createTenantResponse));

        RestCreateTenantRequest request = RestCreateTenantRequest.builder()
            .name(name)
            .username(username)
            .build();

        MvcResult mvcResult = this.mockMvc.perform(
            post("/tenants").content(this.json(request))
                .contentType(contentType))
            .andExpect(status().isOk())
            .andExpect(request().asyncStarted())
            .andReturn();

        this.mockMvc.perform(asyncDispatch(mvcResult))
            .andExpect(status().isCreated())
            .andExpect(content().contentType(contentType))
            .andExpect(jsonPath("$.urn", startsWith("urn:tenant:uuid")))
            .andExpect(jsonPath("$.name").doesNotExist())
            .andExpect(jsonPath("$.admin").isMap())
            .andExpect(jsonPath("$.admin.emailAddress").doesNotExist())
            .andExpect(jsonPath("$.admin.username", is(username)))
            .andExpect(jsonPath("$.admin.password", is(password)))
            .andExpect(jsonPath("$.admin.roles").isArray())
            .andExpect(jsonPath("$.admin.tenantUrn", startsWith("urn:tenant:uuid")))
            .andExpect(jsonPath("$.admin.urn", startsWith("urn:user:uuid")));

    }
}
