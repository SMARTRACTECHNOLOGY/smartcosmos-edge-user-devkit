package net.smartcosmos.extension.tenant.rest.resource.user;

import javax.validation.Valid;

import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.context.request.async.DeferredResult;

import net.smartcosmos.extension.tenant.rest.dto.user.RestCreateOrUpdateUserRequest;
import net.smartcosmos.extension.tenant.rest.service.user.CreateUserService;
import net.smartcosmos.security.EndpointMethodControl;
import net.smartcosmos.security.user.SmartCosmosUser;
import net.smartcosmos.spring.SmartCosmosRdao;

import static org.springframework.http.MediaType.APPLICATION_JSON_UTF8_VALUE;

/**
 * Initially created by SMART COSMOS Team on July 01, 2016.
 */
@SmartCosmosRdao
@Slf4j
@ConditionalOnProperty(prefix = "smt.endpoints.tenant", name = "enabled", matchIfMissing = true)
//@Api
public class CreateUserResource {

    private CreateUserService service;

    @Autowired
    public CreateUserResource(CreateUserService service) { this.service = service; }

    @RequestMapping(value = "/users", method = RequestMethod.POST, produces = APPLICATION_JSON_UTF8_VALUE, consumes = APPLICATION_JSON_UTF8_VALUE)
    @EndpointMethodControl(key = "user.post")
    @ConditionalOnProperty(prefix = "smt.endpoints.user.post", name = "enabled", matchIfMissing = true)
    public DeferredResult<ResponseEntity> createUser(
        @RequestBody @Valid RestCreateOrUpdateUserRequest restCreateUserRequest, SmartCosmosUser user) {

        return service.create(restCreateUserRequest, user);
    }
}


