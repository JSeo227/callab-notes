package dev.collab_sync.global.config;

import dev.collab_sync.global.filter.JwtFilter;
import dev.collab_sync.global.filter.LoginFilter;
import dev.collab_sync.global.jwt.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtUtil jwtUtil;

    //AuthenticationManager가 인자로 받을 AuthenticationConfiguration 객체 생성자 주입
    private final AuthenticationConfiguration authenticationConfiguration;

    //AuthenticationManager Bean 등록
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration configuration) throws Exception {
        return configuration.getAuthenticationManager();
    }

    @Bean
    public BCryptPasswordEncoder bCryptPasswordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {

        /*
          CSRF : 사용자의 의도와 무관하게 요청이 전달되는 공격을 방지하는 보안 기능.
         */

        // CSRF Disable
        http.csrf((auth) -> auth.disable());

        // From 로그인 방식 Disable
        http.formLogin((auth) -> auth.disable());

        // http basic 인증 방식 Disable
        http.httpBasic((auth) -> auth.disable());

        // URL별 접근 제어
        http.authorizeHttpRequests((auth) -> auth
                .requestMatchers("/login", "/", "/join").permitAll()    // 무조건 인가
                .requestMatchers("/admin").hasRole("ADMIN")                 // Admin만 인가
                .anyRequest().authenticated());                               // 로그인한 사용자만

        // JwtFilter 등록
        http.addFilterBefore(new JwtFilter(jwtUtil), LoginFilter.class);

        // Loing 필터 등록 (UsernamePasswordAuthenticationFilter 대체용도 : addFilterAt)
        http.addFilterAt(new LoginFilter(authenticationManager(authenticationConfiguration), jwtUtil), UsernamePasswordAuthenticationFilter.class);

        // 세션 설정 (Stateless)
        http.sessionManagement((session) -> session
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS));

        return http.build();
    }

}
