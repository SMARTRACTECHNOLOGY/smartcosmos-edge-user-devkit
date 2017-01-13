package net.smartcosmos.usermanagement.role.service;

import java.util.List;

import lombok.extern.slf4j.Slf4j;

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
 * Initially created by SMART COSMOS Team on July 01, 2016.
 */
@Slf4j
@Service
public class DeleteRoleService {

    private final RoleDao roleDao;
    private final EventSendingService eventSendingService;

    @Autowired
    public DeleteRoleService(RoleDao roleDao, EventSendingService roleEventSendingService) {

        this.roleDao = roleDao;
        this.eventSendingService = roleEventSendingService;
    }

    public DeferredResult<ResponseEntity> delete(String roleUrn, SmartCosmosUser user) {
        // Async worker thread reduces timeouts and disconnects for long queries and processing.
        DeferredResult<ResponseEntity> response = new DeferredResult<>();
        deleteRoleWorker(response, user, roleUrn);

        return response;
    }

    private void deleteRoleWorker(DeferredResult<ResponseEntity> response, SmartCosmosUser user, String roleUrn) {

        try {
            List<RoleResponse> deleteRoleResponse = roleDao.delete(user.getAccountUrn(), roleUrn);

            if (!deleteRoleResponse.isEmpty()) {
                response.setResult(ResponseEntity.noContent()
                                       .build());
                eventSendingService.sendEvent(user, ROLE_DELETED, deleteRoleResponse.get(0));
            } else {
                response.setResult(ResponseEntity.status(HttpStatus.NOT_FOUND)
                                       .build());

                RoleResponse eventPayload = RoleResponse.builder()
                    .urn(roleUrn)
                    .tenantUrn(user.getAccountUrn())
                    .build();
                eventSendingService.sendEvent(user, ROLE_NOT_FOUND, eventPayload);
            }
        } catch (Exception e) {
            log.debug(e.getMessage(), e);
            response.setErrorResult(e);
        }
    }
}
