package dev.collab_sync.filter;

import dev.collab_sync.domain.RefreshToken;
import dev.collab_sync.repository.RefreshRepository;
import dev.collab_sync.service.RefreshService;
import dev.collab_sync.util.JwtUtil;
import dev.collab_sync.service.LoginService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Collection;
import java.util.Date;
import java.util.Iterator;

import static dev.collab_sync.util.CookieUtil.createCookie;

/**
 * 커스텀 로그인 필터
 * Spring Security의 기본 form login을 비활성화하고 사용자 정의 인증 로직을 제공
 * email을 사용자 식별자로 사용하며, JWT 기반 인증을 위해 구현
 */
@Slf4j
//@RequiredArgsConstructor
public class LoginFilter extends UsernamePasswordAuthenticationFilter {

    private final AuthenticationManager authenticationManager;
    private final JwtUtil jwtUtil;
    private final LoginService loginService;
    private final RefreshService refreshService;

    public LoginFilter(AuthenticationManager authenticationManager, JwtUtil jwtUtil, LoginService loginService, RefreshService refreshService) {
        super.setAuthenticationManager(authenticationManager);
        super.setUsernameParameter("email");  // jwt 토큰 식별자 username → email로 변경
        this.authenticationManager = authenticationManager;
        this.jwtUtil = jwtUtil;
        this.loginService = loginService;
        this.refreshService = refreshService;
    }
    /**
     * 사용자 인증 시도
     * HTTP 요청에서 email(사용자명 대신), password, username을 추출하여 인증 토큰 생성
     */
    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response) throws AuthenticationException {
        String email = obtainUsername(request);     // email을 식별자로 사용
        String password = obtainPassword(request);

        // 토큰 생성
        UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(email, password, null);

        return authenticationManager.authenticate(authToken);
    }

    /**
     * 로그인 성공 처리
     * JWT 토큰 발급 및 응답 헤더 설정
     */
    @Override
    protected void successfulAuthentication(HttpServletRequest request, HttpServletResponse response, FilterChain chain, Authentication authResult) throws IOException, ServletException {

    /* MemberDetails memberDetails = (MemberDetails) authResult.getPrincipal();

     String email = memberDetails.getUsername();
     log.info("Successfully logged in email: {}", email);

     Collection<? extends GrantedAuthority> authorities = authResult.getAuthorities();
     Iterator<? extends GrantedAuthority> iterator = authorities.iterator();
     GrantedAuthority auth = iterator.next();

     String role = auth.getAuthority();
     log.info("Successfully logged in role: {}", role);

     loginService.login(email);

     String token = jwtUtil.generateAccessToken(email, role);
     log.info("Successfully logged in token: {}", token);

     response.addHeader("Authorization", "Bearer " + token);*/

        String email = authResult.getName();

        //로그인
        loginService.login(email);

        Collection<? extends GrantedAuthority> authorities = authResult.getAuthorities();
        Iterator<? extends GrantedAuthority> iterator = authorities.iterator();
        GrantedAuthority auth = iterator.next();
        String role = auth.getAuthority();

        //토큰 생성
        String access = jwtUtil.generateAccessToken(email, role);
        String refresh = jwtUtil.generateRefreshToken(email, role);

        //Refresh 토큰 저장
        LocalDateTime expiresAt = LocalDateTime.now().plusDays(1); // 1일 뒤 만료
        RefreshToken refreshToken = RefreshToken.builder()
                .email(email)
                .refreshToken(refresh)
                .expiresAt(expiresAt)
                .build();
        refreshService.save(refreshToken);

        //응답 설정
        response.setHeader("access", access);
        response.addCookie(createCookie("refresh", refresh, false));
        response.setStatus(HttpStatus.OK.value());

    }

    /**
     * 로그인 실패 시 처리
     * 에러 응답 및 로그 기록
     */
    @Override
    protected void unsuccessfulAuthentication(HttpServletRequest request, HttpServletResponse response, AuthenticationException failed) throws IOException, ServletException {
        log.info("Authentication failed {}", failed.getMessage());
        response.setStatus(401);
    }
}
