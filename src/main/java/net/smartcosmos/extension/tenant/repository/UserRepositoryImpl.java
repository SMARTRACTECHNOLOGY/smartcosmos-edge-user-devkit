package net.smartcosmos.extension.tenant.repository;

import java.util.LinkedHashSet;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

import net.smartcosmos.extension.tenant.domain.AuthorityEntity;
import net.smartcosmos.extension.tenant.domain.RoleEntity;
import net.smartcosmos.extension.tenant.domain.UserEntity;

@Component
public class UserRepositoryImpl implements UserRepositoryCustom {

    @Lazy
    private final UserRepository userRepository;

    @Lazy
    @Autowired
    public UserRepositoryImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public Set<AuthorityEntity> getAuthorities(UUID userId, UUID tenantId) {

        Set<AuthorityEntity> authorities = new LinkedHashSet<>();

        Optional<UserEntity> userOptional = userRepository.findByIdAndTenantId(userId, tenantId);
        if (userOptional.isPresent()) {

            UserEntity user = userOptional.get();
            for (RoleEntity role : user.getRoles()) {
                authorities.addAll(role.getAuthorities());
            }
        }

        return authorities;
    }
}
