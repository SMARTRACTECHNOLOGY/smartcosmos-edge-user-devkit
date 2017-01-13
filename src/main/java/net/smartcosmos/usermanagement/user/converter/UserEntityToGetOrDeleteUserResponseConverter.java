package net.smartcosmos.usermanagement.user.converter;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.core.convert.converter.Converter;
import org.springframework.format.FormatterRegistrar;
import org.springframework.format.FormatterRegistry;
import org.springframework.stereotype.Component;

import net.smartcosmos.cluster.userdetails.domain.AuthorityEntity;
import net.smartcosmos.cluster.userdetails.domain.RoleEntity;
import net.smartcosmos.cluster.userdetails.domain.UserEntity;
import net.smartcosmos.cluster.userdetails.util.UuidUtil;
import net.smartcosmos.usermanagement.user.dto.UserResponse;

/**
 * Initially created by SMART COSMOS Team on June 30, 2016.
 */
@Component
public class UserEntityToGetOrDeleteUserResponseConverter
    implements Converter<UserEntity, UserResponse>, FormatterRegistrar {

    @Override
    public UserResponse convert(UserEntity userEntity) {

        // role and authority strings from role entities
        List<String> roles = new ArrayList<>();
        Set<String> authoritiesSet = new HashSet<>();
        for (RoleEntity role : userEntity.getRoles()) {
            roles.add(role.getName());
            authoritiesSet.addAll(role.getAuthorities()
                                      .stream()
                                      .map(AuthorityEntity::getAuthority)
                                      .collect(Collectors.toList()));
        }

        return UserResponse.builder()
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
