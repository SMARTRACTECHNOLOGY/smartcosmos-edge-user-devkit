package net.smartcosmos.usermanagement.role.service;

import java.util.List;

import lombok.extern.slf4j.Slf4j;

import org.apache.commons.lang.exception.ExceptionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.context.request.async.DeferredResult;

import net.smartcosmos.security.user.SmartCosmosUser;
import net.smartcosmos.usermanagement.event.EventSendingService;
import net.smartcosmos.usermanagement.role.dto.RoleResponse;
import net.smartcosmos.usermanagement.role.persistence.RoleDao;

import static net.smartcosmos.usermanagement.event.RoleEventType.ROLE_DELETED;
import static net.smartcosmos.usermanagement.event.RoleEventType.ROLE_NOT_FOUND;

/**
 * The default implementation of of the {@link DeleteRoleService}.
 */
@Slf4j
@Service
public class DeleteRoleServiceDefault implements DeleteRoleService {

    private final RoleDao roleDao;
    private final EventSendingService eventSendingService;

    @Autowired
    public DeleteRoleServiceDefault(RoleDao roleDao, EventSendingService roleEventSendingService) {

        this.roleDao = roleDao;
        this.eventSendingService = roleEventSendingService;
    }

    @Override
    public void delete(DeferredResult<ResponseEntity<?>> response, String roleUrn, SmartCosmosUser user) {

        try {
            response.setResult(deleteRoleWorker(roleUrn, user));
        } catch (Exception e) {
            Throwable rootException = ExceptionUtils.getRootCause(e);
            String rootCause = "";
            if (rootException != null) {
                rootCause = String.format(", rootCause: '%s'", rootException.toString());
            }
            String msg = String.format("Exception on delete role. role URN: %s, user: %s, cause: '%s'%s.",
                                       roleUrn,
                                       user.getUserUrn(),
                                       e.toString(),
                                       rootCause);
            log.warn(msg);
            log.debug(msg, e);
            response.setErrorResult(e);
        }
    }

    private ResponseEntity<?> deleteRoleWorker(String roleUrn, SmartCosmosUser user) {

        List<RoleResponse> deleteRoleResponse = roleDao.delete(user.getAccountUrn(), roleUrn);

        if (!deleteRoleResponse.isEmpty()) {
            eventSendingService.sendEvent(user, ROLE_DELETED, deleteRoleResponse.get(0));
            return ResponseEntity.noContent()
                .build();
        } else {
            RoleResponse eventPayload = RoleResponse.builder()
                .urn(roleUrn)
                .tenantUrn(user.getAccountUrn())
                .build();
            eventSendingService.sendEvent(user, ROLE_NOT_FOUND, eventPayload);
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .build();
        }
    }
}
