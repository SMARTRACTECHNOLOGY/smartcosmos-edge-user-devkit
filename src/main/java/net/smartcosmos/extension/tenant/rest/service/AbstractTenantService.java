package net.smartcosmos.extension.tenant.rest.service;

import lombok.extern.slf4j.Slf4j;

import org.springframework.core.convert.ConversionService;
import org.springframework.http.ResponseEntity;

import net.smartcosmos.events.SmartCosmosEventException;
import net.smartcosmos.events.SmartCosmosEventTemplate;
import net.smartcosmos.extension.tenant.dao.RoleDao;
import net.smartcosmos.extension.tenant.dao.TenantDao;
import net.smartcosmos.extension.tenant.rest.dto.MessageDto;
import net.smartcosmos.security.user.SmartCosmosUser;

@Slf4j
public class AbstractTenantService {

    protected final TenantDao tenantDao;

    protected final RoleDao roleDao;

    protected final SmartCosmosEventTemplate smartCosmosEventTemplate;

    protected final ConversionService conversionService;

    public AbstractTenantService(
        TenantDao tenantDao,
        RoleDao roleDao,
        SmartCosmosEventTemplate smartCosmosEventTemplate,
        ConversionService conversionService) {
        this.tenantDao = tenantDao;
        this.roleDao = roleDao;
        this.smartCosmosEventTemplate = smartCosmosEventTemplate;
        this.conversionService = conversionService;
    }

    protected void sendEvent(SmartCosmosUser user, String eventType, Object entity) {
        try {
            smartCosmosEventTemplate.sendEvent(entity, eventType, user);
        } catch (SmartCosmosEventException e) {
            log.error(e.getMessage());
            log.debug(e.getMessage(), e);
        }
    }

    public ResponseEntity<?> buildBadRequestResponse(String responseMessage, int code) {
        return ResponseEntity.badRequest()
            .body(MessageDto.builder()
                      .code(code)
                      .message(responseMessage)
                      .build());
    }
}
