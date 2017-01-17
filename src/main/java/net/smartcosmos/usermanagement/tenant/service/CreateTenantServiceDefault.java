package net.smartcosmos.usermanagement.tenant.service;

import java.net.URI;
import java.util.Optional;

import lombok.extern.slf4j.Slf4j;

import org.apache.commons.lang.exception.ExceptionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.context.request.async.DeferredResult;

import net.smartcosmos.security.user.SmartCosmosUser;
import net.smartcosmos.usermanagement.event.EventSendingService;
import net.smartcosmos.usermanagement.tenant.dto.CreateTenantRequest;
import net.smartcosmos.usermanagement.tenant.dto.CreateTenantResponse;
import net.smartcosmos.usermanagement.tenant.persistence.TenantDao;

/**
 * The default implementation of of the {@link CreateTenantService}.
 */
@Slf4j
@Service
public class CreateTenantServiceDefault implements CreateTenantService {

    private final TenantDao tenantDao;
    private final EventSendingService eventSendingService;

    @Autowired
    public CreateTenantServiceDefault(TenantDao tenantDao, EventSendingService tenantEventSendingService) {

        this.tenantDao = tenantDao;
        this.eventSendingService = tenantEventSendingService;
    }

    @Override
    public void create(DeferredResult<ResponseEntity<?>> response, CreateTenantRequest createTenantRequest, SmartCosmosUser user) {

        try {
            response.setResult(createTenantWorker(createTenantRequest, user));
        } catch (Exception e) {
            Throwable rootException = ExceptionUtils.getRootCause(e);
            String rootCause = "";
            if (rootException != null) {
                rootCause = String.format(", rootCause: '%s'", rootException.toString());
            }
            String msg = String.format("Exception on create tenant. request: %s, user: %s, cause: '%s'%s.",
                                       createTenantRequest,
                                       user,
                                       e.toString(),
                                       rootCause);
            log.warn(msg);
            log.debug(msg, e);
            response.setErrorResult(e);
        }
    }

    private ResponseEntity<?> createTenantWorker(CreateTenantRequest createTenantRequest, SmartCosmosUser user) {

        Optional<CreateTenantResponse> createTenantResponse = tenantDao.createTenant(createTenantRequest);

        /**
         * TODO: Use client authentication instead of user's access token from the request context, because there isn't any for tenant creation
         * requests (OBJECTS-1213)
         * author: asiegel
         * date: 17 Jan 2017
         */

        if (createTenantResponse.isPresent()) {
            //            eventSendingService.sendEvent(user, TENANT_CREATED, createTenantResponse.get());
            return buildCreatedResponseEntity(createTenantResponse.get());
        } else {
            //            eventSendingService.sendEvent(user, TENANT_CREATE_FAILED_ALREADY_EXISTS, createTenantRequest);
            return ResponseEntity.status(HttpStatus.CONFLICT)
                .build();
        }
    }

    private ResponseEntity buildCreatedResponseEntity(CreateTenantResponse response) {

        return ResponseEntity
            .created(URI.create(response.getUrn()))
            .body(response);
    }

}
