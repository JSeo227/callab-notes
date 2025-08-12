package dev.collab_sync.domain.refresh;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.messaging.simp.stomp.StompReactorNettyCodec;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class RefreshService {

    private final RefreshRepository refreshRepository;

    /**
     * RefreshToken 저장
     */
    public void save(String email, String refresh) {
        LocalDateTime expiresAt = LocalDateTime.now().plusDays(1);
        Refresh refreshToken = Refresh.builder()
                .email(email)
                .refreshToken(refresh)
                .expiresAt(expiresAt)
                .build();
        refreshRepository.save(refreshToken);
    }

    /**
     * RefreshToken 존재 여부 확인
     */
    public Boolean exists(String refreshToken) {
        return refreshRepository.existsByRefreshToken(refreshToken);
    }

    /**
     * RefreshToken 삭제
     */
    public void delete(String refreshToken) {
        refreshRepository.deleteByRefreshToken(refreshToken);
    }

    /**
     * RefreshToken 삭제 (기간 지난)
     */
    public void deleteByExpired(LocalDateTime expired) {
        refreshRepository.deleteByExpiresAtBefore(expired);
    }
}
