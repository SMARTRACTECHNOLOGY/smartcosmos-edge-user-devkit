package net.smartcosmos.extension.tenant.rest.service;

import java.util.Optional;
import javax.inject.Inject;

import org.springframework.core.convert.ConversionService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import net.smartcosmos.events.SmartCosmosEventTemplate;
import net.smartcosmos.extension.tenant.dao.RoleDao;
import net.smartcosmos.extension.tenant.dao.TenantDao;
import net.smartcosmos.extension.tenant.dto.GetAuthoritiesResponse;
import net.smartcosmos.extension.tenant.rest.dto.RestLoginRequest;
import net.smartcosmos.extension.tenant.rest.dto.RestLoginResponse;
import net.smartcosmos.security.user.SmartCosmosUser;

@Service
public class LoginService extends AbstractTenantService {

    @Inject
    public LoginService(
        TenantDao tenantDao,
        RoleDao roleDao,
        SmartCosmosEventTemplate smartCosmosEventTemplate,
        ConversionService conversionService) {

        super(tenantDao, roleDao, smartCosmosEventTemplate, conversionService);
    }

    public ResponseEntity<?> validateCredentials(RestLoginRequest credentials, SmartCosmosUser user) {

        Optional<GetAuthoritiesResponse> entity = tenantDao.getAuthorities(credentials.getUsername(), credentials.getPassword());
        if (entity.isPresent()) {

            return ResponseEntity.ok(conversionService.convert(entity.get(), RestLoginResponse.class));
        }

        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
    }
}
