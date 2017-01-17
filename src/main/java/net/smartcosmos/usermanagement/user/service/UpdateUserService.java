package net.smartcosmos.usermanagement.user.service;

import java.util.Optional;

import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.convert.ConversionService;
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
 * Initially created by SMART COSMOS Team on July 01, 2016.
 */
@Slf4j
@Service
public class UpdateUserService {

    private final TenantDao tenantDao;
    private final EventSendingService eventSendingService;
    private final ConversionService conversionService;

    @Autowired
    public UpdateUserService(TenantDao tenantDao, EventSendingService userEventSendingService, ConversionService conversionService) {

        this.tenantDao = tenantDao;
        this.eventSendingService = userEventSendingService;
        this.conversionService = conversionService;
    }

    public void update(DeferredResult<ResponseEntity> response, String userUrn, UpdateUserRequest userRequest, SmartCosmosUser user) {

        try {
            response.setResult(update(userUrn, userRequest, user));
        } catch (Exception e) {
            log.debug(e.getMessage(), e);
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
