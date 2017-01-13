package net.smartcosmos.usermanagement.user.converter;

import org.springframework.core.convert.converter.Converter;
import org.springframework.format.FormatterRegistrar;
import org.springframework.format.FormatterRegistry;
import org.springframework.stereotype.Component;

import net.smartcosmos.cluster.userdetails.domain.UserEntity;
import net.smartcosmos.usermanagement.user.dto.CreateUserRequest;

/**
 * Initially created by SMART COSMOS Team on January 13, 2017.
 */
@Component
public class CreateUserRequestToUserEntityConverter
    implements Converter<CreateUserRequest, UserEntity>, FormatterRegistrar

{

    @Override
    public UserEntity convert(CreateUserRequest request) {

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
            .build();
    }

    @Override
    public void registerFormatters(FormatterRegistry registry) {

        registry.addConverter(this);
    }
}
