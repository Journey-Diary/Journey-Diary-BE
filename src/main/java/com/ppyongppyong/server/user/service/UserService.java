package com.ppyongppyong.server.user.service;

import com.ppyongppyong.server.common.UserDetailsImpl;
import com.ppyongppyong.server.common.entity.RefreshToken;
import com.ppyongppyong.server.common.entity.TokenType;
import com.ppyongppyong.server.common.exception.CustomException;
import com.ppyongppyong.server.common.exception.massage.ErrorMsg;
import com.ppyongppyong.server.common.jwt.JwtUtil;
import com.ppyongppyong.server.common.repository.RefreshTokenRepository;
import com.ppyongppyong.server.user.dto.*;
import com.ppyongppyong.server.user.entity.User;
import com.ppyongppyong.server.user.repository.UserRepository;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseCookie;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static com.ppyongppyong.server.common.exception.massage.ErrorMsg.NOT_FOUND_USER;
import static com.ppyongppyong.server.common.exception.massage.SuccessMsg.SIGN_UP_SUCCESS;
import static com.ppyongppyong.server.common.exception.massage.SuccessMsg.TOKEN_REISSUE_SUCCESS;

@Slf4j
@Service
@Validated
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final RefreshTokenRepository refreshTokenRepository;
    private final JwtUtil jwtUtil;
    private final PasswordEncoder passwordEncoder;
    private final UserMapper userMapper;

    //회원가입
    @Transactional
    public String signup(@Valid UserSignupRequestDto userSignupRequestDto) {
        if (!userSignupRequestDto.getPassword().equals(userSignupRequestDto.getPasswordCheck())) {
            throw new CustomException(ErrorMsg.NOT_MATCH_CHECK_PASSWORD);
        }

        String password = passwordEncoder.encode(userSignupRequestDto.getPassword());

        Optional<User> foundUserId = userRepository.findByUserId(userSignupRequestDto.getUserId());
        if (foundUserId.isPresent()) {
            User user = foundUserId.get();
            if (user.isActive()) {
                user.updateIsActive(false);
                userRepository.save(user);
                return SIGN_UP_SUCCESS.getMessage();
            }
            throw new CustomException(ErrorMsg.DUPLICATE_USER_ID);
        }

        User user = userMapper.signupRequestDtoToEntity(password, userSignupRequestDto);
        userRepository.save(user);
        return SIGN_UP_SUCCESS.getMessage();
    }

    // 로그인
    @Transactional
    public UserLoginResponseDto login(UserLoginRequestDto userLoginRequestDto, HttpServletResponse response) throws UnsupportedEncodingException {
        String accountUserId = userLoginRequestDto.getUserId();
        String password = userLoginRequestDto.getPassword();

        User user = userRepository.findByUserId(accountUserId).orElseThrow(
                () -> new CustomException(ErrorMsg.NOT_FOUND_USER)
        );

        if (!passwordEncoder.matches(password, user.getPassword())) {
            throw new CustomException(ErrorMsg.NOT_MATCH_PASSWORD);
        }

        String accessToken = getAccessToken(user, response);
        String refreshToken = getRefreshToken(user, response);


//        response.addHeader(JwtUtil.AUTHORIZATION_HEADER, jwtUtil.createToken(account.getAccountUserId(), AccountRoleEnum.USER));
        return userMapper.entityToLoginResponseDto(user, accessToken, refreshToken);


    }

    private String getRefreshToken(User user, HttpServletResponse response) throws UnsupportedEncodingException {
        String createRefreshToken = jwtUtil.createToken(user.getUserId(), user.getUserRole(), TokenType.REFRESH);
        ResponseCookie cookie = ResponseCookie.from(
                        JwtUtil.REFRESH_HEADER, URLEncoder.encode(createRefreshToken, "UTF-8"))
                .path("/")
                .httpOnly(true)
                .sameSite("None")
                .secure(true)
                .maxAge(JwtUtil.REFRESH_TOKEN_TIME)
                .build();
        response.addHeader("Set-Cookie", cookie.toString());

        Optional<RefreshToken> refreshToken = refreshTokenRepository.findByUserId(user.getUserId());
        Long expiration = JwtUtil.REFRESH_TOKEN_TIME / 1000;

        if (refreshToken.isPresent()) {
            RefreshToken saveedRefreshToken = refreshToken.get().updateToken(createRefreshToken);
            refreshTokenRepository.save(saveedRefreshToken);
        } else {
            RefreshToken newRefreshToken = new RefreshToken(createRefreshToken, user.getUserId());
            refreshTokenRepository.save(newRefreshToken);
        }

        return createRefreshToken;
    }

    private String getAccessToken(User user, HttpServletResponse response) throws UnsupportedEncodingException {
        String createdAccessToken = jwtUtil.createToken(user.getUserId(), user.getUserRole(), TokenType.ACCESS);

        log.info("token : " + createdAccessToken);

        // 4. JWT 토큰 반환
        ResponseCookie cookie = ResponseCookie.from(
                        JwtUtil.AUTHORIZATION_HEADER, URLEncoder.encode(createdAccessToken, "UTF-8"))
                .path("/")
                .httpOnly(true)
                .sameSite("None")
                .secure(true)
                .maxAge(JwtUtil.ACCESS_TOKEN_TIME)
                .build();
        response.addHeader("Set-Cookie", cookie.toString());

        return createdAccessToken;
    }

    //TODO: 현재는 사용하지 않는 로직이지만 추후 Redis 적용 시 필요
//    public static String getClientIp(HttpServletRequest request) {
//        String clientIp = null;
//        boolean isIpInHeader = false;
//
//        List<String> headerList = new ArrayList<>();
//        headerList.add("X-Forwarded-For");
//        headerList.add("HTTP_CLIENT_IP");
//        headerList.add("HTTP_X_FORWARDED_FOR");
//        headerList.add("HTTP_X_FORWARDED");
//        headerList.add("HTTP_FORWARDED_FOR");
//        headerList.add("HTTP_FORWARDED");
//        headerList.add("Proxy-Client-IP");
//        headerList.add("WL-Proxy-Client-IP");
//        headerList.add("HTTP_VIA");
//        headerList.add("IPV6_ADR");
//
//        for (String header : headerList) {
//            clientIp = request.getHeader(header);
//            if (StringUtils.hasText(clientIp) && !clientIp.equals("unknown")) {
//                isIpInHeader = true;
//                break;
//            }
//        }
//
//        if (!isIpInHeader) {
//            clientIp = request.getRemoteAddr();
//        }
//
//        return clientIp;
//    }

    // 액세스 토큰 재발급
    @Transactional(readOnly = true)
    public String reIssueAccessToken(User user, HttpServletResponse httpServletResponse) throws UnsupportedEncodingException {
        // TODO: Redis 서버에서 RefreshToken 점검.
        RefreshToken refreshToken = refreshTokenRepository.findByUserId(user.getUserId()).orElseThrow(
                () -> new CustomException(ErrorMsg.NOT_FOUND_REFRESH_TOKEN));

        //TODO: Redis 사용 시 필요한 코드
        //Ip가 다르면 RefreshToken 제거.
//        if (!getClientIp(httpServletRequest).equals(refreshToken.getIp())) {
//            refreshTokenRepository.deleteById(account.getAccountUserId());
//            throw new CustomException(ErrorMsg.NotMatchedIp);
//        }

        getAccessToken(user, httpServletResponse);
        return TOKEN_REISSUE_SUCCESS.getMessage();
    }

    public List<UserDataDto> searchUser(User searcher, String searchStr) {
        // 유저 인증
        User user = userRepository.findByUserId(searcher.getUserId()).orElseThrow(() -> new CustomException(NOT_FOUND_USER));
        List<User> users = userRepository.findByUserIdContaining(searchStr);
        // 본인 제외?
        users.removeIf(u -> u.getId() == user.getId());

       return users.stream().map(u -> userMapper.entityToUserDataDto(u)).collect(Collectors.toList());
    }
}
