package net.smartcosmos.extension.tenant.rest.resource;

import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.context.request.async.DeferredResult;

import net.smartcosmos.extension.tenant.rest.service.DeleteUserService;
import net.smartcosmos.security.EndpointMethodControl;
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

    @RequestMapping(value = "/users/{urn}", method = RequestMethod.DELETE)
    @EndpointMethodControl(key = "user.delete")
    @ConditionalOnProperty(prefix = "smt.endpoints.user.delete", name = "enabled", matchIfMissing = true)
    public DeferredResult<ResponseEntity> updateObject(
        @RequestParam String urn) {

        return service.delete(urn);
    }
}
