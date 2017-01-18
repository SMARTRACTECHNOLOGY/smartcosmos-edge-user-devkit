package net.smartcosmos.usermanagement.role.resource;

import java.util.ArrayList;
import java.util.List;
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
import net.smartcosmos.usermanagement.role.dto.RoleRequest;
import net.smartcosmos.usermanagement.role.dto.RoleResponse;
import net.smartcosmos.usermanagement.role.exception.RoleAlreadyExistsException;
import net.smartcosmos.usermanagement.role.persistence.RoleDao;

import static org.hamcrest.CoreMatchers.is;
import static org.mockito.Matchers.anyObject;
import static org.mockito.Mockito.anyString;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.validateMockitoUsage;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.asyncDispatch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.request;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import static net.smartcosmos.test.util.CommonTestConstants.CONTENT_TYPE_JSON;
import static net.smartcosmos.test.util.TestUtil.json;

@WebAppConfiguration
@ActiveProfiles("test")
@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = { DevKitUserManagementService.class, ResourceTestConfiguration.class })
@WithMockSmartCosmosUser(authorities = { "https://authorities.smartcosmos.net/roles/update" })
public class UpdateRoleResourceTest {

    @Autowired
    protected RoleDao roleDao;

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
        reset(roleDao);
    }

    // endregion

    /**
     * Test that updating a Role is successful.
     *
     * @throws Exception
     */
    @Test
    public void thatUpdateRoleSucceeds() throws Exception {

        String roleName = "newRole";
        Boolean active = false;

        final String expectedTenantUrn = "urn:tenant:uuid:" + UuidUtil.getNewUuid()
            .toString();

        final String expectedRoleUrn = "urn:role:uuid:" + UuidUtil.getNewUuid()
            .toString();

        List<String> userRoles = new ArrayList<>();
        userRoles.add("User");

        RoleResponse roleResponse = RoleResponse.builder()
            .urn(expectedRoleUrn)
            .active(active)
            .tenantUrn(expectedTenantUrn)
            .name(roleName)
            .build();

        when(roleDao.updateRole(anyString(), anyString(), anyObject())).thenReturn(Optional.ofNullable(roleResponse));

        RoleRequest request = RoleRequest.builder()
            .name(roleName)
            .active(active)
            .build();

        MvcResult mvcResult = this.mockMvc.perform(
            put("/roles/{urn}", expectedRoleUrn)
                .content(json(request))
                .contentType(CONTENT_TYPE_JSON))
            .andExpect(status().isOk())
            .andExpect(request().asyncStarted())
            .andReturn();

        MvcResult result = this.mockMvc.perform(asyncDispatch(mvcResult))
            .andExpect(status().isNoContent())
            .andReturn();
    }

    /**
     * Test that updating a nonexistent Role fails.
     *
     * @throws Exception
     */
    @Test
    public void thatUpdateNonExistentRoleFails() throws Exception {

        String roleName = "newRoleName";
        Boolean active = false;
        final String expectedRoleUrn = "urn:role:uuid:" + UuidUtil.getNewUuid()
            .toString();

        when(roleDao.updateRole(anyString(), anyString(), anyObject())).thenReturn(Optional.empty());

        RoleRequest request = RoleRequest.builder()
            .name(roleName)
            .active(active)
            .build();

        MvcResult mvcResult = this.mockMvc.perform(
            put("/roles/{urn}", expectedRoleUrn)
                .content(json(request))
                .contentType(CONTENT_TYPE_JSON))
            .andExpect(status().isOk())
            .andExpect(request().asyncStarted())
            .andReturn();

        MvcResult result = this.mockMvc.perform(asyncDispatch(mvcResult))
            .andExpect(status().isNotFound())
            .andReturn();
    }

    @Test
    public void thatUpdateExistingRoleNameFails() throws Exception {

        String roleName = "newRoleName";
        Boolean active = false;
        final String roleUrn = "urn:role:uuid:" + UuidUtil.getNewUuid()
            .toString();
        final String message = "someErrorMessage";
        final String tenantUrn = "someTenantUrn";
        final String expectedErrorMessage = String.format("Cannot update role in tenant: '%s'. Name '%s' is already in use.", tenantUrn, message);

        when(roleDao.updateRole(anyString(), anyString(), anyObject())).thenThrow(new RoleAlreadyExistsException(tenantUrn, message));

        RoleRequest request = RoleRequest.builder()
            .name(roleName)
            .active(active)
            .build();

        MvcResult mvcResult = this.mockMvc.perform(
            put("/roles/{urn}", roleUrn)
                .content(json(request))
                .contentType(CONTENT_TYPE_JSON))
            .andExpect(status().isOk())
            .andExpect(request().asyncStarted())
            .andReturn();

        MvcResult result = this.mockMvc.perform(asyncDispatch(mvcResult))
            .andExpect(status().isConflict())
            .andExpect(content().contentType(CONTENT_TYPE_JSON))
            .andExpect(jsonPath("$.message", is(expectedErrorMessage)))
            .andReturn();
    }
}
