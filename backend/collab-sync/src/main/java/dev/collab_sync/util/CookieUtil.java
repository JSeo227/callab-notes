package dev.collab_sync.util;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.util.Optional;

public class CookieUtil {

    // 기본 쿠키 설정 상수
    public static final int DEFAULT_MAX_AGE = 24 * 60 * 60; // 1일 (초 단위)
    public static final String DEFAULT_PATH = "/";

    /**
     * 쿠키 생성
     */
    public static Cookie createCookie(String key, String value) {
        return createCookie(key, value, DEFAULT_MAX_AGE, DEFAULT_PATH, true, false, true);
    }

    public static Cookie createCookie(String key, String value, String path) {
        return createCookie(key, value, DEFAULT_MAX_AGE, path, true, false, true);
    }

    public static Cookie createCookie(String key, String value, boolean setPath) {
        return createCookie(key, value, DEFAULT_MAX_AGE, DEFAULT_PATH, setPath, true, false);
    }

    public static Cookie createCookie(String key, String value, int maxAge) {
        return createCookie(key, value, maxAge, DEFAULT_PATH, true, false, true);
    }

    public static Cookie createCookie(String key, String value, int maxAge, String path) {
        return createCookie(key, value, maxAge, path, true, false, true);
    }

    public static Cookie createCookie(String key, String value, int maxAge, boolean setPath) {
        return createCookie(key, value, maxAge, DEFAULT_PATH, setPath, true, false);
    }

    /**
     * 세션 쿠키 생성 (브라우저 종료시 삭제)
     */
    public static Cookie createSessionCookie(String key, String value) {
        return createCookie(key, value, -1, DEFAULT_PATH, true, false, true);
    }

    public static Cookie createSessionCookie(String key, String value, String path) {
        return createCookie(key, value, -1, path, true, false, true);
    }

    public static Cookie createSessionCookie(String key, String value, boolean setPath) {
        return createCookie(key, value, -1, DEFAULT_PATH, setPath, true, false);
    }

    /**
     * HTTPS 전용 보안 쿠키 생성
     */
    public static Cookie createSecureCookie(String key, String value) {
        return createCookie(key, value, DEFAULT_MAX_AGE, DEFAULT_PATH, true, true, true);
    }

    public static Cookie createSecureCookie(String key, String value, String path) {
        return createCookie(key, value, DEFAULT_MAX_AGE, path, true, true, true);
    }

    public static Cookie createSecureCookie(String key, String value, boolean setPath) {
        return createCookie(key, value, DEFAULT_MAX_AGE, DEFAULT_PATH, setPath, true, true);
    }

    public static Cookie createCookie(String key, String value, int maxAge, String path,
                                      boolean setPath, boolean httpOnly, boolean secure) {
        Cookie cookie = new Cookie(key, value);

        if (maxAge >= 0) {
            cookie.setMaxAge(maxAge);
        }

        // setPath가 true일 때만 path 설정
        if (setPath) {
            // path가 null이거나 빈값이면 DEFAULT_PATH 사용
            String cookiePath = (path == null || path.trim().isEmpty()) ? DEFAULT_PATH : path;
            cookie.setPath(cookiePath);
        }

        cookie.setHttpOnly(httpOnly);
        cookie.setSecure(secure);

        return cookie;
    }

    /**
     * 응답에 쿠키 추가
     */
    public static void addCookie(HttpServletResponse response, String key, String value) {
        Cookie cookie = createCookie(key, value);
        response.addCookie(cookie);
    }

    public static void addCookie(HttpServletResponse response, String key, String value, String path) {
        Cookie cookie = createCookie(key, value, path);
        response.addCookie(cookie);
    }

    public static void addCookie(HttpServletResponse response, String key, String value, boolean setPath) {
        Cookie cookie = createCookie(key, value, setPath);
        response.addCookie(cookie);
    }

    public static void addCookie(HttpServletResponse response, String key, String value, int maxAge) {
        Cookie cookie = createCookie(key, value, maxAge);
        response.addCookie(cookie);
    }

    public static void addCookie(HttpServletResponse response, String key, String value, int maxAge, String path) {
        Cookie cookie = createCookie(key, value, maxAge, path);
        response.addCookie(cookie);
    }

    public static void addCookie(HttpServletResponse response, String key, String value, int maxAge, boolean setPath) {
        Cookie cookie = createCookie(key, value, maxAge, setPath);
        response.addCookie(cookie);
    }

    /**
     * 쿠키 값 조회
     */
    public static Optional<String> getCookieValue(HttpServletRequest request, String key) {
        Cookie[] cookies = request.getCookies();

        if (cookies == null) {
            return Optional.empty();
        }

        for (Cookie cookie : cookies) {
            if (key.equals(cookie.getName())) {
                return Optional.of(cookie.getValue());
            }
        }

        return Optional.empty();
    }

    /**
     * 쿠키 조회
     */
    public static Optional<Cookie> getCookie(HttpServletRequest request, String key) {
        Cookie[] cookies = request.getCookies();

        if (cookies == null) {
            return Optional.empty();
        }

        for (Cookie cookie : cookies) {
            if (key.equals(cookie.getName())) {
                return Optional.of(cookie);
            }
        }

        return Optional.empty();
    }

    /**
     * 쿠키 삭제
     */
    public static void deleteCookie(HttpServletResponse response, String key) {
        deleteCookie(response, key, DEFAULT_PATH);
    }

    public static void deleteCookie(HttpServletResponse response, String key, String path) {
        Cookie cookie = new Cookie(key, "");
        cookie.setMaxAge(0);

        // path가 null이거나 빈값이면 DEFAULT_PATH 사용
        String cookiePath = (path == null || path.trim().isEmpty()) ? DEFAULT_PATH : path;
        cookie.setPath(cookiePath);

        response.addCookie(cookie);
    }

    /**
     * 쿠키 확인
     */
    public static boolean hasCookie(HttpServletRequest request, String key) {
        return getCookie(request, key).isPresent();
    }

    public static class TimeUnit {
        public static final int MINUTE = 60;
        public static final int HOUR = 60 * MINUTE;
        public static final int DAY = 24 * HOUR;
        public static final int WEEK = 7 * DAY;
        public static final int MONTH = 30 * DAY;

        public static int minutes(int minutes) {
            return minutes * MINUTE;
        }

        public static int hours(int hours) {
            return hours * HOUR;
        }

        public static int days(int days) {
            return days * DAY;
        }

        public static int weeks(int weeks) {
            return weeks * WEEK;
        }

        public static int months(int months) {
            return months * MONTH;
        }
    }
}