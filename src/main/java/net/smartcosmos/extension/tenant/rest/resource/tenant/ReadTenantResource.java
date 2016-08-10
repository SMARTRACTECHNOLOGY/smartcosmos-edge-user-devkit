package net.smartcosmos.extension.tenant.rest.resource.tenant;

import static org.springframework.http.MediaType.APPLICATION_JSON_UTF8_VALUE;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import lombok.extern.slf4j.Slf4j;

import net.smartcosmos.annotation.SmartCosmosRdao;
import net.smartcosmos.extension.tenant.rest.service.tenant.ReadTenantService;
import net.smartcosmos.security.EndpointMethodControl;
import net.smartcosmos.security.user.SmartCosmosUser;

@Slf4j
@SmartCosmosRdao
@ConditionalOnProperty(prefix = "smt.endpoints.tenant", name = "enabled", matchIfMissing = true)
public class ReadTenantResource {

    private ReadTenantService readTenantService;

    @Autowired
    public ReadTenantResource(ReadTenantService readTenantService) {
        this.readTenantService = readTenantService;
    }

    @RequestMapping(value = "/tenants/{urn}", method = RequestMethod.GET, produces = APPLICATION_JSON_UTF8_VALUE)
    @EndpointMethodControl(key = "tenant.urn.get")
    @ConditionalOnProperty(prefix = "smt.endpoints.tenant.urn.get", name = "enabled", matchIfMissing = true)
    public ResponseEntity<?> getByUrn(@PathVariable String urn, SmartCosmosUser user) {

        return readTenantService.findByUrn(urn, user);
    }

    @RequestMapping(value = "/tenants", method = RequestMethod.GET, produces = APPLICATION_JSON_UTF8_VALUE)
    @EndpointMethodControl(key = "tenant.get")
    @ConditionalOnProperty(prefix = "smt.endpoints.tenant.get", name = "enabled", matchIfMissing = true)
    public ResponseEntity<?> getByName(@RequestParam(value = "name", required = false, defaultValue = "") String name, SmartCosmosUser user) {

        return readTenantService.query(name, user);
    }
}
