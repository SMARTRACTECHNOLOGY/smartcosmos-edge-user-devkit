package net.smartcosmos.extension.tenant.rest.service.tenant;

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
import net.smartcosmos.extension.tenant.dto.tenant.CreateTenantRequest;
import net.smartcosmos.extension.tenant.dto.tenant.CreateTenantResponse;
import net.smartcosmos.extension.tenant.rest.dto.tenant.RestCreateTenantRequest;
import net.smartcosmos.extension.tenant.rest.dto.tenant.RestCreateTenantResponse;
import net.smartcosmos.extension.tenant.rest.service.EventSendingService;
import net.smartcosmos.security.user.SmartCosmosUser;

import static net.smartcosmos.extension.tenant.rest.utility.TenantEventType.TENANT_CREATED;
import static net.smartcosmos.extension.tenant.rest.utility.TenantEventType.TENANT_CREATE_FAILED_ALREADY_EXISTS;

/**
 * Initially created by SMART COSMOS Team on July 01, 2016.
 */
@Slf4j
@Service
public class CreateTenantService {

    private final TenantDao tenantDao;
    private final EventSendingService eventSendingService;
    private final ConversionService conversionService;

    @Autowired
    public CreateTenantService(TenantDao tenantDao, EventSendingService tenantEventSendingService, ConversionService conversionService) {

        this.tenantDao = tenantDao;
        this.eventSendingService = tenantEventSendingService;
        this.conversionService = conversionService;
    }

    public DeferredResult<ResponseEntity> create(RestCreateTenantRequest restCreateTenantRequest, SmartCosmosUser user) {
        // Async worker thread reduces timeouts and disconnects for long queries and processing.
        DeferredResult<ResponseEntity> response = new DeferredResult<>();
        createTenantWorker(response, restCreateTenantRequest, user);

        return response;
    }

    @Async
    private void createTenantWorker(DeferredResult<ResponseEntity> response, RestCreateTenantRequest restCreateTenantRequest, SmartCosmosUser user) {

        try {
            CreateTenantRequest createTenantRequest = conversionService.convert(restCreateTenantRequest, CreateTenantRequest.class);
            Optional<CreateTenantResponse> createTenantResponse = tenantDao.createTenant(createTenantRequest);

            if (createTenantResponse.isPresent()) {
                RestCreateTenantResponse restResponse = conversionService.convert(createTenantResponse.get(), RestCreateTenantResponse.class);
                ResponseEntity responseEntity = buildCreatedResponseEntity(restResponse);
                response.setResult(responseEntity);
                eventSendingService.sendEvent(user, TENANT_CREATED, createTenantResponse.get());
            } else {
                response.setResult(ResponseEntity.status(HttpStatus.CONFLICT)
                                       .build());
                eventSendingService.sendEvent(user, TENANT_CREATE_FAILED_ALREADY_EXISTS, createTenantRequest);
            }

        } catch (Exception e) {
            log.debug(e.getMessage(), e);
            response.setErrorResult(e);
        }
    }

    private ResponseEntity buildCreatedResponseEntity(RestCreateTenantResponse response) {

        return ResponseEntity
            .created(URI.create(response.getUrn()))
            .body(response);
    }

}
