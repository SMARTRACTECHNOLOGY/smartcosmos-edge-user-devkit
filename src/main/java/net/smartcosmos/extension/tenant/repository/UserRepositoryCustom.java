package net.smartcosmos.extension.tenant.repository;

import java.util.Set;
import java.util.UUID;

import org.springframework.transaction.annotation.Transactional;

import net.smartcosmos.extension.tenant.domain.AuthorityEntity;

public interface UserRepositoryCustom {

    @Transactional
    Set<AuthorityEntity> getAuthorities(UUID userId, UUID tenantId);
}
