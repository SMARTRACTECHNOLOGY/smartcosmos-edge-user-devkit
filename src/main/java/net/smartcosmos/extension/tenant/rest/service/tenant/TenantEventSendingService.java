package net.smartcosmos.extension.tenant.rest.service.tenant;

import java.util.Collection;

import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import net.smartcosmos.events.SmartCosmosEventException;
import net.smartcosmos.events.SmartCosmosEventTemplate;
import net.smartcosmos.extension.tenant.rest.service.EventSendingService;
import net.smartcosmos.extension.tenant.rest.utility.TenantEventType;
import net.smartcosmos.security.user.SmartCosmosUser;

@Slf4j
@Service
public class TenantEventSendingService implements EventSendingService<TenantEventType, Object> {

    private final SmartCosmosEventTemplate smartCosmosEventTemplate;

    @Autowired
    public TenantEventSendingService(SmartCosmosEventTemplate smartCosmosEventTemplate) {

        this.smartCosmosEventTemplate = smartCosmosEventTemplate;
    }

    @Override
    public void sendEvent(SmartCosmosUser user, TenantEventType eventType, Object entity) {

        sendEvent(user, eventType.getEventName(), entity);
    }

    @Override
    public void sendEvent(SmartCosmosUser user, String eventType, Object entity) {

        try {
            smartCosmosEventTemplate.sendEvent(entity, eventType, user);
        } catch (SmartCosmosEventException e) {
            String msg = String.format("Exception processing thing event '%s', entity: '%s', cause: '%s'.", eventType, entity, e.toString());
            log.error(msg);
            log.debug(msg, e);
        }
    }

    @Override
    public void sendEvents(SmartCosmosUser user, TenantEventType eventType, Collection<Object> entities) {

        entities.stream()
            .forEach((item) -> sendEvent(user, eventType.getEventName(), item));
    }

    @Override
    public void sendEvents(SmartCosmosUser user, String eventType, Collection<Object> entities) {

        entities.stream()
            .forEach((item) -> sendEvent(user, eventType, item));
    }
}
