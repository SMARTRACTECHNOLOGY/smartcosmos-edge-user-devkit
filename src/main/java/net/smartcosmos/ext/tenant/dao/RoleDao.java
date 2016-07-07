package net.smartcosmos.ext.tenant.dao;

import java.util.Optional;
import javax.validation.ConstraintViolationException;

import net.smartcosmos.ext.tenant.dto.CreateRoleRequest;
import net.smartcosmos.ext.tenant.dto.CreateRoleResponse;
import net.smartcosmos.ext.tenant.dto.GetRoleResponse;
import net.smartcosmos.ext.tenant.dto.UpdateRoleRequest;
import net.smartcosmos.ext.tenant.dto.UpdateRoleResponse;

/**
 * Initially created by SMART COSMOS Team on June 30, 2016.
 */
public interface RoleDao {

    Optional<CreateRoleResponse> createRole(String tenantUrn, CreateRoleRequest createRoleRequest) throws ConstraintViolationException;

    Optional<UpdateRoleResponse> updateRole(String tenantUrn, UpdateRoleRequest updateRoleRequest) throws ConstraintViolationException;

    Optional<GetRoleResponse> findByNameAndTenantUrn(String tenantUrn, String name) throws ConstraintViolationException;

}
