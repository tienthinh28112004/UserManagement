package com.example.UserManagement.Service;

import com.example.UserManagement.Dto.Request.*;
import com.example.UserManagement.Dto.Response.IntrospectResponse;
import com.example.UserManagement.Dto.Response.RefreshTokenResponse;
import com.example.UserManagement.Dto.Response.SignInResponse;
import com.example.UserManagement.Dto.Response.UserResponse;
import com.nimbusds.jose.JOSEException;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.web.bind.annotation.CookieValue;

import java.text.ParseException;

public interface AuthenticationService {
    UserResponse register(UserCreateRequest request);
    SignInResponse logIn(LogInRequest request, HttpServletResponse response);
    void logOut(LogOutRequest request,HttpServletResponse response) throws ParseException;
    void verifyEmail(VerifyOtpRequest request);
    RefreshTokenResponse refreshToken(@CookieValue(name = "refreshToken") String refreshToken) throws ParseException, JOSEException;
    IntrospectResponse introspect(IntrospectRequest request) throws ParseException;
}
