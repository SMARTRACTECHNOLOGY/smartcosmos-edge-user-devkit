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
import net.smartcosmos.extension.tenant.dto.CreateOrUpdateRoleRequest;
import net.smartcosmos.extension.tenant.dto.CreateOrUpdateRoleResponse;
import net.smartcosmos.extension.tenant.dto.GetRoleResponse;
import net.smartcosmos.extension.tenant.rest.dto.RestCreateOrUpdateRoleRequest;
import net.smartcosmos.security.user.SmartCosmosUser;

/**
 * Initially created by SMART COSMOS Team on July 01, 2016.
 */
@Slf4j
@Service
public class CreateRoleService extends AbstractTenantService {

    @Inject
    public CreateRoleService(
        TenantDao tenantDao, RoleDao roleDao, SmartCosmosEventTemplate smartCosmosEventTemplate, ConversionService
        conversionService) {
        super(tenantDao, roleDao, smartCosmosEventTemplate, conversionService);
    }

    public DeferredResult<ResponseEntity> create(RestCreateOrUpdateRoleRequest restCreateOrUpdateRoleRequest, SmartCosmosUser user) {
        // Async worker thread reduces timeouts and disconnects for long queries and processing.
        DeferredResult<ResponseEntity> response = new DeferredResult<>();
        createRoleWorker(response, user.getAccountUrn(), restCreateOrUpdateRoleRequest);

        return response;
    }

    @Async
    private void createRoleWorker(DeferredResult<ResponseEntity> response, String tenantUrn, RestCreateOrUpdateRoleRequest
        restCreateOrUpdateRoleRequest) {

        try {
            final CreateOrUpdateRoleRequest createRoleRequest = conversionService
                .convert(restCreateOrUpdateRoleRequest, CreateOrUpdateRoleRequest.class);

            Optional<CreateOrUpdateRoleResponse> newRole = roleDao.createRole(tenantUrn, createRoleRequest);

            if (newRole.isPresent()) {
                //sendEvent(null, DefaultEventTypes.ThingCreated, object.get());

                ResponseEntity responseEntity = ResponseEntity
                    .created(URI.create(newRole.get().getUrn()))
                    .body(newRole.get());
                response.setResult(responseEntity);
            } else {
                Optional<GetRoleResponse> alreadyThere = roleDao.findByTenantUrnAndName(tenantUrn, restCreateOrUpdateRoleRequest.getName());
                response.setResult(ResponseEntity.status(HttpStatus.CONFLICT).build());
                //sendEvent(null, DefaultEventTypes.ThingCreateFailedAlreadyExists, alreadyThere.get());
            }

        } catch (Exception e) {
            log.debug(e.getMessage(), e);
            response.setErrorResult(e);
        }
    }
}
