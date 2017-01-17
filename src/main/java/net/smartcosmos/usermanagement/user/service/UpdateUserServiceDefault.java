package net.smartcosmos.usermanagement.user.service;

import java.util.Optional;

import lombok.extern.slf4j.Slf4j;

import org.apache.commons.lang.exception.ExceptionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.context.request.async.DeferredResult;

import net.smartcosmos.security.user.SmartCosmosUser;
import net.smartcosmos.usermanagement.event.EventSendingService;
import net.smartcosmos.usermanagement.tenant.persistence.TenantDao;
import net.smartcosmos.usermanagement.user.dto.UpdateUserRequest;
import net.smartcosmos.usermanagement.user.dto.UserResponse;

import static net.smartcosmos.usermanagement.event.UserEventType.USER_NOT_FOUND;
import static net.smartcosmos.usermanagement.event.UserEventType.USER_UPDATED;

/**
 * The default implementation of of the {@link UpdateUserService}.
 */
@Slf4j
@Service
public class UpdateUserServiceDefault implements UpdateUserService {

    private final TenantDao tenantDao;
    private final EventSendingService eventSendingService;

    @Autowired
    public UpdateUserServiceDefault(TenantDao tenantDao, EventSendingService userEventSendingService) {

        this.tenantDao = tenantDao;
        this.eventSendingService = userEventSendingService;
    }

    @Override
    public void update(DeferredResult<ResponseEntity<?>> response, String userUrn, UpdateUserRequest userRequest, SmartCosmosUser user) {

        try {
            response.setResult(update(userUrn, userRequest, user));
        } catch (Exception e) {
            Throwable rootException = ExceptionUtils.getRootCause(e);
            String rootCause = "";
            if (rootException != null) {
                rootCause = String.format(", rootCause: '%s'", rootException.toString());
            }
            String msg = String.format("Exception on update user. user URN: %s,  request: %s, user: %s, cause: '%s'%s.",
                                       userUrn,
                                       userRequest,
                                       user.getUserUrn(),
                                       e.toString(),
                                       rootCause);
            log.warn(msg);
            log.debug(msg, e);
            response.setErrorResult(e);
        }
    }

    private ResponseEntity<?> update(String userUrn, UpdateUserRequest userRequest, SmartCosmosUser user) {

        Optional<UserResponse> updateUserResponse = tenantDao.updateUser(user.getAccountUrn(), userUrn, userRequest);

        if (updateUserResponse.isPresent()) {
            eventSendingService.sendEvent(user, USER_UPDATED, updateUserResponse.get());
            return ResponseEntity.noContent()
                .build();
        } else {
            eventSendingService.sendEvent(user, USER_NOT_FOUND, userRequest);
            return ResponseEntity.notFound()
                .build();
        }
    }
}
