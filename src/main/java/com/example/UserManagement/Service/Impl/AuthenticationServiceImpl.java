package com.example.UserManagement.Service.Impl;

import com.example.UserManagement.Dto.Request.*;
import com.example.UserManagement.Dto.Response.IntrospectResponse;
import com.example.UserManagement.Dto.Response.RefreshTokenResponse;
import com.example.UserManagement.Dto.Response.SignInResponse;
import com.example.UserManagement.Dto.Response.UserResponse;
import com.example.UserManagement.Entity.User;
import com.example.UserManagement.Enums.ErrorCode;
import com.example.UserManagement.Exception.AppException;
import com.example.UserManagement.Repository.RolesRepository;
import com.example.UserManagement.Repository.UserRepository;
import com.example.UserManagement.Service.AuthenticationService;
import com.example.UserManagement.Service.JwtService;
import com.example.UserManagement.Service.RedisService;
import com.example.UserManagement.Service.UserService;
import com.nimbusds.jose.JOSEException;
import com.nimbusds.jwt.SignedJWT;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
@Slf4j
public class AuthenticationServiceImpl implements AuthenticationService {
    private final UserService userService;
    private final RolesRepository rolesRepository;
    private final JwtService jwtService;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final RedisService redisService;
    //private final EmailVerificationTokenService emailVerificationTokenService;
    //private final OutBoundIdentityClient outBoundIdentityClient;
    //private final OutBoundUserClient outBoundUserClient;

    @Value("${app.jwt.token.expires-in}")
    private Long accessTokenExpireIn;

    //private final String grant_Type ="authorization_code";

    @Override
    public UserResponse register(UserCreateRequest request) {
        return userService.createUser(request);
    }

    @Override
    public SignInResponse logIn(LogInRequest request, HttpServletResponse response) {
        User user=userRepository.findByEmail(request.getEmail())
                .orElseThrow(()->new AppException(ErrorCode.USER_NOT_FOUND));
//        if(user.getEmailVerifiedAt()==null){
//            throw new BadRequestException("Tài khoản của bạn chưa ược kích hoạt,vui lòng xác nhận mã OTP được gửi về mail");
//        }

        if(!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new AppException(ErrorCode.PASSWORD_NOT_MACTH);
        }

        String accessToken = jwtService.generateAccessToken(user);
        String refreshToken = jwtService.generateRefreshToken(user);

        user.setRefreshToken(refreshToken);
        userRepository.save(user);

        Cookie cookie=new Cookie("refreshToken",refreshToken);
        cookie.setPath("/");
        cookie.setDomain("localhost");
        cookie.setHttpOnly(true);//để true
        cookie.setMaxAge(60*60*24);
        cookie.setSecure(false);//true thì chỉ gửi qua https thôi

        response.addCookie(cookie);
        return SignInResponse.builder()
                .userId(user.getId())
                .accessToken(accessToken)
                .accessTokenExpireIn(accessTokenExpireIn)
                .roles(user.getUserHasRoles().stream().map(userHasRole -> userHasRole.getRole().getName()).collect(Collectors.toList()))
                .build();
    }

    @Override
    public void logOut(LogOutRequest request, HttpServletResponse response) throws ParseException {
        if(StringUtils.isBlank(request.getAccessToken())){
            throw new AppException(ErrorCode.TOKEN_NOT_VALID);
        }
        String email = jwtService.extractEmail(request.getAccessToken());
        User user = userRepository.findByEmail(email)
                .orElseThrow(()->new AppException(ErrorCode.USER_NOT_FOUND));
        long accessTokenExpireIn = jwtService.extractTokenExpired(request.getAccessToken());
        if(accessTokenExpireIn > 0){
            String jwtId= SignedJWT.parse(request.getAccessToken()).getJWTClaimsSet().getJWTID();
            redisService.save(jwtId,request.getAccessToken(),accessTokenExpireIn, TimeUnit.MILLISECONDS);
            user.setRefreshToken(null);
            userRepository.save(user);
        }

        Cookie cookie=new Cookie("refreshToken","");
        cookie.setSecure(false);//chỉ https mới truyền được
        cookie.setPath("/");
        cookie.setHttpOnly(true);//đánh dấu httpOnly
        cookie.setMaxAge(0);
        response.addCookie(cookie);
    }

    @Override
    public void verifyEmail(VerifyOtpRequest request) {
        String email=redisService.get(request.getOtp());
        User user=userRepository.findByEmail(email)
                .orElseThrow(()->new AppException(ErrorCode.USER_NOT_FOUND));
        user.setActive(true);
        redisService.delete(request.getOtp());
    }

    @Override
    public RefreshTokenResponse refreshToken(String refreshToken) throws ParseException, JOSEException {
        if(StringUtils.isBlank(refreshToken)){
            throw new AppException(ErrorCode.TOKEN_NOT_VALID);
        }
        String email= jwtService.extractEmail(refreshToken);
        User user = userRepository.findByEmail(email)
                .orElseThrow(()->new AppException(ErrorCode.USER_NOT_FOUND));
        if(!Objects.equals(user.getRefreshToken(),refreshToken)||StringUtils.isBlank(user.getRefreshToken())){
            throw new AppException(ErrorCode.TOKEN_NOT_VALID);
        }
        if(!jwtService.verifyToken(refreshToken,user)){
            throw new AppException(ErrorCode.TOKEN_NOT_VALID);
        }
        String accessToken= jwtService.generateAccessToken(user);
        return RefreshTokenResponse.builder()
                .userId(user.getId())
                .accessToken(accessToken)
                .build();
    }

    @Override
    public IntrospectResponse introspect(IntrospectRequest request) throws ParseException {
        String email= jwtService.extractEmail(request.getAccessToken());
        User user = userRepository.findByEmail(email)
                .orElseThrow(()->new AppException(ErrorCode.USER_NOT_FOUND));
        boolean valid=true;
        List<String> roles=new ArrayList<>();
        try {
            roles = (List<String>) SignedJWT.parse(request.getAccessToken()).getJWTClaimsSet().getClaim("Authority");
            jwtService.verifyToken(request.getAccessToken(),user);
        } catch (JOSEException e) {
            valid=false;
        }
        return IntrospectResponse.builder()
                .valid(valid)
                .role(roles)
                .build();
    }
}
