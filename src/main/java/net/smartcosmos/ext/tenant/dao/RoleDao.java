package net.smartcosmos.ext.tenant.dao;

import java.util.Optional;
import javax.validation.ConstraintViolationException;

import net.smartcosmos.ext.tenant.dto.CreateOrUpdateRoleRequest;
import net.smartcosmos.ext.tenant.dto.CreateOrUpdateRoleResponse;
import net.smartcosmos.ext.tenant.dto.GetRoleResponse;

/**
 * Initially created by SMART COSMOS Team on June 30, 2016.
 */
public interface RoleDao {

    /**
     * Create a new role with authorities in the given tenant.
     *
     * @param tenantUrn tenant
     * @param createRoleRequest role definition
     * @return the new role or Optional.empty() in case of failure
     * @throws ConstraintViolationException
     */
    Optional<CreateOrUpdateRoleResponse> createRole(
            String tenantUrn,
            CreateOrUpdateRoleRequest createRoleRequest)
        throws ConstraintViolationException;

    /**
     * Update a role identified by its URN with authorities in the given tenant.
     *
     * @param tenantUrn tenant
     * @param urn URN of role to be updated
     * @param updateRoleRequest changed properties of role
     * @return the updated role or Optional.empty() in case of failure
     * @throws ConstraintViolationException
     */
    Optional<CreateOrUpdateRoleResponse> updateRole(
            String tenantUrn,
            String urn,
            CreateOrUpdateRoleRequest updateRoleRequest)
        throws ConstraintViolationException;

    /**
     * Find a role identified by their name in the given tenant.
     *
     * @param tenantUrn tenant
     * @param name name of role
     * @return the role or Optional.empty() in case empty search result
     * @throws ConstraintViolationException
     */
    Optional<GetRoleResponse> findByNameAndTenantUrn(String tenantUrn, String name)
        throws ConstraintViolationException;

}
