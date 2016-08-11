package net.smartcosmos.extension.tenant.rest.resource.user;

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
import net.smartcosmos.extension.tenant.rest.service.user.ReadUserService;
import net.smartcosmos.security.user.SmartCosmosUser;

import static org.springframework.http.MediaType.APPLICATION_JSON_UTF8_VALUE;

import static net.smartcosmos.extension.tenant.rest.resource.BasicEndpointConstants.ENDPOINT_ENABLEMENT_PROPERTY_ENABLED;
import static net.smartcosmos.extension.tenant.rest.resource.user.UserEndpointConstants.ENDPOINT_ENABLEMENT_USERS;
import static net.smartcosmos.extension.tenant.rest.resource.user.UserEndpointConstants.ENDPOINT_ENABLEMENT_USERS_READ_ALL;
import static net.smartcosmos.extension.tenant.rest.resource.user.UserEndpointConstants.ENDPOINT_ENABLEMENT_USERS_READ_URN;
import static net.smartcosmos.extension.tenant.rest.resource.user.UserEndpointConstants.ENDPOINT_USERS;
import static net.smartcosmos.extension.tenant.rest.resource.user.UserEndpointConstants.ENDPOINT_USERS_URN;
import static net.smartcosmos.extension.tenant.rest.resource.user.UserEndpointConstants.NAME;
import static net.smartcosmos.extension.tenant.rest.resource.user.UserEndpointConstants.USER_URN;

@Slf4j
@SmartCosmosRdao
@ConditionalOnProperty(prefix = ENDPOINT_ENABLEMENT_USERS, name = ENDPOINT_ENABLEMENT_PROPERTY_ENABLED, matchIfMissing = true)
public class ReadUserResource {

    private ReadUserService readUserService;

    @Autowired
    public ReadUserResource(ReadUserService readUserService) { this.readUserService = readUserService; }

    @RequestMapping(value = ENDPOINT_USERS_URN, method = RequestMethod.GET, produces = APPLICATION_JSON_UTF8_VALUE)
    @ConditionalOnProperty(prefix = ENDPOINT_ENABLEMENT_USERS_READ_URN, name = ENDPOINT_ENABLEMENT_PROPERTY_ENABLED, matchIfMissing = true)
    @PreAuthorize("hasAuthority('https://authorities.smartcosmos.net/users/read') or #userUrn.equals(#user.getUserUrn())")
    public ResponseEntity<?> getByUrn(
        @PathVariable(USER_URN) String userUrn,
        SmartCosmosUser user) {

        return readUserService.findByUrn(userUrn, user);
    }

    @RequestMapping(value = ENDPOINT_USERS, method = RequestMethod.GET, produces = APPLICATION_JSON_UTF8_VALUE)
    @ConditionalOnProperty(prefix = ENDPOINT_ENABLEMENT_USERS_READ_ALL, name = ENDPOINT_ENABLEMENT_PROPERTY_ENABLED, matchIfMissing = true)
    @PreAuthorize("hasAuthority('https://authorities.smartcosmos.net/users/read') or #name.equals(#user.getUsername())")
    public ResponseEntity<?> getByName(
        @RequestParam(value = NAME, required = false, defaultValue = "") String name,
        SmartCosmosUser user) {

        return readUserService.query(name, user);
    }
}
