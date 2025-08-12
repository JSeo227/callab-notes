package dev.collab_sync.domain.login;

import com.fasterxml.jackson.annotation.JsonIgnore;
import dev.collab_sync.domain.member.Member;
import dev.collab_sync.common.base.BaseTimeEntity;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.Objects;

@Data
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Login extends BaseTimeEntity {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id", nullable = false, unique = true)
    private Member member;

    private Integer failedLoginCount;   // 로그인 실패 횟수

    private Boolean isLoggedIn;           // 로그인 여부

    @JsonIgnore
    private LocalDateTime lastLoginAt;

    @Override
    public String toString() {
        return "Login{" +
                "id=" + id +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof Login login)) return false;
        return Objects.equals(id, login.id);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }
}
