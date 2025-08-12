package dev.collab_sync.domain.refresh;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class RefreshService {

    private final RefreshRepository refreshRepository;

    /**
     * RefreshToken 저장
     */
    public void save(Refresh refresh) {
        refreshRepository.save(refresh);
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
}
