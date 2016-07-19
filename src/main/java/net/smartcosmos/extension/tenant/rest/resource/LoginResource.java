package net.smartcosmos.extension.tenant.rest.resource;

import static org.springframework.http.MediaType.APPLICATION_JSON_UTF8_VALUE;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import lombok.extern.slf4j.Slf4j;

import net.smartcosmos.extension.tenant.rest.dto.RestLoginRequest;
import net.smartcosmos.extension.tenant.rest.service.LoginService;
import net.smartcosmos.security.user.SmartCosmosUser;

@RestController
@Slf4j
public class LoginResource {

    private final LoginService loginService;

    @Autowired
    public LoginResource(LoginService loginService) {
        this.loginService = loginService;
    }

    @RequestMapping(value = "login", method = RequestMethod.POST, produces = APPLICATION_JSON_UTF8_VALUE, consumes = APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<?> login(@RequestBody @Valid RestLoginRequest credentials, SmartCosmosUser user){
        return loginService.validateCredentials(credentials, user);
    }
}
