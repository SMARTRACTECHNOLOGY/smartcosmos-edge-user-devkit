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
import net.smartcosmos.ext.tenant.dto.UpdateTenantRequest;
import net.smartcosmos.ext.tenant.dto.UpdateTenantResponse;
import net.smartcosmos.ext.tenant.rest.dto.RestUpdateTenantRequest;

/**
 * Initially created by SMART COSMOS Team on July 01, 2016.
 */
@Slf4j
@Service
public class UpdateTenantService extends AbstractTenantService{

    @Inject
    public UpdateTenantService(TenantDao tenantDao, RoleDao roleDao, SmartCosmosEventTemplate smartCosmosEventTemplate, ConversionService
        conversionService) {
        super(tenantDao, roleDao, smartCosmosEventTemplate, conversionService);
    }


    public DeferredResult<ResponseEntity> create(RestUpdateTenantRequest restUpdateTenantRequest) {
        // Async worker thread reduces timeouts and disconnects for long queries and processing.
        DeferredResult<ResponseEntity> response = new DeferredResult<>();
        createTenantWorker(response, restUpdateTenantRequest);

        return response;
    }

    @Async
    private void createTenantWorker(DeferredResult<ResponseEntity> response, RestUpdateTenantRequest restUpdateTenantRequest) {

        try {
            UpdateTenantRequest updateTenantRequest = conversionService.convert(restUpdateTenantRequest, UpdateTenantRequest.class);
            Optional<UpdateTenantResponse> updateTenantResponseOptional = tenantDao.updateTenant(updateTenantRequest);

            if (updateTenantResponseOptional.isPresent())
            {
                ResponseEntity responseEntity = ResponseEntity
                    .created(URI.create(updateTenantResponseOptional.get().getUrn()))
                    .body(updateTenantResponseOptional.get());
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
