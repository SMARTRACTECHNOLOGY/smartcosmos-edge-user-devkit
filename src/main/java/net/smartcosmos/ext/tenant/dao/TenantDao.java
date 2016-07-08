package net.smartcosmos.ext.tenant.dao;

import java.util.Optional;
import javax.validation.ConstraintViolationException;

import net.smartcosmos.ext.tenant.dto.CreateTenantRequest;
import net.smartcosmos.ext.tenant.dto.CreateTenantResponse;
import net.smartcosmos.ext.tenant.dto.CreateUserRequest;
import net.smartcosmos.ext.tenant.dto.CreateUserResponse;
import net.smartcosmos.ext.tenant.dto.GetTenantResponse;
import net.smartcosmos.ext.tenant.dto.GetUserResponse;

/**
 * Initially created by SMART COSMOS Team on June 30, 2016.
 */
public interface TenantDao {

    Optional<CreateTenantResponse> createTenant(CreateTenantRequest tenantCreate) throws ConstraintViolationException;

    Optional<GetTenantResponse> findTenantByUrn(String tenantUrn);

    Optional<GetTenantResponse> findTenantByName(String name);

    Optional<CreateUserResponse> createUser(CreateUserRequest tenantCreate) throws ConstraintViolationException;

    Optional<GetUserResponse> findUserByUrn(String userUrn);

    Optional<GetUserResponse> findUserByName(String name);

}
