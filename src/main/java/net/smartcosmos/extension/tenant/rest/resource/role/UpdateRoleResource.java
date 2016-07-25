package net.smartcosmos.extension.tenant.rest.resource.role;

import lombok.extern.slf4j.Slf4j;
import net.smartcosmos.annotation.SmartCosmosRdao;
import net.smartcosmos.extension.tenant.rest.dto.role.RestCreateOrUpdateRoleRequest;
import net.smartcosmos.extension.tenant.rest.service.role.UpdateRoleService;
import net.smartcosmos.security.EndpointMethodControl;
import net.smartcosmos.security.user.SmartCosmosUser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
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
@ConditionalOnProperty(prefix = "smt.endpoints.tenant", name = "enabled", matchIfMissing = true)
//@Api
public class UpdateRoleResource {

    private UpdateRoleService service;

    @Autowired
    public UpdateRoleResource(UpdateRoleService service) { this.service = service; }

    @RequestMapping(value = "/roles/{roleUrn}", method = RequestMethod.PUT, produces = APPLICATION_JSON_UTF8_VALUE, consumes = APPLICATION_JSON_UTF8_VALUE)
    @EndpointMethodControl(key = "tenant.put")
    @ConditionalOnProperty(prefix = "smt.endpoints.role.put", name = "enabled", matchIfMissing = true)
    public DeferredResult<ResponseEntity> updateRole(
        @PathVariable String roleUrn,
        @RequestBody @Valid RestCreateOrUpdateRoleRequest requestBody,
        SmartCosmosUser user) {

        return service.update(roleUrn, requestBody, user);
    }
}


