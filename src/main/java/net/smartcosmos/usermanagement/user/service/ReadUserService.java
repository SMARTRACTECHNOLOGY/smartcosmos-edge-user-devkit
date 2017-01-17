package net.smartcosmos.usermanagement.user.service;

import org.springframework.http.ResponseEntity;

import net.smartcosmos.security.user.SmartCosmosUser;

/**
 * Methods to read Users.
 */
public interface ReadUserService {

    /**
     * Finds a User by its URN.
     *
     * @param urn the URN of the User to return
     * @param user the current logged in user
     * @return the response containing the requested User it it exists, otherwise an appropriate error response is returned.
     */
    ResponseEntity<?> findByUrn(String urn, SmartCosmosUser user);

    /**
     * Finds a User by its Name.
     *
     * @param name the name of the User to return
     * @param user the current logged in user
     * @return the response containing the requested User it it exists, otherwise an appropriate error response is returned.
     */
    ResponseEntity<?> findByName(String name, SmartCosmosUser user);

    /**
     * Queries Users by Name. If no name is provided all existing Users will be returned.
     *
     * @param name the name of the User to return (optional)
     * @param user the current logged in user
     * @return the response containing the User(s), or an appropriate error response.
     */
    ResponseEntity<?> query(String name, SmartCosmosUser user);

    /**
     * Finds all Users in the realm of the current user.
     *
     * @param user the current logged in user
     * @return the response containing all Users in the user's tenant.
     */
    ResponseEntity<?> findAll(SmartCosmosUser user);
}
