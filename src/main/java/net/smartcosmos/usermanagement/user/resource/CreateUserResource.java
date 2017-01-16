package net.smartcosmos.usermanagement.user.resource;

import javax.validation.Valid;

import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.context.request.async.DeferredResult;

import net.smartcosmos.annotation.SmartCosmosRdao;
import net.smartcosmos.security.user.SmartCosmosUser;
import net.smartcosmos.usermanagement.user.dto.CreateUserRequest;
import net.smartcosmos.usermanagement.user.service.CreateUserService;

import static org.springframework.http.MediaType.APPLICATION_JSON_UTF8_VALUE;

import static net.smartcosmos.usermanagement.user.resource.UserEndpointConstants.ENDPOINT_ENABLEMENT_PROPERTY_ENABLED;

/**
 * Initially created by SMART COSMOS Team on July 01, 2016.
 */
@SmartCosmosRdao
@Slf4j
@ConditionalOnProperty(prefix = UserEndpointConstants.ENDPOINT_ENABLEMENT_USERS, name = ENDPOINT_ENABLEMENT_PROPERTY_ENABLED, matchIfMissing = true)
@PreAuthorize("hasAuthority('https://authorities.smartcosmos.net/users/create')")
public class CreateUserResource {

    private CreateUserService service;

    @Autowired
    public CreateUserResource(CreateUserService service) { this.service = service; }

    @RequestMapping(value = UserEndpointConstants.ENDPOINT_USERS,
                    method = RequestMethod.POST,
                    produces = APPLICATION_JSON_UTF8_VALUE,
                    consumes = APPLICATION_JSON_UTF8_VALUE)
    @ConditionalOnProperty(prefix = UserEndpointConstants.ENDPOINT_ENABLEMENT_USERS_CREATE,
                           name = ENDPOINT_ENABLEMENT_PROPERTY_ENABLED,
                           matchIfMissing = true)
    public DeferredResult<ResponseEntity<?>> createUser(
        @RequestBody @Valid CreateUserRequest createUserRequest,
        SmartCosmosUser user) {

        DeferredResult<ResponseEntity<?>> response = new DeferredResult<>();
        service.create(response, createUserRequest, user);
        return response;
    }
}


