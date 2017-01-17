package net.smartcosmos.usermanagement.role.service;

import org.springframework.http.ResponseEntity;
import org.springframework.web.context.request.async.DeferredResult;

import net.smartcosmos.security.user.SmartCosmosUser;
import net.smartcosmos.usermanagement.role.dto.RestCreateOrUpdateRoleRequest;

/**
 * Methods for creating Roles.
 */
public interface CreateRoleService {

    /**
     * Creates a Role based on a provided request object.
     *
     * @param response the DeferredResult containing a response, if there is one
     * @param createRoleRequest the Role to create
     * @param user the current logged in user
     */
    void create(DeferredResult<ResponseEntity<?>> response, RestCreateOrUpdateRoleRequest createRoleRequest, SmartCosmosUser user);
}
