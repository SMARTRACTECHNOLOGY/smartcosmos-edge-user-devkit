package net.smartcosmos.extension.tenant.rest.service.tenant;

import lombok.extern.slf4j.Slf4j;
import net.smartcosmos.events.DefaultEventTypes;
import net.smartcosmos.events.SmartCosmosEventTemplate;
import net.smartcosmos.extension.tenant.dao.RoleDao;
import net.smartcosmos.extension.tenant.dao.TenantDao;
import net.smartcosmos.extension.tenant.dto.tenant.TenantResponse;
import net.smartcosmos.extension.tenant.dto.tenant.UpdateTenantRequest;
import net.smartcosmos.extension.tenant.rest.dto.tenant.RestUpdateTenantRequest;
import net.smartcosmos.extension.tenant.rest.service.AbstractTenantService;
import net.smartcosmos.security.user.SmartCosmosUser;
import org.springframework.core.convert.ConversionService;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.web.context.request.async.DeferredResult;

import javax.inject.Inject;
import java.util.Optional;

/**
 * Initially created by SMART COSMOS Team on July 01, 2016.
 */
@Slf4j
@Service
public class UpdateTenantService extends AbstractTenantService {

    @Inject
    public UpdateTenantService(
        TenantDao tenantDao, RoleDao roleDao, SmartCosmosEventTemplate smartCosmosEventTemplate, ConversionService
        conversionService) {
        super(tenantDao, roleDao, smartCosmosEventTemplate, conversionService);
    }

    public DeferredResult<ResponseEntity> update(String tenantUrn, RestUpdateTenantRequest restUpdateTenantRequest, SmartCosmosUser user) {
        // Async worker thread reduces timeouts and disconnects for long queries and processing.
        DeferredResult<ResponseEntity> response = new DeferredResult<>();
        updateTenantWorker(response, user, tenantUrn, restUpdateTenantRequest);

        return response;
    }

    @Async
    private void updateTenantWorker(DeferredResult<ResponseEntity> response, SmartCosmosUser user, String tenantUrn, RestUpdateTenantRequest restUpdateTenantRequest) {

        try {
            UpdateTenantRequest updateTenantRequest = conversionService.convert(restUpdateTenantRequest, UpdateTenantRequest.class);
            Optional<TenantResponse> tenantResponseOptional = tenantDao.updateTenant(tenantUrn, updateTenantRequest);

            if (tenantResponseOptional.isPresent()) {
                ResponseEntity responseEntity = ResponseEntity.noContent().build();
                response.setResult(responseEntity);
                sendEvent(user, DefaultEventTypes.TenantUpdated, tenantResponseOptional.get());
            } else {
                response.setResult(ResponseEntity.notFound().build());
                sendEvent(user, DefaultEventTypes.TenantNotFound, restUpdateTenantRequest);
            }

        } catch (Exception e) {
            log.debug(e.getMessage(), e);
            response.setErrorResult(e);
        }
    }
}
