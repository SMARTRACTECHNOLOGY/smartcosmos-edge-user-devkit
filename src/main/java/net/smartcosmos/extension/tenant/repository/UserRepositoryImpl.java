package net.smartcosmos.extension.tenant.repository;

import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

import javax.validation.ConstraintViolationException;

import org.apache.commons.lang.exception.ExceptionUtils;
import org.hibernate.Hibernate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.TransactionException;
import org.springframework.util.Assert;

import net.smartcosmos.extension.tenant.domain.AuthorityEntity;
import net.smartcosmos.extension.tenant.domain.RoleEntity;
import net.smartcosmos.extension.tenant.domain.UserEntity;

import static java.util.stream.Collectors.toSet;

@Component
public class UserRepositoryImpl implements UserRepositoryCustom {

    @Lazy
    private final UserRepository userRepository;

    @Lazy
    private final RoleRepository roleRepository;

    private final PasswordEncoder passwordEncoder;

    @Lazy
    @Autowired
    public UserRepositoryImpl(UserRepository userRepository, RoleRepository roleRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public UserEntity persist(UserEntity entity) throws ConstraintViolationException, TransactionException {
        try {
            return userRepository.save(entity);
        } catch (TransactionException e) {
            // we expect constraint violations to be the root cause for exceptions here,
            // so we throw this particular exception back to the caller
            if (ExceptionUtils.getRootCause(e) instanceof ConstraintViolationException) {
                throw (ConstraintViolationException) ExceptionUtils.getRootCause(e);
            } else {
                throw e;
            }
        }
    }

    @Override
    public Optional<UserEntity> getUserByCredentials(String username, String password) throws IllegalArgumentException {

        Assert.notNull(username, "username must not be null");
        Assert.notNull(password, "password must not be null");

        Optional<UserEntity> userOptional = userRepository.findByUsernameIgnoreCase(username);

        if (userOptional.isPresent() && passwordEncoder.matches(password, userOptional.get().getPassword())) {
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

    @Override
    public Optional<UserEntity> addRolesToUser(UUID tenantId, UUID id, Collection<String> roleNames) throws IllegalArgumentException {

        Optional<UserEntity> userOptional = userRepository.findByIdAndTenantId(id, tenantId);
        if (userOptional.isPresent()) {
            UserEntity user = userOptional.get();

            Set<RoleEntity> roleSet = initRoleEntities(user);
            Set<RoleEntity> newRoleSet = getRoleEntities(tenantId, roleNames);
            roleSet.addAll(newRoleSet);

            return Optional.of(user);
        }

        return Optional.empty();
    }

    private Set<RoleEntity> getRoleEntities(UUID tenantId, Collection<String> roleNames) {

        return roleNames
            .stream()
            .map(roleName -> {
                Optional<RoleEntity> role = roleRepository.findByNameAndTenantId(roleName, tenantId);
                if (role.isPresent()) {
                    return role.get();
                } else {
                    String msg = String.format("Role '%s' does not exist", roleName);
                    throw new IllegalArgumentException(msg);
                }
            })
            .collect(toSet());
    }

    private Set<RoleEntity> initRoleEntities(UserEntity user) {
        Set<RoleEntity> roleEntities = user.getRoles();

        if (!Hibernate.isInitialized(roleEntities)) {
            Hibernate.initialize(roleEntities);
        }

        return roleEntities;
    }
}
