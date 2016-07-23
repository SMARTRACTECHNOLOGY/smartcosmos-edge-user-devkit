package net.smartcosmos.extension.tenant.rest.resource.tenant;

import lombok.extern.slf4j.Slf4j;
import net.smartcosmos.annotation.SmartCosmosRdao;
import net.smartcosmos.extension.tenant.rest.dto.tenant.RestCreateTenantRequest;
import net.smartcosmos.extension.tenant.rest.service.tenant.CreateTenantService;
import net.smartcosmos.security.EndpointMethodControl;
import net.smartcosmos.security.user.SmartCosmosUser;
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
@ConditionalOnProperty(prefix = "smt.endpoints.tenant", name = "enabled", matchIfMissing = true)
//@Api
public class CreateTenantResource {

    private CreateTenantService service;

    @Autowired
    public CreateTenantResource(CreateTenantService service) { this.service = service; }

    @RequestMapping(value = "/tenants", method = RequestMethod.POST, produces = APPLICATION_JSON_UTF8_VALUE, consumes = APPLICATION_JSON_UTF8_VALUE)
    @EndpointMethodControl(key = "tenant.post")
    @ConditionalOnProperty(prefix = "smt.endpoints.tenant.post", name = "enabled", matchIfMissing = true)
    public DeferredResult<ResponseEntity> createTenant(
        @RequestBody @Valid RestCreateTenantRequest restCreateTenantRequest,
        SmartCosmosUser user) {

        return service.create(restCreateTenantRequest);
    }
}


