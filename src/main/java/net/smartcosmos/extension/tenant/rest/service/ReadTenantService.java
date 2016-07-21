package net.smartcosmos.extension.tenant.rest.service;

import java.util.Optional;
import javax.inject.Inject;

import lombok.extern.slf4j.Slf4j;

import org.springframework.core.convert.ConversionService;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import net.smartcosmos.events.SmartCosmosEventTemplate;
import net.smartcosmos.extension.tenant.dao.RoleDao;
import net.smartcosmos.extension.tenant.dao.TenantDao;
import net.smartcosmos.extension.tenant.dto.GetTenantResponse;
import net.smartcosmos.extension.tenant.rest.dto.RestTenantSingleResponse;
import net.smartcosmos.security.user.SmartCosmosUser;

@Slf4j
@Service
public class ReadTenantService extends AbstractTenantService {

    @Inject
    public ReadTenantService(
        TenantDao tenantDao,
        RoleDao roleDao,
        SmartCosmosEventTemplate smartCosmosEventTemplate,
        ConversionService conversionService) {

        super(tenantDao, roleDao, smartCosmosEventTemplate, conversionService);
    }

    public ResponseEntity<?> findByUrn(String urn) {

        Optional<GetTenantResponse> entity = tenantDao.findTenantByUrn(urn);

        if (entity.isPresent()) {
            // TODO: send event tenant:read
            return ResponseEntity
                .ok()
                .body(conversionService.convert(entity.get(), RestTenantSingleResponse.class));
        }

        // TODO: send event tenant:notFound
        return ResponseEntity.notFound().build();
    }

    public ResponseEntity<?> findByName(String name, SmartCosmosUser user) {

        Optional<GetTenantResponse> entity = tenantDao.findTenantByName(name);

        if (entity.isPresent()) {
            // TODO: send event tenant:read
            return ResponseEntity
                .ok()
                .body(conversionService.convert(entity.get(), RestTenantSingleResponse.class));
        }

        // TODO: send event tenant:notFound
        return ResponseEntity.notFound().build();
    }
}
