package net.smartcosmos.extension.tenant.rest.resource.role;

import static net.smartcosmos.extension.tenant.rest.resource.BasicEndpointConstants.ENDPOINT_ENABLEMENT_PREFIX;

/**
 * A constant interface class for the Role endpoints.
 */
public interface RoleEndpointConstants {

    // region Base Constants

    String ENDPOINT_BASE_NAME_ROLES = "roles";
    String ENDPOINT_ENABLEMENT_ROLES = ENDPOINT_ENABLEMENT_PREFIX + "." + ENDPOINT_BASE_NAME_ROLES;

    // endregion

    // region Path Segment and Parameter Constants

    String ROLE_URN = "roleUrn";
    String NAME = "name";

    // endregion

    // region Resource Paths

    String ENDPOINT_ROLES = "/" + ENDPOINT_BASE_NAME_ROLES;
    String ENDPOINT_ROLES_URN = ENDPOINT_ROLES + "/{" + ROLE_URN + "}";

    // endregion

    // region Endpoint Enablement

    String ENDPOINT_ENABLEMENT_ROLES_CREATE = ENDPOINT_ENABLEMENT_ROLES + ".create";

    String ENDPOINT_ENABLEMENT_ROLES_READ = ENDPOINT_ENABLEMENT_ROLES + ".read";
    String ENDPOINT_ENABLEMENT_ROLES_READ_ALL = ENDPOINT_ENABLEMENT_ROLES_READ + ".all";
    String ENDPOINT_ENABLEMENT_ROLES_READ_URN = ENDPOINT_ENABLEMENT_ROLES_READ + ".urn";

    String ENDPOINT_ENABLEMENT_ROLES_UPDATE = ENDPOINT_ENABLEMENT_ROLES + ".update";

    String ENDPOINT_ENABLEMENT_ROLES_DELETE = ENDPOINT_ENABLEMENT_ROLES + ".delete";

    // endregion
}
