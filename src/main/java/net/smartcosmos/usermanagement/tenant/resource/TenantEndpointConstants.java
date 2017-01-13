package net.smartcosmos.usermanagement.tenant.resource;

/**
 * A constant interface class for the Tenant endpoints.
 */
public interface TenantEndpointConstants {

    String ENDPOINT_ENABLEMENT_PREFIX = "smartcosmos.endpoints";
    String ENDPOINT_ENABLEMENT_PROPERTY_ENABLED = "enabled";

    // region Base Constants

    String ENDPOINT_BASE_NAME_TENANTS = "tenants";

    String ENDPOINT_ENABLEMENT_TENANTS = ENDPOINT_ENABLEMENT_PREFIX + "." + ENDPOINT_BASE_NAME_TENANTS;

    // endregion

    // region Path Segment and Parameter Constants

    String TENANT_URN = "tenantUrn";
    String NAME = "name";

    // endregion

    // region Resource Paths

    String ENDPOINT_TENANTS = "/" + ENDPOINT_BASE_NAME_TENANTS;
    String ENDPOINT_TENANTS_URN = ENDPOINT_TENANTS + "/{" + TENANT_URN + "}";

    // endregion

    // region Endpoint Enablement

    String ENDPOINT_ENABLEMENT_TENANTS_CREATE = ENDPOINT_ENABLEMENT_TENANTS + ".create";

    String ENDPOINT_ENABLEMENT_TENANTS_READ = ENDPOINT_ENABLEMENT_TENANTS + ".read";
    String ENDPOINT_ENABLEMENT_TENANTS_READ_ALL = ENDPOINT_ENABLEMENT_TENANTS_READ + ".all";
    String ENDPOINT_ENABLEMENT_TENANTS_READ_URN = ENDPOINT_ENABLEMENT_TENANTS_READ + ".urn";

    String ENDPOINT_ENABLEMENT_TENANTS_UPDATE = ENDPOINT_ENABLEMENT_TENANTS + ".update";

    // endregion
}
