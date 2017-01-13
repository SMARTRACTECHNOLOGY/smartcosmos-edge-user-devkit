package net.smartcosmos.usermanagement.role.resource;

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
import net.smartcosmos.usermanagement.role.service.ReadRoleService;

import static org.springframework.http.MediaType.APPLICATION_JSON_UTF8_VALUE;

import static net.smartcosmos.usermanagement.role.resource.RoleEndpointConstants.ENDPOINT_ENABLEMENT_PROPERTY_ENABLED;

@SmartCosmosRdao
@Slf4j
@ConditionalOnProperty(prefix = RoleEndpointConstants.ENDPOINT_ENABLEMENT_ROLES, name = ENDPOINT_ENABLEMENT_PROPERTY_ENABLED, matchIfMissing = true)
@PreAuthorize("hasAuthority('https://authorities.smartcosmos.net/roles/read')")
public class ReadRoleResource {

    private ReadRoleService service;

    @Autowired
    public ReadRoleResource(ReadRoleService readRoleService) { this.service = readRoleService; }

    @RequestMapping(value = RoleEndpointConstants.ENDPOINT_ROLES_URN, method = RequestMethod.GET, produces = APPLICATION_JSON_UTF8_VALUE)
    @ConditionalOnProperty(prefix = RoleEndpointConstants.ENDPOINT_ENABLEMENT_ROLES_READ_URN,
                           name = ENDPOINT_ENABLEMENT_PROPERTY_ENABLED,
                           matchIfMissing = true)
    public ResponseEntity<?> getByUrn(
        @PathVariable(RoleEndpointConstants.ROLE_URN) String urn,
        SmartCosmosUser user) {

        return service.findByUrn(urn, user);
    }

    @RequestMapping(value = RoleEndpointConstants.ENDPOINT_ROLES, method = RequestMethod.GET, produces = APPLICATION_JSON_UTF8_VALUE)
    @ConditionalOnProperty(prefix = RoleEndpointConstants.ENDPOINT_ENABLEMENT_ROLES_READ_ALL,
                           name = ENDPOINT_ENABLEMENT_PROPERTY_ENABLED,
                           matchIfMissing = true)
    public ResponseEntity<?> getByName(
        @RequestParam(value = RoleEndpointConstants.NAME, required = false, defaultValue = "") String name,
        SmartCosmosUser user) {

        return service.query(name, user);
    }
}
