package net.smartcosmos.usermanagement.user.converter;

import org.springframework.core.convert.converter.Converter;
import org.springframework.format.FormatterRegistrar;
import org.springframework.format.FormatterRegistry;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import net.smartcosmos.cluster.userdetails.domain.UserEntity;
import net.smartcosmos.usermanagement.user.dto.CreateOrUpdateUserRequest;

/**
 * Initially created by SMART COSMOS Team on June 30, 2016.
 */
@Component
public class CreateOrUpdateUserRequestToUserEntityConverter
    implements Converter<CreateOrUpdateUserRequest, UserEntity>, FormatterRegistrar {

    PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    @Override
    public UserEntity convert(CreateOrUpdateUserRequest request) {

        /*
            The converter ignores the roles, because it isn't able to completely convert them.
            We therefore have to separately add the roles later on.
         */

        return UserEntity.builder()
            .username(request.getUsername())
            .emailAddress(request.getEmailAddress())
            .givenName(request.getGivenName())
            .surname(request.getSurname())
            .active(request.getActive())
            .password(request.getPassword())
            .build();
    }

    @Override
    public void registerFormatters(FormatterRegistry registry) {

        registry.addConverter(this);
    }
}
