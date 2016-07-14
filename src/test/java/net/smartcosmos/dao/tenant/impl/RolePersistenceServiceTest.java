package net.smartcosmos.dao.tenant.impl;

import net.smartcosmos.dao.tenant.TenantPersistenceTestApplication;
import net.smartcosmos.ext.tenant.TenantPersistenceConfig;
import net.smartcosmos.ext.tenant.dto.CreateOrUpdateRoleRequest;
import net.smartcosmos.ext.tenant.dto.CreateOrUpdateRoleResponse;
import net.smartcosmos.ext.tenant.dto.GetRoleResponse;
import net.smartcosmos.ext.tenant.impl.RolePersistenceService;
import net.smartcosmos.ext.tenant.repository.RoleRepository;
import net.smartcosmos.ext.tenant.util.UuidUtil;
import org.junit.After;
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
public class RolePersistenceServiceTest {

    @Autowired
    RolePersistenceService rolePersistenceService;

    @Autowired
    RoleRepository roleRepository;

    @After
    public void tearDown() throws Exception {
        roleRepository.deleteAll();
    }

    private final String tenantRoleTest = UuidUtil.getTenantUrnFromUuid(UuidUtil.getNewUuid());

    @Test
    public void thatCreateRoleSucceeds() {
        final String roleName = "createTestRole";
        final String authority = "testAuth";

        List<String> authorities = new ArrayList<>();
        authorities.add(authority);

        CreateOrUpdateRoleRequest createRole = CreateOrUpdateRoleRequest.builder()
                .active(true)
                .authorities(authorities)
                .name(roleName)
                .build();

        Optional<CreateOrUpdateRoleResponse> createResponse = rolePersistenceService
                .createRole(tenantRoleTest, createRole);

        assertTrue(createResponse.isPresent());
        assertEquals(roleName, createResponse.get().getName());
        assertEquals(1, createResponse.get().getAuthorities().size());
        assertEquals(authority, createResponse.get().getAuthorities().get(0));
    }

    @Test
    public void thatUpdateRoleSucceeds() {
        final String roleName = "updateTestRole";
        final String authority1 = "testAuth1";
        final String authority2 = "testAuth2";

        List<String> authorities = new ArrayList<>();
        authorities.add(authority1);

        CreateOrUpdateRoleRequest createRole = CreateOrUpdateRoleRequest.builder()
                .active(true)
                .authorities(authorities)
                .name(roleName)
                .build();

        Optional<CreateOrUpdateRoleResponse> createResponse = rolePersistenceService
                .createRole(tenantRoleTest, createRole);

        assertTrue(createResponse.isPresent());
        assertEquals(roleName, createResponse.get().getName());
        assertEquals(1, createResponse.get().getAuthorities().size());

        String urn = createResponse.get().getUrn();

        authorities.add(authority2);

        CreateOrUpdateRoleRequest updateRole = CreateOrUpdateRoleRequest.builder()
                .active(true)
                .authorities(authorities)
                .name(roleName)
                .build();

        Optional<CreateOrUpdateRoleResponse> updateResponse = rolePersistenceService
                .updateRole(tenantRoleTest, urn, updateRole);

        assertTrue(updateResponse.isPresent());
        assertEquals(roleName, updateResponse.get().getName());
        assertEquals(2, updateResponse.get().getAuthorities().size());
    }

    @Test
    public void thatLookupRoleByNameSucceeds() {
        final String roleName = "lookupTestRole";
        final String authority = "testAuth";

        List<String> authorities = new ArrayList<>();
        authorities.add(authority);

        CreateOrUpdateRoleRequest createRole = CreateOrUpdateRoleRequest.builder()
                .active(true)
                .authorities(authorities)
                .name(roleName)
                .build();

        Optional<CreateOrUpdateRoleResponse> createResponse = rolePersistenceService
                .createRole(tenantRoleTest, createRole);

        assertTrue(createResponse.isPresent());
        assertEquals(roleName, createResponse.get().getName());
        assertEquals(1, createResponse.get().getAuthorities().size());
        assertEquals(authority, createResponse.get().getAuthorities().get(0));

        Optional<GetRoleResponse> lookupResponse = rolePersistenceService
                .findByTenantUrnAndName(tenantRoleTest, roleName);

        assertTrue(lookupResponse.isPresent());
        assertEquals(roleName, lookupResponse.get().getName());
        assertEquals(1, lookupResponse.get().getAuthorities().size());
        assertEquals(authority, lookupResponse.get().getAuthorities().get(0));
    }

    @Test
    public void thatLookupRoleByNameFails() {
        final String roleName = "noSuchRole";

        Optional<GetRoleResponse> lookupResponse = rolePersistenceService
                .findByTenantUrnAndName(tenantRoleTest, roleName);

        assertFalse(lookupResponse.isPresent());
    }

    @Test
    public void thatDeleteRoleByUrnSucceeds() {
        final String roleName = "deleteTestRole";
        final String authority = "testAuth";

        List<String> authorities = new ArrayList<>();
        authorities.add(authority);

        CreateOrUpdateRoleRequest createRole = CreateOrUpdateRoleRequest.builder()
                .active(true)
                .authorities(authorities)
                .name(roleName)
                .build();

        Optional<CreateOrUpdateRoleResponse> createResponse = rolePersistenceService
                .createRole(tenantRoleTest, createRole);

        assertTrue(createResponse.isPresent());
        assertEquals(roleName, createResponse.get().getName());
        assertEquals(1, createResponse.get().getAuthorities().size());
        assertEquals(authority, createResponse.get().getAuthorities().get(0));

        String urn = createResponse.get().getUrn();

        List<GetRoleResponse> deleteResponse = rolePersistenceService
                .delete(tenantRoleTest, urn);

        assertFalse(deleteResponse.isEmpty());
        assertEquals(1, deleteResponse.size());
    }
}
