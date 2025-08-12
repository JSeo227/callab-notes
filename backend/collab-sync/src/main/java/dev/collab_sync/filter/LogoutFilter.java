package dev.collab_sync.filter;

import dev.collab_sync.domain.login.LoginService;
import dev.collab_sync.domain.refresh.RefreshService;
import dev.collab_sync.util.CookieUtil;
import dev.collab_sync.util.JwtUtil;
import io.jsonwebtoken.ExpiredJwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.filter.GenericFilterBean;

import java.io.IOException;
import java.util.Arrays;

@Slf4j
@RequiredArgsConstructor
public class LogoutFilter extends GenericFilterBean {

    private final JwtUtil jwtUtil;
    private final RefreshService refreshService;
    private final LoginService loginService;

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        doFilter((HttpServletRequest) request, (HttpServletResponse) response, chain);
    }

    public void doFilter(HttpServletRequest request, HttpServletResponse response, FilterChain chain) throws IOException, ServletException {
        //path and method verify
        String requestUri = request.getRequestURI();
        if (!requestUri.matches("^\\/logout$")) {
            chain.doFilter(request, response);
            return;
        }

        String requestMethod = request.getMethod();
        if (!requestMethod.equals("POST")) {
            chain.doFilter(request, response);
            return;
        }

        //get refresh token
        Cookie[] cookies = request.getCookies();
        String refresh = Arrays.stream(cookies)
                .filter((cookie -> cookie.getName().equals("refresh")))
                .map(cookie -> cookie.getValue())
                .findFirst()
                .orElse(null);

        //refresh null check
        if (refresh == null) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            return;
        }

        //expired check
        try {
            jwtUtil.isTokenExpired(refresh);
        } catch (ExpiredJwtException e) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            return;
        }

        // 토큰이 refresh인지 확인 (발급시 페이로드에 명시)
        String category = jwtUtil.getTypeFromToken(refresh);
        if (!category.equals("refresh")) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            return;
        }

        //DB에 저장되어 있는지 확인
        Boolean isExist = refreshService.exists(refresh);
        if (!isExist) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            return;
        }

        String email = jwtUtil.getEmailFromToken(refresh);
        loginService.logout(email);

        //로그아웃 진행
        //Refresh 토큰 DB에서 제거
        refreshService.delete(refresh);

        //Refresh 토큰 Cookie 값 0
        CookieUtil.deleteCookie(response, "refresh");
//        Cookie cookie = new Cookie("refresh", null);
//        cookie.setMaxAge(0);
//        cookie.setPath("/");

//        response.addCookie(cookie);
        response.setStatus(HttpServletResponse.SC_OK);
    }
}