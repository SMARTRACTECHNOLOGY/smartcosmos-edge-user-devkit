package net.smartcosmos.usermanagement.tenant.service;

import org.springframework.http.ResponseEntity;
import org.springframework.web.context.request.async.DeferredResult;

import net.smartcosmos.security.user.SmartCosmosUser;
import net.smartcosmos.usermanagement.tenant.dto.UpdateTenantRequest;

/**
 * Methods to update Tenants.
 */
public interface UpdateTenantService {

    /**
     * Updates the Tenant defined by its URN.
     *
     * @param response the DeferredResult containing a response, if there is one
     * @param tenantUrn the URN of the Tenant to update
     * @param updateTenantRequest the request object
     * @param user the current logged in user
     */
    void update(DeferredResult<ResponseEntity<?>> response, String tenantUrn, UpdateTenantRequest updateTenantRequest, SmartCosmosUser user);
}
