package net.smartcosmos.extension.tenant.converter.user;

import java.util.ArrayList;
import java.util.List;

import org.springframework.core.convert.converter.Converter;
import org.springframework.format.FormatterRegistrar;
import org.springframework.format.FormatterRegistry;
import org.springframework.stereotype.Component;

import net.smartcosmos.extension.tenant.domain.RoleEntity;
import net.smartcosmos.extension.tenant.domain.UserEntity;
import net.smartcosmos.extension.tenant.dto.user.CreateOrUpdateUserResponse;
import net.smartcosmos.extension.tenant.util.UuidUtil;

/**
 * Initially created by SMART COSMOS Team on June 30, 2016.
 */
@Component
public class UserEntityToCreateOrUpdateUserResponseConverter
    implements Converter<UserEntity, CreateOrUpdateUserResponse>, FormatterRegistrar {

    @Override
    public CreateOrUpdateUserResponse convert(UserEntity userEntity) {

        // role entities from role strings
        List<String> roles = new ArrayList<>();
        for (RoleEntity role : userEntity.getRoles()) {
            roles.add(role.getName());
        }

        return CreateOrUpdateUserResponse.builder()
            .urn(UuidUtil.getUserUrnFromUuid(userEntity.getId()))
            .tenantUrn(UuidUtil.getTenantUrnFromUuid(userEntity.getTenantId()))
            .username(userEntity.getUsername())
            .emailAddress(userEntity.getEmailAddress())
            .givenName(userEntity.getGivenName())
            .surname(userEntity.getSurname())
            .roles(roles)
            .active(userEntity.getActive())
            .build();
    }

    @Override
    public void registerFormatters(FormatterRegistry registry) {

        registry.addConverter(this);
    }
}
