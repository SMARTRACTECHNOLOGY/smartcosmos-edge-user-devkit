package net.smartcosmos.extension.tenant.rest.service.user;

import java.util.Optional;

import javax.inject.Inject;

import org.springframework.core.convert.ConversionService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.web.context.request.async.DeferredResult;

import lombok.extern.slf4j.Slf4j;

import net.smartcosmos.events.DefaultEventTypes;
import net.smartcosmos.events.SmartCosmosEventTemplate;
import net.smartcosmos.extension.tenant.dao.RoleDao;
import net.smartcosmos.extension.tenant.dao.TenantDao;
import net.smartcosmos.extension.tenant.dto.user.UserResponse;
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
        deleteUserWorker(response, user, userUrn);

        return response;
    }

    @Async
    private void deleteUserWorker(DeferredResult<ResponseEntity> response, SmartCosmosUser user, String userUrn) {

        try {
            Optional<UserResponse> deleteUserResponse = tenantDao.deleteUserByUrn(user.getAccountUrn(), userUrn);

            if (deleteUserResponse.isPresent()) {
                response.setResult(ResponseEntity.noContent().build());
                sendEvent(user, DefaultEventTypes.UserDeleted, deleteUserResponse.get());
            } else {
                response.setResult(ResponseEntity.status(HttpStatus.NOT_FOUND).build());

                UserResponse eventPayload = UserResponse.builder()
                    .urn(userUrn)
                    .tenantUrn(user.getAccountUrn())
                    .build();
                sendEvent(user, DefaultEventTypes.UserNotFound, eventPayload);
            }

        } catch (Exception e) {
            log.debug(e.getMessage(), e);
            response.setErrorResult(e);
        }
    }
}
