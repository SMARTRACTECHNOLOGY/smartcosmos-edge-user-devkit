package net.smartcosmos.usermanagement.tenant.resource;

import java.util.Optional;

import org.junit.*;
import org.junit.runner.RunWith;
import org.mockito.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import net.smartcosmos.cluster.userdetails.util.UuidUtil;
import net.smartcosmos.test.config.ResourceTestConfiguration;
import net.smartcosmos.test.security.WithMockSmartCosmosUser;
import net.smartcosmos.usermanagement.DevKitUserManagementService;
import net.smartcosmos.usermanagement.tenant.dto.TenantResponse;
import net.smartcosmos.usermanagement.tenant.dto.UpdateTenantRequest;
import net.smartcosmos.usermanagement.tenant.persistence.TenantDao;

import static org.mockito.Matchers.anyObject;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.validateMockitoUsage;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.asyncDispatch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.request;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import static net.smartcosmos.test.util.CommonTestConstants.CONTENT_TYPE_JSON;
import static net.smartcosmos.test.util.TestUtil.json;

@WebAppConfiguration
@ActiveProfiles("test")
@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = { DevKitUserManagementService.class, ResourceTestConfiguration.class })
public class UpdateTenantResourceTest {

    @Autowired
    protected TenantDao tenantDao;

    @Autowired
    WebApplicationContext webApplicationContext;
    MockMvc mockMvc;

    // region Setup

    @Before
    public void setup() throws Exception {

        MockitoAnnotations.initMocks(this);

        this.mockMvc = MockMvcBuilders
            .webAppContextSetup(webApplicationContext)
            .apply(springSecurity())
            .build();
    }

    @After
    public void tearDown() throws Exception {

        validateMockitoUsage();
        reset(tenantDao);
    }

    // endregion

    /**
     * Test that updating a Tenant is successful.
     *
     * @throws Exception
     */
    @Test
    @WithMockSmartCosmosUser(authorities = { "https://authorities.smartcosmos.net/tenants/update" })
    public void thatUpdateTenantSucceeds() throws Exception {

        final String name = "example.com";
        final Boolean active = false;

        final String expectedTenantUrn = "urn:tenant:uuid:" + UuidUtil.getNewUuid()
            .toString();

        TenantResponse updateTenantResponse = TenantResponse.builder()
            .urn(expectedTenantUrn)
            .name(name)
            .active(active)
            .build();

        when(tenantDao.updateTenant(anyString(), anyObject())).thenReturn(Optional.ofNullable(updateTenantResponse));

        UpdateTenantRequest request = UpdateTenantRequest.builder()
            .active(active)
            .name(name)
            .build();

        MvcResult mvcResult = this.mockMvc.perform(
            put("/tenants/{urn}", expectedTenantUrn).content(json(request))
                .contentType(CONTENT_TYPE_JSON))
            .andExpect(status().isOk())
            .andExpect(request().asyncStarted())
            .andReturn();

        this.mockMvc.perform(asyncDispatch(mvcResult))
            .andExpect(status().isNoContent())
            .andReturn();
    }

    /**
     * Test that updating a nonexistent Tenant fails.
     *
     * @throws Exception
     */
    @Test
    @WithMockSmartCosmosUser(authorities = { "https://authorities.smartcosmos.net/tenants/update" })
    public void thatUpdateNonExistentTenantFails() throws Exception {

        final String name = "example.com";
        final Boolean active = false;

        final String expectedTenantUrn = "urn:tenant:uuid:" + UuidUtil.getNewUuid()
            .toString();

        when(tenantDao.updateTenant(anyString(), anyObject())).thenReturn(Optional.empty());

        UpdateTenantRequest request = UpdateTenantRequest.builder()
            .active(active)
            .name(name)
            .build();

        MvcResult mvcResult = this.mockMvc.perform(
            put("/tenants/{urn}", expectedTenantUrn).content(json(request))
                .contentType(CONTENT_TYPE_JSON))
            .andExpect(status().isOk())
            .andExpect(request().asyncStarted())
            .andReturn();

        this.mockMvc.perform(asyncDispatch(mvcResult))
            .andExpect(status().isNotFound())
            .andReturn();
    }

    @Test
    public void thatUpdateTenantAnonymousFails() throws Exception {

        final String name = "example.com";
        final Boolean active = false;

        final String expectedTenantUrn = "urn:tenant:uuid:" + UuidUtil.getNewUuid()
            .toString();

        UpdateTenantRequest request = UpdateTenantRequest.builder()
            .active(active)
            .name(name)
            .build();

        MvcResult mvcResult = this.mockMvc.perform(
            put("/tenants/{urn}", expectedTenantUrn).content(json(request))
                .contentType(CONTENT_TYPE_JSON))
            .andExpect(status().isUnauthorized())
            .andExpect(request().asyncNotStarted())
            .andReturn();
    }

    /**
     * Test that updating a nonexistent Tenant fails.
     *
     * @throws Exception
     */
    @Test
    public void thatUpdateNonExistentTenantAnonymousFails() throws Exception {

        final String name = "example.com";
        final Boolean active = false;

        final String expectedTenantUrn = "urn:tenant:uuid:" + UuidUtil.getNewUuid()
            .toString();

        UpdateTenantRequest request = UpdateTenantRequest.builder()
            .active(active)
            .name(name)
            .build();

        MvcResult mvcResult = this.mockMvc.perform(
            put("/tenants/{urn}", expectedTenantUrn).content(json(request))
                .contentType(CONTENT_TYPE_JSON))
            .andExpect(status().isUnauthorized())
            .andExpect(request().asyncNotStarted())
            .andReturn();
    }
}
