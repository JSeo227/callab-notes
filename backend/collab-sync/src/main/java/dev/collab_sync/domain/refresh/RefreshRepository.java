package dev.collab_sync.domain.refresh;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;

import java.time.LocalDateTime;

public interface RefreshRepository extends JpaRepository<Refresh, Long> {
    Boolean existsByRefreshToken(String refreshToken);

    @Modifying
    void deleteByRefreshToken(String refreshToken);

    @Modifying
    void deleteByExpiresAtBefore(LocalDateTime expiresAt);
}
