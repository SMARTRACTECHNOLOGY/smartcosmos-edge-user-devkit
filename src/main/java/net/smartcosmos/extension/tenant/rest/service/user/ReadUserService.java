package net.smartcosmos.extension.tenant.rest.service.user;

import lombok.extern.slf4j.Slf4j;
import net.smartcosmos.events.DefaultEventTypes;
import net.smartcosmos.events.SmartCosmosEventTemplate;
import net.smartcosmos.extension.tenant.dao.RoleDao;
import net.smartcosmos.extension.tenant.dao.TenantDao;
import net.smartcosmos.extension.tenant.dto.user.UserResponse;
import net.smartcosmos.extension.tenant.rest.dto.user.RestUserResponse;
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

        Optional<UserResponse> entity = tenantDao.findUserByUrn(user.getAccountUrn(), urn);

        if (entity.isPresent()) {
            sendEvent(user, DefaultEventTypes.UserRead, entity.get());
            return ResponseEntity
                .ok()
                .body(conversionService.convert(entity.get(), RestUserResponse.class));
        }

        UserResponse eventPayload = UserResponse.builder()
            .urn(urn)
            .tenantUrn(user.getAccountUrn())
            .build();
        sendEvent(user, DefaultEventTypes.UserNotFound, eventPayload);
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

        List<UserResponse> userList = tenantDao.findAllUsers(user.getAccountUrn());
        for (UserResponse userResponse : userList) {
            sendEvent(user, DefaultEventTypes.UserRead, userResponse);
        }

        return ResponseEntity
                .ok()
                .body(convertList(userList, UserResponse.class, RestUserResponse.class));
    }

    public ResponseEntity<?> findByName(String name, SmartCosmosUser user) {

        Optional<UserResponse> entity = tenantDao.findUserByName(user.getAccountUrn(), name);

        if (entity.isPresent()) {
            sendEvent(user, DefaultEventTypes.UserRead, entity.get());
            return ResponseEntity
                .ok()
                .body(conversionService.convert(entity.get(), RestUserResponse.class));
        }

        UserResponse eventPayload = UserResponse.builder()
            .username(name)
            .tenantUrn(user.getAccountUrn())
            .build();
        sendEvent(user, DefaultEventTypes.UserNotFound, eventPayload);
        return ResponseEntity.notFound().build();
    }
}
