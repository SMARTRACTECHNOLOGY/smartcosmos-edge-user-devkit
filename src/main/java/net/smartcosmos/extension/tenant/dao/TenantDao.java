package net.smartcosmos.extension.tenant.dao;

import java.util.Optional;

import javax.validation.ConstraintViolationException;

import net.smartcosmos.extension.tenant.dto.user.CreateOrUpdateUserResponse;
import net.smartcosmos.extension.tenant.dto.tenant.CreateTenantRequest;
import net.smartcosmos.extension.tenant.dto.tenant.CreateTenantResponse;
import net.smartcosmos.extension.tenant.dto.user.CreateUserRequest;
import net.smartcosmos.extension.tenant.dto.authentication.GetAuthoritiesResponse;
import net.smartcosmos.extension.tenant.dto.user.GetOrDeleteUserResponse;
import net.smartcosmos.extension.tenant.dto.tenant.GetTenantResponse;
import net.smartcosmos.extension.tenant.dto.tenant.UpdateTenantRequest;
import net.smartcosmos.extension.tenant.dto.tenant.UpdateTenantResponse;
import net.smartcosmos.extension.tenant.dto.user.UpdateUserRequest;

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
