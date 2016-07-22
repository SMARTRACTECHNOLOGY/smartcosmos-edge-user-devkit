package net.smartcosmos.extension.tenant.dao;

import java.util.Optional;

import javax.validation.ConstraintViolationException;

import net.smartcosmos.extension.tenant.dto.CreateOrUpdateUserResponse;
import net.smartcosmos.extension.tenant.dto.CreateTenantRequest;
import net.smartcosmos.extension.tenant.dto.CreateTenantResponse;
import net.smartcosmos.extension.tenant.dto.CreateUserRequest;
import net.smartcosmos.extension.tenant.dto.GetAuthoritiesResponse;
import net.smartcosmos.extension.tenant.dto.GetOrDeleteUserResponse;
import net.smartcosmos.extension.tenant.dto.GetTenantResponse;
import net.smartcosmos.extension.tenant.dto.UpdateTenantRequest;
import net.smartcosmos.extension.tenant.dto.UpdateTenantResponse;
import net.smartcosmos.extension.tenant.dto.UpdateUserRequest;

/**
 * Initially created by SMART COSMOS Team on June 30, 2016.
 */
public interface TenantDao {

    Optional<CreateTenantResponse> createTenant(CreateTenantRequest tenantCreate) throws ConstraintViolationException;

    Optional<UpdateTenantResponse> updateTenant(String tenantUrn, UpdateTenantRequest tenantUpdate) throws ConstraintViolationException;

    Optional<GetTenantResponse> findTenantByUrn(String tenantUrn);

    Optional<GetTenantResponse> findTenantByName(String name);

    Optional<CreateOrUpdateUserResponse> createUser(String tenantUrn, CreateUserRequest userCreate) throws ConstraintViolationException;

    Optional<CreateOrUpdateUserResponse> updateUser(String tenantUrn, String userUrn, UpdateUserRequest userUpdate)
        throws ConstraintViolationException;

    Optional<GetOrDeleteUserResponse> findUserByUrn(String tenantUrn, String userUrn);

    Optional<GetOrDeleteUserResponse> findUserByName(String tenantUrn, String name);

    Optional<GetOrDeleteUserResponse> deleteUserByUrn(String tenantUrn, String urn);

    Optional<GetAuthoritiesResponse> getAuthorities(String username, String password);

}
