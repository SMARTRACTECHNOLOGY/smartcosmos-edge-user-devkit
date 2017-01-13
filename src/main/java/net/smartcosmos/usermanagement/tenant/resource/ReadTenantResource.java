package net.smartcosmos.usermanagement.tenant.resource;

import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import net.smartcosmos.annotation.SmartCosmosRdao;
import net.smartcosmos.security.user.SmartCosmosUser;
import net.smartcosmos.usermanagement.tenant.service.ReadTenantService;

import static org.springframework.http.MediaType.APPLICATION_JSON_UTF8_VALUE;

import static net.smartcosmos.usermanagement.tenant.resource.TenantEndpointConstants.ENDPOINT_ENABLEMENT_PROPERTY_ENABLED;

@Slf4j
@SmartCosmosRdao
@ConditionalOnProperty(prefix = TenantEndpointConstants.ENDPOINT_ENABLEMENT_TENANTS,
                       name = ENDPOINT_ENABLEMENT_PROPERTY_ENABLED,
                       matchIfMissing = true)
public class ReadTenantResource {

    private ReadTenantService readTenantService;

    @Autowired
    public ReadTenantResource(ReadTenantService readTenantService) { this.readTenantService = readTenantService; }

    @RequestMapping(value = TenantEndpointConstants.ENDPOINT_TENANTS_URN, method = RequestMethod.GET, produces = APPLICATION_JSON_UTF8_VALUE)
    @ConditionalOnProperty(prefix = TenantEndpointConstants.ENDPOINT_ENABLEMENT_TENANTS_READ_URN,
                           name = ENDPOINT_ENABLEMENT_PROPERTY_ENABLED,
                           matchIfMissing = true)
    @PreAuthorize("hasAuthority('https://authorities.smartcosmos.net/tenants/read') or #tenantUrn.equals(#user.getAccountUrn())")
    public ResponseEntity<?> getByUrn(
        @PathVariable(TenantEndpointConstants.TENANT_URN) String tenantUrn,
        SmartCosmosUser user) {

        return readTenantService.findByUrn(tenantUrn, user);
    }

    @RequestMapping(value = TenantEndpointConstants.ENDPOINT_TENANTS, method = RequestMethod.GET, produces = APPLICATION_JSON_UTF8_VALUE)
    @ConditionalOnProperty(prefix = TenantEndpointConstants.ENDPOINT_ENABLEMENT_TENANTS_READ_ALL,
                           name = ENDPOINT_ENABLEMENT_PROPERTY_ENABLED,
                           matchIfMissing = true)
    @PreAuthorize("hasAuthority('https://authorities.smartcosmos.net/tenants/read')")
    public ResponseEntity<?> getByName(
        @RequestParam(value = TenantEndpointConstants.NAME, required = false, defaultValue = "") String name,
        SmartCosmosUser user) {

        return readTenantService.query(name, user);
    }
}
