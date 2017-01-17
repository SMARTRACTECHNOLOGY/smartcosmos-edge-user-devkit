package net.smartcosmos.usermanagement.role.persistence;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.junit.*;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.IntegrationTest;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;

import net.smartcosmos.cluster.userdetails.repository.RoleRepository;
import net.smartcosmos.cluster.userdetails.util.UuidUtil;
import net.smartcosmos.usermanagement.DevKitUserManagementService;
import net.smartcosmos.usermanagement.role.dto.RoleRequest;
import net.smartcosmos.usermanagement.role.dto.RoleResponse;

import static org.junit.Assert.*;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = { DevKitUserManagementService.class })
@ActiveProfiles("test")
@WebAppConfiguration
@IntegrationTest({ "spring.cloud.config.enabled=false", "eureka.client.enabled:false" })
@ComponentScan
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

        RoleRequest createRole = RoleRequest.builder()
            .active(true)
            .authorities(authorities)
            .name(roleName)
            .build();

        Optional<RoleResponse> createResponse = rolePersistenceService
            .createRole(tenantRoleTest, createRole);

        assertTrue(createResponse.isPresent());
        assertEquals(roleName,
                     createResponse.get()
                         .getName());
        assertEquals(1,
                     createResponse.get()
                         .getAuthorities()
                         .size());
        assertTrue(createResponse.get()
                       .getAuthorities()
                       .contains(authority));
    }

    @Test
    public void thatUpdateRoleSucceeds() {

        final String roleName = "updateTestRole";
        final String newRoleName = "updateTestNewRoleName";
        final String authority1 = "testAuth1";
        final String authority2 = "testAuth2";

        List<String> authorities = new ArrayList<>();
        authorities.add(authority1);

        RoleRequest createRole = RoleRequest.builder()
            .active(true)
            .authorities(authorities)
            .name(newRoleName)
            .build();

        Optional<RoleResponse> createResponse = rolePersistenceService
            .createRole(tenantRoleTest, createRole);

        assertTrue(createResponse.isPresent());
        assertEquals(newRoleName,
                     createResponse.get()
                         .getName());
        assertEquals(1,
                     createResponse.get()
                         .getAuthorities()
                         .size());

        String urn = createResponse.get()
            .getUrn();

        authorities.add(authority2);

        RoleRequest updateRole = RoleRequest.builder()
            .active(true)
            .authorities(authorities)
            .name(roleName)
            .build();

        Optional<RoleResponse> updateResponse = rolePersistenceService
            .updateRole(tenantRoleTest, urn, updateRole);

        assertTrue(updateResponse.isPresent());
        assertEquals(roleName,
                     updateResponse.get()
                         .getName());
        assertEquals(2,
                     updateResponse.get()
                         .getAuthorities()
                         .size());
    }

    @Test(expected = IllegalArgumentException.class)
    public void thatUpdateRoleFailsWhenNameAlreadyExistsForDifferentRole() {

        final String roleName1 = "updateTestRole1";
        final String authority1 = "testAuth1";

        final String roleName2 = "updateTestRole2";
        final String authority2 = "testAuth2";

        List<String> authorities1 = new ArrayList<>();
        authorities1.add(authority1);

        List<String> authorities2 = new ArrayList<>();
        authorities2.add(authority2);

        RoleRequest createRole1 = RoleRequest.builder()
            .active(true)
            .authorities(authorities1)
            .name(roleName1)
            .build();

        RoleRequest createRole2 = RoleRequest.builder()
            .active(true)
            .authorities(authorities2)
            .name(roleName2)
            .build();

        Optional<RoleResponse> createResponse1 = rolePersistenceService
            .createRole(tenantRoleTest, createRole1);

        Optional<RoleResponse> createResponse2 = rolePersistenceService
            .createRole(tenantRoleTest, createRole2);

        assertTrue(createResponse1.isPresent());
        assertEquals(roleName1,
                     createResponse1.get()
                         .getName());
        assertEquals(1,
                     createResponse1.get()
                         .getAuthorities()
                         .size());

        String urn = createResponse1.get()
            .getUrn();

        authorities1.add(authority2);

        RoleRequest updateRole = RoleRequest.builder()
            .active(true)
            .authorities(authorities1)
            .name(roleName2)
            .build();

        rolePersistenceService.updateRole(tenantRoleTest, urn, updateRole);
    }

    @Test
    public void thatLookupRoleByNameSucceeds() {

        final String roleName = "lookupTestRole";
        final String authority = "testAuth";

        List<String> authorities = new ArrayList<>();
        authorities.add(authority);

        RoleRequest createRole = RoleRequest.builder()
            .active(true)
            .authorities(authorities)
            .name(roleName)
            .build();

        Optional<RoleResponse> createResponse = rolePersistenceService
            .createRole(tenantRoleTest, createRole);

        assertTrue(createResponse.isPresent());
        assertEquals(roleName,
                     createResponse.get()
                         .getName());
        assertEquals(1,
                     createResponse.get()
                         .getAuthorities()
                         .size());
        assertTrue(createResponse.get()
                       .getAuthorities()
                       .contains(authority));

        Optional<RoleResponse> lookupResponse = rolePersistenceService
            .findRoleByName(tenantRoleTest, roleName);

        assertTrue(lookupResponse.isPresent());
        assertEquals(roleName,
                     lookupResponse.get()
                         .getName());
        assertEquals(1,
                     lookupResponse.get()
                         .getAuthorities()
                         .size());
        assertTrue(lookupResponse.get()
                       .getAuthorities()
                       .contains(authority));
    }

    @Test
    public void thatLookupRoleByNameFails() {

        final String roleName = "noSuchRole";

        Optional<RoleResponse> lookupResponse = rolePersistenceService
            .findRoleByName(tenantRoleTest, roleName);

        assertFalse(lookupResponse.isPresent());
    }

    @Test
    public void thatDeleteRoleByUrnSucceeds() {

        final String roleName = "deleteTestRole";
        final String authority = "testAuth";

        List<String> authorities = new ArrayList<>();
        authorities.add(authority);

        RoleRequest createRole = RoleRequest.builder()
            .active(true)
            .authorities(authorities)
            .name(roleName)
            .build();

        Optional<RoleResponse> createResponse = rolePersistenceService
            .createRole(tenantRoleTest, createRole);

        assertTrue(createResponse.isPresent());
        assertEquals(roleName,
                     createResponse.get()
                         .getName());
        assertEquals(1,
                     createResponse.get()
                         .getAuthorities()
                         .size());
        assertTrue(createResponse.get()
                       .getAuthorities()
                       .contains(authority));

        String urn = createResponse.get()
            .getUrn();

        List<RoleResponse> deleteResponse = rolePersistenceService
            .delete(tenantRoleTest, urn);

        assertFalse(deleteResponse.isEmpty());
        assertEquals(1, deleteResponse.size());
    }

    @Test
    public void thatFindRoleByUrnSucceeds() throws Exception {

        final String roleName = "findByUrnTestRole";
        final String authority = "testAuth";

        List<String> authorities = new ArrayList<>();
        authorities.add(authority);

        RoleRequest createRole = RoleRequest.builder()
            .active(true)
            .authorities(authorities)
            .name(roleName)
            .build();

        Optional<RoleResponse> createResponse = rolePersistenceService
            .createRole(tenantRoleTest, createRole);

        assertTrue(createResponse.isPresent());
        assertEquals(roleName,
                     createResponse.get()
                         .getName());
        assertEquals(1,
                     createResponse.get()
                         .getAuthorities()
                         .size());
        assertTrue(createResponse.get()
                       .getAuthorities()
                       .contains(authority));

        String urn = createResponse.get()
            .getUrn();

        Optional<RoleResponse> findResponse = rolePersistenceService
            .findRoleByUrn(tenantRoleTest, urn);

        assertTrue(findResponse.isPresent());
        assertEquals(roleName,
                     findResponse.get()
                         .getName());
    }

    @Test
    public void thatFindAllRolesSucceeds() throws Exception {

        final String roleName = "findAllTestRole";
        final String authority = "testAuth";

        List<String> authorities = new ArrayList<>();
        authorities.add(authority);

        RoleRequest createRole = RoleRequest.builder()
            .active(true)
            .authorities(authorities)
            .name(roleName)
            .build();

        Optional<RoleResponse> createResponse = rolePersistenceService
            .createRole(tenantRoleTest, createRole);

        assertTrue(createResponse.isPresent());
        assertEquals(roleName,
                     createResponse.get()
                         .getName());
        assertEquals(1,
                     createResponse.get()
                         .getAuthorities()
                         .size());
        assertTrue(createResponse.get()
                       .getAuthorities()
                       .contains(authority));

        List<RoleResponse> findResponse = rolePersistenceService
            .findAllRoles(tenantRoleTest);

        assertFalse(findResponse.isEmpty());
    }
}
