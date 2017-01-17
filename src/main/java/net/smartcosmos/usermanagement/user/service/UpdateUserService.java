package net.smartcosmos.usermanagement.user.service;

import org.springframework.http.ResponseEntity;
import org.springframework.web.context.request.async.DeferredResult;

import net.smartcosmos.security.user.SmartCosmosUser;
import net.smartcosmos.usermanagement.user.dto.UpdateUserRequest;

/**
 * Methods to update Users.
 */
public interface UpdateUserService {

    /**
     * Updates the User defined by its URN.
     *
     * @param response the DeferredResult containing a response, if there is one
     * @param userUrn the URN of the User to update
     * @param userRequest the request object
     * @param user the current logged in user
     */
    void update(DeferredResult<ResponseEntity<?>> response, String userUrn, UpdateUserRequest userRequest, SmartCosmosUser user);
}
