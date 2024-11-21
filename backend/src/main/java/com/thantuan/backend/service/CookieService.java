package com.thantuan.backend.service;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.NonNull;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.Arrays;

@Service
public class CookieService {
    @Value("${project.jwt_cookie_expiration_time}")
    private long EXPIRATION_TIME;

    @Value("${project.jwt_cookie_refresh_token_time}")
    private long REFRESH_EXPIRATION_TIME;

    public String getJwtFromCookie(@NonNull HttpServletRequest request, String cookieName) {
        Cookie[] cookies = request.getCookies();
        if (cookies != null) {
            return Arrays.stream(cookies).filter(cookie -> cookie.getName()
                                                                .equals(cookieName))
                                                                .map(Cookie::getValue)
                                                                .findFirst()
                                                                .orElse(null);
        }
        return null;
    }

    public void setJwtInCookie(@NonNull HttpServletResponse response, String token) {
        resetSessionDefault(response);
        Cookie cookieJwt = new Cookie("jwt", token);
        cookieJwt.setHttpOnly(true);
//    cookieJwt.setSecure(true);
        cookieJwt.setPath("/");
        cookieJwt.setMaxAge((int) EXPIRATION_TIME / 1000);
        response.addCookie(cookieJwt);
    }

    public void setRefreshJwtInCookie(@NonNull HttpServletResponse response, String token) {
        resetSessionDefault(response);
        Cookie cookieRefreshJwt = new Cookie("jwt_refresh", token);
        cookieRefreshJwt.setHttpOnly(true);
//    cookieRefreshJwt.setSecure(true);
        cookieRefreshJwt.setPath("/");
        cookieRefreshJwt.setMaxAge((int) (REFRESH_EXPIRATION_TIME / 1000));
        response.addCookie(cookieRefreshJwt);

    }

    public void resetSessionDefault(@NonNull HttpServletResponse response) {
        Cookie session = new Cookie("JSESSIONID", null);
        session.setHttpOnly(true);
//    session.setSecure(true);
        session.setPath("/");
        session.setMaxAge(0);
        response.addCookie(session);
    }

    public void resetCookie(@NonNull HttpServletResponse response) {
        Cookie cookieJwt = new Cookie("jwt", null);
        cookieJwt.setHttpOnly(true);
//    cookieJwt.setSecure(true);
        cookieJwt.setPath("/");
        cookieJwt.setMaxAge(0);
        response.addCookie(cookieJwt);

        Cookie cookieRefreshJwt = new Cookie("jwt_refresh", null);
        cookieRefreshJwt.setHttpOnly(true);
//    cookieRefreshJwt.setSecure(true);
        cookieRefreshJwt.setPath("/");
        cookieRefreshJwt.setMaxAge(0);
        response.addCookie(cookieRefreshJwt);
    }
}
