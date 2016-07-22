package net.smartcosmos.extension.tenant.rest.service.tenant;

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
import net.smartcosmos.extension.tenant.dto.tenant.UpdateTenantRequest;
import net.smartcosmos.extension.tenant.dto.tenant.TenantResponse;
import net.smartcosmos.extension.tenant.rest.dto.tenant.RestUpdateTenantRequest;
import net.smartcosmos.extension.tenant.rest.service.AbstractTenantService;
import net.smartcosmos.security.user.SmartCosmosUser;

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

    public DeferredResult<ResponseEntity> create(RestUpdateTenantRequest restUpdateTenantRequest, SmartCosmosUser user) {
        // Async worker thread reduces timeouts and disconnects for long queries and processing.
        DeferredResult<ResponseEntity> response = new DeferredResult<>();
        createTenantWorker(response, user.getAccountUrn(), restUpdateTenantRequest);

        return response;
    }

    @Async
    private void createTenantWorker(DeferredResult<ResponseEntity> response, String tenantUrn, RestUpdateTenantRequest restUpdateTenantRequest) {

        try {
            UpdateTenantRequest updateTenantRequest = conversionService.convert(restUpdateTenantRequest, UpdateTenantRequest.class);
            Optional<TenantResponse> TenantResponseOptional = tenantDao.updateTenant(tenantUrn, updateTenantRequest);

            if (TenantResponseOptional.isPresent()) {
                ResponseEntity responseEntity = ResponseEntity
                    .created(URI.create(TenantResponseOptional.get().getUrn()))
                    .body(TenantResponseOptional.get());
                response.setResult(responseEntity);
            } else {
                response.setResult(ResponseEntity.status(HttpStatus.CONFLICT).build());
                // sendEvent(createTenantRequest, DefaultEventTypes.ThingCreateFailedAlreadyExists, createTenantRequest);
            }

        } catch (Exception e) {
            log.debug(e.getMessage(), e);
            response.setErrorResult(e);
        }
    }

}
