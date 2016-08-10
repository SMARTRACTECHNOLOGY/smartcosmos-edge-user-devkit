package net.smartcosmos.extension.tenant.rest.service.role;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
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
import net.smartcosmos.extension.tenant.dto.role.RoleResponse;
import net.smartcosmos.extension.tenant.rest.service.AbstractTenantService;
import net.smartcosmos.security.user.SmartCosmosUser;

/**
 * Initially created by SMART COSMOS Team on July 01, 2016.
 */
@Slf4j
@Service
public class DeleteRoleService extends AbstractTenantService {

    @Autowired
    public DeleteRoleService(TenantDao tenantDao, RoleDao roleDao, SmartCosmosEventTemplate smartCosmosEventTemplate,
            ConversionService conversionService) {
        super(tenantDao, roleDao, smartCosmosEventTemplate, conversionService);
    }

    public DeferredResult<ResponseEntity> delete(String roleUrn, SmartCosmosUser user) {
        // Async worker thread reduces timeouts and disconnects for long queries and processing.
        DeferredResult<ResponseEntity> response = new DeferredResult<>();
        deleteRoleWorker(response, user, roleUrn);

        return response;
    }

    @Async
    private void deleteRoleWorker(DeferredResult<ResponseEntity> response, SmartCosmosUser user, String roleUrn) {

        try {
            List<RoleResponse> deleteRoleResponse = roleDao.delete(user.getAccountUrn(), roleUrn);

            if (!deleteRoleResponse.isEmpty()) {
                response.setResult(ResponseEntity.noContent().build());
                sendEvent(user, DefaultEventTypes.RoleDeleted, deleteRoleResponse.get(0));
            }
            else {
                response.setResult(ResponseEntity.status(HttpStatus.NOT_FOUND).build());

                RoleResponse eventPayload = RoleResponse.builder().urn(roleUrn).tenantUrn(user.getAccountUrn()).build();
                sendEvent(user, DefaultEventTypes.RoleNotFound, eventPayload);
            }
        }
        catch (Exception e) {
            log.debug(e.getMessage(), e);
            response.setErrorResult(e);
        }
    }
}
