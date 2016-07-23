package net.smartcosmos.extension.tenant.rest.resource.role;

import lombok.extern.slf4j.Slf4j;
import net.smartcosmos.annotation.SmartCosmosRdao;
import net.smartcosmos.extension.tenant.rest.service.role.DeleteRoleService;
import net.smartcosmos.security.EndpointMethodControl;
import net.smartcosmos.security.user.SmartCosmosUser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.context.request.async.DeferredResult;

/**
 * Initially created by SMART COSMOS Team on July 01, 2016.
 */
@SmartCosmosRdao
@Slf4j
@ConditionalOnProperty(prefix = "smt.endpoints.tenant", name = "enabled", matchIfMissing = true)
//@Api
public class DeleteRoleResource {

    private DeleteRoleService service;

    @Autowired
    public DeleteRoleResource(DeleteRoleService service) { this.service = service; }

    @RequestMapping(value = "/roles/{roleUrn}", method = RequestMethod.DELETE)
    @EndpointMethodControl(key = "role.delete")
    @ConditionalOnProperty(prefix = "smt.endpoints.role.delete", name = "enabled", matchIfMissing = true)
    public DeferredResult<ResponseEntity> deleteUser(
        @PathVariable String roleUrn,
        SmartCosmosUser user) {

        return service.delete(roleUrn, user);
    }
}
