package net.smartcosmos.extension.tenant.rest.service.user;

import java.util.Optional;
import javax.inject.Inject;

import lombok.extern.slf4j.Slf4j;

import org.springframework.core.convert.ConversionService;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import net.smartcosmos.events.DefaultEventTypes;
import net.smartcosmos.events.SmartCosmosEventTemplate;
import net.smartcosmos.extension.tenant.dao.RoleDao;
import net.smartcosmos.extension.tenant.dao.TenantDao;
import net.smartcosmos.extension.tenant.dto.user.GetOrDeleteUserResponse;
import net.smartcosmos.extension.tenant.rest.service.AbstractTenantService;
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

    public ResponseEntity<?> findByUrn(String urn, SmartCosmosUser user) {

        Optional<GetOrDeleteUserResponse> entity = tenantDao.findUserByUrn(user.getAccountUrn(), urn);

        if (entity.isPresent()) {
            sendEvent(user, DefaultEventTypes.UserRead, entity.get());
            return ResponseEntity
                .ok()
                .body(conversionService.convert(entity.get(), GetOrDeleteUserResponse.class));
        }

        GetOrDeleteUserResponse eventPayload = GetOrDeleteUserResponse.builder()
            .urn(urn)
            .tenantUrn(user.getAccountUrn())
            .build();
        sendEvent(user, DefaultEventTypes.UserNotFound, eventPayload);
        return ResponseEntity.notFound().build();
    }

    public ResponseEntity<?> findByName(String name, SmartCosmosUser user) {

        Optional<GetOrDeleteUserResponse> entity = tenantDao.findUserByName(user.getAccountUrn(), name);

        if (entity.isPresent()) {
            sendEvent(user, DefaultEventTypes.UserRead, entity.get());
            return ResponseEntity
                .ok()
                .body(conversionService.convert(entity.get(), GetOrDeleteUserResponse.class));
        }

        GetOrDeleteUserResponse eventPayload = GetOrDeleteUserResponse.builder()
            .username(name)
            .tenantUrn(user.getAccountUrn())
            .build();
        sendEvent(user, DefaultEventTypes.UserNotFound, eventPayload);
        return ResponseEntity.notFound().build();
    }
}
