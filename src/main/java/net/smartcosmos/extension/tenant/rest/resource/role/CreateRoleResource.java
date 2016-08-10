package net.smartcosmos.extension.tenant.rest.resource.role;

import lombok.extern.slf4j.Slf4j;
import net.smartcosmos.extension.tenant.rest.dto.role.RestCreateOrUpdateRoleRequest;
import net.smartcosmos.extension.tenant.rest.service.role.CreateRoleService;
import net.smartcosmos.security.EndpointMethodControl;
import net.smartcosmos.security.user.SmartCosmosUser;
import net.smartcosmos.annotation.SmartCosmosRdao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.context.request.async.DeferredResult;

import javax.validation.Valid;

import static org.springframework.http.MediaType.APPLICATION_JSON_UTF8_VALUE;

/**
 * Initially created by SMART COSMOS Team on July 01, 2016.
 */
@SmartCosmosRdao
@Slf4j
@ConditionalOnProperty(prefix = "smt.endpoints.role", name = "enabled", matchIfMissing = true)
//@Api
public class CreateRoleResource {

    private CreateRoleService service;

    @Autowired
    public CreateRoleResource(CreateRoleService service) { this.service = service; }

    @RequestMapping(value = "/roles", method = RequestMethod.POST, produces = APPLICATION_JSON_UTF8_VALUE, consumes = APPLICATION_JSON_UTF8_VALUE)
    @EndpointMethodControl(key = "role.post")
    @ConditionalOnProperty(prefix = "smt.endpoints.user.post", name = "enabled", matchIfMissing = true)
    public DeferredResult<ResponseEntity> createRole(
        @RequestBody @Valid RestCreateOrUpdateRoleRequest requestBody,
        SmartCosmosUser user) {

        return service.create(requestBody, user);
    }
}


