package com.ppyongppyong.server.common.jwt;

import com.ppyongppyong.server.common.UserDetailsServiceImpl;
import com.ppyongppyong.server.common.entity.RefreshToken;
import com.ppyongppyong.server.common.entity.TokenState;
import com.ppyongppyong.server.common.entity.TokenType;
import com.ppyongppyong.server.common.exception.CustomException;
import com.ppyongppyong.server.common.exception.massage.ErrorMsg;
import com.ppyongppyong.server.common.repository.RefreshTokenRepository;
import com.ppyongppyong.server.user.entity.UserRoleEnum;
import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import jakarta.servlet.http.Cookie;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.security.Key;
import java.util.Base64;
import java.util.Date;
import java.util.Optional;

@Slf4j
@Component
@RequiredArgsConstructor
public class JwtUtil {
    private final UserDetailsServiceImpl userDetailsService;
    private final RefreshTokenRepository refreshTokenRepository;
    public static final String AUTHORIZATION_HEADER = "Authorization";
    public static final String REFRESH_HEADER = "Refresh";
    public static final String AUTHORIZATION_KEY = "auth";
    private static final String BEARER_PREFIX = "Bearer ";
    //    private static final long TOKEN_TIME = 60 * 60 * 1000L * 24 * 365;
    public static final long ACCESS_TOKEN_TIME = 2 * 60 * 1000L;  // 2분
    public static final long REFRESH_TOKEN_TIME = 30 * 60 * 1000L; //30분

    @Value("${jwt.secret.key}")
    private String secretKey;
    private Key key;
    private final SignatureAlgorithm signatureAlgorithm = SignatureAlgorithm.HS256;

    @PostConstruct
    public void init() {
        byte[] bytes = Base64.getDecoder().decode(secretKey);
        key = Keys.hmacShaKeyFor(bytes);
    }

    // header 토큰을 가져오기
    public String resolveToken(Cookie cookie) throws UnsupportedEncodingException {
        String bearerToken = URLDecoder.decode(cookie.getValue(), "UTF-8");
        if (StringUtils.hasText(bearerToken) && bearerToken.startsWith(BEARER_PREFIX)) {
            return bearerToken.substring(7);
        }
        return null;
    }

//    public String resolveToken(HttpServletRequest request) {
//        String bearerToken = request.getHeader(AUTHORIZATION_HEADER);
//        if (StringUtils.hasText(bearerToken) && bearerToken.startsWith(BEARER_PREFIX)) {
//            return bearerToken.substring(7);
//        }
//        return null;
//    }


    // 토큰 생성
    public String createToken(String userName, UserRoleEnum role, TokenType tokenType) {
        Date date = new Date();
        long time = tokenType == TokenType.ACCESS ? ACCESS_TOKEN_TIME : REFRESH_TOKEN_TIME;

        return BEARER_PREFIX +
                Jwts.builder()
                        .setSubject(userName)
                        .claim(AUTHORIZATION_KEY, role)
                        .setExpiration(new Date(date.getTime() + time))
                        .setIssuedAt(date)
                        .signWith(key, signatureAlgorithm)
                        .compact();
    }

    // 토큰 검증
    public TokenState validateToken(String accessToken) {
        try {
            Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(accessToken);
            return TokenState.VALID;
        } catch (SecurityException | MalformedJwtException e) {
            log.info("Invalid JWT signature, 유효하지 않는 JWT 서명 입니다.");
            throw new CustomException(ErrorMsg.TOKEN_SECURITY_EXCEPTION_OR_MALFORMED_JWT_EXCEPTION);
        } catch (ExpiredJwtException e) {
            log.info("Expired JWT token, 만료된 JWT token 입니다.");
            return TokenState.EXPIRED;
        } catch (UnsupportedJwtException e) {
            log.info("Unsupported JWT token, 지원되지 않는 JWT 토큰 입니다.");
            throw new CustomException(ErrorMsg.TOKEN_UNSUPPORTED_JWT_EXCEPTION);
        } catch (IllegalArgumentException e) {
            log.info("JWT claims is empty, 잘못된 JWT 토큰 입니다.");
            throw new CustomException(ErrorMsg.TOKEN_ILLEGAL_ARGUMENT_EXCEPTION);
        }
//        catch (SignatureException e) {
//            log.info("SignatureExcepted JWT token, JWT 토큰의 문자열이 지워졌습니다.");
//        }
    }

    // refreshToken 토큰 검증
    public Boolean validateRefreshToken(String refreshToken) {
        // 1차 토큰 검증
        if (validateToken(refreshToken) != TokenState.VALID) {
            return false;
        }

        // DB에 저장한 토큰 비교
        Optional<RefreshToken> savedRefreshToken = refreshTokenRepository.findByUserId(getUserInfoFromToken(refreshToken).getSubject());

        return savedRefreshToken.isPresent() && refreshToken.equals(savedRefreshToken.get().getRefreshToken().substring(7));
    }

    public Claims getUserInfoFromToken(String token) {
        return Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token).getBody();
    }

    public Authentication createAuthentication(String email) {
        log.info(email);
        UserDetails userDetails = userDetailsService.loadUserByUsername(email);
        return new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
    }
}