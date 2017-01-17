package net.smartcosmos.usermanagement.tenant.service;

import org.springframework.http.ResponseEntity;

import net.smartcosmos.security.user.SmartCosmosUser;

/**
 * Methods for reading Tenants.
 */
public interface ReadTenantService {

    /**
     * Finds a Tenant by its URN.
     *
     * @param urn the URN of the Tenant to return
     * @param user the current logged in user
     * @return the response containing the requested Role if existent. Otherwise an appropriate error response is returned.
     */
    ResponseEntity<?> findByUrn(String urn, SmartCosmosUser user);

    /**
     * Finds a Tenant by its Name.
     *
     * @param name the name of the Tenant to return
     * @param user the current logged in user
     * @return the response containing the requested Tenant if existent. Otherwise an appropriate error response is returned.
     */
    ResponseEntity<?> findByName(String name, SmartCosmosUser user);

    /**
     * Queries Tenants by Name. If no name is provided all existing Tenants will be returned.
     *
     * @param name the name of the Tenant to return (optional)
     * @param user the current logged in user
     * @return the response containing the Tenant(s), or an appropriate error response.
     */
    ResponseEntity<?> query(String name, SmartCosmosUser user);

    /**
     * Finds all Tenants.
     *
     * @param user the current logged in user.
     * @return the response containing all Tenants.
     */
    ResponseEntity<?> findAll(SmartCosmosUser user);
}
