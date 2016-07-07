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
import net.smartcosmos.ext.tenant.dto.CreateTenantRequest;
import net.smartcosmos.ext.tenant.dto.CreateTenantResponse;
import net.smartcosmos.ext.tenant.dto.GetTenantResponse;
import net.smartcosmos.ext.tenant.rest.dto.RestCreateTenantRequest;

/**
 * Initially created by SMART COSMOS Team on July 01, 2016.
 */
@Slf4j
@Service
public class CreateTenantService extends AbstractTenantService{

    @Inject
    public CreateTenantService(TenantDao tenantDao, SmartCosmosEventTemplate smartCosmosEventTemplate, ConversionService conversionService) {
        super(tenantDao, smartCosmosEventTemplate, conversionService);
    }


    public DeferredResult<ResponseEntity> create(RestCreateTenantRequest restCreateTenantRequest) {
        // Async worker thread reduces timeouts and disconnects for long queries and processing.
        DeferredResult<ResponseEntity> response = new DeferredResult<>();
        createTenantWorker(response, restCreateTenantRequest);

        return response;
    }

    @Async
    private void createTenantWorker(DeferredResult<ResponseEntity> response, RestCreateTenantRequest restCreateTenantRequest) {

        try {
            CreateTenantRequest createTenantRequest = conversionService.convert(restCreateTenantRequest, CreateTenantRequest.class);
            Optional<CreateTenantResponse> createTenantResponse = tenantDao.createTenant(createTenantRequest);

            if (createTenantResponse.isPresent())
            {
                //sendEvent(null, DefaultEventTypes.ThingCreated, object.get());

                ResponseEntity responseEntity = ResponseEntity
                    .created(URI.create(createTenantResponse.get().getUrn()))
                    .body(createTenantResponse.get());
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
