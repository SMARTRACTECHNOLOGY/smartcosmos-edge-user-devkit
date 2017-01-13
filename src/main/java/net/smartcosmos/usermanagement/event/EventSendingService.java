package net.smartcosmos.usermanagement.event;

import java.util.Collection;

import net.smartcosmos.security.user.SmartCosmosUser;

/**
 * A service that sends message to the event service.
 */
public interface EventSendingService<EVENT, ENTITY> {

    /**
     * Send the event using {@link EVENT} event type.
     *
     * @param user the user making the request
     * @param eventType the event type
     * @param entity the object being reported
     */
    void sendEvent(SmartCosmosUser user, EVENT eventType, ENTITY entity);

    void sendEvent(SmartCosmosUser user, String eventType, ENTITY entity);

    void sendEvents(SmartCosmosUser user, EVENT eventType, Collection<ENTITY> entities);

    void sendEvents(SmartCosmosUser user, String eventType, Collection<ENTITY> entities);
}
