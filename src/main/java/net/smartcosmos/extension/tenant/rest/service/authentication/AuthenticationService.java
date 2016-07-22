package net.smartcosmos.extension.tenant.rest.service.authentication;

import java.util.Optional;
import javax.inject.Inject;

import org.springframework.core.convert.ConversionService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import net.smartcosmos.events.SmartCosmosEventTemplate;
import net.smartcosmos.extension.tenant.dao.RoleDao;
import net.smartcosmos.extension.tenant.dao.TenantDao;
import net.smartcosmos.extension.tenant.dto.authentication.GetAuthoritiesResponse;
import net.smartcosmos.extension.tenant.rest.dto.authentication.RestAuthenticateRequest;
import net.smartcosmos.extension.tenant.rest.dto.authentication.RestAuthenticateResponse;
import net.smartcosmos.extension.tenant.rest.service.AbstractTenantService;

@Service
public class AuthenticationService extends AbstractTenantService {

    @Inject
    public AuthenticationService(
        TenantDao tenantDao,
        RoleDao roleDao,
        SmartCosmosEventTemplate smartCosmosEventTemplate,
        ConversionService conversionService) {

        super(tenantDao, roleDao, smartCosmosEventTemplate, conversionService);
    }

    public ResponseEntity<?> authenticate(RestAuthenticateRequest authenticate) {

        Optional<GetAuthoritiesResponse> entity = tenantDao.getAuthorities(authenticate.getName(), authenticate.getCredentials());
        if (entity.isPresent()) {

            return ResponseEntity.ok(conversionService.convert(entity.get(), RestAuthenticateResponse.class));
        }

        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
    }
}
