package net.smartcosmos.extension.tenant.rest.resource.role;

import java.util.ArrayList;
import java.util.List;

import org.junit.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.web.servlet.MvcResult;

import net.smartcosmos.cluster.userdetails.util.UuidUtil;
import net.smartcosmos.extension.tenant.rest.resource.AbstractTestResource;
import net.smartcosmos.test.security.WithMockSmartCosmosUser;
import net.smartcosmos.usermanagement.DevKitUserManagementService;
import net.smartcosmos.usermanagement.role.dto.RoleResponse;
import net.smartcosmos.usermanagement.role.persistence.RoleDao;

import static org.mockito.Mockito.anyString;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.asyncDispatch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.request;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Unit Testing sample for deleting Roles.
 */
@org.springframework.boot.test.SpringApplicationConfiguration(classes = { DevKitUserManagementService.class })
@WithMockSmartCosmosUser(authorities = { "https://authorities.smartcosmos.net/roles/delete" })
public class DeleteRoleResourceTest extends AbstractTestResource {

    @Autowired
    protected RoleDao roleDao;

    @After
    public void tearDown() throws Exception {

        reset(roleDao);
    }

    /**
     * Test that deleting a Role is successful.
     *
     * @throws Exception
     */
    @Test
    public void thatDeleteRoleSucceeds() throws Exception {

        final String expectedTenantUrn = "urn:tenant:uuid:" + UuidUtil.getNewUuid()
            .toString();
        final String expectedRoleUrn = "urn:role:uuid:" + UuidUtil.getNewUuid()
            .toString();

        RoleResponse deleteResponse = RoleResponse.builder()
            .urn(expectedRoleUrn)
            .tenantUrn(expectedTenantUrn)
            .active(true)
            .build();
        List<RoleResponse> responseList = new ArrayList<>();
        responseList.add(deleteResponse);

        when(roleDao.delete(anyString(), anyString())).thenReturn(responseList);

        MvcResult mvcResult = this.mockMvc.perform(
            delete("/roles/{urn}", expectedRoleUrn).contentType(contentType))
            .andExpect(status().isOk())
            .andExpect(request().asyncStarted())
            .andReturn();

        this.mockMvc.perform(asyncDispatch(mvcResult))
            .andExpect(status().isNoContent());
    }

    /**
     * Test that deleting a nonexistent Role fails.
     *
     * @throws Exception
     */
    @Test
    public void thatDeleteNonexistentRoleFails() throws Exception {

        final String expectedRoleUrn = "urn:role:uuid:" + UuidUtil.getNewUuid()
            .toString();

        when(roleDao.delete(anyString(), anyString())).thenReturn(new ArrayList<>());

        MvcResult mvcResult = this.mockMvc.perform(
            delete("/roles/{urn}", expectedRoleUrn).contentType(contentType))
            .andExpect(status().isOk())
            .andExpect(request().asyncStarted())
            .andReturn();

        this.mockMvc.perform(asyncDispatch(mvcResult))
            .andExpect(status().isNotFound());
    }
}
