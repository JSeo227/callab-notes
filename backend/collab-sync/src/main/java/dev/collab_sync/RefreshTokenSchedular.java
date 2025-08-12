package dev.collab_sync;

import dev.collab_sync.domain.refresh.RefreshService;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
@RequiredArgsConstructor
public class RefreshTokenSchedular {
//
//    private final RefreshService refreshService;
//
//    @Scheduled(fixedRate = 60000)
//    public void deleteByRefreshToken(LocalDateTime expired) {
//        refreshService.deleteByExpired(expired);
//    }
}
