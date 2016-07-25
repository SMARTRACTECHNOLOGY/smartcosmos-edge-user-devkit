package net.smartcosmos.extension.tenant.rest.service;

import lombok.extern.slf4j.Slf4j;

import org.springframework.core.convert.ConversionService;
import org.springframework.core.convert.TypeDescriptor;
import org.springframework.http.ResponseEntity;

import net.smartcosmos.events.SmartCosmosEventException;
import net.smartcosmos.events.SmartCosmosEventTemplate;
import net.smartcosmos.extension.tenant.dao.RoleDao;
import net.smartcosmos.extension.tenant.dao.TenantDao;
import net.smartcosmos.extension.tenant.rest.dto.MessageDto;
import net.smartcosmos.security.user.SmartCosmosUser;

import java.util.List;

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
            if (user != null) {
                smartCosmosEventTemplate.sendEvent(entity, eventType, user);
            } else {
                smartCosmosEventTemplate.sendEvent(entity, eventType);
            }
        } catch (SmartCosmosEventException e) {
            log.error(e.getMessage());
            log.debug(e.getMessage(), e);
        }
    }

    /**
     * Uses the conversion service to convert a typed list into another typed list.
     *
     * @param list the list
     * @param sourceClass the class of the source type
     * @param targetClass the class of the target type
     * @param <S> the generic source type
     * @param <T> the generic target type
     * @return the converted typed list
     */
    @SuppressWarnings("unchecked")
    protected <S, T> List<T> convertList(List<S> list, Class sourceClass, Class targetClass) {

        TypeDescriptor sourceDescriptor = TypeDescriptor.collection(List.class, TypeDescriptor.valueOf(sourceClass));
        TypeDescriptor targetDescriptor = TypeDescriptor.collection(List.class, TypeDescriptor.valueOf(targetClass));

        return (List<T>) conversionService.convert(list, sourceDescriptor, targetDescriptor);
    }

    protected ResponseEntity<?> buildBadRequestResponse(String responseMessage, int code) {
        return ResponseEntity.badRequest()
            .body(MessageDto.builder()
                      .code(code)
                      .message(responseMessage)
                      .build());
    }
}
