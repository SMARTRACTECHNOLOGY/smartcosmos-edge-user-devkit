package net.smartcosmos.usermanagement.role.service;

import java.util.Optional;

import lombok.extern.slf4j.Slf4j;

import org.apache.commons.lang.exception.ExceptionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.convert.ConversionService;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.context.request.async.DeferredResult;

import net.smartcosmos.security.user.SmartCosmosUser;
import net.smartcosmos.usermanagement.event.EventSendingService;
import net.smartcosmos.usermanagement.role.dto.RoleRequest;
import net.smartcosmos.usermanagement.role.dto.RoleResponse;
import net.smartcosmos.usermanagement.role.persistence.RoleDao;

import static net.smartcosmos.usermanagement.event.RoleEventType.ROLE_NOT_FOUND;
import static net.smartcosmos.usermanagement.event.RoleEventType.ROLE_UPDATED;

/**
 * Initially created by SMART COSMOS Team on July 01, 2016.
 */
@Slf4j
@Service
public class UpdateRoleServiceDefault implements UpdateRoleService {

    private final RoleDao roleDao;
    private final EventSendingService eventSendingService;
    private final ConversionService conversionService;

    @Autowired
    public UpdateRoleServiceDefault(RoleDao roleDao, EventSendingService roleEventSendingService, ConversionService conversionService) {

        this.roleDao = roleDao;
        this.eventSendingService = roleEventSendingService;
        this.conversionService = conversionService;
    }

    @Override
    public void update(
        DeferredResult<ResponseEntity<?>> response,
        String roleUrn,
        RoleRequest updateRoleRequest,
        SmartCosmosUser user) {

        try {
            response.setResult(updateRoleWorker(user.getAccountUrn(), updateRoleRequest, user));
        } catch (Exception e) {
            Throwable rootException = ExceptionUtils.getRootCause(e);
            String rootCause = "";
            if (rootException != null) {
                rootCause = String.format(", rootCause: '%s'", rootException.toString());
            }
            String msg = String.format("Exception on update role. role: %s,  request: %s, user: %s, cause: '%s'%s.",
                                       roleUrn,
                                       updateRoleRequest,
                                       user.getUserUrn(),
                                       e.toString(),
                                       rootCause);
            log.warn(msg);
            log.debug(msg, e);
            response.setErrorResult(e);
        }
    }

    private ResponseEntity<?> updateRoleWorker(String roleUrn, RoleRequest restRequest, SmartCosmosUser user) {

        final RoleRequest updateRoleRequest = conversionService
            .convert(restRequest, RoleRequest.class);

        Optional<RoleResponse> updateRoleResponse = roleDao.updateRole(user.getAccountUrn(), roleUrn, updateRoleRequest);

        if (updateRoleResponse.isPresent()) {
            eventSendingService.sendEvent(user, ROLE_UPDATED, updateRoleResponse.get());
            return ResponseEntity.noContent()
                .build();
        } else {
            RoleResponse eventPayload = RoleResponse.builder()
                .urn(roleUrn)
                .tenantUrn(user.getAccountUrn())
                .build();
            eventSendingService.sendEvent(user, ROLE_NOT_FOUND, eventPayload);
            return ResponseEntity.notFound()
                .build();
        }
    }

}
