package net.smartcosmos.usermanagement.user.resource;

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
import net.smartcosmos.usermanagement.tenant.persistence.TenantDao;
import net.smartcosmos.usermanagement.user.dto.UpdateUserRequest;
import net.smartcosmos.usermanagement.user.dto.UserResponse;

import static org.mockito.Matchers.anyObject;
import static org.mockito.Mockito.anyString;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.asyncDispatch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.request;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Unit Testing sample for updating Users.
 */
@org.springframework.boot.test.SpringApplicationConfiguration(classes = { DevKitUserManagementService.class })
@WithMockSmartCosmosUser(authorities = { "https://authorities.smartcosmos.net/users/update" })
public class UpdateUserResourceTest extends AbstractTestResource {

    @Autowired
    protected TenantDao tenantDao;

    private String tenantUrn;

    private List<String> adminRoleOnly = new ArrayList<>();
    private List<String> userRoleOnly = new ArrayList<>();
    private List<String> adminAndUserRoles = new ArrayList<>();

    @After
    public void tearDown() throws Exception {

        reset(tenantDao);
    }

    /**
     * Test that updating a User is successful.
     *
     * @throws Exception
     */
    @Test
    public void thatUpdateUserSucceeds() throws Exception {

        String username = "newUser";
        String emailAddress = "newUser@example.com";
        Boolean active = false;
        String givenName = "John";
        String surname = "Doe";
        String password = "newPassword";

        final String expectedTenantUrn = "urn:tenant:uuid:" + UuidUtil.getNewUuid()
            .toString();

        final String expectedUserUrn = "urn:user:uuid:" + UuidUtil.getNewUuid()
            .toString();

        List<String> userRoles = new ArrayList<>();
        userRoles.add("User");

        UserResponse userResponse = UserResponse.builder()
            .urn(expectedUserUrn)
            .active(active)
            .tenantUrn(expectedTenantUrn)
            .username(username)
            .roles(userRoles)
            .emailAddress(emailAddress)
            .givenName(givenName)
            .surname(surname)
            .build();

        when(tenantDao.updateUser(anyString(), anyString(), anyObject())).thenReturn(Optional.ofNullable(userResponse));

        UpdateUserRequest request = UpdateUserRequest.builder()
            .username(username)
            .emailAddress(emailAddress)
            .roles(userRoleOnly)
            .password(password)
            .build();

        MvcResult mvcResult = this.mockMvc.perform(
            put("/users/{urn}", expectedUserUrn)
                .content(this.json(request))
                .contentType(contentType))
            .andExpect(status().isOk())
            .andExpect(request().asyncStarted())
            .andReturn();

        MvcResult result = this.mockMvc.perform(asyncDispatch(mvcResult))
            .andExpect(status().isNoContent())
            .andReturn();
    }

    @Test
    @WithMockSmartCosmosUser(authorities = {}, usernUrn = "myOwnUrn")
    public void thatUpdateOwnUserSucceeds() throws Exception {

        String ownUrn = "myOwnUrn";
        String username = "newUser";
        String emailAddress = "newUser@example.com";
        Boolean active = false;
        String givenName = "John";
        String surname = "Doe";

        final String expectedTenantUrn = "urn:tenant:uuid:" + UuidUtil.getNewUuid()
            .toString();

        final String expectedUserUrn = ownUrn;

        List<String> userRoles = new ArrayList<>();
        userRoles.add("User");

        UserResponse userResponse = UserResponse.builder()
            .urn(expectedUserUrn)
            .active(active)
            .tenantUrn(expectedTenantUrn)
            .username(username)
            .roles(userRoles)
            .emailAddress(emailAddress)
            .givenName(givenName)
            .surname(surname)
            .build();

        when(tenantDao.updateUser(anyString(), anyString(), anyObject())).thenReturn(Optional.ofNullable(userResponse));

        UpdateUserRequest request = UpdateUserRequest.builder()
            .username(username)
            .emailAddress(emailAddress)
            .roles(userRoleOnly)
            .build();

        MvcResult mvcResult = this.mockMvc.perform(
            put("/users/{urn}", expectedUserUrn)
                .content(this.json(request))
                .contentType(contentType))
            .andExpect(status().isOk())
            .andExpect(request().asyncStarted())
            .andReturn();

        MvcResult result = this.mockMvc.perform(asyncDispatch(mvcResult))
            .andExpect(status().isNoContent())
            .andReturn();
    }

    @Test
    @WithMockSmartCosmosUser(authorities = {}, usernUrn = "myOwnUrn")
    public void thatUpdateOwnRoleFails() throws Exception {

        String ownUrn = "myOwnUrn";
        String username = "newUser";
        String emailAddress = "newUser@example.com";

        final String expectedTenantUrn = "urn:tenant:uuid:" + UuidUtil.getNewUuid()
            .toString();

        final String expectedUserUrn = ownUrn;

        List<String> userRoles = new ArrayList<>();
        userRoles.add("Admin");

        UpdateUserRequest request = UpdateUserRequest.builder()
            .username(username)
            .emailAddress(emailAddress)
            .roles(userRoles)
            .build();

        MvcResult mvcResult = this.mockMvc.perform(
            put("/users/{urn}", expectedUserUrn)
                .content(this.json(request))
                .contentType(contentType))
            .andExpect(status().isForbidden())
            .andExpect(request().asyncNotStarted())
            .andReturn();
    }

    /**
     * Test that updating a nonexistent User fails.
     *
     * @throws Exception
     */
    @Test
    public void thatUpdateNonexistentUserFails() throws Exception {

        String username = "newUser";
        String emailAddress = "newUser@example.com";

        final String expectedTenantUrn = "urn:tenant:uuid:" + UuidUtil.getNewUuid()
            .toString();

        final String expectedUserUrn = "urn:user:uuid:" + UuidUtil.getNewUuid()
            .toString();

        List<String> userRoles = new ArrayList<>();
        userRoles.add("User");

        when(tenantDao.updateUser(anyString(), anyString(), anyObject())).thenReturn(Optional.empty());

        UpdateUserRequest request = UpdateUserRequest.builder()
            .username(username)
            .emailAddress(emailAddress)
            .roles(userRoleOnly)
            .build();

        MvcResult mvcResult = this.mockMvc.perform(
            put("/users/{urn}", expectedUserUrn)
                .content(this.json(request))
                .contentType(contentType))
            .andExpect(status().isOk())
            .andExpect(request().asyncStarted())
            .andReturn();

        MvcResult result = this.mockMvc.perform(asyncDispatch(mvcResult))
            .andExpect(status().isNotFound())
            .andReturn();
    }
}
