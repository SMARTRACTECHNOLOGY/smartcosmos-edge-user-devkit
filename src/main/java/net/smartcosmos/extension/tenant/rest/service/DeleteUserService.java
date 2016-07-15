package net.smartcosmos.extension.tenant.rest.service;

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
import net.smartcosmos.extension.tenant.dao.RoleDao;
import net.smartcosmos.extension.tenant.dao.TenantDao;
import net.smartcosmos.extension.tenant.dto.GetOrDeleteUserResponse;

/**
 * Initially created by SMART COSMOS Team on July 01, 2016.
 */
@Slf4j
@Service
public class DeleteUserService extends AbstractTenantService{

    @Inject
    public DeleteUserService(TenantDao tenantDao, RoleDao roleDao, SmartCosmosEventTemplate smartCosmosEventTemplate, ConversionService
        conversionService) {
        super(tenantDao, roleDao, smartCosmosEventTemplate, conversionService);
    }


    public DeferredResult<ResponseEntity> delete(String urn) {
        // Async worker thread reduces timeouts and disconnects for long queries and processing.
        DeferredResult<ResponseEntity> response = new DeferredResult<>();
        updateUserWorker(response, urn);

        return response;
    }

    @Async
    private void updateUserWorker(DeferredResult<ResponseEntity> response, String urn) {

        try {
            Optional<GetOrDeleteUserResponse> deleteUserResponse = tenantDao.deleteUserByUrn(urn);

            if (deleteUserResponse.isPresent())
            {
                //sendEvent(null, DefaultEventTypes.ThingCreated, object.get());

                ResponseEntity responseEntity = ResponseEntity
                    .created(URI.create(deleteUserResponse.get().getUrn()))
                    .body(deleteUserResponse.get());
                response.setResult(responseEntity);
            }
            else {
                response.setResult(ResponseEntity.status(HttpStatus.NOT_FOUND).build());
                // sendEvent(createTenantRequest, DefaultEventTypes.ThingCreateFailedAlreadyExists, createTenantRequest);
            }

        } catch (Exception e) {
            log.debug(e.getMessage(), e);
            response.setErrorResult(e);
        }
    }
}
