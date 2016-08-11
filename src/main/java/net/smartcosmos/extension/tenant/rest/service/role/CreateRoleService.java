package net.smartcosmos.extension.tenant.rest.service.role;

import java.net.URI;
import java.util.Optional;

import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.convert.ConversionService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.context.request.async.DeferredResult;

import net.smartcosmos.extension.tenant.dao.RoleDao;
import net.smartcosmos.extension.tenant.dto.role.CreateOrUpdateRoleRequest;
import net.smartcosmos.extension.tenant.dto.role.RoleResponse;
import net.smartcosmos.extension.tenant.rest.dto.role.RestCreateOrUpdateRoleRequest;
import net.smartcosmos.extension.tenant.rest.service.EventSendingService;
import net.smartcosmos.security.user.SmartCosmosUser;

import static net.smartcosmos.extension.tenant.rest.utility.RoleEventType.ROLE_CREATED;
import static net.smartcosmos.extension.tenant.rest.utility.RoleEventType.ROLE_CREATE_FAILED_ALREADY_EXISTS;

/**
 * Initially created by SMART COSMOS Team on July 01, 2016.
 */
@Slf4j
@Service
public class CreateRoleService {

    private final RoleDao roleDao;
    private final EventSendingService eventSendingService;
    private final ConversionService conversionService;

    @Autowired
    public CreateRoleService(RoleDao roleDao, EventSendingService roleEventSendingService, ConversionService conversionService) {

        this.roleDao = roleDao;
        this.eventSendingService = roleEventSendingService;
        this.conversionService = conversionService;
    }

    public DeferredResult<ResponseEntity> create(RestCreateOrUpdateRoleRequest restCreateOrUpdateRoleRequest, SmartCosmosUser user) {
        // Async worker thread reduces timeouts and disconnects for long queries and processing.
        DeferredResult<ResponseEntity> response = new DeferredResult<>();
        createRoleWorker(response, user.getAccountUrn(), restCreateOrUpdateRoleRequest, user);

        return response;
    }

    private void createRoleWorker(
        DeferredResult<ResponseEntity> response, String tenantUrn, RestCreateOrUpdateRoleRequest
        roleRequest, SmartCosmosUser user) {

        try {
            final CreateOrUpdateRoleRequest createRoleRequest = conversionService
                .convert(roleRequest, CreateOrUpdateRoleRequest.class);

            Optional<RoleResponse> newRole = roleDao.createRole(tenantUrn, createRoleRequest);

            if (newRole.isPresent()) {
                ResponseEntity responseEntity = buildCreatedResponseEntity(newRole.get());
                response.setResult(responseEntity);
                eventSendingService.sendEvent(user, ROLE_CREATED, newRole.get());
            } else {
                response.setResult(ResponseEntity.status(HttpStatus.CONFLICT)
                                       .build());
                eventSendingService.sendEvent(user, ROLE_CREATE_FAILED_ALREADY_EXISTS, roleRequest);
            }

        } catch (Exception e) {
            log.debug(e.getMessage(), e);
            response.setErrorResult(e);
        }
    }

    private ResponseEntity buildCreatedResponseEntity(RoleResponse response) {

        return ResponseEntity
            .created(URI.create(response.getUrn()))
            .body(response);
    }
}
