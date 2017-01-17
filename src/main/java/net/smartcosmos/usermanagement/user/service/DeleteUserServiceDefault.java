package net.smartcosmos.usermanagement.user.service;

import java.util.Optional;

import lombok.extern.slf4j.Slf4j;

import org.apache.commons.lang.exception.ExceptionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.context.request.async.DeferredResult;

import net.smartcosmos.security.user.SmartCosmosUser;
import net.smartcosmos.usermanagement.event.EventSendingService;
import net.smartcosmos.usermanagement.tenant.persistence.TenantDao;
import net.smartcosmos.usermanagement.user.dto.UserResponse;

import static net.smartcosmos.usermanagement.event.UserEventType.USER_DELETED;
import static net.smartcosmos.usermanagement.event.UserEventType.USER_NOT_FOUND;

/**
 * The default implementation of the {@link DeleteUserService}.
 */
@Slf4j
@Service
public class DeleteUserServiceDefault implements DeleteUserService {

    private final TenantDao tenantDao;
    private final EventSendingService eventSendingService;

    @Autowired
    public DeleteUserServiceDefault(TenantDao tenantDao, EventSendingService userEventSendingService) {

        this.tenantDao = tenantDao;
        this.eventSendingService = userEventSendingService;
    }

    @Override
    public void delete(DeferredResult<ResponseEntity<?>> response, String userUrn, SmartCosmosUser user) {

        try {
            response.setResult(deleteUserWorker(userUrn, user));
        } catch (Exception e) {
            Throwable rootException = ExceptionUtils.getRootCause(e);
            String rootCause = "";
            if (rootException != null) {
                rootCause = String.format(", rootCause: '%s'", rootException.toString());
            }
            String msg = String.format("Exception on delete user. user URN: %s, user: %s, cause: '%s'%s.",
                                       userUrn,
                                       user.getUserUrn(),
                                       e.toString(),
                                       rootCause);
            log.warn(msg);
            log.debug(msg, e);
            response.setErrorResult(e);
        }
    }

    private ResponseEntity<?> deleteUserWorker(String userUrn, SmartCosmosUser user) {

        Optional<UserResponse> deleteUserResponse = tenantDao.deleteUserByUrn(user.getAccountUrn(), userUrn);

        if (deleteUserResponse.isPresent()) {
            eventSendingService.sendEvent(user, USER_DELETED, deleteUserResponse.get());
            return ResponseEntity.noContent()
                .build();
        } else {
            UserResponse eventPayload = UserResponse.builder()
                .urn(userUrn)
                .tenantUrn(user.getAccountUrn())
                .build();
            eventSendingService.sendEvent(user, USER_NOT_FOUND, eventPayload);
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .build();
        }
    }
}
