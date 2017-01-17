package net.smartcosmos.usermanagement.tenant.service;

import org.springframework.http.ResponseEntity;
import org.springframework.web.context.request.async.DeferredResult;

import net.smartcosmos.security.user.SmartCosmosUser;
import net.smartcosmos.usermanagement.tenant.dto.CreateTenantRequest;

/**
 * Methods for creating Tenants.
 */
public interface CreateTenantService {

    /**
     * @param response the DeferredResult containing a response, if there is one
     * @param createTenantRequest the Tenant to create
     * @param user the current logged in user (may be ANONYMOUS)
     */
    void create(DeferredResult<ResponseEntity<?>> response, CreateTenantRequest createTenantRequest, SmartCosmosUser user);
}
