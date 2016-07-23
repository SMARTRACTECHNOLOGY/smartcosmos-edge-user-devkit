package net.smartcosmos.extension.tenant.rest.service.role;

import lombok.extern.slf4j.Slf4j;
import net.smartcosmos.events.SmartCosmosEventTemplate;
import net.smartcosmos.extension.tenant.dao.RoleDao;
import net.smartcosmos.extension.tenant.dao.TenantDao;
import net.smartcosmos.extension.tenant.dto.role.RoleResponse;
import net.smartcosmos.extension.tenant.rest.dto.role.RestRoleResponse;
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
public class ReadRoleService extends AbstractTenantService {

    @Inject
    public ReadRoleService(
            TenantDao tenantDao, RoleDao roleDao, SmartCosmosEventTemplate smartCosmosEventTemplate, ConversionService
            conversionService) {
        super(tenantDao, roleDao, smartCosmosEventTemplate, conversionService);
    }

    public ResponseEntity<?> findByUrn(String urn, SmartCosmosUser user) {

        Optional<RoleResponse> entity = roleDao.findRoleByUrn(user.getAccountUrn(), urn);

        if (entity.isPresent()) {
            // TODO Enable event after merge in framework
            // sendEvent(user, DefaultEventTypes.RoleRead, entity.get());
            return ResponseEntity
                    .ok()
                    .body(conversionService.convert(entity.get(), RestRoleResponse.class));
        }

        RoleResponse eventPayload = RoleResponse.builder()
                .urn(urn)
                .tenantUrn(user.getAccountUrn())
                .build();
        // TODO Enable event after merge in framework
        // sendEvent(user, DefaultEventTypes.RoleNotFound, eventPayload);
        return ResponseEntity.notFound().build();
    }

    public ResponseEntity<?> query(String name, SmartCosmosUser user) {
        if (StringUtils.isBlank(name)) {
            return findAll(user);
        } else {
            return findByName(name, user);
        }
    }

    private ResponseEntity<?> findAll(SmartCosmosUser user) {

        List<RoleResponse> roleList = roleDao.findAllRoles(user.getAccountUrn());
        for (RoleResponse role : roleList) {
            // TODO Enable event after merge in framework
            // sendEvent(user, DefaultEventTypes.RoleRead, role);
        }

        return ResponseEntity
            .ok()
            .body(convertList(roleList, RoleResponse.class, RestRoleResponse.class));
    }

    public ResponseEntity<?> findByName(String name, SmartCosmosUser user) {

        Optional<RoleResponse> entity = roleDao.findRoleByName(user.getAccountUrn(), name);

        if (entity.isPresent()) {
            // TODO Enable event after merge in framework
            // sendEvent(user, DefaultEventTypes.RoleRead, entity.get());
            return ResponseEntity
                .ok()
                .body(conversionService.convert(entity.get(), RestRoleResponse.class));
        }

        RoleResponse eventPayload = RoleResponse.builder()
            .name(name)
            .tenantUrn(user.getAccountUrn())
            .build();
        // TODO Enable event after merge in framework
        // sendEvent(user, DefaultEventTypes.RoleNotFound, eventPayload);
        return ResponseEntity.notFound().build();
    }
}
