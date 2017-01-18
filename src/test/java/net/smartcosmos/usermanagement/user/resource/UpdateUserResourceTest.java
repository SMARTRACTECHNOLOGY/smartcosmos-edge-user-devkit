package net.smartcosmos.usermanagement.user.resource;

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
import net.smartcosmos.usermanagement.tenant.persistence.TenantDao;
import net.smartcosmos.usermanagement.user.dto.UpdateUserRequest;
import net.smartcosmos.usermanagement.user.dto.UserResponse;

import static org.mockito.Matchers.anyObject;
import static org.mockito.Mockito.anyString;
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

/**
 * Unit Testing sample for updating Users.
 */
@WebAppConfiguration
@ActiveProfiles("test")
@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = { DevKitUserManagementService.class, ResourceTestConfiguration.class })
@WithMockSmartCosmosUser(authorities = { "https://authorities.smartcosmos.net/users/update" })
public class UpdateUserResourceTest {

    @Autowired
    protected TenantDao tenantDao;

    private List<String> userRoleOnly = new ArrayList<>();

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
                .content(json(request))
                .contentType(CONTENT_TYPE_JSON))
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
                .content(json(request))
                .contentType(CONTENT_TYPE_JSON))
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
                .content(json(request))
                .contentType(CONTENT_TYPE_JSON))
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
    public void thatUpdateNonExistentUserFails() throws Exception {

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
                .content(json(request))
                .contentType(CONTENT_TYPE_JSON))
            .andExpect(status().isOk())
            .andExpect(request().asyncStarted())
            .andReturn();

        MvcResult result = this.mockMvc.perform(asyncDispatch(mvcResult))
            .andExpect(status().isNotFound())
            .andReturn();
    }
}
