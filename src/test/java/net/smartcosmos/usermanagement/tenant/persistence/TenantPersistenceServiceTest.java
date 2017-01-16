package net.smartcosmos.usermanagement.tenant.persistence;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.junit.*;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.IntegrationTest;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;

import net.smartcosmos.cluster.userdetails.repository.UserRepository;
import net.smartcosmos.cluster.userdetails.util.UuidUtil;
import net.smartcosmos.usermanagement.DevKitUserManagementService;
import net.smartcosmos.usermanagement.tenant.dto.CreateTenantRequest;
import net.smartcosmos.usermanagement.tenant.dto.CreateTenantResponse;
import net.smartcosmos.usermanagement.tenant.dto.TenantResponse;
import net.smartcosmos.usermanagement.tenant.dto.UpdateTenantRequest;
import net.smartcosmos.usermanagement.tenant.repository.TenantRepository;
import net.smartcosmos.usermanagement.user.dto.CreateUserRequest;
import net.smartcosmos.usermanagement.user.dto.CreateUserResponse;
import net.smartcosmos.usermanagement.user.dto.UpdateUserRequest;
import net.smartcosmos.usermanagement.user.dto.UserResponse;

import static org.junit.Assert.*;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = { DevKitUserManagementService.class })
@ActiveProfiles("test")
@WebAppConfiguration
@IntegrationTest({ "spring.cloud.config.enabled=false", "eureka.client.enabled:false" })
public class TenantPersistenceServiceTest {

    @Autowired
    TenantPersistenceService tenantPersistenceService;

    @Autowired
    TenantRepository tenantRepository;

    @Autowired
    UserRepository userRepository;

    @Before
    public void setup() throws Exception {

        prepareTenantForUserTests();
    }

    @After
    public void tearDown() throws Exception {

        tenantRepository.deleteAll();
        userRepository.deleteAll();
    }

    // region TenantPersistenceTests

    @Test
    public void thatCreateTenantSucceeds() {

        final String TENANT = "createTestTenant";
        final String USER = "createTestAdmin";

        CreateTenantRequest createTenantRequest = CreateTenantRequest.builder()
            .active(true)
            .name(TENANT)
            .username(USER)
            .build();

        Optional<CreateTenantResponse> createTenantResponse = tenantPersistenceService.createTenant(createTenantRequest);

        assertTrue(createTenantResponse.isPresent());

        assertEquals(USER,
                     createTenantResponse.get()
                         .getAdmin()
                         .getUsername());
        assertFalse(createTenantResponse.get()
                        .getUrn()
                        .isEmpty());
    }

    @Test
    public void thatUpdateTenantActiveSucceeds() {

        final String TENANT = "updateTenantActive";
        final String USER = "updateAdminActive";

        CreateTenantRequest createTenantRequest = CreateTenantRequest.builder()
            .active(true)
            .name(TENANT)
            .username(USER)
            .build();

        Optional<CreateTenantResponse> createTenantResponse = tenantPersistenceService.createTenant(createTenantRequest);

        assertTrue(createTenantResponse.isPresent());

        //assertTrue(createTenantResponse.get().getActive());
        //assertEquals(TENANT, createTenantResponse.get().getName());
        assertEquals(USER,
                     createTenantResponse.get()
                         .getAdmin()
                         .getUsername());
        assertFalse(createTenantResponse.get()
                        .getUrn()
                        .isEmpty());

        String urn = createTenantResponse.get()
            .getUrn();

        UpdateTenantRequest updateTenantRequest = UpdateTenantRequest.builder()
            .active(false)
            .build();

        Optional<TenantResponse> updateResponse = tenantPersistenceService.updateTenant(urn, updateTenantRequest);

        assertTrue(updateResponse.isPresent());
        assertFalse(updateResponse.get()
                        .getActive());
        assertEquals(TENANT,
                     updateResponse.get()
                         .getName());
        assertEquals(urn,
                     updateResponse.get()
                         .getUrn());

        Optional<TenantResponse> getTenantResponse = tenantPersistenceService.findTenantByName(TENANT);

        assertTrue(getTenantResponse.isPresent());
        assertFalse(getTenantResponse.get()
                        .getActive());
        assertEquals(TENANT,
                     getTenantResponse.get()
                         .getName());
        assertEquals(urn,
                     getTenantResponse.get()
                         .getUrn());
    }

    @Test
    public void thatUpdateTenantNameSucceeds() {

        final String TENANT = "updateTenantName";
        final String TENANT_NEW = "updateTenantNewName";
        final String USER = "updateAdmin";

        CreateTenantRequest createTenantRequest = CreateTenantRequest.builder()
            .active(true)
            .name(TENANT)
            .username(USER)
            .build();

        Optional<CreateTenantResponse> createTenantResponse = tenantPersistenceService.createTenant(createTenantRequest);

        assertTrue(createTenantResponse.isPresent());

        //assertTrue(createTenantResponse.get().getActive());
        //assertEquals(TENANT, createTenantResponse.get().getName());
        assertEquals(USER,
                     createTenantResponse.get()
                         .getAdmin()
                         .getUsername());
        assertFalse(createTenantResponse.get()
                        .getUrn()
                        .isEmpty());

        String urn = createTenantResponse.get()
            .getUrn();

        UpdateTenantRequest updateTenantRequest = UpdateTenantRequest.builder()
            .active(true)
            .name(TENANT_NEW)
            .build();

        Optional<TenantResponse> updateResponse = tenantPersistenceService.updateTenant(urn, updateTenantRequest);

        assertTrue(updateResponse.isPresent());
        assertTrue(updateResponse.get()
                       .getActive());
        assertEquals(TENANT_NEW,
                     updateResponse.get()
                         .getName());

        Optional<TenantResponse> getTenantResponse = tenantPersistenceService.findTenantByName(TENANT_NEW);

        assertTrue(getTenantResponse.isPresent());

        assertTrue(getTenantResponse.get()
                       .getActive());
        assertEquals(TENANT_NEW,
                     getTenantResponse.get()
                         .getName());
        assertEquals(urn,
                     getTenantResponse.get()
                         .getUrn());
    }

    @Test
    public void thatUpdateTenantInvalidUrnFails() {

        final String TENANT_NEW = "updateTenantNew";

        String urn = UuidUtil.getTenantUrnFromUuid(UuidUtil.getNewUuid());

        UpdateTenantRequest updateTenantRequest = UpdateTenantRequest.builder()
            .active(false)
            .name(TENANT_NEW)
            .build();

        Optional<TenantResponse> updateResponse = tenantPersistenceService.updateTenant(urn, updateTenantRequest);

        assertFalse(updateResponse.isPresent());
    }

    @Test
    public void thatLookupTenantByUrnSucceeds() {

        final String TENANT = "lookupByUrnTenant";
        final String USER = "lookupByUrnAdmin";

        CreateTenantRequest createTenantRequest = CreateTenantRequest.builder()
            .active(true)
            .name(TENANT)
            .username(USER)
            .build();

        Optional<CreateTenantResponse> createTenantResponse = tenantPersistenceService.createTenant(createTenantRequest);

        assertTrue(createTenantResponse.isPresent());

        //assertTrue(createTenantResponse.get().getActive());
        //assertEquals(TENANT, createTenantResponse.get().getName());
        assertEquals(USER,
                     createTenantResponse.get()
                         .getAdmin()
                         .getUsername());
        assertFalse(createTenantResponse.get()
                        .getUrn()
                        .isEmpty());

        String urn = createTenantResponse.get()
            .getUrn();

        Optional<TenantResponse> getTenantResponse = tenantPersistenceService.findTenantByUrn(urn);

        assertTrue(getTenantResponse.isPresent());

        assertTrue(getTenantResponse.get()
                       .getActive());
        assertEquals(TENANT,
                     getTenantResponse.get()
                         .getName());
        assertEquals(urn,
                     getTenantResponse.get()
                         .getUrn());
    }

    @Test
    public void thatLookupTenantByUrnFails() {

        String urn = UuidUtil.getTenantUrnFromUuid(UuidUtil.getNewUuid());

        Optional<TenantResponse> getTenantResponse = tenantPersistenceService.findTenantByUrn(urn);

        assertFalse(getTenantResponse.isPresent());
    }

    public void thatLookupTenantByNameSucceeds() {

        final String TENANT = "lookupByNameTenant";
        final String USER = "lookupByNameAdmin";

        CreateTenantRequest createTenantRequest = CreateTenantRequest.builder()
            .active(true)
            .name(TENANT)
            .username(USER)
            .build();

        Optional<CreateTenantResponse> createTenantResponse = tenantPersistenceService.createTenant(createTenantRequest);

        assertTrue(createTenantResponse.isPresent());

        //assertTrue(createTenantResponse.get().getActive());
        //assertEquals(TENANT, createTenantResponse.get().getName());
        assertEquals(USER,
                     createTenantResponse.get()
                         .getAdmin()
                         .getUsername());
        assertFalse(createTenantResponse.get()
                        .getUrn()
                        .isEmpty());

        String urn = createTenantResponse.get()
            .getUrn();

        Optional<TenantResponse> getTenantResponse = tenantPersistenceService.findTenantByName(urn);

        assertTrue(getTenantResponse.isPresent());

        assertTrue(getTenantResponse.get()
                       .getActive());
        assertEquals(TENANT,
                     getTenantResponse.get()
                         .getName());
        assertEquals(urn,
                     getTenantResponse.get()
                         .getUrn());
    }

    @Test
    public void thatLookupTenantByNameFails() {

        final String TENANT = "noSuchNameTenant";

        Optional<TenantResponse> getTenantResponse = tenantPersistenceService.findTenantByName(UuidUtil.getTenantUrnFromUuid(UUID.randomUUID())
        );

        assertFalse(getTenantResponse.isPresent());
    }

    @Test
    public void thatFindAllTenantsSucceeds() {

        List<TenantResponse> tenantResponse = tenantPersistenceService.findAllTenants();

        assertNotNull(tenantResponse);
        assertFalse(tenantResponse.isEmpty());
    }

    // endregion

    // region UserPersistenceTests
    private String testUserTenantUrn;

    // prepare the tenant to do tests with users...
    private void prepareTenantForUserTests() throws Exception {

        final String TENANT = "createUserTestTenant";
        final String USER = "createUserTestAdmin";

        CreateTenantRequest createTenantRequest = CreateTenantRequest.builder()
            .active(true)
            .name(TENANT)
            .username(USER)
            .build();

        Optional<CreateTenantResponse> createTenantResponse =
            tenantPersistenceService.createTenant(createTenantRequest);

        if (!createTenantResponse.isPresent()) {
            throw new Exception("prepareTenantForUserTests: cannot create tenant to do user tests");
        }
        testUserTenantUrn = createTenantResponse.get()
            .getUrn();
    }

    @Test
    public void thatCreateUserSucceeds() {

        final String emailAddress = "create.user@example.com";
        final String givenName = "user";
        final String role = "User";
        final String surname = "create";
        final String username = "create.user";

        List<String> roles = new ArrayList<>();
        roles.add(role);

        CreateUserRequest createUserRequest = CreateUserRequest.builder()
            .active(true)
            .emailAddress(emailAddress)
            .givenName(givenName)
            .roles(roles)
            .surname(surname)
            .username(username)
            .build();

        Optional<CreateUserResponse> userResponse = tenantPersistenceService.createUser(testUserTenantUrn, createUserRequest);

        assertTrue(userResponse.isPresent());
        assertEquals(1,
                     userResponse.get()
                         .getRoles()
                         .size());
        assertTrue(userResponse.get()
                       .getRoles()
                       .contains(role));
        assertEquals(username,
                     userResponse.get()
                         .getUsername());
    }

    @Test
    public void thatCreateUserMultipleRolesSucceeds() {

        final String emailAddress = "create.user@example.com";
        final String givenName = "user";
        final String role1 = "User";
        final String role2 = "Admin";
        final String surname = "create";
        final String username = "create.user";

        List<String> roles = new ArrayList<>();
        roles.add(role1);
        roles.add(role2);

        CreateUserRequest createUserRequest = CreateUserRequest.builder()
            .active(true)
            .emailAddress(emailAddress)
            .givenName(givenName)
            .roles(roles)
            .surname(surname)
            .username(username)
            .build();

        Optional<CreateUserResponse> userResponse = tenantPersistenceService.createUser(testUserTenantUrn, createUserRequest);

        assertTrue(userResponse.isPresent());
        assertEquals(roles.size(),
                     userResponse.get()
                         .getRoles()
                         .size());
        assertTrue(userResponse.get()
                       .getRoles()
                       .contains(role1));
        assertTrue(userResponse.get()
                       .getRoles()
                       .contains(role2));
        assertEquals(username,
                     userResponse.get()
                         .getUsername());
    }

    @Test
    public void thatDeleteUserSucceeds() {

        // identical to thatCreateUserSucceeds until ***
        final String emailAddress = "create.user@example.com";
        final String givenName = "user";
        final String role = "User";
        final String surname = "create";
        final String username = "create.user";

        List<String> roles = new ArrayList<>();
        roles.add(role);

        CreateUserRequest createUserRequest = CreateUserRequest.builder()
            .active(true)
            .emailAddress(emailAddress)
            .givenName(givenName)
            .roles(roles)
            .surname(surname)
            .username(username)
            .build();

        Optional<CreateUserResponse> userResponse = tenantPersistenceService.createUser(testUserTenantUrn, createUserRequest);

        assertTrue(userResponse.isPresent());
        assertEquals(roles.size(),
                     userResponse.get()
                         .getRoles()
                         .size());
        assertTrue(userResponse.get()
                       .getRoles()
                       .contains(role));
        assertEquals(username,
                     userResponse.get()
                         .getUsername());

        // *** no longer identical to thatCreateUserSucceeds

        Optional<UserResponse> getResponse = tenantPersistenceService.findUserByUrn(testUserTenantUrn,
                                                                                    userResponse.get()
                                                                                        .getUrn());
        Optional<UserResponse> deleteResponse = tenantPersistenceService.deleteUserByUrn(testUserTenantUrn,
                                                                                         getResponse.get()
                                                                                             .getUrn());

        assertTrue(getResponse.isPresent());
        assertEquals(emailAddress,
                     getResponse.get()
                         .getEmailAddress());
        assertEquals(givenName,
                     getResponse.get()
                         .getGivenName());
        assertEquals(roles.size(),
                     getResponse.get()
                         .getRoles()
                         .size());
        assertTrue(userResponse.get()
                       .getRoles()
                       .contains(role));
        assertEquals(surname,
                     getResponse.get()
                         .getSurname());
        assertEquals(username,
                     getResponse.get()
                         .getUsername());

        assertTrue(deleteResponse.isPresent());
        assertEquals(emailAddress,
                     deleteResponse.get()
                         .getEmailAddress());
        assertEquals(givenName,
                     deleteResponse.get()
                         .getGivenName());
        assertEquals(roles.size(),
                     deleteResponse.get()
                         .getRoles()
                         .size());
        assertTrue(userResponse.get()
                       .getRoles()
                       .contains(role));
        assertEquals(surname,
                     deleteResponse.get()
                         .getSurname());
        assertEquals(username,
                     deleteResponse.get()
                         .getUsername());

    }

    @Test
    public void thatCreateUserSuccessInvalidRole() {

        final String emailAddress = "noSuchRole.user@example.com";
        final String givenName = "user";
        final String role = "NoSuchRole";
        final String surname = "noSuchRole";
        final String username = "noSuchRole.user";

        List<String> roles = new ArrayList<>();
        roles.add(role);

        CreateUserRequest createUserRequest = CreateUserRequest.builder()
            .active(true)
            .emailAddress(emailAddress)
            .givenName(givenName)
            .roles(roles)
            .surname(surname)
            .username(username)
            .build();

        Optional<CreateUserResponse> userResponse = tenantPersistenceService.createUser(testUserTenantUrn, createUserRequest);

        assertTrue(userResponse.isPresent());

        assertTrue(!userResponse.get()
            .getRoles()
            .contains(role));
    }

    @Test(expected = IllegalArgumentException.class)
    public void thatCreateUserFailsInvalidTenant() {

        final String emailAddress = "noSuchTenant.user@example.com";
        final String givenName = "user";
        final String role = "User";
        final String surname = "noSuchTenant";
        final String username = "noSuchTenant.user";
        final String noSuchTenant = UuidUtil.getTenantUrnFromUuid(UuidUtil.getNewUuid());

        List<String> roles = new ArrayList<>();
        roles.add(role);

        CreateUserRequest createUserRequest = CreateUserRequest.builder()
            .active(true)
            .emailAddress(emailAddress)
            .givenName(givenName)
            .roles(roles)
            .surname(surname)
            .username(username)
            .build();

        Optional<CreateUserResponse> userResponse = tenantPersistenceService.createUser(noSuchTenant, createUserRequest);

    }

    @Test
    public void thatCreateUserDuplicateUsernameFails() throws Exception {

        final String emailAddress1 = "create.duplicate.user1@example.com";
        final String emailAddress2 = "create.duplicate.user2@example.com";
        final String givenName = "user";
        final String role = "User";
        final String surname = "create";
        final String username = "create.duplicate.user";

        List<String> roles = new ArrayList<>();
        roles.add(role);

        CreateUserRequest createUserRequest1 = CreateUserRequest.builder()
            .active(true)
            .emailAddress(emailAddress1)
            .givenName(givenName)
            .roles(roles)
            .surname(surname)
            .username(username)
            .build();

        Optional<CreateUserResponse> userResponse1 = tenantPersistenceService.createUser(testUserTenantUrn, createUserRequest1);

        assertTrue(userResponse1.isPresent());
        assertEquals(roles.size(),
                     userResponse1.get()
                         .getRoles()
                         .size());
        assertTrue(userResponse1.get()
                       .getRoles()
                       .contains(role));
        assertEquals(username,
                     userResponse1.get()
                         .getUsername());

        CreateUserRequest createUserRequest2 = CreateUserRequest.builder()
            .active(true)
            .emailAddress(emailAddress2)
            .givenName(givenName)
            .roles(roles)
            .surname(surname)
            .username(username)
            .build();

        Optional<CreateUserResponse> userResponse2 = tenantPersistenceService.createUser(testUserTenantUrn, createUserRequest2);
        assertFalse(userResponse2.isPresent());
    }

    @Test
    public void thatCreateUserDuplicateUsernameDifferentTenantFails() throws Exception {

        final String emailAddress1 = "create.tenant.user1@example.com";
        final String emailAddress2 = "create.tenant.user1@example.com";
        final String givenName = "user";
        final String role = "User";
        final String surname = "create";
        final String username = "create.tenant.user";

        final String tenantUrn1 = tenantPersistenceService.createTenant(
            CreateTenantRequest
                .builder()
                .active(true)
                .name("Tenant 1")
                .username(emailAddress1)
                .build())
            .get()
            .getUrn();

        final String tenantUrn2 = tenantPersistenceService.createTenant(
            CreateTenantRequest
                .builder()
                .active(true)
                .name("Tenant 2")
                .username("randomwhocares@example.com")
                .build())
            .get()
            .getUrn();

        CreateUserRequest createUserRequest2 = CreateUserRequest.builder()
            .active(true)
            .emailAddress(emailAddress2)
            .givenName(givenName)
            .roles(Arrays.asList(role))
            .surname(surname)
            .username(emailAddress1)
            .build();

        Optional<CreateUserResponse> userResponse2 = tenantPersistenceService.createUser(tenantUrn2, createUserRequest2);
        assertFalse(userResponse2.isPresent());
    }

    @Test
    public void thatFindUserByUserNameSucceeds() throws Exception {

        String username = "FindByUsernameTestUser";
        String emailAddress = "username.user@example.com";
        String givenName = "John";
        String surname = "Doe";

        List<String> roles = new ArrayList<>();
        roles.add("Admin");

        CreateUserRequest userRequest = CreateUserRequest.builder()
            .username(username)
            .active(true)
            .emailAddress(emailAddress)
            .roles(roles)
            .givenName(givenName)
            .surname(surname)
            .build();
        CreateUserResponse createResponse = tenantPersistenceService.createUser(testUserTenantUrn, userRequest)
            .get();

        Optional<UserResponse> getResponse = tenantPersistenceService.findUserByName(testUserTenantUrn, username);

        assertTrue(getResponse.isPresent());
        assertEquals(createResponse.getUrn(),
                     getResponse.get()
                         .getUrn());
        assertEquals(true,
                     getResponse.get()
                         .getActive());
        assertNotNull(getResponse.get()
                          .getActive());
        assertEquals(createResponse.getRoles(),
                     getResponse.get()
                         .getRoles());
        assertEquals(username,
                     getResponse.get()
                         .getUsername());
        assertEquals(emailAddress,
                     getResponse.get()
                         .getEmailAddress());
        assertEquals(givenName,
                     getResponse.get()
                         .getGivenName());
        assertEquals(surname,
                     getResponse.get()
                         .getSurname());
        assertEquals(testUserTenantUrn,
                     getResponse.get()
                         .getTenantUrn());
    }

    @Test
    public void thatFindUserByUrnSucceeds() throws Exception {

        String username = "FindByUrnTestUser";
        String emailAddress = "urn.user@example.com";
        String givenName = "John";
        String surname = "Doe";

        List<String> roles = new ArrayList<>();
        roles.add("Admin");

        CreateUserRequest userRequest = CreateUserRequest.builder()
            .username(username)
            .active(true)
            .emailAddress(emailAddress)
            .roles(roles)
            .givenName(givenName)
            .surname(surname)
            .build();
        CreateUserResponse createResponse = tenantPersistenceService.createUser(testUserTenantUrn, userRequest)
            .get();

        Optional<UserResponse> getResponse = tenantPersistenceService.findUserByName(testUserTenantUrn, username);

        assertTrue(getResponse.isPresent());
        assertEquals(createResponse.getUrn(),
                     getResponse.get()
                         .getUrn());
        assertEquals(true,
                     getResponse.get()
                         .getActive());
        assertNotNull(getResponse.get()
                          .getActive());
        assertEquals(createResponse.getRoles(),
                     getResponse.get()
                         .getRoles());
        assertEquals(username,
                     getResponse.get()
                         .getUsername());
        assertEquals(emailAddress,
                     getResponse.get()
                         .getEmailAddress());
        assertEquals(givenName,
                     getResponse.get()
                         .getGivenName());
        assertEquals(surname,
                     getResponse.get()
                         .getSurname());
        assertEquals(testUserTenantUrn,
                     getResponse.get()
                         .getTenantUrn());
    }

    @Test
    public void thatUpdateUserSucceeds() throws Exception {

        String username = "UpdateTestUser";
        String emailAddress1 = "update.user1@example.com";
        String emailAddress2 = "update.user1@example.com";
        String givenName = "John";
        String surname = "Doe";

        List<String> roles = new ArrayList<>();
        roles.add("Admin");

        CreateUserRequest createRequest = CreateUserRequest.builder()
            .username(username)
            .active(true)
            .emailAddress(emailAddress1)
            .roles(roles)
            .givenName(givenName)
            .surname(surname)
            .build();
        CreateUserResponse createResponse = tenantPersistenceService.createUser(testUserTenantUrn, createRequest)
            .get();

        UpdateUserRequest updateRequest = UpdateUserRequest.builder()
            .active(false)
            .emailAddress(emailAddress2)
            .build();

        Optional<UserResponse> updateResponse = tenantPersistenceService.updateUser(testUserTenantUrn, createResponse.getUrn(),
                                                                                    updateRequest);
        assertTrue(updateResponse.isPresent());
        assertEquals(createResponse.getUrn(),
                     updateResponse.get()
                         .getUrn());
        assertEquals(false,
                     updateResponse.get()
                         .getActive());
        assertEquals(emailAddress2,
                     updateResponse.get()
                         .getEmailAddress());

        Optional<UserResponse> findResponse = tenantPersistenceService.findUserByUrn(testUserTenantUrn, createResponse.getUrn());
        assertTrue(findResponse.isPresent());
        assertEquals(createResponse.getUrn(),
                     findResponse.get()
                         .getUrn());
        assertEquals(false,
                     findResponse.get()
                         .getActive());
        assertEquals(emailAddress2,
                     findResponse.get()
                         .getEmailAddress());
    }

    @Test
    public void thatFindAllUsersSucceeds() throws Exception {

        List<UserResponse> users = tenantPersistenceService.findAllUsers(testUserTenantUrn);

        assertNotNull(users);
        assertFalse(users.isEmpty());
    }

    // endregion
}
