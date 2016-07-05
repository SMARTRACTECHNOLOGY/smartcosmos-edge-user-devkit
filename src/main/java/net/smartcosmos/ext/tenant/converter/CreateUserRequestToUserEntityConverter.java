package net.smartcosmos.ext.tenant.converter;

import org.springframework.core.convert.converter.Converter;
import org.springframework.format.FormatterRegistrar;
import org.springframework.format.FormatterRegistry;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import net.smartcosmos.ext.tenant.domain.UserEntity;
import net.smartcosmos.ext.tenant.util.UuidUtil;
import net.smartcosmos.ext.tenant.dto.CreateUserRequest;

/**
 * Initially created by SMART COSMOS Team on June 30, 2016.
 */
@Component
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
            .password("PleaseChangeMeImmediately")
            .roles(StringUtils.collectionToDelimitedString(createUserRequest.getRoles(), " "))
            .authorities(StringUtils.collectionToDelimitedString(createUserRequest.getAuthorities(), " "))
            .active(createUserRequest.getActive())
            .build();
    }

    @Override
    public void registerFormatters(FormatterRegistry registry) {

        registry.addConverter(this);
    }
}
