package com.ppyongppyong.server.common.jwt;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ppyongppyong.server.common.dto.StatusResponseDto;
import com.ppyongppyong.server.common.entity.TokenState;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Slf4j
@RequiredArgsConstructor //서비스 생성할 때 겸해서 같이 같이온다(repository)
public class JwtAuthFilter extends OncePerRequestFilter {

    private final JwtUtil jwtUtil;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {

        Cookie[] cookies = request.getCookies();
        String accessToken = null;
        String refreshToken = null;

        log.info("cookies: ", cookies);

        if(cookies != null) {
            for(Cookie cookie : cookies) {
                if(cookie == null) {
                    continue;
                }
                if(cookie.getName().equals(JwtUtil.AUTHORIZATION_HEADER)) {
                    accessToken = jwtUtil.resolveToken(cookie);
                } else if(cookie.getName().equals(JwtUtil.REFRESH_HEADER)) {
                    refreshToken = jwtUtil.resolveToken(cookie);
                }
            }
        }

        log.info("Access Token: {}", accessToken);
        log.info("Refresh Token: {}", refreshToken);

        if(accessToken != null) {
            if (jwtUtil.validateToken(accessToken) == TokenState.VALID) {
                setAuthentication(jwtUtil.getUserInfoFromToken(accessToken).getSubject());
            } else if (jwtUtil.validateToken(accessToken) == TokenState.EXPIRED) {
                ResponseCookie responseCookie = ResponseCookie.from(JwtUtil.AUTHORIZATION_HEADER, null)
                        .path("/")
                        .httpOnly(true)
                        .sameSite("None")
                        .secure(true)
                        .maxAge(1)
                        .build();
                response.addHeader("Set-Cookie", responseCookie.toString());
                jwtExceptionHandler(response, "NEED REISSUE", HttpStatus.SEE_OTHER);
                return;
            }
        } else if (refreshToken != null) {
            if (jwtUtil.validateRefreshToken(refreshToken)) {
                setAuthentication(jwtUtil.getUserInfoFromToken(refreshToken).getSubject());
            }
        }
        filterChain.doFilter(request, response);
    }

    //Authentication -> context -> SecurityContextHolder
    public void setAuthentication(String username) {
        SecurityContext context = SecurityContextHolder.createEmptyContext();
        Authentication authentication = jwtUtil.createAuthentication(username);
        context.setAuthentication(authentication);

        SecurityContextHolder.setContext(context);
    }
    public void jwtExceptionHandler(HttpServletResponse response, String msg, HttpStatus httpStatus) {
        response.setStatus(httpStatus.value());
        response.setContentType("application/json");
        try {
            String json = new ObjectMapper().writeValueAsString(new StatusResponseDto<>(httpStatus.value(), msg));
            response.getWriter().write(json);
        } catch (Exception e) {
            log.error(e.getMessage());
        }
    }

}