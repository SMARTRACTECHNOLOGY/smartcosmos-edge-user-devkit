package net.smartcosmos.extension.tenant.rest.resource.user;

import javax.validation.Valid;

import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.context.request.async.DeferredResult;

import net.smartcosmos.annotation.SmartCosmosRdao;
import net.smartcosmos.extension.tenant.rest.dto.user.RestCreateOrUpdateUserRequest;
import net.smartcosmos.extension.tenant.rest.service.user.UpdateUserService;
import net.smartcosmos.security.user.SmartCosmosUser;

import static org.springframework.http.MediaType.APPLICATION_JSON_UTF8_VALUE;

import static net.smartcosmos.extension.tenant.rest.resource.BasicEndpointConstants.ENDPOINT_ENABLEMENT_PROPERTY_ENABLED;
import static net.smartcosmos.extension.tenant.rest.resource.user.UserEndpointConstants.ENDPOINT_ENABLEMENT_USERS;
import static net.smartcosmos.extension.tenant.rest.resource.user.UserEndpointConstants.ENDPOINT_ENABLEMENT_USERS_UPDATE;
import static net.smartcosmos.extension.tenant.rest.resource.user.UserEndpointConstants.ENDPOINT_USERS_URN;
import static net.smartcosmos.extension.tenant.rest.resource.user.UserEndpointConstants.USER_URN;

/**
 * Initially created by SMART COSMOS Team on July 01, 2016.
 */
@SmartCosmosRdao
@Slf4j
@ConditionalOnProperty(prefix = ENDPOINT_ENABLEMENT_USERS, name = ENDPOINT_ENABLEMENT_PROPERTY_ENABLED, matchIfMissing = true)
//@Api
public class UpdateUserResource {

    private UpdateUserService service;

    @Autowired
    public UpdateUserResource(UpdateUserService service) { this.service = service; }

    @RequestMapping(value = ENDPOINT_USERS_URN,
                    method = RequestMethod.PUT,
                    produces = APPLICATION_JSON_UTF8_VALUE,
                    consumes = APPLICATION_JSON_UTF8_VALUE)
    @ConditionalOnProperty(prefix = ENDPOINT_ENABLEMENT_USERS_UPDATE, name = ENDPOINT_ENABLEMENT_PROPERTY_ENABLED, matchIfMissing = true)
    @PreAuthorize(
        "hasAuthority('https://authorities.smartcosmos.net/users/update') "
        + "or (#userUrn.equals(#user.getUserUrn()) and (#requestBody.roles == null or #requestBody.roles.size() == 0))")
    public DeferredResult<ResponseEntity> updateUser(
        @PathVariable(USER_URN) String userUrn,
        @RequestBody @Valid RestCreateOrUpdateUserRequest requestBody,
        SmartCosmosUser user) {

        DeferredResult<ResponseEntity> response = new DeferredResult<>();
        service.update(response, userUrn, requestBody, user);
        return response;
    }
}


