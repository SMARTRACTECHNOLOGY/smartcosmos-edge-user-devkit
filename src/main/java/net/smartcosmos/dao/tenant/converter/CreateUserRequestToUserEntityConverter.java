package net.smartcosmos.dao.tenant.converter;

import org.springframework.core.convert.converter.Converter;
import org.springframework.format.FormatterRegistrar;
import org.springframework.format.FormatterRegistry;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import net.smartcosmos.dao.tenant.domain.UserEntity;
import net.smartcosmos.dao.tenant.util.UuidUtil;
import net.smartcosmos.dto.tenant.CreateUserRequest;

/**
 * Initially created by SMART COSMOS Team on June 30, 2016.
 */
public class CreateUserRequestToUserEntityConverter
    implements Converter<CreateUserRequest, UserEntity>, FormatterRegistrar {

    PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    @Override
    public UserEntity convert(CreateUserRequest createUserRequest) {
        return UserEntity.builder()
            .id(UuidUtil.getNewUuid())
            .tenantId(UuidUtil.getUuidFromUrn(createUserRequest.getTenantUrn()))
            .username(createUserRequest.getUsername())
            .emailAddress(createUserRequest.getEmailAddress())
            .givenName(createUserRequest.getGivenName())
            .surname(createUserRequest.getSurname())
            .password(passwordEncoder.encode("PleaseChangeMeImmediately"))
            .active(createUserRequest.getActive())
            .build();
    }

    @Override
    public void registerFormatters(FormatterRegistry registry) {

        registry.addConverter(this);
    }
}
