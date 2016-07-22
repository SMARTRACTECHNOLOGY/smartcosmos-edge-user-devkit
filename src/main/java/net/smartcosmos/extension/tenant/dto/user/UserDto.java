package net.smartcosmos.extension.tenant.dto.user;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NonNull;
import lombok.ToString;

/**
 * This is the response from the User Details Service that will contain the necessary
 * information for caching purposes. While not required, if the password hash is filled
 * this will speed up authentication considerably, since it can be queried against the
 * native Spring Security Cache.
 *
 * @author voor
 */
@Data
@Builder
@AllArgsConstructor
@ToString(exclude = "passwordHash")
public class UserDto {

    @NonNull
    private final String tenantUrn;

    @NonNull
    private final String userUrn;

    @NonNull
    private final String username;

    private String passwordHash;

    @NonNull
    private List<String> roles;
}
