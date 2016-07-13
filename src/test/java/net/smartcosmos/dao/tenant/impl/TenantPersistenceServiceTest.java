package net.smartcosmos.dao.tenant.impl;

import net.smartcosmos.dao.tenant.TenantPersistenceTestApplication;
import net.smartcosmos.ext.tenant.TenantPersistenceConfig;
import net.smartcosmos.ext.tenant.dto.CreateTenantRequest;
import net.smartcosmos.ext.tenant.dto.CreateTenantResponse;
import net.smartcosmos.ext.tenant.dto.GetTenantResponse;
import net.smartcosmos.ext.tenant.impl.TenantPersistenceService;
import net.smartcosmos.ext.tenant.repository.TenantRepository;
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

    @After
    public void tearDown() throws Exception {
        tenantRepository.deleteAll();
    }

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
}
