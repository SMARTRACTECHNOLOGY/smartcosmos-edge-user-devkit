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

    Optional<CreateOrUpdateRoleResponse> createRole(String tenantUrn, CreateOrUpdateRoleRequest createRoleRequest)
        throws ConstraintViolationException;

    Optional<CreateOrUpdateRoleResponse> updateRole(String tenantUrn, CreateOrUpdateRoleRequest updateRoleRequest)
        throws ConstraintViolationException;

    Optional<GetRoleResponse> findByNameAndTenantUrn(String tenantUrn, String name)
        throws ConstraintViolationException;

}
