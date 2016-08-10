package net.smartcosmos.extension.tenant.rest.utility;

import org.apache.commons.lang.StringUtils;

public enum TenantEventType {

    TENANT_READ("tenant:read"),
    TENANT_CONFIRMED("tenant:confirmed"),
    TENANT_CREATED("tenant:created"),
    TENANT_UPDATED("tenant:updated"),
    TENANT_CREATE_FAILED_ALREADY_EXISTS("tenant:createFailedAlreadyExists"),
    TENANT_NOT_FOUND("tenant:notFound"),
    UNKNOWN("Unknown");

    private String eventName;

    TenantEventType(String anEventName) {

        eventName = anEventName;
    }

    private String eventName() {

        return eventName;
    }

    public static TenantEventType fromString(String anEventName) {

        if (StringUtils.isBlank(anEventName)) {
            throw new IllegalArgumentException("Invalid value for event name.  Null, blank and whitespace are not allowed. value: '" +
                                               anEventName + "'.");
        }

        for (TenantEventType eventType : TenantEventType.values()) {
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
