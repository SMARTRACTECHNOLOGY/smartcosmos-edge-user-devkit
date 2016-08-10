package net.smartcosmos.extension.tenant.rest.service.tenant;

import lombok.extern.slf4j.Slf4j;
import net.smartcosmos.events.DefaultEventTypes;
import net.smartcosmos.events.SmartCosmosEventTemplate;
import net.smartcosmos.extension.tenant.dao.RoleDao;
import net.smartcosmos.extension.tenant.dao.TenantDao;
import net.smartcosmos.extension.tenant.dto.tenant.TenantResponse;
import net.smartcosmos.extension.tenant.rest.dto.tenant.RestTenantResponse;
import net.smartcosmos.extension.tenant.rest.service.AbstractTenantService;
import net.smartcosmos.security.user.SmartCosmosUser;
import org.apache.commons.lang.StringUtils;
import org.springframework.core.convert.ConversionService;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import javax.inject.Inject;
import java.util.List;
import java.util.Optional;

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

    public ResponseEntity<?> findByUrn(String urn, SmartCosmosUser user) {

        Optional<TenantResponse> entity = tenantDao.findTenantByUrn(urn);

        if (entity.isPresent()) {
            sendEvent(user, DefaultEventTypes.TenantRead, entity.get());
            return ResponseEntity
                .ok()
                .body(conversionService.convert(entity.get(), RestTenantResponse.class));
        }

        sendEvent(user, DefaultEventTypes.TenantNotFound, urn);
        return ResponseEntity.notFound().build();
    }

    public ResponseEntity<?> query(String name, SmartCosmosUser user) {
        if (StringUtils.isBlank(name)) {
            return findAll(user);
        } else {
            return findByName(name, user);
        }
    }

    public ResponseEntity<?> findAll(SmartCosmosUser user) {

        List<TenantResponse> tenantList = tenantDao.findAllTenants();
        for (TenantResponse tenant : tenantList) {
            sendEvent(user, DefaultEventTypes.TenantRead, tenant);
        }

        return ResponseEntity
                .ok()
                .body(convertList(tenantList, TenantResponse.class, RestTenantResponse.class));
    }

    public ResponseEntity<?> findByName(String name, SmartCosmosUser user) {

        Optional<TenantResponse> entity = tenantDao.findTenantByName(name);

        if (entity.isPresent()) {
            sendEvent(user, DefaultEventTypes.TenantRead, entity.get());
            return ResponseEntity
                .ok()
                .body(conversionService.convert(entity.get(), RestTenantResponse.class));
        }

        sendEvent(user, DefaultEventTypes.TenantNotFound, name);
        return ResponseEntity.notFound().build();
    }
}
