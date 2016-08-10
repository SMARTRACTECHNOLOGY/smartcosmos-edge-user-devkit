package net.smartcosmos.extension.tenant.rest.service.role;

import lombok.extern.slf4j.Slf4j;
import net.smartcosmos.events.DefaultEventTypes;
import net.smartcosmos.events.SmartCosmosEventTemplate;
import net.smartcosmos.extension.tenant.dao.RoleDao;
import net.smartcosmos.extension.tenant.dao.TenantDao;
import net.smartcosmos.extension.tenant.dto.role.CreateOrUpdateRoleRequest;
import net.smartcosmos.extension.tenant.dto.role.RoleResponse;
import net.smartcosmos.extension.tenant.rest.dto.role.RestCreateOrUpdateRoleRequest;
import net.smartcosmos.extension.tenant.rest.service.AbstractTenantService;
import net.smartcosmos.security.user.SmartCosmosUser;
import org.springframework.core.convert.ConversionService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.web.context.request.async.DeferredResult;

import javax.inject.Inject;
import java.net.URI;
import java.util.Optional;

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
        roleRequest, SmartCosmosUser user) {

        try {
            final CreateOrUpdateRoleRequest createRoleRequest = conversionService
                .convert(roleRequest, CreateOrUpdateRoleRequest.class);

            Optional<RoleResponse> newRole = roleDao.createRole(tenantUrn, createRoleRequest);

            if (newRole.isPresent()) {
                ResponseEntity responseEntity = buildCreatedResponseEntity(newRole.get());
                response.setResult(responseEntity);
                sendEvent(user, DefaultEventTypes.RoleCreated, newRole.get());
            } else {
                response.setResult(ResponseEntity.status(HttpStatus.CONFLICT).build());
                sendEvent(user, DefaultEventTypes.RoleCreateFailedAlreadyExists, roleRequest);
            }

        } catch (Exception e) {
            log.debug(e.getMessage(), e);
            response.setErrorResult(e);
        }
    }

    private ResponseEntity buildCreatedResponseEntity(RoleResponse response) {
        return ResponseEntity
            .created(URI.create(response.getUrn()))
            .body(response);
    }
}
