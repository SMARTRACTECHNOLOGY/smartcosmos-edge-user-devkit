package net.smartcosmos.ext.tenant.rest.resource;

import javax.validation.Valid;

import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.context.request.async.DeferredResult;

import net.smartcosmos.ext.tenant.rest.dto.RestCreateRoleRequest;
import net.smartcosmos.ext.tenant.rest.service.CreateRoleService;
import net.smartcosmos.security.EndpointMethodControl;
import net.smartcosmos.spring.SmartCosmosRdao;

import static org.springframework.http.MediaType.APPLICATION_JSON_UTF8_VALUE;

/**
 * Initially created by SMART COSMOS Team on July 01, 2016.
 */
@SmartCosmosRdao
@Slf4j
@ConditionalOnProperty(prefix = "smt.endpoints.tenant", name = "enabled", matchIfMissing = true)
//@Api
public class CreateRoleResource {

    private CreateRoleService service;

    @Autowired
    public CreateRoleResource(CreateRoleService service) { this.service = service; }

    @RequestMapping(value = "/roles", method = RequestMethod.POST, produces = APPLICATION_JSON_UTF8_VALUE, consumes = APPLICATION_JSON_UTF8_VALUE)
    @EndpointMethodControl(key = "role.post")
    @ConditionalOnProperty(prefix = "smt.endpoints.user.post", name = "enabled", matchIfMissing = true)
    public DeferredResult<ResponseEntity> createRole(
        @RequestBody @Valid RestCreateRoleRequest restCreateRoleRequest) {

        return service.create(restCreateRoleRequest);
    }
}


