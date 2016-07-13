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
import net.smartcosmos.ext.tenant.dto.CreateUserRequest;
import net.smartcosmos.ext.tenant.dto.CreateOrUpdateUserResponse;
import net.smartcosmos.ext.tenant.dto.GetUserResponse;
import net.smartcosmos.ext.tenant.rest.dto.RestCreateUserRequest;

/**
 * Initially created by SMART COSMOS Team on July 01, 2016.
 */
@Slf4j
@Service
public class CreateUserService extends AbstractTenantService{

    @Inject
    public CreateUserService(TenantDao tenantDao, RoleDao roleDao, SmartCosmosEventTemplate smartCosmosEventTemplate, ConversionService
        conversionService) {
        super(tenantDao, roleDao, smartCosmosEventTemplate, conversionService);
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

            Optional<CreateOrUpdateUserResponse> newUser = tenantDao.createUser(createUserRequest);;

            if (newUser.isPresent())
            {
                //sendEvent(null, DefaultEventTypes.ThingCreated, object.get());

                ResponseEntity responseEntity = ResponseEntity
                    .created(URI.create(newUser.get().getUrn()))
                    .body(newUser.get());
                response.setResult(responseEntity);
            }
            else {
                Optional<GetUserResponse> alreadyThere = tenantDao.findUserByUrn(newUser.get().getUrn());
                response.setResult(ResponseEntity.status(HttpStatus.CONFLICT).build());
                //sendEvent(null, DefaultEventTypes.ThingCreateFailedAlreadyExists, alreadyThere.get());
            }

        } catch (Exception e) {
            log.debug(e.getMessage(), e);
            response.setErrorResult(e);
        }
    }


}
