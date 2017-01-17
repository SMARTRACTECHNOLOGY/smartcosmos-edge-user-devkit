package net.smartcosmos.usermanagement.role.service;

import java.net.URI;
import java.util.Optional;

import lombok.extern.slf4j.Slf4j;

import org.apache.commons.lang.exception.ExceptionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.convert.ConversionService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.context.request.async.DeferredResult;

import net.smartcosmos.security.user.SmartCosmosUser;
import net.smartcosmos.usermanagement.event.EventSendingService;
import net.smartcosmos.usermanagement.role.dto.RoleRequest;
import net.smartcosmos.usermanagement.role.dto.RoleResponse;
import net.smartcosmos.usermanagement.role.persistence.RoleDao;

import static net.smartcosmos.usermanagement.event.RoleEventType.ROLE_CREATED;
import static net.smartcosmos.usermanagement.event.RoleEventType.ROLE_CREATE_FAILED_ALREADY_EXISTS;

/**
 * The default implementation of the {@link CreateRoleService}.
 */
@Slf4j
@Service
public class CreateRoleServiceDefault implements CreateRoleService {

    private final RoleDao roleDao;
    private final EventSendingService eventSendingService;
    private final ConversionService conversionService;

    @Autowired
    public CreateRoleServiceDefault(RoleDao roleDao, EventSendingService roleEventSendingService, ConversionService conversionService) {

        this.roleDao = roleDao;
        this.eventSendingService = roleEventSendingService;
        this.conversionService = conversionService;
    }

    @Override
    public void create(DeferredResult<ResponseEntity<?>> response, RoleRequest createRoleRequest, SmartCosmosUser user) {

        try {
            response.setResult(createRoleWorker(user.getAccountUrn(), createRoleRequest, user));
        } catch (Exception e) {
            Throwable rootException = ExceptionUtils.getRootCause(e);
            String rootCause = "";
            if (rootException != null) {
                rootCause = String.format(", rootCause: '%s'", rootException.toString());
            }
            String msg = String.format("Exception on create role. request: %s, user: %s, cause: '%s', rootCause: '%s'.",
                                       createRoleRequest,
                                       user.getUserUrn(),
                                       e.toString(),
                                       rootCause);
            log.warn(msg);
            log.debug(msg, e);
            response.setErrorResult(e);
        }
    }

    private ResponseEntity<?> createRoleWorker(String tenantUrn, RoleRequest roleRequest, SmartCosmosUser user) {

        final RoleRequest createRoleRequest = conversionService
            .convert(roleRequest, RoleRequest.class);

        Optional<RoleResponse> newRole = roleDao.createRole(tenantUrn, createRoleRequest);

        if (newRole.isPresent()) {
            ResponseEntity responseEntity = buildCreatedResponseEntity(newRole.get());
            eventSendingService.sendEvent(user, ROLE_CREATED, newRole.get());
            return responseEntity;
        } else {
            eventSendingService.sendEvent(user, ROLE_CREATE_FAILED_ALREADY_EXISTS, roleRequest);
            return ResponseEntity.status(HttpStatus.CONFLICT)
                .build();
        }
    }

    private ResponseEntity buildCreatedResponseEntity(RoleResponse response) {

        return ResponseEntity
            .created(URI.create(response.getUrn()))
            .body(response);
    }
}
