package net.smartcosmos.extension.tenant.util;

import org.springframework.security.crypto.password.PasswordEncoder;

import net.smartcosmos.extension.tenant.domain.UserEntity;
import net.smartcosmos.extension.tenant.dto.UpdateUserRequest;

public class MergeUtil {

    public static UserEntity merge(UserEntity user, UpdateUserRequest request, PasswordEncoder encoder) {
        
        if (request.getActive() != null) {
            user.setActive(request.getActive());
        }
        if (request.getUsername() != null) {
            user.setUsername(request.getUsername());
        }
        if (request.getGivenName() != null) {
            user.setGivenName(request.getGivenName());
        }
        if (request.getSurname() != null) {
            user.setSurname(request.getSurname());
        }
        if (request.getEmailAddress() != null) {
            user.setEmailAddress(request.getEmailAddress());
        }
        if (request.getPassword() != null) {
            user.setPassword(encoder.encode(request.getPassword()));
        }

        return user;
    }
}
