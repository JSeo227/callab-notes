package dev.collab_sync.service;

import dev.collab_sync.controller.dto.UserDetails;
import dev.collab_sync.domain.Member;
import dev.collab_sync.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;


/**
 * 커스텀 사용자 정보 조회 서비스 (검증)
 * Spring Security의 UserDetailsService 인터페이스를 구현
 * 인증 과정에서 사용자 정보를 데이터베이스에서 조회하여 CustomUserDetails로 변환
 * AuthenticationManager가 사용자 인증 시 이 서비스를 통해 사용자 정보를 로드
 */
@Service
@RequiredArgsConstructor
public class UserDetailService implements UserDetailsService {

    private final MemberRepository memberRepository;

    @Override
    public org.springframework.security.core.userdetails.UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {

        Member member = memberRepository.findByEmail(email);

        if (member != null) {
            return new UserDetails(member);
        }

        throw new UsernameNotFoundException("Not found: " + email);
    }
}
