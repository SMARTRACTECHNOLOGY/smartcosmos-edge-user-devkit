package net.smartcosmos.usermanagement.role.service;

import org.springframework.http.ResponseEntity;
import org.springframework.web.context.request.async.DeferredResult;

import net.smartcosmos.security.user.SmartCosmosUser;

/**
 * Methods for deleting Roles.
 */
public interface DeleteRoleService {

    /**
     * @param response the DeferredResult containing a response, if there is one
     * @param roleUrn the URN of the Role to delete
     * @param user the current logged in user
     */
    void delete(DeferredResult<ResponseEntity<?>> response, String roleUrn, SmartCosmosUser user);
}
