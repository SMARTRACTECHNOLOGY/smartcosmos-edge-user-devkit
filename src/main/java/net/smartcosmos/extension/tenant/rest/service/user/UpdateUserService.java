package net.smartcosmos.extension.tenant.rest.service.user;

import java.util.Optional;
import javax.inject.Inject;

import lombok.extern.slf4j.Slf4j;

import org.springframework.core.convert.ConversionService;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.web.context.request.async.DeferredResult;

import net.smartcosmos.events.DefaultEventTypes;
import net.smartcosmos.events.SmartCosmosEventTemplate;
import net.smartcosmos.extension.tenant.dao.RoleDao;
import net.smartcosmos.extension.tenant.dao.TenantDao;
import net.smartcosmos.extension.tenant.dto.user.CreateOrUpdateUserResponse;
import net.smartcosmos.extension.tenant.dto.user.UpdateUserRequest;
import net.smartcosmos.extension.tenant.rest.dto.user.RestCreateOrUpdateUserRequest;
import net.smartcosmos.extension.tenant.rest.service.AbstractTenantService;
import net.smartcosmos.security.user.SmartCosmosUser;

/**
 * Initially created by SMART COSMOS Team on July 01, 2016.
 */
@Slf4j
@Service
public class UpdateUserService extends AbstractTenantService {

    @Inject
    public UpdateUserService(
        TenantDao tenantDao, RoleDao roleDao, SmartCosmosEventTemplate smartCosmosEventTemplate, ConversionService
        conversionService) {
        super(tenantDao, roleDao, smartCosmosEventTemplate, conversionService);
    }

    public DeferredResult<ResponseEntity> create(RestCreateOrUpdateUserRequest userRequest, SmartCosmosUser user) {
        // Async worker thread reduces timeouts and disconnects for long queries and processing.
        DeferredResult<ResponseEntity> response = new DeferredResult<>();
        updateUserWorker(response, user, userRequest);

        return response;
    }

    @Async
    private void updateUserWorker(DeferredResult<ResponseEntity> response, SmartCosmosUser user, RestCreateOrUpdateUserRequest userRequest) {

        try {
            UpdateUserRequest updateUserRequest = conversionService.convert(userRequest, UpdateUserRequest.class);
            Optional<CreateOrUpdateUserResponse> updateUserResponse = tenantDao.updateUser(user.getAccountUrn(), user.getUserUrn(), updateUserRequest);

            if (updateUserResponse.isPresent()) {
                ResponseEntity responseEntity = ResponseEntity.noContent().build();
                response.setResult(responseEntity);
                sendEvent(user, DefaultEventTypes.UserUpdated, updateUserResponse.get());
            } else {
                response.setResult(ResponseEntity.notFound().build());
                sendEvent(user, DefaultEventTypes.UserNotFound, userRequest);
            }

        } catch (Exception e) {
            log.debug(e.getMessage(), e);
            response.setErrorResult(e);
        }
    }
}