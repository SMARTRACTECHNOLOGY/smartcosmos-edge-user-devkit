package net.smartcosmos.extension.tenant.dao;

import java.util.List;
import java.util.Optional;

import javax.validation.ConstraintViolationException;

import net.smartcosmos.extension.tenant.dto.tenant.CreateTenantRequest;
import net.smartcosmos.extension.tenant.dto.tenant.CreateTenantResponse;
import net.smartcosmos.extension.tenant.dto.tenant.TenantResponse;
import net.smartcosmos.extension.tenant.dto.tenant.UpdateTenantRequest;
import net.smartcosmos.extension.tenant.dto.user.CreateOrUpdateUserRequest;
import net.smartcosmos.extension.tenant.dto.user.CreateUserResponse;
import net.smartcosmos.extension.tenant.dto.user.UserResponse;

/**
 * Initially created by SMART COSMOS Team on June 30, 2016.
 */
public interface TenantDao {

    String[] DEFAULT_ADMIN_AUTHORITIES = { "https://authorities.smartcosmos.net/things/create", "https://authorities.smartcosmos.net/things/read",
            "https://authorities.smartcosmos.net/things/update", "https://authorities.smartcosmos.net/things/delete",
            "https://authorities.smartcosmos.net/metadata/create", "https://authorities.smartcosmos.net/metadata/read",
            "https://authorities.smartcosmos.net/metadata/update", "https://authorities.smartcosmos.net/metadata/delete",
            "https://authorities.smartcosmos.net/relationships/create", "https://authorities.smartcosmos.net/relationships/read",
            "https://authorities.smartcosmos.net/relationships/delete" };

    String[] DEFAULT_USER_AUTHORITIES = { "https://authorities.smartcosmos.net/things/read", "https://authorities.smartcosmos.net/metadata/read",
            "https://authorities.smartcosmos.net/relationships/read", };

    Optional<CreateTenantResponse> createTenant(CreateTenantRequest tenantCreate) throws ConstraintViolationException;

    Optional<TenantResponse> updateTenant(String tenantUrn, UpdateTenantRequest tenantUpdate) throws ConstraintViolationException;

    Optional<TenantResponse> findTenantByUrn(String tenantUrn);

    Optional<TenantResponse> findTenantByName(String name);

    Optional<CreateUserResponse> createUser(String tenantUrn, CreateOrUpdateUserRequest userCreate) throws ConstraintViolationException;

    Optional<UserResponse> updateUser(String tenantUrn, String userUrn, CreateOrUpdateUserRequest userUpdate) throws ConstraintViolationException;

    Optional<UserResponse> findUserByUrn(String tenantUrn, String userUrn);

    Optional<UserResponse> findUserByName(String tenantUrn, String name);

    Optional<UserResponse> deleteUserByUrn(String tenantUrn, String urn);

    List<TenantResponse> findAllTenants();

    List<UserResponse> findAllUsers(String tenantUrn);
}
