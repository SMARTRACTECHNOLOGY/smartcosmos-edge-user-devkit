package net.smartcosmos.usermanagement.user.resource;

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
import net.smartcosmos.usermanagement.user.service.ReadUserService;

import static org.springframework.http.MediaType.APPLICATION_JSON_UTF8_VALUE;

import static net.smartcosmos.usermanagement.user.resource.UserEndpointConstants.ENDPOINT_ENABLEMENT_PROPERTY_ENABLED;

@Slf4j
@SmartCosmosRdao
@ConditionalOnProperty(prefix = UserEndpointConstants.ENDPOINT_ENABLEMENT_USERS, name = ENDPOINT_ENABLEMENT_PROPERTY_ENABLED, matchIfMissing = true)
public class ReadUserResource {

    private ReadUserService readUserService;

    @Autowired
    public ReadUserResource(ReadUserService readUserService) { this.readUserService = readUserService; }

    @RequestMapping(value = UserEndpointConstants.ENDPOINT_USERS_URN, method = RequestMethod.GET, produces = APPLICATION_JSON_UTF8_VALUE)
    @ConditionalOnProperty(prefix = UserEndpointConstants.ENDPOINT_ENABLEMENT_USERS_READ_URN,
                           name = ENDPOINT_ENABLEMENT_PROPERTY_ENABLED,
                           matchIfMissing = true)
    @PreAuthorize("hasAuthority('https://authorities.smartcosmos.net/users/read') or #userUrn.equals(#user.getUserUrn())")
    public ResponseEntity<?> getByUrn(
        @PathVariable(UserEndpointConstants.USER_URN) String userUrn,
        SmartCosmosUser user) {

        return readUserService.findByUrn(userUrn, user);
    }

    @RequestMapping(value = UserEndpointConstants.ENDPOINT_USERS, method = RequestMethod.GET, produces = APPLICATION_JSON_UTF8_VALUE)
    @ConditionalOnProperty(prefix = UserEndpointConstants.ENDPOINT_ENABLEMENT_USERS_READ_ALL,
                           name = ENDPOINT_ENABLEMENT_PROPERTY_ENABLED,
                           matchIfMissing = true)
    @PreAuthorize("hasAuthority('https://authorities.smartcosmos.net/users/read') or #name.equals(#user.getUsername())")
    public ResponseEntity<?> getByName(
        @RequestParam(value = UserEndpointConstants.NAME, required = false, defaultValue = "") String name,
        SmartCosmosUser user) {

        return readUserService.query(name, user);
    }
}
