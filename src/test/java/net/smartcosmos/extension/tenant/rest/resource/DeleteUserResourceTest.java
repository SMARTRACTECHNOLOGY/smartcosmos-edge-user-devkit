package net.smartcosmos.extension.tenant.rest.resource;

import java.util.ArrayList;
import java.util.List;

import org.codehaus.jettison.json.JSONObject;
import org.junit.*;
import org.springframework.test.web.servlet.MvcResult;

import net.smartcosmos.extension.tenant.TenantPersistenceTestApplication;
import net.smartcosmos.extension.tenant.rest.dto.RestCreateTenantRequest;
import net.smartcosmos.extension.tenant.rest.dto.RestCreateUserRequest;
import net.smartcosmos.extension.tenant.util.UuidUtil;

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
@org.springframework.boot.test.SpringApplicationConfiguration(classes = { TenantPersistenceTestApplication.class })
public class DeleteUserResourceTest extends AbstractTestResource {

    private String tenantUrn;

    private List<String> adminRoleOnly = new ArrayList<>();
    private List<String> userRoleOnly = new ArrayList<>();
    private List<String> adminAndUserRoles = new ArrayList<>();

    @Before
    public void setUp() throws Exception {

        // create tenant with default admin user, so we can create other users
        final String name = "example2.com";
        final String username = "admin@example2.com";
        final String expectedPassword = "PleaseChangeMeImmediately";

        final String expectedUrn = "urn:tenant:uuid:" + UuidUtil.getNewUuid()
            .toString();

        MvcResult mvcResult = this.mockMvc.perform(
            post("/tenant/").content(this.json(RestCreateTenantRequest.builder()
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

        JSONObject json = new JSONObject(result.getResponse()
                                             .getContentAsString());
        tenantUrn = (String) json.get("urn");
    }

    @After
    public void tearDown() throws Exception {

    }

    /**
     * Test that creating a Tenant is successful.
     *
     * @throws Exception
     */
    @Test
    public void thatDeleteUserSucceeds() throws Exception {

        String username = "newUser";
        String emailAddress = "newUser@example2.com";

        MvcResult mvcResult = this.mockMvc.perform(
            post("/user/").content(this.json(RestCreateUserRequest.builder()
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

        JSONObject json = new JSONObject(result.getResponse()
                                             .getContentAsString());
        String userUrn = (String) json.get("urn");

        MvcResult mvcDeleteResult = this.mockMvc.perform(post("/user/" + userUrn))
            .andExpect(status().isOk())
            .andExpect(request().asyncStarted())
            .andReturn();

        MvcResult deleteResult = this.mockMvc.perform(asyncDispatch(mvcResult))
            .andExpect(status().isNoContent())
            .andReturn();

        //        // And the user is really gone, right?
        //        MvcResult verifyResult = this.mockMvc.perform(get("/user/"))

    }

}
