package net.smartcosmos.extension.tenant.rest.service.user;

import java.net.URI;
import java.util.Optional;

import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.convert.ConversionService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.web.context.request.async.DeferredResult;

import net.smartcosmos.extension.tenant.dao.TenantDao;
import net.smartcosmos.extension.tenant.dto.user.CreateOrUpdateUserRequest;
import net.smartcosmos.extension.tenant.dto.user.CreateUserResponse;
import net.smartcosmos.extension.tenant.rest.dto.user.RestCreateOrUpdateUserRequest;
import net.smartcosmos.extension.tenant.rest.service.EventSendingService;
import net.smartcosmos.security.user.SmartCosmosUser;

import static net.smartcosmos.extension.tenant.rest.utility.UserEventType.USER_CREATED;
import static net.smartcosmos.extension.tenant.rest.utility.UserEventType.USER_CREATE_FAILED_ALREADY_EXISTS;

/**
 * Initially created by SMART COSMOS Team on July 01, 2016.
 */
@Slf4j
@Service
public class CreateUserService {

    private final TenantDao tenantDao;
    private final EventSendingService eventSendingService;
    private final ConversionService conversionService;

    @Autowired
    public CreateUserService(TenantDao tenantDao, EventSendingService userEventSendingService, ConversionService conversionService) {

        this.tenantDao = tenantDao;
        this.eventSendingService = userEventSendingService;
        this.conversionService = conversionService;
    }

    public DeferredResult<ResponseEntity> create(RestCreateOrUpdateUserRequest restCreateUserRequest, SmartCosmosUser user) {
        // Async worker thread reduces timeouts and disconnects for long queries and processing.
        DeferredResult<ResponseEntity> response = new DeferredResult<>();
        createUserWorker(response, user, restCreateUserRequest);

        return response;
    }

    @Async
    private void createUserWorker(
        DeferredResult<ResponseEntity> response, SmartCosmosUser user, RestCreateOrUpdateUserRequest
        restCreateUserRequest) {

        try {
            final CreateOrUpdateUserRequest createUserRequest = conversionService.convert(restCreateUserRequest, CreateOrUpdateUserRequest.class);

            Optional<CreateUserResponse> newUser = tenantDao.createUser(user.getAccountUrn(), createUserRequest);

            if (newUser.isPresent()) {
                ResponseEntity responseEntity = buildCreatedResponseEntity(newUser.get());
                response.setResult(responseEntity);
                eventSendingService.sendEvent(user, USER_CREATED, newUser.get());
            } else {
                response.setResult(ResponseEntity.status(HttpStatus.CONFLICT)
                                       .build());
                eventSendingService.sendEvent(user, USER_CREATE_FAILED_ALREADY_EXISTS, createUserRequest);
            }

        } catch (Exception e) {
            log.debug(e.getMessage(), e);
            response.setErrorResult(e);
        }
    }

    private ResponseEntity buildCreatedResponseEntity(CreateUserResponse response) {

        return ResponseEntity
            .created(URI.create(response.getUrn()))
            .body(response);
    }

}
