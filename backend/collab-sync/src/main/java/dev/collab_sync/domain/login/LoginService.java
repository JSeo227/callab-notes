package dev.collab_sync.domain.login;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@Transactional
@RequiredArgsConstructor
public class LoginService {

    private final LoginRepository loginRepository;

    public void login(String email) {
        Login login = loginRepository.findByMemberEmail(email);

        if (login != null) {
            login.setIsLoggedIn(true);
            login.setLastLoginAt(LocalDateTime.now());
        }
    }
}
