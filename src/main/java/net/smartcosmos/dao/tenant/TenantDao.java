package net.smartcosmos.dao.tenant;

import java.util.Optional;
import javax.validation.ConstraintViolationException;

import net.smartcosmos.dto.tenant.CreateTenantRequest;
import net.smartcosmos.dto.tenant.CreateTenantResponse;
import net.smartcosmos.dto.tenant.CreateUserRequest;
import net.smartcosmos.dto.tenant.CreateUserResponse;

/**
 * Initially created by SMART COSMOS Team on June 30, 2016.
 */
public interface TenantDao {

    Optional<CreateTenantResponse> createTenant(CreateTenantRequest tenantCreate) throws ConstraintViolationException;

    Optional<CreateUserResponse> createUser(CreateUserRequest tenantCreate) throws ConstraintViolationException;


}
