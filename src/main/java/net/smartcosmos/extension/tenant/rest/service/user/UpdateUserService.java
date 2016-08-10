package net.smartcosmos.extension.tenant.rest.service.user;

import java.util.Optional;

import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.convert.ConversionService;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.web.context.request.async.DeferredResult;

import net.smartcosmos.extension.tenant.dao.TenantDao;
import net.smartcosmos.extension.tenant.dto.user.CreateOrUpdateUserRequest;
import net.smartcosmos.extension.tenant.dto.user.UserResponse;
import net.smartcosmos.extension.tenant.rest.dto.user.RestCreateOrUpdateUserRequest;
import net.smartcosmos.extension.tenant.rest.service.EventSendingService;
import net.smartcosmos.security.user.SmartCosmosUser;

import static net.smartcosmos.extension.tenant.rest.utility.UserEventType.USER_NOT_FOUND;
import static net.smartcosmos.extension.tenant.rest.utility.UserEventType.USER_UPDATED;

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

    public DeferredResult<ResponseEntity> update(String userUrn, RestCreateOrUpdateUserRequest userRequest, SmartCosmosUser user) {
        // Async worker thread reduces timeouts and disconnects for long queries and processing.
        DeferredResult<ResponseEntity> response = new DeferredResult<>();
        updateUserWorker(response, user, userUrn, userRequest);

        return response;
    }

    @Async
    private void updateUserWorker(
        DeferredResult<ResponseEntity> response,
        SmartCosmosUser user,
        String userUrn,
        RestCreateOrUpdateUserRequest userRequest) {

        try {
            CreateOrUpdateUserRequest updateUserRequest = conversionService.convert(userRequest, CreateOrUpdateUserRequest.class);
            Optional<UserResponse> updateUserResponse = tenantDao.updateUser(user.getAccountUrn(), userUrn, updateUserRequest);

            if (updateUserResponse.isPresent()) {
                ResponseEntity responseEntity = ResponseEntity.noContent()
                    .build();
                response.setResult(responseEntity);
                eventSendingService.sendEvent(user, USER_UPDATED, updateUserResponse.get());
            } else {
                response.setResult(ResponseEntity.notFound()
                                       .build());
                eventSendingService.sendEvent(user, USER_NOT_FOUND, userRequest);
            }

        } catch (Exception e) {
            log.debug(e.getMessage(), e);
            response.setErrorResult(e);
        }
    }
}
