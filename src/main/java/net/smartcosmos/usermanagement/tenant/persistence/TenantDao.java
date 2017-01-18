package net.smartcosmos.usermanagement.tenant.persistence;

import java.util.List;
import java.util.Optional;
import javax.validation.ConstraintViolationException;

import net.smartcosmos.usermanagement.tenant.dto.CreateTenantRequest;
import net.smartcosmos.usermanagement.tenant.dto.CreateTenantResponse;
import net.smartcosmos.usermanagement.tenant.dto.TenantResponse;
import net.smartcosmos.usermanagement.tenant.dto.UpdateTenantRequest;
import net.smartcosmos.usermanagement.user.dto.CreateUserRequest;
import net.smartcosmos.usermanagement.user.dto.CreateUserResponse;
import net.smartcosmos.usermanagement.user.dto.UpdateUserRequest;
import net.smartcosmos.usermanagement.user.dto.UserResponse;

/**
 * Interface defining methods used when dealing with Tenant datastore information.
 */
public interface TenantDao {

    String[] DEFAULT_ADMIN_AUTHORITIES = {
        "https://authorities.smartcosmos.net/things/create",
        "https://authorities.smartcosmos.net/things/read",
        "https://authorities.smartcosmos.net/things/update",
        "https://authorities.smartcosmos.net/things/delete",
        "https://authorities.smartcosmos.net/metadata/create",
        "https://authorities.smartcosmos.net/metadata/read",
        "https://authorities.smartcosmos.net/metadata/update",
        "https://authorities.smartcosmos.net/metadata/delete",
        "https://authorities.smartcosmos.net/relationships/create",
        "https://authorities.smartcosmos.net/relationships/read",
        "https://authorities.smartcosmos.net/relationships/delete",
        "https://authorities.smartcosmos.net/tenants/read",
        "https://authorities.smartcosmos.net/tenants/update",
        "https://authorities.smartcosmos.net/users/create",
        "https://authorities.smartcosmos.net/users/read",
        "https://authorities.smartcosmos.net/users/update",
        "https://authorities.smartcosmos.net/users/delete",
        "https://authorities.smartcosmos.net/roles/create",
        "https://authorities.smartcosmos.net/roles/read",
        "https://authorities.smartcosmos.net/roles/update",
        "https://authorities.smartcosmos.net/roles/delete"
    };

    String[] DEFAULT_USER_AUTHORITIES = {
        "https://authorities.smartcosmos.net/things/read",
        "https://authorities.smartcosmos.net/metadata/read",
        "https://authorities.smartcosmos.net/relationships/read",
    };

    Optional<CreateTenantResponse> createTenant(CreateTenantRequest tenantCreate) throws ConstraintViolationException;

    Optional<TenantResponse> updateTenant(String tenantUrn, UpdateTenantRequest tenantUpdate) throws ConstraintViolationException;

    Optional<TenantResponse> findTenantByUrn(String tenantUrn);

    Optional<TenantResponse> findTenantByName(String name);

    Optional<CreateUserResponse> createUser(String tenantUrn, CreateUserRequest userCreate) throws ConstraintViolationException;

    Optional<UserResponse> updateUser(String tenantUrn, String userUrn, UpdateUserRequest userUpdate) throws ConstraintViolationException;

    Optional<UserResponse> findUserByUrn(String tenantUrn, String userUrn);

    Optional<UserResponse> findUserByName(String tenantUrn, String name);

    Optional<UserResponse> deleteUserByUrn(String tenantUrn, String urn);

    List<TenantResponse> findAllTenants();

    List<UserResponse> findAllUsers(String tenantUrn);
}
