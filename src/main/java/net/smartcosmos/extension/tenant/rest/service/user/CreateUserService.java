package net.smartcosmos.extension.tenant.rest.service.user;

import java.net.URI;
import java.util.Optional;

import javax.inject.Inject;

import org.springframework.core.convert.ConversionService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.web.context.request.async.DeferredResult;

import lombok.extern.slf4j.Slf4j;

import net.smartcosmos.events.DefaultEventTypes;
import net.smartcosmos.events.SmartCosmosEventTemplate;
import net.smartcosmos.extension.tenant.dao.RoleDao;
import net.smartcosmos.extension.tenant.dao.TenantDao;
import net.smartcosmos.extension.tenant.dto.user.CreateOrUpdateUserResponse;
import net.smartcosmos.extension.tenant.dto.user.CreateUserRequest;
import net.smartcosmos.extension.tenant.rest.dto.user.RestCreateOrUpdateUserRequest;
import net.smartcosmos.extension.tenant.rest.service.AbstractTenantService;
import net.smartcosmos.security.user.SmartCosmosUser;

/**
 * Initially created by SMART COSMOS Team on July 01, 2016.
 */
@Slf4j
@Service
public class CreateUserService extends AbstractTenantService {

    @Inject
    public CreateUserService(
        TenantDao tenantDao, RoleDao roleDao, SmartCosmosEventTemplate smartCosmosEventTemplate, ConversionService
        conversionService) {
        super(tenantDao, roleDao, smartCosmosEventTemplate, conversionService);
    }

    public DeferredResult<ResponseEntity> create(RestCreateOrUpdateUserRequest restCreateUserRequest, SmartCosmosUser user) {
        // Async worker thread reduces timeouts and disconnects for long queries and processing.
        DeferredResult<ResponseEntity> response = new DeferredResult<>();
        createUserWorker(response, user, restCreateUserRequest);

        return response;
    }

    @Async
    private void createUserWorker(DeferredResult<ResponseEntity> response, SmartCosmosUser user, RestCreateOrUpdateUserRequest
        restCreateUserRequest) {

        try {
            final CreateUserRequest createUserRequest = conversionService.convert(restCreateUserRequest, CreateUserRequest.class);

            Optional<CreateOrUpdateUserResponse> newUser = tenantDao.createUser(user.getAccountUrn(), createUserRequest);

            if (newUser.isPresent()) {
                ResponseEntity responseEntity = buildCreatedResponseEntity(newUser.get());
                response.setResult(responseEntity);
                sendEvent(user, DefaultEventTypes.UserCreated, newUser.get());
            } else {
                response.setResult(ResponseEntity.status(HttpStatus.CONFLICT).build());
                sendEvent(user, DefaultEventTypes.UserCreateFailedAlreadyExists, createUserRequest);
            }

        } catch (Exception e) {
            log.debug(e.getMessage(), e);
            response.setErrorResult(e);
        }
    }

    private ResponseEntity buildCreatedResponseEntity(CreateOrUpdateUserResponse response) {
        return ResponseEntity
            .created(URI.create(response.getUrn()))
            .body(response);
    }

}
