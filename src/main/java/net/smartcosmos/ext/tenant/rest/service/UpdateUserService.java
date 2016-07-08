package net.smartcosmos.ext.tenant.rest.service;

import java.net.URI;
import java.util.Optional;
import javax.inject.Inject;

import lombok.extern.slf4j.Slf4j;

import org.springframework.core.convert.ConversionService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.web.context.request.async.DeferredResult;

import net.smartcosmos.events.SmartCosmosEventTemplate;
import net.smartcosmos.ext.tenant.dao.RoleDao;
import net.smartcosmos.ext.tenant.dao.TenantDao;
import net.smartcosmos.ext.tenant.dto.CreateOrUpdateUserResponse;
import net.smartcosmos.ext.tenant.dto.UpdateUserRequest;
import net.smartcosmos.ext.tenant.rest.dto.RestUpdateUserRequest;

/**
 * Initially created by SMART COSMOS Team on July 01, 2016.
 */
@Slf4j
@Service
public class UpdateUserService extends AbstractTenantService{

    @Inject
    public UpdateUserService(TenantDao tenantDao, RoleDao roleDao, SmartCosmosEventTemplate smartCosmosEventTemplate, ConversionService
        conversionService) {
        super(tenantDao, roleDao, smartCosmosEventTemplate, conversionService);
    }


    public DeferredResult<ResponseEntity> create(RestUpdateUserRequest restUpdateUserRequest) {
        // Async worker thread reduces timeouts and disconnects for long queries and processing.
        DeferredResult<ResponseEntity> response = new DeferredResult<>();
        updateUserWorker(response, restUpdateUserRequest);

        return response;
    }

    @Async
    private void updateUserWorker(DeferredResult<ResponseEntity> response, RestUpdateUserRequest restUpdateUserRequest) {

        try {
            UpdateUserRequest updateUserRequest = conversionService.convert(restUpdateUserRequest, UpdateUserRequest.class);
            Optional<CreateOrUpdateUserResponse> updateUserResponse = tenantDao.updateUser(updateUserRequest);

            if (updateUserResponse.isPresent())
            {
                //sendEvent(null, DefaultEventTypes.ThingCreated, object.get());

                ResponseEntity responseEntity = ResponseEntity
                    .created(URI.create(updateUserResponse.get().getUrn()))
                    .body(updateUserResponse.get());
                response.setResult(responseEntity);
            }
            else {
                response.setResult(ResponseEntity.status(HttpStatus.CONFLICT).build());
                // sendEvent(createTenantRequest, DefaultEventTypes.ThingCreateFailedAlreadyExists, createTenantRequest);
            }

        } catch (Exception e) {
            log.debug(e.getMessage(), e);
            response.setErrorResult(e);
        }
    }
}
