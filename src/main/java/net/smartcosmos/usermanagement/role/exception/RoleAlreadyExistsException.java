package net.smartcosmos.usermanagement.role.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * The requested role already exists in the system
 */
@ResponseStatus(value = HttpStatus.CONFLICT, reason = "Role already exists.")
public class RoleAlreadyExistsException extends RuntimeException {

    /**
     * Create the exception with the rolename in the message;
     *
     * @param tenantUrn the tenant URN for the role
     * @param roleName the name of the role
     */
    public RoleAlreadyExistsException(String tenantUrn, String roleName) {

        super(String.format("Cannot update role in tenant: '%s'. Name '%s' is already in use.", tenantUrn, roleName));
    }
}
