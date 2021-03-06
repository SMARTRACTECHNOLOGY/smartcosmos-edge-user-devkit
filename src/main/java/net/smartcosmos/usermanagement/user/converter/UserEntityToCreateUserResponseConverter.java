package net.smartcosmos.usermanagement.user.converter;

import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.core.convert.converter.Converter;
import org.springframework.format.FormatterRegistrar;
import org.springframework.format.FormatterRegistry;
import org.springframework.stereotype.Component;

import net.smartcosmos.cluster.userdetails.domain.RoleEntity;
import net.smartcosmos.cluster.userdetails.domain.UserEntity;
import net.smartcosmos.cluster.userdetails.util.UuidUtil;
import net.smartcosmos.usermanagement.user.dto.CreateUserResponse;

/**
 * Convert a {@link UserEntity} to a {@link CreateUserResponse}.
 */
@Component
public class UserEntityToCreateUserResponseConverter implements Converter<UserEntity, CreateUserResponse>, FormatterRegistrar {

    @Override
    public CreateUserResponse convert(UserEntity userEntity) {

        Set<String> roles = userEntity.getRoles()
            .stream()
            .map(RoleEntity::getName)
            .collect(Collectors.toSet());

        return CreateUserResponse.builder()
            .urn(UuidUtil.getUserUrnFromUuid(userEntity.getId()))
            .tenantUrn(UuidUtil.getTenantUrnFromUuid(userEntity.getTenantId()))
            .username(userEntity.getUsername())
            .roles(roles)
            .build();
    }

    @Override
    public void registerFormatters(FormatterRegistry registry) {

        registry.addConverter(this);
    }
}
