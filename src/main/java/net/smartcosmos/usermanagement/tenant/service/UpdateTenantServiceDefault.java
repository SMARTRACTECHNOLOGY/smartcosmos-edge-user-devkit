package net.smartcosmos.usermanagement.tenant.service;

import java.util.Optional;

import lombok.extern.slf4j.Slf4j;

import org.apache.commons.lang.exception.ExceptionUtils;
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
public class UpdateTenantServiceDefault implements UpdateTenantService {

    private final TenantDao tenantDao;
    private final EventSendingService eventSendingService;
    private final ConversionService conversionService;

    @Autowired
    public UpdateTenantServiceDefault(TenantDao tenantDao, EventSendingService tenantEventSendingService, ConversionService conversionService) {

        this.tenantDao = tenantDao;
        this.eventSendingService = tenantEventSendingService;
        this.conversionService = conversionService;
    }

    @Override
    public void update(
        DeferredResult<ResponseEntity<?>> response,
        String tenantUrn,
        RestUpdateTenantRequest updateTenantRequest,
        SmartCosmosUser user) {

        try {
            response.setResult(updateTenantWorker(tenantUrn, updateTenantRequest, user));
        } catch (Exception e) {
            Throwable rootException = ExceptionUtils.getRootCause(e);
            String rootCause = "";
            if (rootException != null) {
                rootCause = String.format(", rootCause: '%s'", rootException.toString());
            }
            String msg = String.format("Exception on update tenant. request: %s, user: %s, cause: '%s'%s.",
                                       updateTenantRequest,
                                       user.getAccountUrn(),
                                       e.toString(),
                                       rootCause);
            log.warn(msg);
            log.debug(msg, e);
            response.setErrorResult(e);
        }
    }

    private ResponseEntity<?> updateTenantWorker(String tenantUrn, RestUpdateTenantRequest restUpdateTenantRequest, SmartCosmosUser user) {

        UpdateTenantRequest updateTenantRequest = conversionService.convert(restUpdateTenantRequest, UpdateTenantRequest.class);
        Optional<TenantResponse> tenantResponseOptional = tenantDao.updateTenant(tenantUrn, updateTenantRequest);

        if (tenantResponseOptional.isPresent()) {
            eventSendingService.sendEvent(user, TENANT_UPDATED, tenantResponseOptional.get());
            return ResponseEntity.noContent()
                .build();
        } else {
            eventSendingService.sendEvent(user, TENANT_NOT_FOUND, restUpdateTenantRequest);
            return ResponseEntity.notFound()
                .build();
        }
    }
}
