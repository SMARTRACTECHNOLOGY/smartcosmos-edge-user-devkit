package net.smartcosmos.usermanagement.event;

import org.apache.commons.lang.StringUtils;

public enum RoleEventType {

    ROLE_CREATED("role.created"),
    ROLE_READ("role.read"),
    ROLE_CREATE_FAILED_ALREADY_EXISTS("role.createFailedAlreadyExists"),
    ROLE_NOT_FOUND("role.notFound"),
    ROLE_DELETED("role.deleted"),
    ROLE_UPDATED("role.updated"),
    UNKNOWN("Unknown");

    private String eventName;

    RoleEventType(String anEventName) {

        eventName = anEventName;
    }

    private String eventName() {

        return eventName;
    }

    public static RoleEventType fromString(String anEventName) {

        if (StringUtils.isBlank(anEventName)) {
            throw new IllegalArgumentException("Invalid value for event name.  Null, blank and whitespace are not allowed. value: '" +
                                               anEventName + "'.");
        }

        for (RoleEventType eventType : RoleEventType.values()) {
            if (eventType.eventName()
                .equalsIgnoreCase(anEventName)) {
                return eventType;
            }
        }

        return UNKNOWN;
    }

    public String getEventName() {

        return eventName;
    }
}
