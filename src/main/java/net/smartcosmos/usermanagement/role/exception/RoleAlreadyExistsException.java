package net.smartcosmos.usermanagement.role.exception;

import net.smartcosmos.exceptions.SmartCosmosException;

/**
 * The requested role already exists in the system
 */
public class RoleAlreadyExistsException extends SmartCosmosException {

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
