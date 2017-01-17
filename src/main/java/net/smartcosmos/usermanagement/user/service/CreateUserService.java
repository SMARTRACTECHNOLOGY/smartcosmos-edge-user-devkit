package net.smartcosmos.usermanagement.user.service;

import org.springframework.http.ResponseEntity;
import org.springframework.web.context.request.async.DeferredResult;

import net.smartcosmos.security.user.SmartCosmosUser;
import net.smartcosmos.usermanagement.user.dto.CreateUserRequest;

/**
 * Methods to create Users.
 */
public interface CreateUserService {

    /**
     * Creates a new User.
     *
     * @param response the DeferredResult containing a response, if there is one
     * @param createUserRequest the user creation request
     * @param user the current logged in user
     */
    void create(DeferredResult<ResponseEntity<?>> response, CreateUserRequest createUserRequest, SmartCosmosUser user);
}
