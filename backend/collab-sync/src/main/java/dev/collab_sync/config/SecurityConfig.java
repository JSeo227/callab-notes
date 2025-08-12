package dev.collab_sync.config;

import dev.collab_sync.filter.JwtFilter;
import dev.collab_sync.filter.LoginFilter;
import dev.collab_sync.domain.refresh.RefreshService;
import dev.collab_sync.filter.LogoutFilter;
import dev.collab_sync.util.JwtUtil;
import dev.collab_sync.domain.login.LoginService;
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

import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;

import java.util.Collections;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final LoginService loginService;

    private final RefreshService refreshService;

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
          CORS : 서로 다른 출처(origin) 간에 안전하게 자원을 공유할 수 있도록 허용하는 보안 정책.
        */

        // CORS Setting
        http.cors((auth) -> auth.configurationSource((CorsConfigurationSource) request -> {
            CorsConfiguration configuration = new CorsConfiguration();

            configuration.setAllowedOrigins(Collections.singletonList("http://localhost:3000"));
            configuration.setAllowedMethods(Collections.singletonList("*"));
            configuration.setAllowCredentials(true);
            configuration.setAllowedHeaders(Collections.singletonList("*"));
            configuration.setMaxAge(3600L);
            configuration.setExposedHeaders(Collections.singletonList("Authorization"));
            return configuration;
        }));

        /*
          CSRF : 사용자의 의도와 무관하게 요청이 전달되는 공격을 방지하는 보안 기능.
         */

        // CSRF Disable
        http.csrf((auth) -> auth.disable());

        // form 로그인 방식 Disable
        http.formLogin((auth) -> auth.disable());

        // http basic 인증 방식 Disable
        http.httpBasic((auth) -> auth.disable());

        // form 로그아웃 방식 Disable
        http.logout((auth) -> auth.disable());

        // URL별 접근 제어
        http.authorizeHttpRequests((auth) -> auth
                .requestMatchers("/login", "/", "/join").permitAll()    // 무조건 인가
                .requestMatchers("/admin").hasRole("VIEWER")                 // Admin만 인가
                .requestMatchers("/reissue").permitAll()
                .anyRequest().authenticated());                               // 로그인한 사용자만

        // Jwt 필터 등록
        http.addFilterBefore(new JwtFilter(jwtUtil), LoginFilter.class);

        // Login 필터 등록 (UsernamePasswordAuthenticationFilter 대체용도 : addFilterAt)
        http.addFilterAt(new LoginFilter(authenticationManager(authenticationConfiguration), jwtUtil, loginService, refreshService), UsernamePasswordAuthenticationFilter.class);

        // Logout 필터 등록
        http.addFilterAt(new LogoutFilter(jwtUtil, refreshService, loginService), org.springframework.security.web.authentication.logout.LogoutFilter.class);

        // 세션 설정 (Stateless)
        http.sessionManagement((session) -> session
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS));

        return http.build();
    }

}
