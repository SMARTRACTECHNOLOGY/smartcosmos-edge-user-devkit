package net.smartcosmos.usermanagement.role.resource;

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
import net.smartcosmos.usermanagement.role.dto.RoleRequest;
import net.smartcosmos.usermanagement.role.service.CreateRoleService;

import static org.springframework.http.MediaType.APPLICATION_JSON_UTF8_VALUE;

import static net.smartcosmos.usermanagement.role.resource.RoleEndpointConstants.ENDPOINT_ENABLEMENT_PROPERTY_ENABLED;

/**
 * The endpoints for creating Roles.
 */
@SmartCosmosRdao
@Slf4j
@ConditionalOnProperty(prefix = RoleEndpointConstants.ENDPOINT_ENABLEMENT_ROLES, name = ENDPOINT_ENABLEMENT_PROPERTY_ENABLED, matchIfMissing = true)
@PreAuthorize("hasAuthority('https://authorities.smartcosmos.net/roles/create')")
public class CreateRoleResource {

    private CreateRoleService service;

    @Autowired
    public CreateRoleResource(CreateRoleService service) { this.service = service; }

    @RequestMapping(value = RoleEndpointConstants.ENDPOINT_ROLES,
                    method = RequestMethod.POST,
                    produces = APPLICATION_JSON_UTF8_VALUE,
                    consumes = APPLICATION_JSON_UTF8_VALUE)
    @ConditionalOnProperty(prefix = RoleEndpointConstants.ENDPOINT_ENABLEMENT_ROLES_CREATE,
                           name = ENDPOINT_ENABLEMENT_PROPERTY_ENABLED,
                           matchIfMissing = true)
    public DeferredResult<ResponseEntity<?>> createRole(
        @RequestBody @Valid RoleRequest requestBody,
        SmartCosmosUser user) {

        DeferredResult<ResponseEntity<?>> response = new DeferredResult<>();
        service.create(response, requestBody, user);
        return response;
    }
}


