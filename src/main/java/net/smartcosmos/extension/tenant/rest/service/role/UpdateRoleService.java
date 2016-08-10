package net.smartcosmos.extension.tenant.rest.service.role;

import java.util.Optional;

import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.convert.ConversionService;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.web.context.request.async.DeferredResult;

import net.smartcosmos.extension.tenant.dao.RoleDao;
import net.smartcosmos.extension.tenant.dto.role.CreateOrUpdateRoleRequest;
import net.smartcosmos.extension.tenant.dto.role.RoleResponse;
import net.smartcosmos.extension.tenant.rest.dto.role.RestCreateOrUpdateRoleRequest;
import net.smartcosmos.extension.tenant.rest.service.EventSendingService;
import net.smartcosmos.security.user.SmartCosmosUser;

import static net.smartcosmos.extension.tenant.rest.utility.RoleEventType.ROLE_NOT_FOUND;
import static net.smartcosmos.extension.tenant.rest.utility.RoleEventType.ROLE_UPDATED;

/**
 * Initially created by SMART COSMOS Team on July 01, 2016.
 */
@Slf4j
@Service
public class UpdateRoleService {

    private final RoleDao roleDao;
    private final EventSendingService eventSendingService;
    private final ConversionService conversionService;

    @Autowired
    public UpdateRoleService(RoleDao roleDao, EventSendingService roleEventSendingService, ConversionService conversionService) {

        this.roleDao = roleDao;
        this.eventSendingService = roleEventSendingService;
        this.conversionService = conversionService;
    }

    public DeferredResult<ResponseEntity> update(String roleUrn, RestCreateOrUpdateRoleRequest updateRequest, SmartCosmosUser user) {
        // Async worker thread reduces timeouts and disconnects for long queries and processing.
        DeferredResult<ResponseEntity> response = new DeferredResult<>();
        updateRoleWorker(response, roleUrn, updateRequest, user);

        return response;
    }

    @Async
    private void updateRoleWorker(
        DeferredResult<ResponseEntity> response,
        String roleUrn,
        RestCreateOrUpdateRoleRequest restRequest,
        SmartCosmosUser user) {

        try {
            final CreateOrUpdateRoleRequest updateRoleRequest = conversionService
                .convert(restRequest, CreateOrUpdateRoleRequest.class);

            Optional<RoleResponse> updateRoleResponse = roleDao.updateRole(user.getAccountUrn(), roleUrn, updateRoleRequest);

            if (updateRoleResponse.isPresent()) {
                eventSendingService.sendEvent(user, ROLE_UPDATED, updateRoleResponse.get());

                ResponseEntity responseEntity = ResponseEntity.noContent()
                    .build();
                response.setResult(responseEntity);
            } else {
                RoleResponse eventPayload = RoleResponse.builder()
                    .urn(roleUrn)
                    .tenantUrn(user.getAccountUrn())
                    .build();
                eventSendingService.sendEvent(user, ROLE_NOT_FOUND, eventPayload);

                ResponseEntity responseEntity = ResponseEntity.notFound()
                    .build();
                response.setResult(responseEntity);
            }

        } catch (Exception e) {
            log.debug(e.getMessage(), e);
            response.setErrorResult(e);
        }
    }

}
