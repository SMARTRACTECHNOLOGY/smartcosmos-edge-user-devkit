package net.smartcosmos.extension.tenant.rest.resource.role;

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
import net.smartcosmos.extension.tenant.rest.dto.role.RestCreateOrUpdateRoleRequest;
import net.smartcosmos.extension.tenant.rest.service.role.CreateRoleService;
import net.smartcosmos.security.user.SmartCosmosUser;

import static org.springframework.http.MediaType.APPLICATION_JSON_UTF8_VALUE;

import static net.smartcosmos.extension.tenant.rest.resource.BasicEndpointConstants.ENDPOINT_ENABLEMENT_PROPERTY_ENABLED;
import static net.smartcosmos.extension.tenant.rest.resource.role.RoleEndpointConstants.ENDPOINT_ENABLEMENT_ROLES;
import static net.smartcosmos.extension.tenant.rest.resource.role.RoleEndpointConstants.ENDPOINT_ENABLEMENT_ROLES_CREATE;
import static net.smartcosmos.extension.tenant.rest.resource.role.RoleEndpointConstants.ENDPOINT_ROLES;

/**
 * Initially created by SMART COSMOS Team on July 01, 2016.
 */
@SmartCosmosRdao
@Slf4j
@ConditionalOnProperty(prefix = ENDPOINT_ENABLEMENT_ROLES, name = ENDPOINT_ENABLEMENT_PROPERTY_ENABLED, matchIfMissing = true)
//@Api
public class CreateRoleResource {

    private CreateRoleService service;

    @Autowired
    public CreateRoleResource(CreateRoleService service) { this.service = service; }

    @RequestMapping(value = ENDPOINT_ROLES,
                    method = RequestMethod.POST,
                    produces = APPLICATION_JSON_UTF8_VALUE,
                    consumes = APPLICATION_JSON_UTF8_VALUE)
    @ConditionalOnProperty(prefix = ENDPOINT_ENABLEMENT_ROLES_CREATE, name = ENDPOINT_ENABLEMENT_PROPERTY_ENABLED, matchIfMissing = true)
    public DeferredResult<ResponseEntity> createRole(
        @RequestBody @Valid RestCreateOrUpdateRoleRequest requestBody,
        SmartCosmosUser user) {

        return service.create(requestBody, user);
    }
}


