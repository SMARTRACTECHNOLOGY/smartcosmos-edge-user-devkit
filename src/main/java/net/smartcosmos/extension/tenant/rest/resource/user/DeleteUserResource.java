package net.smartcosmos.extension.tenant.rest.resource.user;

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
import net.smartcosmos.extension.tenant.rest.service.user.DeleteUserService;
import net.smartcosmos.security.user.SmartCosmosUser;

import static net.smartcosmos.extension.tenant.rest.resource.BasicEndpointConstants.ENDPOINT_ENABLEMENT_PROPERTY_ENABLED;
import static net.smartcosmos.extension.tenant.rest.resource.user.UserEndpointConstants.ENDPOINT_ENABLEMENT_USERS;
import static net.smartcosmos.extension.tenant.rest.resource.user.UserEndpointConstants.ENDPOINT_ENABLEMENT_USERS_DELETE;
import static net.smartcosmos.extension.tenant.rest.resource.user.UserEndpointConstants.ENDPOINT_USERS_URN;
import static net.smartcosmos.extension.tenant.rest.resource.user.UserEndpointConstants.USER_URN;

/**
 * Initially created by SMART COSMOS Team on July 01, 2016.
 */
@SmartCosmosRdao
@Slf4j
@ConditionalOnProperty(prefix = ENDPOINT_ENABLEMENT_USERS, name = ENDPOINT_ENABLEMENT_PROPERTY_ENABLED, matchIfMissing = true)
@PreAuthorize("hasAuthority('https://authorities.smartcosmos.net/users/delete')")
//@Api
public class DeleteUserResource {

    private DeleteUserService service;

    @Autowired
    public DeleteUserResource(DeleteUserService service) { this.service = service; }

    @RequestMapping(value = ENDPOINT_USERS_URN, method = RequestMethod.DELETE)
    @ConditionalOnProperty(prefix = ENDPOINT_ENABLEMENT_USERS_DELETE, name = ENDPOINT_ENABLEMENT_PROPERTY_ENABLED, matchIfMissing = true)
    public DeferredResult<ResponseEntity> deleteUser(
        @PathVariable(USER_URN) String userUrn,
        SmartCosmosUser user) {

        return service.delete(userUrn, user);
    }
}
