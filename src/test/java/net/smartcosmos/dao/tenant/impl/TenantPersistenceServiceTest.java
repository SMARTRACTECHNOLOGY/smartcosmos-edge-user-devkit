package net.smartcosmos.dao.tenant.impl;

import net.smartcosmos.dao.tenant.TenantPersistenceTestApplication;
import net.smartcosmos.ext.tenant.TenantPersistenceConfig;
import net.smartcosmos.ext.tenant.dto.CreateOrUpdateUserResponse;
import net.smartcosmos.ext.tenant.dto.CreateTenantRequest;
import net.smartcosmos.ext.tenant.dto.CreateTenantResponse;
import net.smartcosmos.ext.tenant.dto.CreateUserRequest;
import net.smartcosmos.ext.tenant.dto.GetTenantResponse;
import net.smartcosmos.ext.tenant.impl.TenantPersistenceService;
import net.smartcosmos.ext.tenant.repository.TenantRepository;
import net.smartcosmos.ext.tenant.util.UuidUtil;
import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.IntegrationTest;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

@SuppressWarnings("Duplicates")
@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = {
        TenantPersistenceTestApplication.class,
        TenantPersistenceConfig.class})
@ActiveProfiles("test")
@WebAppConfiguration
@IntegrationTest({ "spring.cloud.config.enabled=false", "eureka.client.enabled:false" })
public class TenantPersistenceServiceTest {

    @Autowired
    TenantPersistenceService tenantPersistenceService;

    @Autowired
    TenantRepository tenantRepository;

    @Before
    public void setup() throws Exception {
        prepareTenantForUserTests();
    }


    @After
    public void tearDown() throws Exception {
        tenantRepository.deleteAll();
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

    @Test
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

    @Ignore
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

    // endregion
}
