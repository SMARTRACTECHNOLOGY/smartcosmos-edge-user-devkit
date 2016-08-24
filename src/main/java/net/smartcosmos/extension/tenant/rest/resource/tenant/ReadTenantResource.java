package net.smartcosmos.extension.tenant.rest.resource.tenant;

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
import net.smartcosmos.extension.tenant.rest.service.tenant.ReadTenantService;
import net.smartcosmos.security.user.SmartCosmosUser;

import static org.springframework.http.MediaType.APPLICATION_JSON_UTF8_VALUE;

import static net.smartcosmos.extension.tenant.rest.resource.BasicEndpointConstants.ENDPOINT_ENABLEMENT_PROPERTY_ENABLED;
import static net.smartcosmos.extension.tenant.rest.resource.tenant.TenantEndpointConstants.ENDPOINT_ENABLEMENT_TENANTS;
import static net.smartcosmos.extension.tenant.rest.resource.tenant.TenantEndpointConstants.ENDPOINT_ENABLEMENT_TENANTS_READ_ALL;
import static net.smartcosmos.extension.tenant.rest.resource.tenant.TenantEndpointConstants.ENDPOINT_ENABLEMENT_TENANTS_READ_URN;
import static net.smartcosmos.extension.tenant.rest.resource.tenant.TenantEndpointConstants.ENDPOINT_TENANTS;
import static net.smartcosmos.extension.tenant.rest.resource.tenant.TenantEndpointConstants.ENDPOINT_TENANTS_URN;
import static net.smartcosmos.extension.tenant.rest.resource.tenant.TenantEndpointConstants.NAME;
import static net.smartcosmos.extension.tenant.rest.resource.tenant.TenantEndpointConstants.TENANT_URN;

@Slf4j
@SmartCosmosRdao
@ConditionalOnProperty(prefix = ENDPOINT_ENABLEMENT_TENANTS, name = ENDPOINT_ENABLEMENT_PROPERTY_ENABLED, matchIfMissing = true)
public class ReadTenantResource {

    private ReadTenantService readTenantService;

    @Autowired
    public ReadTenantResource(ReadTenantService readTenantService) { this.readTenantService = readTenantService; }

    @RequestMapping(value = ENDPOINT_TENANTS_URN, method = RequestMethod.GET, produces = APPLICATION_JSON_UTF8_VALUE)
    @ConditionalOnProperty(prefix = ENDPOINT_ENABLEMENT_TENANTS_READ_URN, name = ENDPOINT_ENABLEMENT_PROPERTY_ENABLED, matchIfMissing = true)
    @PreAuthorize("hasAuthority('https://authorities.smartcosmos.net/tenants/read') or #tenantUrn.equals(#user.getAccountUrn())")
    public ResponseEntity<?> getByUrn(
        @PathVariable(TENANT_URN) String tenantUrn,
        SmartCosmosUser user) {

        return readTenantService.findByUrn(tenantUrn, user);
    }

    @RequestMapping(value = ENDPOINT_TENANTS, method = RequestMethod.GET, produces = APPLICATION_JSON_UTF8_VALUE)
    @ConditionalOnProperty(prefix = ENDPOINT_ENABLEMENT_TENANTS_READ_ALL, name = ENDPOINT_ENABLEMENT_PROPERTY_ENABLED, matchIfMissing = true)
    @PreAuthorize("hasAuthority('https://authorities.smartcosmos.net/tenants/read')")
    public ResponseEntity<?> getByName(
        @RequestParam(value = NAME, required = false, defaultValue = "") String name,
        SmartCosmosUser user) {

        return readTenantService.query(name, user);
    }
}
