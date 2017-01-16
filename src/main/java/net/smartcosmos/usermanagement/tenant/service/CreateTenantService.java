package net.smartcosmos.usermanagement.tenant.service;

import java.net.URI;
import java.util.Optional;

import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.convert.ConversionService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.context.request.async.DeferredResult;

import net.smartcosmos.security.user.SmartCosmosUser;
import net.smartcosmos.usermanagement.event.EventSendingService;
import net.smartcosmos.usermanagement.tenant.dto.CreateTenantRequest;
import net.smartcosmos.usermanagement.tenant.dto.CreateTenantResponse;
import net.smartcosmos.usermanagement.tenant.dto.RestCreateTenantRequest;
import net.smartcosmos.usermanagement.tenant.dto.RestCreateTenantResponse;
import net.smartcosmos.usermanagement.tenant.persistence.TenantDao;

import static net.smartcosmos.usermanagement.event.TenantEventType.TENANT_CREATED;
import static net.smartcosmos.usermanagement.event.TenantEventType.TENANT_CREATE_FAILED_ALREADY_EXISTS;

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