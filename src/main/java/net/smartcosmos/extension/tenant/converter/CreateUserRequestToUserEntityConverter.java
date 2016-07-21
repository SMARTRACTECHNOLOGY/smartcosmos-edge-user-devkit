package net.smartcosmos.extension.tenant.converter;

import org.springframework.core.convert.converter.Converter;
import org.springframework.format.FormatterRegistrar;
import org.springframework.format.FormatterRegistry;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import net.smartcosmos.extension.tenant.domain.UserEntity;
import net.smartcosmos.extension.tenant.dto.CreateUserRequest;
import net.smartcosmos.extension.tenant.util.UuidUtil;

/**
 * Initially created by SMART COSMOS Team on June 30, 2016.
 */
@Component
public class CreateUserRequestToUserEntityConverter
    implements Converter<CreateUserRequest, UserEntity>, FormatterRegistrar {

    PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    @Override
    public UserEntity convert(CreateUserRequest createUserRequest) {

        /*
            The converter ignores the roles, because it isn't able to completely convert them.
            We therefore have to separately add the roles later on.
         */

        return UserEntity.builder()
            .id(UuidUtil.getNewUuid())
            .username(createUserRequest.getUsername())
            .emailAddress(createUserRequest.getEmailAddress())
            .givenName(createUserRequest.getGivenName())
            .surname(createUserRequest.getSurname())
            .active(createUserRequest.getActive())
            .build();
    }

    @Override
    public void registerFormatters(FormatterRegistry registry) {

        registry.addConverter(this);
    }
}
