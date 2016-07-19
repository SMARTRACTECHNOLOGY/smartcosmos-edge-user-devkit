package net.smartcosmos.extension.tenant.repository;

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
import net.smartcosmos.extension.tenant.domain.RoleEntity;

import static org.junit.Assert.*;

@SuppressWarnings("Duplicates")
@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = {
    TenantPersistenceTestApplication.class,
    TenantPersistenceConfig.class })
@ActiveProfiles("test")
@WebAppConfiguration
@IntegrationTest({ "spring.cloud.config.enabled=false", "eureka.client.enabled:false" })
public class RoleRepositoryTest {

    @Autowired
    RoleRepository repository;

    private UUID id;
    private UUID tenantId;
    private String name = "roleName";
    private Boolean active = true;

    @Before
    public void setUp() throws Exception {

        tenantId = UUID.randomUUID();

        RoleEntity role = RoleEntity.builder()
            .tenantId(tenantId)
            .name(name)
            .active(active)
            .build();

        role = repository.save(role);

        id = role.getId();
    }

    @After
    public void tearDown() throws Exception {
        repository.deleteAll();
    }

    @Test
    public void persistEmptyAuthorities() throws Exception {

        final UUID tenantId = UUID.randomUUID();
        final String name = "authName";
        final Boolean active = true;

        RoleEntity createRole = RoleEntity.builder()
            .tenantId(tenantId)
            .name(name)
            .active(active)
            .build();

        RoleEntity persistRole = repository.save(createRole);

        assertNotNull(persistRole);
        assertEquals(tenantId, persistRole.getTenantId());
        assertEquals(active, persistRole.getActive());
        assertEquals(name, persistRole.getName());
        assertTrue(persistRole.getAuthorities().isEmpty());
    }

    @Test
    public void findById() throws Exception {

        Optional<RoleEntity> optional = repository.findById(id);
        assertTrue(optional.isPresent());

        RoleEntity role = optional.get();

        assertEquals(tenantId, role.getTenantId());
        assertEquals(active, role.getActive());
        assertEquals(name, role.getName());
    }

    @Test
    public void findByName() throws Exception {

        Optional<RoleEntity> optional = repository.findByNameAndTenantId(name, tenantId);
        assertTrue(optional.isPresent());

        RoleEntity role = optional.get();

        assertEquals(tenantId, role.getTenantId());
        assertEquals(active, role.getActive());
        assertEquals(name, role.getName());
    }
}
