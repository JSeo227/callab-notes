package dev.collab_sync.domain.login;

import org.springframework.data.jpa.repository.JpaRepository;

public interface LoginRepository extends JpaRepository<Login, Long> {
    Login findByMemberEmail(String email);
}
