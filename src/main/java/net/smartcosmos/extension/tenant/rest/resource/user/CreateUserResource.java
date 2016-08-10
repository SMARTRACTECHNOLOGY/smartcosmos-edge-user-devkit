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

import net.smartcosmos.annotation.SmartCosmosRdao;
import net.smartcosmos.extension.tenant.rest.dto.user.RestCreateOrUpdateUserRequest;
import net.smartcosmos.extension.tenant.rest.service.user.CreateUserService;
import net.smartcosmos.security.user.SmartCosmosUser;

import static org.springframework.http.MediaType.APPLICATION_JSON_UTF8_VALUE;

import static net.smartcosmos.extension.tenant.rest.resource.BasicEndpointConstants.ENDPOINT_ENABLEMENT_PROPERTY_ENABLED;
import static net.smartcosmos.extension.tenant.rest.resource.user.UserEndpointConstants.ENDPOINT_ENABLEMENT_USERS;
import static net.smartcosmos.extension.tenant.rest.resource.user.UserEndpointConstants.ENDPOINT_ENABLEMENT_USERS_CREATE;
import static net.smartcosmos.extension.tenant.rest.resource.user.UserEndpointConstants.ENDPOINT_USERS;

/**
 * Initially created by SMART COSMOS Team on July 01, 2016.
 */
@SmartCosmosRdao
@Slf4j
@ConditionalOnProperty(prefix = ENDPOINT_ENABLEMENT_USERS, name = ENDPOINT_ENABLEMENT_PROPERTY_ENABLED, matchIfMissing = true)
//@Api
public class CreateUserResource {

    private CreateUserService service;

    @Autowired
    public CreateUserResource(CreateUserService service) { this.service = service; }

    @RequestMapping(value = ENDPOINT_USERS,
                    method = RequestMethod.POST,
                    produces = APPLICATION_JSON_UTF8_VALUE,
                    consumes = APPLICATION_JSON_UTF8_VALUE)
    @ConditionalOnProperty(prefix = ENDPOINT_ENABLEMENT_USERS_CREATE, name = ENDPOINT_ENABLEMENT_PROPERTY_ENABLED, matchIfMissing = true)
    public DeferredResult<ResponseEntity> createUser(
        @RequestBody @Valid RestCreateOrUpdateUserRequest restCreateUserRequest, SmartCosmosUser user) {

        return service.create(restCreateUserRequest, user);
    }
}


