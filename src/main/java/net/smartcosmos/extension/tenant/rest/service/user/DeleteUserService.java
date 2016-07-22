package net.smartcosmos.extension.tenant.rest.service.user;

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
import net.smartcosmos.extension.tenant.dto.user.GetOrDeleteUserResponse;
import net.smartcosmos.extension.tenant.rest.service.AbstractTenantService;
import net.smartcosmos.security.user.SmartCosmosUser;

/**
 * Initially created by SMART COSMOS Team on July 01, 2016.
 */
@Slf4j
@Service
public class DeleteUserService extends AbstractTenantService {

    @Inject
    public DeleteUserService(
        TenantDao tenantDao, RoleDao roleDao, SmartCosmosEventTemplate smartCosmosEventTemplate, ConversionService
        conversionService) {
        super(tenantDao, roleDao, smartCosmosEventTemplate, conversionService);
    }

    public DeferredResult<ResponseEntity> delete(String userUrn, SmartCosmosUser user) {
        // Async worker thread reduces timeouts and disconnects for long queries and processing.
        DeferredResult<ResponseEntity> response = new DeferredResult<>();
        updateUserWorker(response, user.getAccountUrn(), userUrn);

        return response;
    }

    @Async
    private void updateUserWorker(DeferredResult<ResponseEntity> response, String tenantUrn, String userUrn) {

        try {
            Optional<GetOrDeleteUserResponse> deleteUserResponse = tenantDao.deleteUserByUrn(tenantUrn, userUrn);

            if (deleteUserResponse.isPresent()) {
                //sendEvent(null, DefaultEventTypes.ThingCreated, object.get());

                response.setResult(ResponseEntity.noContent().build());
            } else {
                response.setResult(ResponseEntity.status(HttpStatus.NOT_FOUND).build());
                // sendEvent(createTenantRequest, DefaultEventTypes.ThingCreateFailedAlreadyExists, createTenantRequest);
            }

        } catch (Exception e) {
            log.debug(e.getMessage(), e);
            response.setErrorResult(e);
        }
    }
}
