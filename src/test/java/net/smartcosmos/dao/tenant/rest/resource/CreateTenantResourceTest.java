package net.smartcosmos.dao.tenant.rest.resource;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.junit.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.web.servlet.MvcResult;

import net.smartcosmos.ext.tenant.dao.TenantDao;
import net.smartcosmos.ext.tenant.dto.CreateTenantRequest;
import net.smartcosmos.ext.tenant.dto.CreateTenantResponse;
import net.smartcosmos.ext.tenant.dto.CreateUserResponse;
import net.smartcosmos.ext.tenant.rest.dto.RestCreateTenantRequest;
import net.smartcosmos.ext.tenant.util.UuidUtil;

import static org.hamcrest.Matchers.is;
import static org.mockito.Matchers.anyObject;
import static org.mockito.Mockito.anyString;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
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
public class CreateTenantResourceTest extends AbstractTestResource {

    @Autowired
    protected TenantDao tenantDao;

    @After
    public void tearDown() throws Exception {
        //reset(tenantDao);
    }

    /**
     * Test that creating a Tenant is successful.
     *
     * @throws Exception
     */
    @Test
    public void thatCreateTenantSucceeds() throws Exception {

        final String name = "example.com";
        final String emailAddress = "spam@example.com";
        final String expectedPassword = "PaSsWoRd";

        final String expectedUrn = "urn:tenant:uuid:" + UuidUtil.getNewUuid()
            .toString();

        when(tenantDao.findTenantByUrn(anyString())).thenReturn(Optional.empty());

        CreateTenantRequest createTenantRequest = CreateTenantRequest.builder()
            .name(name)
            .username(emailAddress)
            .build();

        List<String> authorities = new ArrayList<>();
        List<String> adminRoles = new ArrayList<>();
        adminRoles.add("Admin");

        CreateUserResponse createUserResponse = CreateUserResponse.builder()
            .emailAddress(emailAddress)
            .authorities(authorities)
            .roles(adminRoles)
            .password(expectedPassword)
            .build();

        when(tenantDao.createTenant(createTenantRequest)).thenReturn(Optional.ofNullable(CreateTenantResponse.builder()
                                                                                             .urn(expectedUrn)
                                                                                             .name(name)
                                                                                             .admin(createUserResponse)
                                                                                             .active(true)
                                                                                             .build()));

        MvcResult mvcResult = this.mockMvc.perform(
            post("/tenant/").content(this.json(RestCreateTenantRequest.builder()
                                                   .name(name)
                                                   .username(emailAddress)
                                                   .build()))
                .contentType
                    (contentType))
            .andExpect(status().isOk())
            .andExpect(request().asyncStarted())
            .andReturn();

        this.mockMvc.perform(asyncDispatch(mvcResult))
            .andExpect(status().isCreated())
            .andExpect(content().contentType(contentType))
            .andExpect(jsonPath("$.urn", is(expectedUrn)))
            .andExpect(jsonPath("$.name", is(name)))
            .andExpect(jsonPath("$.admin.emailAddress", is(name)));

        verify(tenantDao, times(1)).createTenant(anyObject());
        verify(tenantDao, times(1)).createUser(anyObject());
        verifyNoMoreInteractions(tenantDao);
    }

}
