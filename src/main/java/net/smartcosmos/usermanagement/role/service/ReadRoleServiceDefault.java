package net.smartcosmos.usermanagement.role.service;

import java.util.List;
import java.util.Optional;

import lombok.extern.slf4j.Slf4j;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.convert.ConversionService;
import org.springframework.core.convert.TypeDescriptor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import net.smartcosmos.security.user.SmartCosmosUser;
import net.smartcosmos.usermanagement.event.EventSendingService;
import net.smartcosmos.usermanagement.role.dto.RestRoleResponse;
import net.smartcosmos.usermanagement.role.dto.RoleResponse;
import net.smartcosmos.usermanagement.role.persistence.RoleDao;

import static net.smartcosmos.usermanagement.event.RoleEventType.ROLE_NOT_FOUND;
import static net.smartcosmos.usermanagement.event.RoleEventType.ROLE_READ;

@Slf4j
@Service
public class ReadRoleServiceDefault {

    private final RoleDao roleDao;
    private final EventSendingService eventSendingService;
    private final ConversionService conversionService;

    @Autowired
    public ReadRoleServiceDefault(RoleDao roleDao, EventSendingService roleEventSendingService, ConversionService conversionService) {

        this.roleDao = roleDao;
        this.eventSendingService = roleEventSendingService;
        this.conversionService = conversionService;
    }

    public ResponseEntity<?> findByUrn(String urn, SmartCosmosUser user) {

        Optional<RoleResponse> entity = roleDao.findRoleByUrn(user.getAccountUrn(), urn);

        if (entity.isPresent()) {
            eventSendingService.sendEvent(user, ROLE_READ, entity.get());
            return ResponseEntity
                .ok()
                .body(conversionService.convert(entity.get(), RestRoleResponse.class));
        }

        RoleResponse eventPayload = RoleResponse.builder()
            .urn(urn)
            .tenantUrn(user.getAccountUrn())
            .build();
        eventSendingService.sendEvent(user, ROLE_NOT_FOUND, eventPayload);
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

    private ResponseEntity<?> findAll(SmartCosmosUser user) {

        List<RoleResponse> roleList = roleDao.findAllRoles(user.getAccountUrn());
        for (RoleResponse role : roleList) {
            eventSendingService.sendEvent(user, ROLE_READ, role);
        }

        return ResponseEntity
            .ok()
            .body(convertList(roleList, RoleResponse.class, RestRoleResponse.class));
    }

    public ResponseEntity<?> findByName(String name, SmartCosmosUser user) {

        Optional<RoleResponse> entity = roleDao.findRoleByName(user.getAccountUrn(), name);

        if (entity.isPresent()) {
            eventSendingService.sendEvent(user, ROLE_READ, entity.get());
            return ResponseEntity
                .ok()
                .body(conversionService.convert(entity.get(), RestRoleResponse.class));
        }

        RoleResponse eventPayload = RoleResponse.builder()
            .name(name)
            .tenantUrn(user.getAccountUrn())
            .build();
        eventSendingService.sendEvent(user, ROLE_NOT_FOUND, eventPayload);
        return ResponseEntity.notFound()
            .build();
    }

    private <S, T> List<T> convertList(List<S> list, Class sourceClass, Class targetClass) {

        TypeDescriptor sourceDescriptor = TypeDescriptor.collection(List.class, TypeDescriptor.valueOf(sourceClass));
        TypeDescriptor targetDescriptor = TypeDescriptor.collection(List.class, TypeDescriptor.valueOf(targetClass));

        return (List<T>) conversionService.convert(list, sourceDescriptor, targetDescriptor);
    }
}
