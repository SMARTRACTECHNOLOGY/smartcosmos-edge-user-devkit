package net.smartcosmos.ext.tenant.dao;

import java.util.Optional;
import javax.validation.ConstraintViolationException;

import net.smartcosmos.ext.tenant.dto.CreateOrUpdateUserResponse;
import net.smartcosmos.ext.tenant.dto.CreateTenantRequest;
import net.smartcosmos.ext.tenant.dto.CreateTenantResponse;
import net.smartcosmos.ext.tenant.dto.CreateUserRequest;
import net.smartcosmos.ext.tenant.dto.GetTenantResponse;
import net.smartcosmos.ext.tenant.dto.GetUserResponse;
import net.smartcosmos.ext.tenant.dto.UpdateTenantRequest;
import net.smartcosmos.ext.tenant.dto.UpdateTenantResponse;
import net.smartcosmos.ext.tenant.dto.UpdateUserRequest;

/**
 * Initially created by SMART COSMOS Team on June 30, 2016.
 */
public interface TenantDao {

    Optional<CreateTenantResponse> createTenant(CreateTenantRequest tenantCreate) throws ConstraintViolationException;

    Optional<UpdateTenantResponse> updateTenant(UpdateTenantRequest tenantUpdate) throws ConstraintViolationException;

    Optional<GetTenantResponse> findTenantByUrn(String tenantUrn);

    Optional<GetTenantResponse> findTenantByName(String name);

    Optional<CreateOrUpdateUserResponse> createUser(CreateUserRequest userCreate) throws ConstraintViolationException;

    Optional<CreateOrUpdateUserResponse> updateUser(UpdateUserRequest userUpdate) throws ConstraintViolationException;

    Optional<GetUserResponse> findUserByUrn(String userUrn);

    Optional<GetUserResponse> findUserByName(String name);

}
