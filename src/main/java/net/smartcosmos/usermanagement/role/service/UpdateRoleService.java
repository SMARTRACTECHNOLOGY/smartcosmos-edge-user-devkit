package net.smartcosmos.usermanagement.role.service;

import org.springframework.http.ResponseEntity;
import org.springframework.web.context.request.async.DeferredResult;

import net.smartcosmos.security.user.SmartCosmosUser;
import net.smartcosmos.usermanagement.role.dto.RoleRequest;

public interface UpdateRoleService {

    /**
     * Updates the Role defined by its URN.
     *
     * @param response the DeferredResult containing a response, if there is one
     * @param roleUrn the URN of the Role to update
     * @param updateRoleRequest the request object
     * @param user the current logged in user
     */
    void update(
        DeferredResult<ResponseEntity<?>> response,
        String roleUrn,
        RoleRequest updateRoleRequest,
        SmartCosmosUser user);
}
