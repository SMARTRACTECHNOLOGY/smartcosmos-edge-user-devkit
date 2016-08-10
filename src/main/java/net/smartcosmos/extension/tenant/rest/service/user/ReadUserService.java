package net.smartcosmos.extension.tenant.rest.service.user;

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
import net.smartcosmos.extension.tenant.dto.user.UserResponse;
import net.smartcosmos.extension.tenant.rest.dto.user.RestUserResponse;
import net.smartcosmos.extension.tenant.rest.service.EventSendingService;
import net.smartcosmos.security.user.SmartCosmosUser;

import static net.smartcosmos.extension.tenant.rest.utility.UserEventType.USER_NOT_FOUND;
import static net.smartcosmos.extension.tenant.rest.utility.UserEventType.USER_READ;

@Slf4j
@Service
public class ReadUserService {

    private final TenantDao tenantDao;
    private final EventSendingService eventSendingService;
    private final ConversionService conversionService;

    @Autowired
    public ReadUserService(TenantDao tenantDao, EventSendingService userEventSendingService, ConversionService conversionService) {

        this.tenantDao = tenantDao;
        this.eventSendingService = userEventSendingService;
        this.conversionService = conversionService;
    }

    public ResponseEntity<?> findByUrn(String urn, SmartCosmosUser user) {

        Optional<UserResponse> entity = tenantDao.findUserByUrn(user.getAccountUrn(), urn);

        if (entity.isPresent()) {
            eventSendingService.sendEvent(user, USER_READ, entity.get());
            return ResponseEntity
                .ok()
                .body(conversionService.convert(entity.get(), RestUserResponse.class));
        }

        UserResponse eventPayload = UserResponse.builder()
            .urn(urn)
            .tenantUrn(user.getAccountUrn())
            .build();
        eventSendingService.sendEvent(user, USER_NOT_FOUND, eventPayload);
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

        List<UserResponse> userList = tenantDao.findAllUsers(user.getAccountUrn());
        for (UserResponse userResponse : userList) {
            eventSendingService.sendEvent(user, USER_READ, userResponse);
        }

        return ResponseEntity
            .ok()
            .body(convertList(userList, UserResponse.class, RestUserResponse.class));
    }

    public ResponseEntity<?> findByName(String name, SmartCosmosUser user) {

        Optional<UserResponse> entity = tenantDao.findUserByName(user.getAccountUrn(), name);

        if (entity.isPresent()) {
            eventSendingService.sendEvent(user, USER_READ, entity.get());
            return ResponseEntity
                .ok()
                .body(conversionService.convert(entity.get(), RestUserResponse.class));
        }

        UserResponse eventPayload = UserResponse.builder()
            .username(name)
            .tenantUrn(user.getAccountUrn())
            .build();
        eventSendingService.sendEvent(user, USER_NOT_FOUND, eventPayload);
        return ResponseEntity.notFound()
            .build();
    }

    private <S, T> List<T> convertList(List<S> list, Class sourceClass, Class targetClass) {

        TypeDescriptor sourceDescriptor = TypeDescriptor.collection(List.class, TypeDescriptor.valueOf(sourceClass));
        TypeDescriptor targetDescriptor = TypeDescriptor.collection(List.class, TypeDescriptor.valueOf(targetClass));

        return (List<T>) conversionService.convert(list, sourceDescriptor, targetDescriptor);
    }
}
