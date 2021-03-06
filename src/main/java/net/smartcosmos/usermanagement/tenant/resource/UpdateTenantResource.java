package net.smartcosmos.usermanagement.tenant.resource;

import javax.validation.Valid;

import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.context.request.async.DeferredResult;

import net.smartcosmos.annotation.SmartCosmosRdao;
import net.smartcosmos.security.user.SmartCosmosUser;
import net.smartcosmos.usermanagement.tenant.dto.UpdateTenantRequest;
import net.smartcosmos.usermanagement.tenant.service.UpdateTenantService;

import static org.springframework.http.MediaType.APPLICATION_JSON_UTF8_VALUE;

import static net.smartcosmos.usermanagement.tenant.resource.TenantEndpointConstants.ENDPOINT_ENABLEMENT_PROPERTY_ENABLED;

/**
 * Endpoints for updating tenant information.
 */
@SmartCosmosRdao
@Slf4j
@ConditionalOnProperty(prefix = TenantEndpointConstants.ENDPOINT_ENABLEMENT_TENANTS,
                       name = ENDPOINT_ENABLEMENT_PROPERTY_ENABLED,
                       matchIfMissing = true)
@PreAuthorize("hasAuthority('https://authorities.smartcosmos.net/tenants/update')")
public class UpdateTenantResource {

    private UpdateTenantService service;

    @Autowired
    public UpdateTenantResource(UpdateTenantService service) { this.service = service; }

    @RequestMapping(value = TenantEndpointConstants.ENDPOINT_TENANTS_URN,
                    method = RequestMethod.PUT,
                    produces = APPLICATION_JSON_UTF8_VALUE,
                    consumes = APPLICATION_JSON_UTF8_VALUE)
    @ConditionalOnProperty(prefix = TenantEndpointConstants.ENDPOINT_ENABLEMENT_TENANTS_UPDATE,
                           name = ENDPOINT_ENABLEMENT_PROPERTY_ENABLED,
                           matchIfMissing = true)
    public DeferredResult<ResponseEntity<?>> updateTenant(
        @PathVariable(TenantEndpointConstants.TENANT_URN) String tenantUrn,
        @RequestBody @Valid UpdateTenantRequest updateTenantRequest,
        SmartCosmosUser user) {

        DeferredResult<ResponseEntity<?>> response = new DeferredResult<>();
        service.update(response, tenantUrn, updateTenantRequest, user);
        return response;
    }
}


