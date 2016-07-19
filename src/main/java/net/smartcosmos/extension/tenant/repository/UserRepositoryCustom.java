package net.smartcosmos.extension.tenant.repository;

import java.util.Optional;
import java.util.Set;
import java.util.UUID;

import org.springframework.transaction.annotation.Transactional;

import net.smartcosmos.extension.tenant.domain.AuthorityEntity;
import net.smartcosmos.extension.tenant.domain.UserEntity;

public interface UserRepositoryCustom {

    @Transactional
    Optional<UserEntity> getUserByCredentials(String username, String password);

    @Transactional
    Set<AuthorityEntity> getAuthorities(UUID userId, UUID tenantId);

}
