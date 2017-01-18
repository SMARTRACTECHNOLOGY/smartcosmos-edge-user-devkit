package net.smartcosmos.usermanagement.user.service;

import java.net.URI;
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
import net.smartcosmos.usermanagement.user.dto.CreateUserRequest;
import net.smartcosmos.usermanagement.user.dto.CreateUserResponse;

import static net.smartcosmos.usermanagement.event.UserEventType.USER_CREATED;
import static net.smartcosmos.usermanagement.event.UserEventType.USER_CREATE_FAILED_ALREADY_EXISTS;

/**
 * The default implementation of the {@link CreateUserService}.
 */
@Slf4j
@Service
public class CreateUserServiceDefault implements CreateUserService {

    private final TenantDao tenantDao;
    private final EventSendingService eventSendingService;

    @Autowired
    public CreateUserServiceDefault(TenantDao tenantDao, EventSendingService userEventSendingService) {

        this.tenantDao = tenantDao;
        this.eventSendingService = userEventSendingService;
    }

    @Override
    public void create(DeferredResult<ResponseEntity<?>> response, CreateUserRequest createUserRequest, SmartCosmosUser user) {

        try {
            response.setResult(createUserWorker(user, createUserRequest));
        } catch (Exception e) {
            Throwable rootException = ExceptionUtils.getRootCause(e);
            String rootCause = "";
            if (rootException != null) {
                rootCause = String.format(", rootCause: '%s'", rootException.toString());
            }
            String msg = String.format("Exception on create user. request: %s, user: %s, cause: '%s', rootCause: '%s'.",
                                       createUserRequest,
                                       user,
                                       e.toString(),
                                       rootCause);
            log.warn(msg);
            log.debug(msg, e);
            response.setErrorResult(e);
        }
    }

    private ResponseEntity<?> createUserWorker(SmartCosmosUser user, CreateUserRequest createUserRequest) {

        Optional<CreateUserResponse> newUser = tenantDao.createUser(user.getAccountUrn(), createUserRequest);

        if (newUser.isPresent()) {
            ResponseEntity responseEntity = buildCreatedResponseEntity(newUser.get());
            eventSendingService.sendEvent(user, USER_CREATED, newUser.get());
            return responseEntity;
        } else {
            eventSendingService.sendEvent(user, USER_CREATE_FAILED_ALREADY_EXISTS, createUserRequest);
            return ResponseEntity.status(HttpStatus.CONFLICT)
                .build();
        }
    }

    private ResponseEntity buildCreatedResponseEntity(CreateUserResponse response) {

        return ResponseEntity
            .created(URI.create(response.getUrn()))
            .body(response);
    }

}
