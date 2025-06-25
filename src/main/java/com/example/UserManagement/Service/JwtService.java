package com.example.UserManagement.Service;


import com.example.UserManagement.Entity.User;
import com.nimbusds.jose.JOSEException;

import java.text.ParseException;

public interface JwtService {
    String generateAccessToken(User user);
    String generateRefreshToken(User user);
    String extractEmail(String accessToken) throws ParseException;
    boolean verifyToken(String token,User user) throws ParseException, JOSEException;
    long extractTokenExpired(String token);
}
