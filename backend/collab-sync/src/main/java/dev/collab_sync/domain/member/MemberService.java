package dev.collab_sync.domain.member;

import dev.collab_sync.common.type.RoleType;
import dev.collab_sync.domain.login.Login;
import dev.collab_sync.domain.login.LoginRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class MemberService {

    private final MemberRepository memberRepository;
    private final LoginRepository loginRepository;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;

    public void join(JoinDto dto) {
        String email = dto.getEmail();
        String password = dto.getPassword();
        String username = dto.getUsername();
        RoleType role = RoleType.VIEWER;

        boolean isExist = memberRepository.existsByEmail(email);

        if (isExist) {
            throw new IllegalStateException("email already exists");
        }

        /*
            특정 조건 만족시
            if role = RoleTpe.OWNER
            else role = RoleType.EDITOR
         */

        Member member = Member.builder()
                .email(email)
                .password(bCryptPasswordEncoder.encode(password))
                .username(username)
                .role(role)
                .build();

        memberRepository.save(member);

        Login login = Login.builder()
                .member(member)
                .failedLoginCount(0)
                .isLoggedIn(false)
                .lastLoginAt(null)
                .build();

        loginRepository.save(login);

    }
}
