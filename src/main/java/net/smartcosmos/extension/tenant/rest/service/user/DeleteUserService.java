package net.smartcosmos.extension.tenant.rest.service.user;

import java.util.Optional;

import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.convert.ConversionService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.context.request.async.DeferredResult;

import net.smartcosmos.extension.tenant.dao.TenantDao;
import net.smartcosmos.extension.tenant.dto.user.UserResponse;
import net.smartcosmos.extension.tenant.rest.service.EventSendingService;
import net.smartcosmos.security.user.SmartCosmosUser;

import static net.smartcosmos.extension.tenant.rest.utility.UserEventType.USER_DELETED;
import static net.smartcosmos.extension.tenant.rest.utility.UserEventType.USER_NOT_FOUND;

/**
 * Initially created by SMART COSMOS Team on July 01, 2016.
 */
@Slf4j
@Service
public class DeleteUserService {

    private final TenantDao tenantDao;
    private final EventSendingService eventSendingService;
    private final ConversionService conversionService;

    @Autowired
    public DeleteUserService(TenantDao tenantDao, EventSendingService userEventSendingService, ConversionService conversionService) {

        this.tenantDao = tenantDao;
        this.eventSendingService = userEventSendingService;
        this.conversionService = conversionService;
    }

    public DeferredResult<ResponseEntity> delete(String userUrn, SmartCosmosUser user) {
        // Async worker thread reduces timeouts and disconnects for long queries and processing.
        DeferredResult<ResponseEntity> response = new DeferredResult<>();
        deleteUserWorker(response, user, userUrn);

        return response;
    }

    private void deleteUserWorker(DeferredResult<ResponseEntity> response, SmartCosmosUser user, String userUrn) {

        try {
            Optional<UserResponse> deleteUserResponse = tenantDao.deleteUserByUrn(user.getAccountUrn(), userUrn);

            if (deleteUserResponse.isPresent()) {
                response.setResult(ResponseEntity.noContent()
                                       .build());
                eventSendingService.sendEvent(user, USER_DELETED, deleteUserResponse.get());
            } else {
                response.setResult(ResponseEntity.status(HttpStatus.NOT_FOUND)
                                       .build());

                UserResponse eventPayload = UserResponse.builder()
                    .urn(userUrn)
                    .tenantUrn(user.getAccountUrn())
                    .build();
                eventSendingService.sendEvent(user, USER_NOT_FOUND, eventPayload);
            }

        } catch (Exception e) {
            log.debug(e.getMessage(), e);
            response.setErrorResult(e);
        }
    }
}
