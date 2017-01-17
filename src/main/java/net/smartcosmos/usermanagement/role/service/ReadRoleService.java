package net.smartcosmos.usermanagement.role.service;

import org.springframework.http.ResponseEntity;

import net.smartcosmos.security.user.SmartCosmosUser;

/**
 * Methods for reading Roles.
 */
public interface ReadRoleService {

    /**
     * Finds a Role by its URN.
     *
     * @param urn the URN of the Role to return
     * @param user the current logged in user
     * @return the response containing the requested Role it it exists, otherwise an appropriate error response is returned.
     */
    ResponseEntity<?> findByUrn(String urn, SmartCosmosUser user);

    /**
     * Finds a Role by its Name.
     *
     * @param name the name of the Role to return
     * @param user the current logged in user
     * @return the response containing the requested Role it it exists, otherwise an appropriate error response is returned.
     */
    ResponseEntity<?> findByName(String name, SmartCosmosUser user);

    /**
     * Queries Roles by Name. If no name is provided all existing Roles will be returned.
     *
     * @param name the name of the Role to return (optional)
     * @param user the current logged in user
     * @return the response containing the Role(s), or an appropriate error response.
     */
    ResponseEntity<?> query(String name, SmartCosmosUser user);

    /**
     * Finds all Roles in the realm of the current user.
     *
     * @param user the current logged in user
     * @return the response containing all Roles in the user's tenant.
     */
    ResponseEntity<?> findAll(SmartCosmosUser user);
}
