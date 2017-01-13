package net.smartcosmos.usermanagement.tenant.service;

import java.util.Optional;

import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.convert.ConversionService;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.context.request.async.DeferredResult;

import net.smartcosmos.security.user.SmartCosmosUser;
import net.smartcosmos.usermanagement.event.EventSendingService;
import net.smartcosmos.usermanagement.tenant.dto.RestUpdateTenantRequest;
import net.smartcosmos.usermanagement.tenant.dto.TenantResponse;
import net.smartcosmos.usermanagement.tenant.dto.UpdateTenantRequest;
import net.smartcosmos.usermanagement.tenant.persistence.TenantDao;

import static net.smartcosmos.usermanagement.event.TenantEventType.TENANT_NOT_FOUND;
import static net.smartcosmos.usermanagement.event.TenantEventType.TENANT_UPDATED;

/**
 * Initially created by SMART COSMOS Team on July 01, 2016.
 */
@Slf4j
@Service
public class UpdateTenantService {

    private final TenantDao tenantDao;
    private final EventSendingService eventSendingService;
    private final ConversionService conversionService;

    @Autowired
    public UpdateTenantService(TenantDao tenantDao, EventSendingService tenantEventSendingService, ConversionService conversionService) {

        this.tenantDao = tenantDao;
        this.eventSendingService = tenantEventSendingService;
        this.conversionService = conversionService;
    }

    public DeferredResult<ResponseEntity> update(String tenantUrn, RestUpdateTenantRequest restUpdateTenantRequest, SmartCosmosUser user) {
        // Async worker thread reduces timeouts and disconnects for long queries and processing.
        DeferredResult<ResponseEntity> response = new DeferredResult<>();
        updateTenantWorker(response, user, tenantUrn, restUpdateTenantRequest);

        return response;
    }

    private void updateTenantWorker(
        DeferredResult<ResponseEntity> response,
        SmartCosmosUser user,
        String tenantUrn,
        RestUpdateTenantRequest restUpdateTenantRequest) {

        try {
            UpdateTenantRequest updateTenantRequest = conversionService.convert(restUpdateTenantRequest, UpdateTenantRequest.class);
            Optional<TenantResponse> tenantResponseOptional = tenantDao.updateTenant(tenantUrn, updateTenantRequest);

            if (tenantResponseOptional.isPresent()) {
                ResponseEntity responseEntity = ResponseEntity.noContent()
                    .build();
                response.setResult(responseEntity);
                eventSendingService.sendEvent(user, TENANT_UPDATED, tenantResponseOptional.get());
            } else {
                response.setResult(ResponseEntity.notFound()
                                       .build());
                eventSendingService.sendEvent(user, TENANT_NOT_FOUND, restUpdateTenantRequest);
            }

        } catch (Exception e) {
            log.debug(e.getMessage(), e);
            response.setErrorResult(e);
        }
    }
}
