package net.smartcosmos.ext.tenant.converter;

import java.util.Arrays;

import org.springframework.core.convert.converter.Converter;
import org.springframework.format.FormatterRegistrar;
import org.springframework.format.FormatterRegistry;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import net.smartcosmos.ext.tenant.domain.UserEntity;
import net.smartcosmos.ext.tenant.dto.CreateUserResponse;
import net.smartcosmos.ext.tenant.util.UuidUtil;

/**
 * Initially created by SMART COSMOS Team on June 30, 2016.
 */
@Component
public class UserEntityToCreateUserResponseConverter
    implements Converter<UserEntity, CreateUserResponse>, FormatterRegistrar {

    @Override
    public CreateUserResponse convert(UserEntity userEntity) {
        return CreateUserResponse.builder()
            .urn(UuidUtil.getUserUrnFromUuid(userEntity.getId()))
            .tenantUrn(UuidUtil.getTenantUrnFromUuid(userEntity.getTenantId()))
            .username(userEntity.getUsername())
            .emailAddress(userEntity.getEmailAddress())
            .givenName(userEntity.getGivenName())
            .surname(userEntity.getSurname())
            .roles(Arrays.asList(StringUtils.tokenizeToStringArray(userEntity.getRoles(), " ")))
            .authorities(Arrays.asList(StringUtils.tokenizeToStringArray(userEntity.getAuthorities(), " ")))
            .active(userEntity.getActive())
            .build();
    }

    @Override
    public void registerFormatters(FormatterRegistry registry) {

        registry.addConverter(this);
    }
}
