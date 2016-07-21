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
import net.smartcosmos.extension.tenant.dto.GetOrDeleteUserResponse;
import net.smartcosmos.security.user.SmartCosmosUser;

@Slf4j
@Service
public class ReadUserService extends AbstractTenantService {

    @Inject
    public ReadUserService(
        TenantDao tenantDao,
        RoleDao roleDao,
        SmartCosmosEventTemplate smartCosmosEventTemplate,
        ConversionService conversionService) {

        super(tenantDao, roleDao, smartCosmosEventTemplate, conversionService);
    }

    public ResponseEntity<?> findByUrn(String urn) {

        Optional<GetOrDeleteUserResponse> entity = tenantDao.findUserByUrn(urn);

        if (entity.isPresent()) {
            // TODO: send event user:read
            return ResponseEntity
                .ok()
                .body(conversionService.convert(entity.get(), GetOrDeleteUserResponse.class));
        }

        // TODO: send event tenant:notFound
        return ResponseEntity.notFound().build();
    }

    public ResponseEntity<?> findByName(String name, SmartCosmosUser user) {

        Optional<GetOrDeleteUserResponse> entity = tenantDao.findUserByName(name);

        if (entity.isPresent()) {
            // TODO: send event tenant:read
            return ResponseEntity
                .ok()
                .body(conversionService.convert(entity.get(), GetOrDeleteUserResponse.class));
        }

        // TODO: send event tenant:notFound
        return ResponseEntity.notFound().build();
    }
}
