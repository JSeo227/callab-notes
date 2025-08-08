package dev.collab_sync.service;

import dev.collab_sync.controller.dto.JoinDto;
import dev.collab_sync.domain.Member;
import dev.collab_sync.domain.RoleType;
import dev.collab_sync.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class JoinService {

    private final MemberRepository memberRepository;
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
    }

}
