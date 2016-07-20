package net.smartcosmos.extension.tenant.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.junit.*;
import org.junit.runner.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.IntegrationTest;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;

import net.smartcosmos.extension.tenant.TenantPersistenceConfig;
import net.smartcosmos.extension.tenant.TenantPersistenceTestApplication;
import net.smartcosmos.extension.tenant.dto.CreateOrUpdateUserResponse;
import net.smartcosmos.extension.tenant.dto.CreateTenantRequest;
import net.smartcosmos.extension.tenant.dto.CreateTenantResponse;
import net.smartcosmos.extension.tenant.dto.CreateUserRequest;
import net.smartcosmos.extension.tenant.dto.GetAuthoritiesResponse;
import net.smartcosmos.extension.tenant.dto.GetOrDeleteUserResponse;
import net.smartcosmos.extension.tenant.dto.GetTenantResponse;
import net.smartcosmos.extension.tenant.dto.UpdateTenantRequest;
import net.smartcosmos.extension.tenant.dto.UpdateTenantResponse;
import net.smartcosmos.extension.tenant.repository.TenantRepository;
import net.smartcosmos.extension.tenant.repository.UserRepository;
import net.smartcosmos.extension.tenant.util.UuidUtil;

import static org.junit.Assert.*;

@SuppressWarnings("Duplicates")
@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = {
    TenantPersistenceTestApplication.class,
    TenantPersistenceConfig.class })
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

        assertTrue(createTenantResponse.get().getActive());
        assertEquals(TENANT, createTenantResponse.get().getName());
        assertEquals(USER, createTenantResponse.get().getAdmin().getUsername());
        assertFalse(createTenantResponse.get().getUrn().isEmpty());
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

        assertTrue(createTenantResponse.get().getActive());
        assertEquals(TENANT, createTenantResponse.get().getName());
        assertEquals(USER, createTenantResponse.get().getAdmin().getUsername());
        assertFalse(createTenantResponse.get().getUrn().isEmpty());

        String urn = createTenantResponse.get().getUrn();

        UpdateTenantRequest updateTenantRequest = UpdateTenantRequest.builder()
            .urn(urn)
            .active(false)
            .build();

        Optional<UpdateTenantResponse> updateResponse = tenantPersistenceService.updateTenant(updateTenantRequest);

        assertTrue(updateResponse.isPresent());
        assertFalse(updateResponse.get().getActive());
        assertEquals(TENANT, updateResponse.get().getName());
        assertEquals(urn, updateResponse.get().getUrn());

        Optional<GetTenantResponse> getTenantResponse = tenantPersistenceService.findTenantByName(TENANT);

        assertTrue(getTenantResponse.isPresent());
        assertFalse(getTenantResponse.get().getActive());
        assertEquals(TENANT, getTenantResponse.get().getName());
        assertEquals(urn, getTenantResponse.get().getUrn());
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

        assertTrue(createTenantResponse.get().getActive());
        assertEquals(TENANT, createTenantResponse.get().getName());
        assertEquals(USER, createTenantResponse.get().getAdmin().getUsername());
        assertFalse(createTenantResponse.get().getUrn().isEmpty());

        String urn = createTenantResponse.get().getUrn();

        UpdateTenantRequest updateTenantRequest = UpdateTenantRequest.builder()
            .urn(urn)
            .active(true)
            .name(TENANT_NEW)
            .build();

        Optional<UpdateTenantResponse> updateResponse = tenantPersistenceService.updateTenant(updateTenantRequest);

        assertTrue(updateResponse.isPresent());
        assertTrue(updateResponse.get().getActive());
        assertEquals(TENANT_NEW, updateResponse.get().getName());

        Optional<GetTenantResponse> getTenantResponse = tenantPersistenceService.findTenantByName(TENANT_NEW);

        assertTrue(getTenantResponse.isPresent());

        assertTrue(getTenantResponse.get().getActive());
        assertEquals(TENANT_NEW, getTenantResponse.get().getName());
        assertEquals(urn, getTenantResponse.get().getUrn());
    }

    @Test
    public void thatUpdateTenantInvalidUrnFails() {
        final String TENANT_NEW = "updateTenantNew";

        String urn = UuidUtil.getTenantUrnFromUuid(UuidUtil.getNewUuid());

        UpdateTenantRequest updateTenantRequest = UpdateTenantRequest.builder()
            .urn(urn)
            .active(false)
            .name(TENANT_NEW)
            .build();

        Optional<UpdateTenantResponse> updateResponse = tenantPersistenceService.updateTenant(updateTenantRequest);

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

        assertTrue(createTenantResponse.get().getActive());
        assertEquals(TENANT, createTenantResponse.get().getName());
        assertEquals(USER, createTenantResponse.get().getAdmin().getUsername());
        assertFalse(createTenantResponse.get().getUrn().isEmpty());

        String urn = createTenantResponse.get().getUrn();

        Optional<GetTenantResponse> getTenantResponse = tenantPersistenceService.findTenantByUrn(urn);

        assertTrue(getTenantResponse.isPresent());

        assertTrue(getTenantResponse.get().getActive());
        assertEquals(TENANT, getTenantResponse.get().getName());
        assertEquals(urn, getTenantResponse.get().getUrn());
    }

    @Test
    public void thatLookupTenantByUrnFails() {

        String urn = UuidUtil.getTenantUrnFromUuid(UuidUtil.getNewUuid());

        Optional<GetTenantResponse> getTenantResponse = tenantPersistenceService.findTenantByUrn(urn);

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

        assertTrue(createTenantResponse.get().getActive());
        assertEquals(TENANT, createTenantResponse.get().getName());
        assertEquals(USER, createTenantResponse.get().getAdmin().getUsername());
        assertFalse(createTenantResponse.get().getUrn().isEmpty());

        String urn = createTenantResponse.get().getUrn();

        Optional<GetTenantResponse> getTenantResponse = tenantPersistenceService.findTenantByName(TENANT);

        assertTrue(getTenantResponse.isPresent());

        assertTrue(getTenantResponse.get().getActive());
        assertEquals(TENANT, getTenantResponse.get().getName());
        assertEquals(urn, getTenantResponse.get().getUrn());
    }

    @Test
    public void thatLookupTenantByNameFails() {

        final String TENANT = "noSuchNameTenant";

        Optional<GetTenantResponse> getTenantResponse = tenantPersistenceService.findTenantByName(TENANT);

        assertFalse(getTenantResponse.isPresent());
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
        testUserTenantUrn = createTenantResponse.get().getUrn();
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
            .tenantUrn(testUserTenantUrn)
            .build();

        Optional<CreateOrUpdateUserResponse> userResponse = tenantPersistenceService.createUser(createUserRequest);

        assertTrue(userResponse.isPresent());
        assertEquals(emailAddress, userResponse.get().getEmailAddress());
        assertEquals(givenName, userResponse.get().getGivenName());
        assertEquals(roles.size(), userResponse.get().getRoles().size());
        assertEquals(role, userResponse.get().getRoles().get(0));
        assertEquals(surname, userResponse.get().getSurname());
        assertEquals(username, userResponse.get().getUsername());
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
            .tenantUrn(testUserTenantUrn)
            .build();

        Optional<CreateOrUpdateUserResponse> userResponse = tenantPersistenceService.createUser(createUserRequest);

        assertTrue(userResponse.isPresent());
        assertEquals(emailAddress, userResponse.get().getEmailAddress());
        assertEquals(givenName, userResponse.get().getGivenName());
        assertEquals(roles.size(), userResponse.get().getRoles().size());
        assertTrue(userResponse.get().getRoles().contains(role1));
        assertTrue(userResponse.get().getRoles().contains(role2));
        assertEquals(surname, userResponse.get().getSurname());
        assertEquals(username, userResponse.get().getUsername());
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
            .tenantUrn(testUserTenantUrn)
            .build();

        Optional<CreateOrUpdateUserResponse> userResponse = tenantPersistenceService.createUser(createUserRequest);

        assertTrue(userResponse.isPresent());
        assertEquals(emailAddress, userResponse.get().getEmailAddress());
        assertEquals(givenName, userResponse.get().getGivenName());
        assertEquals(roles.size(), userResponse.get().getRoles().size());
        assertEquals(role, userResponse.get().getRoles().get(0));
        assertEquals(surname, userResponse.get().getSurname());
        assertEquals(username, userResponse.get().getUsername());

        // *** no longer identical to thatCreateUserSucceeds

        Optional<GetOrDeleteUserResponse> getResponse = tenantPersistenceService.findUserByUrn(userResponse.get().getUrn());
        Optional<GetOrDeleteUserResponse> deleteResponse = tenantPersistenceService.deleteUserByUrn(getResponse.get().getUrn());

        assertTrue(getResponse.isPresent());
        assertEquals(emailAddress, getResponse.get().getEmailAddress());
        assertEquals(givenName, getResponse.get().getGivenName());
        assertEquals(roles.size(), getResponse.get().getRoles().size());
        assertEquals(role, getResponse.get().getRoles().get(0));
        assertEquals(surname, getResponse.get().getSurname());
        assertEquals(username, getResponse.get().getUsername());

        assertTrue(deleteResponse.isPresent());
        assertEquals(emailAddress, deleteResponse.get().getEmailAddress());
        assertEquals(givenName, deleteResponse.get().getGivenName());
        assertEquals(roles.size(), deleteResponse.get().getRoles().size());
        assertEquals(role, deleteResponse.get().getRoles().get(0));
        assertEquals(surname, deleteResponse.get().getSurname());
        assertEquals(username, deleteResponse.get().getUsername());

    }

    @Test(expected = IllegalArgumentException.class)
    public void thatCreateUserFailsInvalidRole() {

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
            .tenantUrn(testUserTenantUrn)
            .build();

        Optional<CreateOrUpdateUserResponse> userResponse = tenantPersistenceService.createUser(createUserRequest);
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
            .tenantUrn(noSuchTenant)
            .build();

        Optional<CreateOrUpdateUserResponse> userResponse = tenantPersistenceService.createUser(createUserRequest);
    }

    @Test
    public void thatGetAuthoritiesSucceeds() throws Exception {

        String username = "authorityTestUser";
        String emailAddress = "authority.user@example.com";

        List<String> roles = new ArrayList<>();
        roles.add("Admin");

        CreateUserRequest userRequest = CreateUserRequest.builder()
            .username(username)
            .active(true)
            .emailAddress(emailAddress)
            .roles(roles)
            .givenName("John")
            .surname("Doe")
            .tenantUrn(testUserTenantUrn)
            .build();
        String password = tenantPersistenceService.createUser(userRequest).get().getPassword();

        Optional<GetAuthoritiesResponse> authorities = tenantPersistenceService.getAuthorities(username, password);

        assertTrue(authorities.isPresent());
        assertFalse(authorities.get().getAuthorities().isEmpty());
        assertEquals(2, authorities.get().getAuthorities().size());
        assertTrue(authorities.get().getAuthorities().contains("smartcosmos.things.read"));
        assertTrue(authorities.get().getAuthorities().contains("smartcosmos.things.write"));
    }

    @Test
    public void thatGetAuthoritiesReturnsEmptySetForMissingRole() throws Exception {

        String username = "NoAuthorityTestUser";
        String emailAddress = "authority.user@example.com";

        List<String> roles = new ArrayList<>();

        CreateUserRequest userRequest = CreateUserRequest.builder()
            .username(username)
            .active(true)
            .emailAddress(emailAddress)
            .roles(roles)
            .givenName("John")
            .surname("Doe")
            .tenantUrn(testUserTenantUrn)
            .build();
        String password = tenantPersistenceService.createUser(userRequest).get().getPassword();

        Optional<GetAuthoritiesResponse> authorities = tenantPersistenceService.getAuthorities(username, password);

        assertTrue(authorities.isPresent());
        assertTrue(authorities.get().getAuthorities().isEmpty());
    }

    @Test
    public void thatGetAuthoritiesReturnsNoDuplicates() throws Exception {

        String username = "multipleRoleAuthorityTestUser";
        String emailAddress = "authority.user@example.com";

        List<String> roles = new ArrayList<>();
        roles.add("Admin");
        roles.add("User");

        CreateUserRequest userRequest = CreateUserRequest.builder()
            .username(username)
            .active(true)
            .emailAddress(emailAddress)
            .roles(roles)
            .givenName("John")
            .surname("Doe")
            .tenantUrn(testUserTenantUrn)
            .build();
        String password = tenantPersistenceService.createUser(userRequest).get().getPassword();

        Optional<GetAuthoritiesResponse> authorities = tenantPersistenceService.getAuthorities(username, password);

        assertTrue(authorities.isPresent());
        assertFalse(authorities.get().getAuthorities().isEmpty());
        assertEquals(2, authorities.get().getAuthorities().size());
        assertTrue(authorities.get().getAuthorities().contains("smartcosmos.things.read"));
        assertTrue(authorities.get().getAuthorities().contains("smartcosmos.things.write"));
    }

    @Test
    public void thatGetAuthoritiesInvalidPasswordFails() throws Exception {

        String username = "invalidAuthorityTestUser";
        String emailAddress = "invalid.user@example.com";

        List<String> roles = new ArrayList<>();
        roles.add("Admin");

        CreateUserRequest userRequest = CreateUserRequest.builder()
            .username(username)
            .active(true)
            .emailAddress(emailAddress)
            .roles(roles)
            .givenName("John")
            .surname("Doe")
            .tenantUrn(testUserTenantUrn)
            .build();
        tenantPersistenceService.createUser(userRequest);

        Optional<GetAuthoritiesResponse> authorities = tenantPersistenceService.getAuthorities(username, "invalid");

        assertFalse(authorities.isPresent());
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
            .tenantUrn(testUserTenantUrn)
            .build();

        Optional<CreateOrUpdateUserResponse> userResponse1 = tenantPersistenceService.createUser(createUserRequest1);

        assertTrue(userResponse1.isPresent());
        assertEquals(emailAddress1, userResponse1.get().getEmailAddress());
        assertEquals(givenName, userResponse1.get().getGivenName());
        assertEquals(roles.size(), userResponse1.get().getRoles().size());
        assertEquals(role, userResponse1.get().getRoles().get(0));
        assertEquals(surname, userResponse1.get().getSurname());
        assertEquals(username, userResponse1.get().getUsername());

        CreateUserRequest createUserRequest2 = CreateUserRequest.builder()
            .active(true)
            .emailAddress(emailAddress2)
            .givenName(givenName)
            .roles(roles)
            .surname(surname)
            .username(username)
            .tenantUrn(testUserTenantUrn)
            .build();

        Optional<CreateOrUpdateUserResponse> userResponse2 = tenantPersistenceService.createUser(createUserRequest2);
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
        final String tenantUrn1 = UuidUtil.getTenantUrnFromUuid(UUID.randomUUID());
        final String tenantUrn2 = UuidUtil.getTenantUrnFromUuid(UUID.randomUUID());

        List<String> roles = new ArrayList<>();

        CreateUserRequest createUserRequest1 = CreateUserRequest.builder()
            .active(true)
            .emailAddress(emailAddress1)
            .givenName(givenName)
            .roles(roles)
            .surname(surname)
            .username(username)
            .tenantUrn(tenantUrn1)
            .build();

        Optional<CreateOrUpdateUserResponse> userResponse1 = tenantPersistenceService.createUser(createUserRequest1);

        assertTrue(userResponse1.isPresent());
        assertEquals(emailAddress1, userResponse1.get().getEmailAddress());
        assertEquals(givenName, userResponse1.get().getGivenName());
        assertEquals(roles.size(),userResponse1.get().getRoles().size());
        assertEquals(surname, userResponse1.get().getSurname());
        assertEquals(username, userResponse1.get().getUsername());

        CreateUserRequest createUserRequest2 = CreateUserRequest.builder()
            .active(true)
            .emailAddress(emailAddress2)
            .givenName(givenName)
            .roles(roles)
            .surname(surname)
            .username(username)
            .tenantUrn(tenantUrn2)
            .build();

        Optional<CreateOrUpdateUserResponse> userResponse2 = tenantPersistenceService.createUser(createUserRequest2);
        assertFalse(userResponse2.isPresent());
//        assertEquals(emailAddress2, userResponse2.get().getEmailAddress());
//        assertEquals(givenName, userResponse2.get().getGivenName());
//        assertEquals(roles.size(),userResponse2.get().getRoles().size());
//        assertEquals(surname, userResponse2.get().getSurname());
//        assertEquals(username, userResponse2.get().getUsername());
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
            .tenantUrn(testUserTenantUrn)
            .build();
        CreateOrUpdateUserResponse createResponse = tenantPersistenceService.createUser(userRequest).get();

        Optional<GetOrDeleteUserResponse> getResponse = tenantPersistenceService.findUserByName(username);

        assertTrue(getResponse.isPresent());
        assertEquals(createResponse.getUrn(), getResponse.get().getUrn());
        assertEquals(createResponse.getActive(), getResponse.get().getActive());
        assertNotNull(getResponse.get().getActive());
        assertEquals(createResponse.getRoles(), getResponse.get().getRoles());
        assertEquals(username, getResponse.get().getUsername());
        assertEquals(emailAddress, getResponse.get().getEmailAddress());
        assertEquals(givenName, getResponse.get().getGivenName());
        assertEquals(surname, getResponse.get().getSurname());
        assertEquals(testUserTenantUrn, getResponse.get().getTenantUrn());
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
            .tenantUrn(testUserTenantUrn)
            .build();
        CreateOrUpdateUserResponse createResponse = tenantPersistenceService.createUser(userRequest).get();

        Optional<GetOrDeleteUserResponse> getResponse = tenantPersistenceService.findUserByName(username);

        assertTrue(getResponse.isPresent());
        assertEquals(createResponse.getUrn(), getResponse.get().getUrn());
        assertEquals(createResponse.getActive(), getResponse.get().getActive());
        assertNotNull(getResponse.get().getActive());
        assertEquals(createResponse.getRoles(), getResponse.get().getRoles());
        assertEquals(username, getResponse.get().getUsername());
        assertEquals(emailAddress, getResponse.get().getEmailAddress());
        assertEquals(givenName, getResponse.get().getGivenName());
        assertEquals(surname, getResponse.get().getSurname());
        assertEquals(testUserTenantUrn, getResponse.get().getTenantUrn());
    }

    @Test
    public void thatUpdateUpserSucceeds() throws Exception {
        // TODO updateUser(UpdateUserRequest updateUserRequest)
    }

    // endregion
}
