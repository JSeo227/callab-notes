package dev.collab_sync.domain.refresh;

import dev.collab_sync.util.JwtUtil;
import io.jsonwebtoken.ExpiredJwtException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.Arrays;

import static dev.collab_sync.util.CookieUtil.createCookie;

@Slf4j
@RestController
@RequiredArgsConstructor
public class ReissueController {

    private final JwtUtil jwtUtil;
    private final RefreshService refreshService;

    @PostMapping("/reissue")
    public ResponseEntity<?> reissue(HttpServletRequest request, HttpServletResponse response) {

//         = null;

        Cookie[] cookies = request.getCookies();
        /*for (Cookie cookie : cookies) {
            if (cookie.getName().equals("refresh")) {
                refresh = cookie.getValue();
            }

        }*/
        String refresh = Arrays.stream(cookies)
                .filter(cookie -> cookie.getName().equals("refresh"))
                .map(cookie -> cookie.getValue())
                .findFirst()
                .orElse(null);

        if (refresh == null) {
            return new ResponseEntity<>("Refresh token null", HttpStatus.BAD_REQUEST);
        }

        //expired check
        try {
            jwtUtil.isTokenExpired(refresh);
        } catch (ExpiredJwtException e) {
            //response status code
            return new ResponseEntity<>("Refresh token expired", HttpStatus.BAD_REQUEST);
        }

        // 토큰이 refresh인지 확인 (발급시 페이로드에 명시)
        String tokenType = jwtUtil.getTypeFromToken(refresh);

        if (!tokenType.equals("refresh")) {
            //response status code
            return new ResponseEntity<>("Invalid refresh token", HttpStatus.BAD_REQUEST);
        }

        //DB에 저장되어 있는지 확인
        Boolean isExist = refreshService.exists(refresh);
        if (!isExist) {
            //response body
            return new ResponseEntity<>("Refresh token not found in database", HttpStatus.BAD_REQUEST);
        }

        String email = jwtUtil.getEmailFromToken(refresh);
        String role = jwtUtil.getRoleFromToken(refresh);

        //make new JWT
        String newAccess = jwtUtil.generateAccessToken(email, role);
        String makeNewRefresh = jwtUtil.generateRefreshToken(email, role);

        //Refresh 토큰 저장 DB에 기존의 Refresh 토큰 삭제 후 새 Refresh 토큰 저장
        refreshService.delete(refresh);
        refreshService.save(email, refresh);

        //response
        response.setHeader("access", newAccess);
        response.addCookie(createCookie("refresh", makeNewRefresh, false));

        return new ResponseEntity<>(HttpStatus.OK);
    }

}
