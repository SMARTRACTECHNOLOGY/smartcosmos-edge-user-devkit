package net.smartcosmos.usermanagement.user.resource;

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
import net.smartcosmos.security.user.SmartCosmosUser;
import net.smartcosmos.usermanagement.user.service.DeleteUserService;

import static net.smartcosmos.usermanagement.user.resource.UserEndpointConstants.ENDPOINT_ENABLEMENT_PROPERTY_ENABLED;

/**
 * Initially created by SMART COSMOS Team on July 01, 2016.
 */
@SmartCosmosRdao
@Slf4j
@ConditionalOnProperty(prefix = UserEndpointConstants.ENDPOINT_ENABLEMENT_USERS, name = ENDPOINT_ENABLEMENT_PROPERTY_ENABLED, matchIfMissing = true)
@PreAuthorize("hasAuthority('https://authorities.smartcosmos.net/users/delete')")
public class DeleteUserResource {

    private DeleteUserService service;

    @Autowired
    public DeleteUserResource(DeleteUserService service) { this.service = service; }

    @RequestMapping(value = UserEndpointConstants.ENDPOINT_USERS_URN, method = RequestMethod.DELETE)
    @ConditionalOnProperty(prefix = UserEndpointConstants.ENDPOINT_ENABLEMENT_USERS_DELETE,
                           name = ENDPOINT_ENABLEMENT_PROPERTY_ENABLED,
                           matchIfMissing = true)
    public DeferredResult<ResponseEntity<?>> deleteUser(
        @PathVariable(UserEndpointConstants.USER_URN) String userUrn,
        SmartCosmosUser user) {

        DeferredResult<ResponseEntity<?>> response = new DeferredResult<>();
        service.delete(response, userUrn, user);
        return response;
    }
}
