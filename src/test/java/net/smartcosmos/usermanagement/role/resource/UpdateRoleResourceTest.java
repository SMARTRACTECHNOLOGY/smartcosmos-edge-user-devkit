package net.smartcosmos.usermanagement.role.resource;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.junit.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.web.servlet.MvcResult;

import net.smartcosmos.cluster.userdetails.util.UuidUtil;
import net.smartcosmos.test.AbstractTestResource;
import net.smartcosmos.test.security.WithMockSmartCosmosUser;
import net.smartcosmos.usermanagement.DevKitUserManagementService;
import net.smartcosmos.usermanagement.role.dto.RoleRequest;
import net.smartcosmos.usermanagement.role.dto.RoleResponse;
import net.smartcosmos.usermanagement.role.persistence.RoleDao;

import static org.hamcrest.Matchers.is;
import static org.mockito.Matchers.anyObject;
import static org.mockito.Mockito.anyString;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.asyncDispatch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.request;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Unit Testing sample for updating Roles.
 */
@org.springframework.boot.test.SpringApplicationConfiguration(classes = { DevKitUserManagementService.class })
@WithMockSmartCosmosUser(authorities = { "https://authorities.smartcosmos.net/roles/update" })
public class UpdateRoleResourceTest extends AbstractTestResource {

    @Autowired
    protected RoleDao roleDao;

    private String tenantUrn;

    private List<String> adminRoleOnly = new ArrayList<>();
    private List<String> userRoleOnly = new ArrayList<>();
    private List<String> adminAndUserRoles = new ArrayList<>();

    @After
    public void tearDown() throws Exception {

        reset(roleDao);
    }

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
                .content(this.json(request))
                .contentType(contentType))
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
                .content(this.json(request))
                .contentType(contentType))
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
        final String expectedMessage = "someErrorMessage";

        when(roleDao.updateRole(anyString(), anyString(), anyObject())).thenThrow(new IllegalArgumentException(expectedMessage));

        RoleRequest request = RoleRequest.builder()
            .name(roleName)
            .active(active)
            .build();

        MvcResult mvcResult = this.mockMvc.perform(
            put("/roles/{urn}", roleUrn)
                .content(this.json(request))
                .contentType(contentType))
            .andExpect(status().isOk())
            .andExpect(request().asyncStarted())
            .andReturn();

        MvcResult result = this.mockMvc.perform(asyncDispatch(mvcResult))
            .andExpect(status().isConflict())
            .andExpect(content().contentType(contentType))
            .andExpect(jsonPath("$.message", is(expectedMessage)))
            .andReturn();
    }
}
