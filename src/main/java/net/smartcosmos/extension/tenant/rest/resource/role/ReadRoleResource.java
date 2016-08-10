package net.smartcosmos.extension.tenant.rest.resource.role;

import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import net.smartcosmos.annotation.SmartCosmosRdao;
import net.smartcosmos.extension.tenant.rest.service.role.ReadRoleService;
import net.smartcosmos.security.user.SmartCosmosUser;

import static org.springframework.http.MediaType.APPLICATION_JSON_UTF8_VALUE;

import static net.smartcosmos.extension.tenant.rest.resource.BasicEndpointConstants.ENDPOINT_ENABLEMENT_PROPERTY_ENABLED;
import static net.smartcosmos.extension.tenant.rest.resource.role.RoleEndpointConstants.ENDPOINT_ENABLEMENT_ROLES;
import static net.smartcosmos.extension.tenant.rest.resource.role.RoleEndpointConstants.ENDPOINT_ENABLEMENT_ROLES_READ_ALL;
import static net.smartcosmos.extension.tenant.rest.resource.role.RoleEndpointConstants.ENDPOINT_ENABLEMENT_ROLES_READ_URN;
import static net.smartcosmos.extension.tenant.rest.resource.role.RoleEndpointConstants.ENDPOINT_ROLES;
import static net.smartcosmos.extension.tenant.rest.resource.role.RoleEndpointConstants.ENDPOINT_ROLES_URN;
import static net.smartcosmos.extension.tenant.rest.resource.role.RoleEndpointConstants.NAME;
import static net.smartcosmos.extension.tenant.rest.resource.role.RoleEndpointConstants.ROLE_URN;

@SmartCosmosRdao
@Slf4j
@ConditionalOnProperty(prefix = ENDPOINT_ENABLEMENT_ROLES, name = ENDPOINT_ENABLEMENT_PROPERTY_ENABLED, matchIfMissing = true)
public class ReadRoleResource {

    private ReadRoleService service;

    @Autowired
    public ReadRoleResource(ReadRoleService readRoleService) { this.service = readRoleService; }

    @RequestMapping(value = ENDPOINT_ROLES_URN, method = RequestMethod.GET, produces = APPLICATION_JSON_UTF8_VALUE)
    @ConditionalOnProperty(prefix = ENDPOINT_ENABLEMENT_ROLES_READ_URN, name = ENDPOINT_ENABLEMENT_PROPERTY_ENABLED, matchIfMissing = true)
    public ResponseEntity<?> getByUrn(
        @PathVariable(ROLE_URN) String urn,
        SmartCosmosUser user) {

        return service.findByUrn(urn, user);
    }

    @RequestMapping(value = ENDPOINT_ROLES, method = RequestMethod.GET, produces = APPLICATION_JSON_UTF8_VALUE)
    @ConditionalOnProperty(prefix = ENDPOINT_ENABLEMENT_ROLES_READ_ALL, name = ENDPOINT_ENABLEMENT_PROPERTY_ENABLED, matchIfMissing = true)
    public ResponseEntity<?> getByName(
        @RequestParam(value = NAME, required = false, defaultValue = "") String name,
        SmartCosmosUser user) {

        return service.query(name, user);
    }
}
