package net.smartcosmos.ext.tenant.converter;

import java.util.HashSet;
import java.util.Set;

import org.springframework.core.convert.converter.Converter;
import org.springframework.format.FormatterRegistrar;
import org.springframework.format.FormatterRegistry;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import net.smartcosmos.ext.tenant.domain.RoleEntity;
import net.smartcosmos.ext.tenant.domain.UserEntity;
import net.smartcosmos.ext.tenant.dto.CreateUserRequest;
import net.smartcosmos.ext.tenant.util.UuidUtil;

/**
 * Initially created by SMART COSMOS Team on June 30, 2016.
 */
@Component
public class CreateUserRequestToUserEntityConverter
    implements Converter<CreateUserRequest, UserEntity>, FormatterRegistrar {

    PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();


    @Override
    public UserEntity convert(CreateUserRequest createUserRequest) {

        // role entities from role strings
        Set<RoleEntity> roleEntities = new HashSet<>();
        for (String role: createUserRequest.getRoles()) {
            roleEntities.add(RoleEntity.builder().name(role).build());
        }

        return UserEntity.builder()
            .id(UuidUtil.getNewUuid())
            .tenantId(UuidUtil.getUuidFromUrn(createUserRequest.getTenantUrn()))
            .username(createUserRequest.getUsername())
            .emailAddress(createUserRequest.getEmailAddress())
            .givenName(createUserRequest.getGivenName())
            .surname(createUserRequest.getSurname())
            .password("PleaseChangeMeImmediately")
            .roles(roleEntities)
            .active(createUserRequest.getActive())
            .build();
    }

    @Override
    public void registerFormatters(FormatterRegistry registry) {

        registry.addConverter(this);
    }
}
