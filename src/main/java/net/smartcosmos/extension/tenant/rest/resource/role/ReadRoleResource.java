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
import net.smartcosmos.security.EndpointMethodControl;
import net.smartcosmos.security.user.SmartCosmosUser;

import static org.springframework.http.MediaType.APPLICATION_JSON_UTF8_VALUE;

@SmartCosmosRdao
@Slf4j
@ConditionalOnProperty(prefix = "smt.endpoints.role", name = "enabled", matchIfMissing = true)
public class ReadRoleResource {

    private ReadRoleService service;

    @Autowired
    public ReadRoleResource(ReadRoleService readRoleService) { this.service = readRoleService; }

    @RequestMapping(value = "/roles/{urn}", method = RequestMethod.GET, produces = APPLICATION_JSON_UTF8_VALUE)
    @EndpointMethodControl(key = "role.urn.get")
    @ConditionalOnProperty(prefix = "smt.endpoints.role.urn.get", name = "enabled", matchIfMissing = true)
    public ResponseEntity<?> getByUrn(@PathVariable String urn, SmartCosmosUser user) {

        return service.findByUrn(urn, user);
    }

    @RequestMapping(value = "/roles", method = RequestMethod.GET, produces = APPLICATION_JSON_UTF8_VALUE)
    @EndpointMethodControl(key = "role.get")
    @ConditionalOnProperty(prefix = "smt.endpoints.role.get", name = "enabled", matchIfMissing = true)
    public ResponseEntity<?> getByName(
        @RequestParam(value = "name", required = false, defaultValue = "") String name, SmartCosmosUser user) {

        return service.query(name, user);
    }
}
