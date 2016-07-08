package net.smartcosmos.dao.tenant.rest.resource;

import java.util.ArrayList;
import java.util.List;

import org.codehaus.jettison.json.JSONObject;
import org.junit.*;
import org.springframework.test.web.servlet.MvcResult;

import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.startsWith;
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
@org.springframework.boot.test.SpringApplicationConfiguration(classes = { net.smartcosmos.dao.tenant.TenantPersistenceTestApplication.class })
public class CreateUserResourceTest extends AbstractTestResource {

    private String tenantUrn;

    private List<String> adminRoleOnly = new ArrayList<>();
    private List<String> userRoleOnly = new ArrayList<>();
    private List<String> adminAndUserRoles = new ArrayList<>();

    @org.junit.Before
    public void setUp() throws Exception {

        // create tenant with default admin user, so we can create other users
        final String name = "example1.com";
        final String username = "admin@example1.com";
        final String expectedPassword = "PleaseChangeMeImmediately";

        final String expectedUrn = "urn:tenant:uuid:" + net.smartcosmos.ext.tenant.util.UuidUtil.getNewUuid()
            .toString();

        MvcResult mvcResult = this.mockMvc.perform(
            post("/tenant/").content(this.json(net.smartcosmos.ext.tenant.rest.dto.RestCreateTenantRequest.builder()
                                                   .name(name)
                                                   .username(username)
                                                   .build()))
                .contentType(contentType))
            .andExpect(status().isOk())
            .andExpect(request().asyncStarted())
            .andReturn();

        MvcResult result = this.mockMvc.perform(asyncDispatch(mvcResult))
            .andExpect(status().isCreated())
            .andExpect(content().contentType(contentType))
            .andExpect(jsonPath("$.urn", startsWith("urn:tenant:uuid")))
            .andExpect(jsonPath("$.name", is(name)))
            .andExpect(jsonPath("$.admin.emailAddress", is(username)))
            .andExpect(jsonPath("$.admin.username", is(username)))
            .andExpect(jsonPath("$.admin.urn", startsWith("urn:user:uuid")))
            .andReturn();

        JSONObject json = new JSONObject(result.getResponse().getContentAsString());
        tenantUrn = (String) json.get("urn");
    }

    @org.junit.After
    public void tearDown() throws Exception {

    }
        /**
         * Test that creating a Tenant is successful.
         *
         * @throws Exception
         */
    @Test
    @Ignore
    public void thatCreateUserSucceeds() throws Exception {

        String username = "newUser";
        String emailAddress = "newUser@example.com";

        org.springframework.test.web.servlet.MvcResult mvcResult = this.mockMvc.perform(
            post("/user/").content(this.json(net.smartcosmos.ext.tenant.rest.dto.RestCreateUserRequest.builder()
                                                 .tenantUrn(tenantUrn)
                                                 .username(username)
                                                 .emailAddress(emailAddress)
                                                 .roles(userRoleOnly)
                                                 .build()))
                .contentType(contentType))
                .andExpect(status().isOk())
                .andExpect(request().asyncStarted())
                .andReturn();

        MvcResult result = this.mockMvc.perform(asyncDispatch(mvcResult))
            .andExpect(status().isCreated())
            .andExpect(content().contentType(contentType))
            .andExpect(jsonPath("$.urn", startsWith("urn:user:uuid")))
            .andExpect(jsonPath("$.username", is(username)))
            .andExpect(jsonPath("$.emailAddress", is(emailAddress)))
            .andExpect(jsonPath("$.tenantUrn", startsWith("urn:tenant:uuid")))
            .andReturn();

    }

}
