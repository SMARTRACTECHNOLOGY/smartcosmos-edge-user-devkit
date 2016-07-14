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

import net.smartcosmos.events.SmartCosmosEventTemplate;
import net.smartcosmos.ext.tenant.dao.RoleDao;
import net.smartcosmos.ext.tenant.dao.TenantDao;
import net.smartcosmos.ext.tenant.dto.CreateOrUpdateRoleRequest;
import net.smartcosmos.ext.tenant.dto.CreateOrUpdateRoleResponse;
import net.smartcosmos.ext.tenant.dto.GetRoleResponse;
import net.smartcosmos.ext.tenant.rest.dto.RestCreateOrUpdateRoleRequest;

/**
 * Initially created by SMART COSMOS Team on July 01, 2016.
 */
@Slf4j
@Service
public class UpdateRoleService extends AbstractTenantService{

    @Inject
    public UpdateRoleService(TenantDao tenantDao, RoleDao roleDao, SmartCosmosEventTemplate smartCosmosEventTemplate, ConversionService
        conversionService) {
        super(tenantDao, roleDao, smartCosmosEventTemplate, conversionService);
    }


    public DeferredResult<ResponseEntity> update(RestCreateOrUpdateRoleRequest restCreateOrUpdateRoleRequest) {
        // Async worker thread reduces timeouts and disconnects for long queries and processing.
        DeferredResult<ResponseEntity> response = new DeferredResult<>();
        updateRoleWorker(response, restCreateOrUpdateRoleRequest);

        return response;
    }

    // TODO: create different workers for "create" and "update", and provide the URN for "update" only...
    // FIXME: implement something for "whatever"...

    @Async
    private void updateRoleWorker(DeferredResult<ResponseEntity> response, RestCreateOrUpdateRoleRequest restCreateOrUpdateRoleRequest) {

        try {
            final CreateOrUpdateRoleRequest createRoleRequest = conversionService.convert(restCreateOrUpdateRoleRequest, CreateOrUpdateRoleRequest.class);

            Optional<CreateOrUpdateRoleResponse> newUser = roleDao.updateRole("whatever", "urn", createRoleRequest);

            if (newUser.isPresent())
            {
                //sendEvent(null, DefaultEventTypes.ThingCreated, object.get());

                ResponseEntity responseEntity = ResponseEntity
                    .created(URI.create(newUser.get().getUrn()))
                    .body(newUser.get());
                response.setResult(responseEntity);
            }
            else {
                Optional<GetRoleResponse> alreadyThere = roleDao.findByTenantUrnAndName("tenantUrnHere", restCreateOrUpdateRoleRequest.getName());
                response.setResult(ResponseEntity.status(HttpStatus.CONFLICT).build());
                //sendEvent(null, DefaultEventTypes.ThingCreateFailedAlreadyExists, alreadyThere.get());
            }

        } catch (Exception e) {
            log.debug(e.getMessage(), e);
            response.setErrorResult(e);
        }
    }


}
