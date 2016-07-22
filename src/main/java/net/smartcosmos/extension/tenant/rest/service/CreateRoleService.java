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

import net.smartcosmos.events.DefaultEventTypes;
import net.smartcosmos.events.SmartCosmosEventTemplate;
import net.smartcosmos.extension.tenant.dao.RoleDao;
import net.smartcosmos.extension.tenant.dao.TenantDao;
import net.smartcosmos.extension.tenant.dto.CreateOrUpdateRoleRequest;
import net.smartcosmos.extension.tenant.dto.RoleResponse;
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
        createRoleWorker(response, user.getAccountUrn(), restCreateOrUpdateRoleRequest, user);

        return response;
    }

    @Async
    private void createRoleWorker(DeferredResult<ResponseEntity> response, String tenantUrn, RestCreateOrUpdateRoleRequest
        restCreateOrUpdateRoleRequest, SmartCosmosUser user) {

        try {
            final CreateOrUpdateRoleRequest createRoleRequest = conversionService
                .convert(restCreateOrUpdateRoleRequest, CreateOrUpdateRoleRequest.class);

            Optional<RoleResponse> newRole = roleDao.createRole(tenantUrn, createRoleRequest);

            if (newRole.isPresent()) {
                ResponseEntity responseEntity = buildCreatedResponseEntity(newRole.get());
                response.setResult(responseEntity);
                sendEvent(user, DefaultEventTypes.RoleCreated, newRole.get());
            } else {
                response.setResult(ResponseEntity.status(HttpStatus.CONFLICT).build());

                RoleResponse eventPayload = RoleResponse.builder()
                    .name(createRoleRequest.getName())
                    .active(createRoleRequest.getActive())
                    .authorities(createRoleRequest.getAuthorities())
                    .build();
                sendEvent(user, DefaultEventTypes.RoleCreateFailedAlreadyExists, eventPayload);
            }

        } catch (Exception e) {
            log.debug(e.getMessage(), e);
            response.setErrorResult(e);
        }
    }

    private ResponseEntity buildCreatedResponseEntity(RoleResponse newRole) {
        return ResponseEntity
            .created(URI.create(newRole.getUrn()))
            .body(newRole);
    }
}
