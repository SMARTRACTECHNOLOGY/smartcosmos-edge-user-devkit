package net.smartcosmos.extension.tenant.rest.service.tenant;

import java.util.List;
import java.util.Optional;

import lombok.extern.slf4j.Slf4j;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.convert.ConversionService;
import org.springframework.core.convert.TypeDescriptor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import net.smartcosmos.extension.tenant.dao.TenantDao;
import net.smartcosmos.extension.tenant.dto.tenant.TenantResponse;
import net.smartcosmos.extension.tenant.rest.dto.tenant.RestTenantResponse;
import net.smartcosmos.extension.tenant.rest.service.EventSendingService;
import net.smartcosmos.security.user.SmartCosmosUser;

import static net.smartcosmos.extension.tenant.rest.utility.TenantEventType.TENANT_NOT_FOUND;
import static net.smartcosmos.extension.tenant.rest.utility.TenantEventType.TENANT_READ;

@Slf4j
@Service
public class ReadTenantService {

    private final TenantDao tenantDao;
    private final EventSendingService eventSendingService;
    private final ConversionService conversionService;

    @Autowired
    public ReadTenantService(TenantDao tenantDao, EventSendingService tenantEventSendingService, ConversionService conversionService) {

        this.tenantDao = tenantDao;
        this.eventSendingService = tenantEventSendingService;
        this.conversionService = conversionService;
    }

    public ResponseEntity<?> findByUrn(String urn, SmartCosmosUser user) {

        Optional<TenantResponse> entity = tenantDao.findTenantByUrn(urn);

        if (entity.isPresent()) {
            eventSendingService.sendEvent(user, TENANT_READ, entity.get());
            return ResponseEntity
                .ok()
                .body(conversionService.convert(entity.get(), RestTenantResponse.class));
        }

        eventSendingService.sendEvent(user, TENANT_NOT_FOUND, urn);
        return ResponseEntity.notFound()
            .build();
    }

    public ResponseEntity<?> query(String name, SmartCosmosUser user) {

        if (StringUtils.isBlank(name)) {
            return findAll(user);
        } else {
            return findByName(name, user);
        }
    }

    public ResponseEntity<?> findAll(SmartCosmosUser user) {

        List<TenantResponse> tenantList = tenantDao.findAllTenants();
        for (TenantResponse tenant : tenantList) {
            eventSendingService.sendEvent(user, TENANT_READ, tenant);
        }

        return ResponseEntity
            .ok()
            .body(convertList(tenantList, TenantResponse.class, RestTenantResponse.class));
    }

    public ResponseEntity<?> findByName(String name, SmartCosmosUser user) {

        Optional<TenantResponse> entity = tenantDao.findTenantByName(name);

        if (entity.isPresent()) {
            eventSendingService.sendEvent(user, TENANT_READ, entity.get());
            return ResponseEntity
                .ok()
                .body(conversionService.convert(entity.get(), RestTenantResponse.class));
        }

        eventSendingService.sendEvent(user, TENANT_NOT_FOUND, name);
        return ResponseEntity.notFound()
            .build();
    }

    private <S, T> List<T> convertList(List<S> list, Class sourceClass, Class targetClass) {

        TypeDescriptor sourceDescriptor = TypeDescriptor.collection(List.class, TypeDescriptor.valueOf(sourceClass));
        TypeDescriptor targetDescriptor = TypeDescriptor.collection(List.class, TypeDescriptor.valueOf(targetClass));

        return (List<T>) conversionService.convert(list, sourceDescriptor, targetDescriptor);
    }
}
