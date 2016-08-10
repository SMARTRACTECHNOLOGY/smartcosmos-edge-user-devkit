package net.smartcosmos.extension.tenant.rest.resource.user;

import static net.smartcosmos.extension.tenant.rest.resource.BasicEndpointConstants.ENDPOINT_ENABLEMENT_PREFIX;

/**
 * A constant interface class for the User endpoints.
 */
public interface UserEndpointConstants {

    // region Base Constants

    String ENDPOINT_BASE_NAME_USERS = "users";

    String ENDPOINT_ENABLEMENT_USERS = ENDPOINT_ENABLEMENT_PREFIX + "." + ENDPOINT_BASE_NAME_USERS;

    // endregion

    // region Path Segment and Parameter Constants

    String USER_URN = "userUrn";
    String NAME = "name";

    // endregion

    // region Resource Paths

    String ENDPOINT_USERS = "/" + ENDPOINT_BASE_NAME_USERS;
    String ENDPOINT_USERS_URN = ENDPOINT_USERS + "/{" + USER_URN + "}";

    // endregion

    // region Endpoint Enablement

    String ENDPOINT_ENABLEMENT_USERS_CREATE = ENDPOINT_ENABLEMENT_USERS + ".create";

    String ENDPOINT_ENABLEMENT_USERS_READ = ENDPOINT_ENABLEMENT_USERS + ".read";
    String ENDPOINT_ENABLEMENT_USERS_READ_ALL = ENDPOINT_ENABLEMENT_USERS_READ + ".all";
    String ENDPOINT_ENABLEMENT_USERS_READ_URN = ENDPOINT_ENABLEMENT_USERS_READ + ".urn";

    String ENDPOINT_ENABLEMENT_USERS_UPDATE = ENDPOINT_ENABLEMENT_USERS + ".update";

    String ENDPOINT_ENABLEMENT_USERS_DELETE = ENDPOINT_ENABLEMENT_USERS + ".delete";

    // endregion
}
