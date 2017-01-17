package net.smartcosmos.usermanagement.user.service;

import org.springframework.http.ResponseEntity;
import org.springframework.web.context.request.async.DeferredResult;

import net.smartcosmos.security.user.SmartCosmosUser;

/**
 * Methods to delete Users.
 */
public interface DeleteUserService {

    /**
     * Deletes a User by its URN.
     *
     * @param response the DeferredResult containing a response, if there is one
     * @param userUrn the URN of the User to delete
     * @param user the current logged in user
     */
    void delete(DeferredResult<ResponseEntity<?>> response, String userUrn, SmartCosmosUser user);
}
