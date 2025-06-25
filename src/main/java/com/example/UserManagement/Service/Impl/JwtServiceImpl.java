package com.example.UserManagement.Service.Impl;

import com.example.UserManagement.Entity.Roles;
import com.example.UserManagement.Entity.User;
import com.example.UserManagement.Entity.UserHasRole;
import com.example.UserManagement.Enums.ErrorCode;
import com.example.UserManagement.Exception.AppException;
import com.example.UserManagement.Service.JwtService;
import com.example.UserManagement.Service.RedisService;
import com.nimbusds.jose.*;
import com.nimbusds.jose.crypto.MACSigner;
import com.nimbusds.jose.crypto.MACVerifier;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.text.ParseException;
import java.util.*;

@Service
@RequiredArgsConstructor
@Slf4j
public class JwtServiceImpl implements JwtService {
    private final RedisService redisService;

    @Value("${app.secret}")
    private String secretKey;

    @Value("${app.jwt.token.expires-in}")
    private Long accessTokenExpireIn;

    @Value("${app.jwt.refresh-token.expires-in}")
    private Long refreshTokenExpireIn;
    @Override
    public String generateAccessToken(User user) {
        JWSHeader header = new JWSHeader((JWSAlgorithm.HS512));

        JWTClaimsSet claimsSet = new JWTClaimsSet.Builder()
                .subject(user.getEmail())
                .issuer("Tiến Thịnh")
                .issueTime(new Date())
                .expirationTime(new Date(new Date().getTime() + accessTokenExpireIn))
                .jwtID(UUID.randomUUID().toString())
                .claim("Authority",buildAuthority(user))
                .build();

        Payload payload = new Payload(claimsSet.toJSONObject());//chuyển về dạng json

        JWSObject jwsObject = new JWSObject(header,payload);

        try{
            jwsObject.sign(new MACSigner(secretKey));//ký chữ ký của mình
        } catch (JOSEException e) {
            throw new RuntimeException(e);
        }
        return jwsObject.serialize();
    }

    @Override
    public String generateRefreshToken(User user) {
        JWSHeader header = new JWSHeader(JWSAlgorithm.HS512);

        JWTClaimsSet claimsSet = new JWTClaimsSet.Builder()
                .subject(user.getEmail())
                .issuer("Tiến Thịnh")
                .issueTime(new Date())
                .expirationTime(new Date(new Date().getTime() + refreshTokenExpireIn))
                .jwtID(UUID.randomUUID().toString())
                .build();
        Payload payload = new Payload(claimsSet.toJSONObject());

        JWSObject jwsObject = new JWSObject(header,payload);

        try{
            jwsObject.sign(new MACSigner(secretKey));
        } catch (JOSEException e) {
            throw new RuntimeException(e);
        }
        return jwsObject.serialize();
    }

    @Override
    public String extractEmail(String accessToken) throws ParseException {
        SignedJWT signedJWT = SignedJWT.parse(accessToken);
        return signedJWT.getJWTClaimsSet().getSubject();
    }

    @Override
    public boolean verifyToken(String token, User user) throws JOSEException, ParseException {
        SignedJWT signedJWT = SignedJWT.parse(token);
        String jwtId = signedJWT.getJWTClaimsSet().getJWTID();
        if(StringUtils.isNotBlank(redisService.get(jwtId))){
            throw new AppException(ErrorCode.TOKEN_NOT_VALID);
        }
        String email = signedJWT.getJWTClaimsSet().getSubject();
        var expiration=signedJWT.getJWTClaimsSet().getExpirationTime();
        if(!Objects.equals(email,user.getEmail())){
            throw new AppException(ErrorCode.INVALID_EMAIL);
        }
        if(expiration.before(new Date())){
            throw  new RuntimeException();
        }
        return signedJWT.verify(new MACVerifier(secretKey));
    }

    @Override
    public long extractTokenExpired(String token) {
        //thời gian còn lại của token để lưu vào redis
        try{
            long expirationTime = SignedJWT.parse(token)
                    .getJWTClaimsSet().getExpirationTime().getTime();
            long currentTime = System.currentTimeMillis();
            return Math.max(expirationTime-currentTime,0);
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }
    }

    private List<String> buildAuthority(User user){
        List<String> authority = new ArrayList<>();
        user.getUserHasRoles().stream().map(UserHasRole::getRole).map(Roles::getName).forEach(authority::add);
        return authority;
    }
}
