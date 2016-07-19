package net.smartcosmos.extension.tenant.repository;

import java.util.Collection;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

import javax.validation.ConstraintViolationException;

import org.springframework.transaction.TransactionException;
import org.springframework.transaction.annotation.Transactional;

import net.smartcosmos.extension.tenant.domain.AuthorityEntity;
import net.smartcosmos.extension.tenant.domain.UserEntity;

public interface UserRepositoryCustom {

    UserEntity persist(UserEntity entity) throws ConstraintViolationException, TransactionException;

    @Transactional
    Optional<UserEntity> getUserByCredentials(String username, String password);

    @Transactional
    Set<AuthorityEntity> getAuthorities(UUID userId, UUID tenantId);

    @Transactional
    Optional<UserEntity> addRolesToUser(UUID tenantId, UUID id, Collection<String> roleNames) throws IllegalArgumentException;
}
