package net.smartcosmos.extension.tenant.rest.resource;

import javax.validation.Valid;

import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import net.smartcosmos.extension.tenant.rest.dto.RestAuthenticateRequest;
import net.smartcosmos.extension.tenant.rest.service.LoginService;
import net.smartcosmos.security.user.SmartCosmosUser;

import static org.springframework.http.MediaType.APPLICATION_JSON_UTF8_VALUE;

@RestController
@Slf4j
public class AuthenticationResource {

    private final AuthenticationService authenticationService;

    @Autowired
    public AuthenticationResource(AuthenticationService authenticationService) {
        this.authenticationService = authenticationService;
    }

    @RequestMapping(value = "authenticate", method = RequestMethod.POST, produces = APPLICATION_JSON_UTF8_VALUE,
                    consumes = APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<?> authenticate(@RequestBody @Valid RestAuthenticateRequest authenticate, SmartCosmosUser user){
        return authenticationService.authenticate(authenticate, user);
    }
}
