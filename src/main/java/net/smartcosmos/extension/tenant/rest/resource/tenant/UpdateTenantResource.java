package net.smartcosmos.extension.tenant.rest.resource.tenant;

import lombok.extern.slf4j.Slf4j;
import net.smartcosmos.extension.tenant.rest.dto.tenant.RestUpdateTenantRequest;
import net.smartcosmos.extension.tenant.rest.service.tenant.UpdateTenantService;
import net.smartcosmos.security.EndpointMethodControl;
import net.smartcosmos.security.user.SmartCosmosUser;
import net.smartcosmos.annotation.SmartCosmosRdao;
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
public class UpdateTenantResource {

    private UpdateTenantService service;

    @Autowired
    public UpdateTenantResource(UpdateTenantService service) { this.service = service; }

    @RequestMapping(value = "/tenants/{tenantUrn}", method = RequestMethod.PUT, produces = APPLICATION_JSON_UTF8_VALUE, consumes = APPLICATION_JSON_UTF8_VALUE)
    @EndpointMethodControl(key = "tenant.put")
    @ConditionalOnProperty(prefix = "smt.endpoints.tenant.put", name = "enabled", matchIfMissing = true)
    public DeferredResult<ResponseEntity> updateTenant(
        @PathVariable String tenantUrn,
        @RequestBody @Valid RestUpdateTenantRequest restUpdateTenantRequest,
        SmartCosmosUser user) {

        return service.update(tenantUrn, restUpdateTenantRequest, user);
    }
}


