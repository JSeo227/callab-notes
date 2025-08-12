package dev.collab_sync.repository;

import dev.collab_sync.domain.RefreshToken;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;

public interface RefreshRepository extends JpaRepository<RefreshToken, Long> {
    Boolean existsByRefreshToken(String refreshToken);

    @Modifying
    void deleteByRefreshToken(String refreshToken);
}
