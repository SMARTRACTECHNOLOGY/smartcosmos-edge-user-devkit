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

import net.smartcosmos.events.DefaultEventTypes;
import net.smartcosmos.events.SmartCosmosEventTemplate;
import net.smartcosmos.ext.tenant.dao.TenantDao;
import net.smartcosmos.ext.tenant.dto.CreateTenantResponse;
import net.smartcosmos.ext.tenant.dto.CreateUserRequest;
import net.smartcosmos.ext.tenant.dto.CreateUserResponse;
import net.smartcosmos.ext.tenant.rest.dto.RestCreateUserRequest;

/**
 * Initially created by SMART COSMOS Team on July 01, 2016.
 */
@Slf4j
@Service
public class CreateUserService extends AbstractTenantService{

    @Inject
    public CreateUserService(TenantDao tenantDao, SmartCosmosEventTemplate smartCosmosEventTemplate, ConversionService conversionService) {
        super(tenantDao, smartCosmosEventTemplate, conversionService);
    }


    public DeferredResult<ResponseEntity> create(RestCreateUserRequest restCreateUserRequest) {
        // Async worker thread reduces timeouts and disconnects for long queries and processing.
        DeferredResult<ResponseEntity> response = new DeferredResult<>();
        createUserWorker(response, restCreateUserRequest);

        return response;
    }

    @Async
    private void createUserWorker(DeferredResult<ResponseEntity> response, RestCreateUserRequest restCreateUserRequest) {

        try {
            final CreateUserRequest createUserRequest = conversionService.convert(restCreateUserRequest, CreateUserRequest.class);

            Optional<CreateUserResponse> object = tenantDao.createUser(createUserRequest);;

            if (object.isPresent())
            {
                sendEvent(null, DefaultEventTypes.ThingCreated, object.get());

                ResponseEntity responseEntity = ResponseEntity
                    .created(URI.create(object.get().getUrn()))
                    .body(object.get());
                response.setResult(responseEntity);
            }
            else {
                Optional<CreateTenantResponse> alreadyThere = tenantDao.findTenantByUrn(object.get().getUrn());
                response.setResult(ResponseEntity.status(HttpStatus.CONFLICT).build());
                sendEvent(null, DefaultEventTypes.ThingCreateFailedAlreadyExists, alreadyThere.get());
            }

        } catch (Exception e) {
            log.debug(e.getMessage(), e);
            response.setErrorResult(e);
        }
    }


}
