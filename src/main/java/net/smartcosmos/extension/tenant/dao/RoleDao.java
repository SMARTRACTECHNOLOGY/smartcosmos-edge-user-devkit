package net.smartcosmos.extension.tenant.dao;

import java.util.Optional;
import javax.validation.ConstraintViolationException;

import net.smartcosmos.extension.tenant.dto.CreateOrUpdateRoleRequest;
import net.smartcosmos.extension.tenant.dto.CreateOrUpdateRoleResponse;
import net.smartcosmos.extension.tenant.dto.GetRoleResponse;
import net.smartcosmos.extension.tenant.dto.CreateOrUpdateRoleRequest;
import net.smartcosmos.extension.tenant.dto.CreateOrUpdateRoleResponse;
import net.smartcosmos.extension.tenant.dto.GetRoleResponse;

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
