package net.smartcosmos.extension.tenant.rest.resource.role;

import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.context.request.async.DeferredResult;

import net.smartcosmos.annotation.SmartCosmosRdao;
import net.smartcosmos.extension.tenant.rest.service.role.DeleteRoleService;
import net.smartcosmos.security.user.SmartCosmosUser;

import static net.smartcosmos.extension.tenant.rest.resource.BasicEndpointConstants.ENDPOINT_ENABLEMENT_PROPERTY_ENABLED;
import static net.smartcosmos.extension.tenant.rest.resource.role.RoleEndpointConstants.ENDPOINT_ENABLEMENT_ROLES;
import static net.smartcosmos.extension.tenant.rest.resource.role.RoleEndpointConstants.ENDPOINT_ENABLEMENT_ROLES_DELETE;
import static net.smartcosmos.extension.tenant.rest.resource.role.RoleEndpointConstants.ENDPOINT_ROLES_URN;
import static net.smartcosmos.extension.tenant.rest.resource.role.RoleEndpointConstants.ROLE_URN;

/**
 * Initially created by SMART COSMOS Team on July 01, 2016.
 */
@SmartCosmosRdao
@Slf4j
@ConditionalOnProperty(prefix = ENDPOINT_ENABLEMENT_ROLES, name = ENDPOINT_ENABLEMENT_PROPERTY_ENABLED, matchIfMissing = true)
@PreAuthorize("hasAuthority('https://authorities.smartcosmos.net/roles/delete')")
//@Api
public class DeleteRoleResource {

    private DeleteRoleService service;

    @Autowired
    public DeleteRoleResource(DeleteRoleService service) { this.service = service; }

    @RequestMapping(value = ENDPOINT_ROLES_URN, method = RequestMethod.DELETE)
    @ConditionalOnProperty(prefix = ENDPOINT_ENABLEMENT_ROLES_DELETE, name = ENDPOINT_ENABLEMENT_PROPERTY_ENABLED, matchIfMissing = true)
    public DeferredResult<ResponseEntity> deleteRole(
        @PathVariable(ROLE_URN) String roleUrn,
        SmartCosmosUser user) {

        return service.delete(roleUrn, user);
    }
}
