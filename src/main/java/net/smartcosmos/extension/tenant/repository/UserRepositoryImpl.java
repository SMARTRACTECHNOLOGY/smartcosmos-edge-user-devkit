package net.smartcosmos.extension.tenant.repository;

import java.util.LinkedHashSet;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

import net.smartcosmos.extension.tenant.domain.AuthorityEntity;
import net.smartcosmos.extension.tenant.domain.RoleEntity;
import net.smartcosmos.extension.tenant.domain.UserEntity;

@Component
public class UserRepositoryImpl implements UserRepositoryCustom {

    @Lazy
    private final UserRepository userRepository;

    private final PasswordEncoder passwordEncoder;

    @Lazy
    @Autowired
    public UserRepositoryImpl(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public Optional<UserEntity> getUserByCredentials(String username, String password) {

        Assert.notNull(username, "username must not be null");
        // Assert.notNull(password, "password must not be null");

        Optional<UserEntity> userOptional = userRepository.findByUsername(username);

        if (userOptional.isPresent() /* && passwordEncoder.matches(password, userOptional.get().getPassword())*/) {
            return userOptional;
        }

        return Optional.empty();
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
