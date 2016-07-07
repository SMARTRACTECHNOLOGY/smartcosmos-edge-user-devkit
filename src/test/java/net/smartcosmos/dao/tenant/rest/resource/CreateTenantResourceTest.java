package net.smartcosmos.dao.tenant.rest.resource;

import java.util.ArrayList;
import java.util.List;

import org.junit.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.web.servlet.MvcResult;

import net.smartcosmos.ext.tenant.dao.TenantDao;
import net.smartcosmos.ext.tenant.rest.dto.RestCreateTenantRequest;
import net.smartcosmos.ext.tenant.util.UuidUtil;

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
@org.springframework.boot.test.SpringApplicationConfiguration(classes = { net.smartcosmos.dao.tenant.TenantPersistenceTestApplication.class,
                                                                          net.smartcosmos.ext.tenant.TenantPersistenceConfig.class })
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
    @Ignore
    public void thatCreateTenantSucceeds() throws Exception {

        final String name = "example.com";
        final String username = "spam@example.com";
        final String expectedPassword = "PleaseChangeMeImmediately";

        final String expectedUrn = "urn:tenant:uuid:" + UuidUtil.getNewUuid()
            .toString();

        List<String> authorities = new ArrayList<>();
        List<String> adminRoles = new ArrayList<>();
        adminRoles.add("Admin");

        MvcResult mvcResult = this.mockMvc.perform(
            post("/tenant/").content(this.json(RestCreateTenantRequest.builder()
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
