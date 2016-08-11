package net.smartcosmos.extension.tenant.rest.utility;

import org.apache.commons.lang.StringUtils;

public enum UserEventType {

    USER_READ("user.read"),
    USER_BATCH_START("user.batch.start"),
    USER_BATCH_STOP("user.batch.stop"),
    USER_CREATED("user.created"),
    USER_LOGIN_FAILURE("user.login.failure"),
    USER_LOGIN_SUCCESS("user.login.success"),
    USER_PASSWORD_CHANGED("user.password.changed"),
    USER_PASSWORD_RESET("user.password.reset"),
    USER_REGISTRATION_REQUEST("user.registration.requested"),
    USER_UPDATED("user.updated"),
    USER_CREATE_FAILED_ALREADY_EXISTS("user.createFailedAlreadyExists"),
    USER_DELETED("user.deleted"),
    USER_NOT_FOUND("user.notFound"),
    UNKNOWN("Unknown");

    private String eventName;

    UserEventType(String anEventName) {

        eventName = anEventName;
    }

    private String eventName() {

        return eventName;
    }

    public static UserEventType fromString(String anEventName) {

        if (StringUtils.isBlank(anEventName)) {
            throw new IllegalArgumentException("Invalid value for event name.  Null, blank and whitespace are not allowed. value: '" +
                                               anEventName + "'.");
        }

        for (UserEventType eventType : UserEventType.values()) {
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
