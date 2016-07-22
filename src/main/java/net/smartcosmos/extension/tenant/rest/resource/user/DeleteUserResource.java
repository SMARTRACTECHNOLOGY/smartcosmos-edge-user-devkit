package net.smartcosmos.extension.tenant.rest.resource.user;

import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.context.request.async.DeferredResult;

import net.smartcosmos.extension.tenant.rest.service.user.DeleteUserService;
import net.smartcosmos.security.EndpointMethodControl;
import net.smartcosmos.security.user.SmartCosmosUser;
import net.smartcosmos.spring.SmartCosmosRdao;

/**
 * Initially created by SMART COSMOS Team on July 01, 2016.
 */
@SmartCosmosRdao
@Slf4j
@ConditionalOnProperty(prefix = "smt.endpoints.tenant", name = "enabled", matchIfMissing = true)
//@Api
public class DeleteUserResource {

    private DeleteUserService service;

    @Autowired
    public DeleteUserResource(DeleteUserService service) { this.service = service; }

    @RequestMapping(value = "/users/{userUrn}", method = RequestMethod.DELETE)
    @EndpointMethodControl(key = "user.delete")
    @ConditionalOnProperty(prefix = "smt.endpoints.user.delete", name = "enabled", matchIfMissing = true)
    public DeferredResult<ResponseEntity> deleteUser(
        @PathVariable String userUrn,
        SmartCosmosUser user) {

        return service.delete(userUrn, user);
    }
}
