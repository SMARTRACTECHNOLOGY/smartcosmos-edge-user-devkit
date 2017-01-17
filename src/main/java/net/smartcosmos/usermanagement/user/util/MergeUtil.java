package net.smartcosmos.usermanagement.user.util;

import net.smartcosmos.cluster.userdetails.domain.UserEntity;
import net.smartcosmos.usermanagement.user.dto.UpdateUserRequest;

public class MergeUtil {

    public static UserEntity merge(UserEntity user, UpdateUserRequest request) {

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
        if (request.getPassword() != null) {
            user.setPassword(request.getPassword());
        }
        if (request.getEmailAddress() != null) {
            user.setEmailAddress(request.getEmailAddress());
        }
        if (request.getPassword() != null) {
            user.setPassword(request.getPassword());
        }

        return user;
    }
}
