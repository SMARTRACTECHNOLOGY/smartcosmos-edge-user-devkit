package net.smartcosmos.extension.tenant.converter;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.springframework.core.convert.converter.Converter;
import org.springframework.format.FormatterRegistrar;
import org.springframework.format.FormatterRegistry;
import org.springframework.stereotype.Component;

import net.smartcosmos.extension.tenant.domain.AuthorityEntity;
import net.smartcosmos.extension.tenant.domain.RoleEntity;
import net.smartcosmos.extension.tenant.domain.UserEntity;
import net.smartcosmos.extension.tenant.dto.GetOrDeleteUserResponse;
import net.smartcosmos.extension.tenant.util.UuidUtil;

/**
 * Initially created by SMART COSMOS Team on June 30, 2016.
 */
@Component
public class UserEntityToGetOrDeleteUserResponseConverter
    implements Converter<UserEntity, GetOrDeleteUserResponse>, FormatterRegistrar {

    @Override
    public GetOrDeleteUserResponse convert(UserEntity userEntity) {

        // role and authority strings from role entities
        List<String> roles = new ArrayList<>();
        Set<String> authoritiesSet = new HashSet<>();
        for (RoleEntity role : userEntity.getRoles()) {
            roles.add(role.getName());
            for (AuthorityEntity authorityEntity : role.getAuthorities()) {
                authoritiesSet.add(authorityEntity.getAuthority());
            }
        }
        List<String> authorities = new ArrayList<>(authoritiesSet);

        return GetOrDeleteUserResponse.builder()
            .urn(UuidUtil.getUserUrnFromUuid(userEntity.getId()))
            .tenantUrn(UuidUtil.getTenantUrnFromUuid(userEntity.getTenantId()))
            .username(userEntity.getUsername())
            .emailAddress(userEntity.getEmailAddress())
            .givenName(userEntity.getGivenName())
            .surname(userEntity.getSurname())
            .roles(roles)
            .authorities(authorities)
            .active(userEntity.getActive())
            .build();
    }

    @Override
    public void registerFormatters(FormatterRegistry registry) {

        registry.addConverter(this);
    }
}
