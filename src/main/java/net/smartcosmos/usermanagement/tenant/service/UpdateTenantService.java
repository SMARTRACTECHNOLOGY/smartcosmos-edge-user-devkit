package net.smartcosmos.usermanagement.tenant.service;

import org.springframework.http.ResponseEntity;
import org.springframework.web.context.request.async.DeferredResult;

import net.smartcosmos.security.user.SmartCosmosUser;
import net.smartcosmos.usermanagement.tenant.dto.RestUpdateTenantRequest;

public interface UpdateTenantService {

    void update(DeferredResult<ResponseEntity<?>> response, String tenantUrn, RestUpdateTenantRequest updateTenantRequest, SmartCosmosUser user);
}
