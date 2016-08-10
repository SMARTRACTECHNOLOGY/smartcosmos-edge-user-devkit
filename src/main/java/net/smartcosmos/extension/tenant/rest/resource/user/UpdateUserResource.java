package net.smartcosmos.extension.tenant.rest.resource.user;

import static org.springframework.http.MediaType.APPLICATION_JSON_UTF8_VALUE;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.context.request.async.DeferredResult;

import lombok.extern.slf4j.Slf4j;

import net.smartcosmos.annotation.SmartCosmosRdao;
import net.smartcosmos.extension.tenant.rest.dto.user.RestCreateOrUpdateUserRequest;
import net.smartcosmos.extension.tenant.rest.service.user.UpdateUserService;
import net.smartcosmos.security.EndpointMethodControl;
import net.smartcosmos.security.user.SmartCosmosUser;

/**
 * Initially created by SMART COSMOS Team on July 01, 2016.
 */
@SmartCosmosRdao
@Slf4j
@ConditionalOnProperty(prefix = "smt.endpoints.tenant", name = "enabled", matchIfMissing = true)
// @Api
public class UpdateUserResource {

    private UpdateUserService service;

    @Autowired
    public UpdateUserResource(UpdateUserService service) {
        this.service = service;
    }

    @RequestMapping(value = "/users/{userUrn}", method = RequestMethod.PUT, produces = APPLICATION_JSON_UTF8_VALUE, consumes = APPLICATION_JSON_UTF8_VALUE)
    @EndpointMethodControl(key = "tenant.put")
    @ConditionalOnProperty(prefix = "smt.endpoints.user.put", name = "enabled", matchIfMissing = true)
    public DeferredResult<ResponseEntity> updateUser(@PathVariable String userUrn, @RequestBody @Valid RestCreateOrUpdateUserRequest requestBody,
            SmartCosmosUser user) {

        return service.update(userUrn, requestBody, user);
    }
}
