package com.example.UserManagement.Controller;

import com.example.UserManagement.Dto.Request.*;
import com.example.UserManagement.Dto.Response.*;
import com.example.UserManagement.Service.AuthenticationService;
import com.nimbusds.jose.JOSEException;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.text.ParseException;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
@Slf4j
@Validated
public class AuthenticationController {
    private final AuthenticationService authenticationService;
    @PostMapping("/register")
    public ApiResponse<UserResponse> register(@RequestBody @Valid UserCreateRequest request){
        return ApiResponse.<UserResponse>builder()
                .message("registered successfully")
                .result(authenticationService.register(request))
                .build();
    }

    @PostMapping("/email-verification")//token của emailVerifyToken
    public ApiResponse<Void> emailVerification(@RequestBody VerifyOtpRequest request){
        authenticationService.verifyEmail(request);
        return ApiResponse.<Void>builder()
                .message("Email verification successfully")
                .build();
    }


    @PostMapping("/login")
    public ApiResponse<SignInResponse> login(
            @RequestBody @Valid LogInRequest loginRequest, HttpServletResponse response){
        return ApiResponse.<SignInResponse>builder()
                .message("Login successfully")
                .result(authenticationService.logIn(loginRequest,response))
                .build();
    }

    @PostMapping("/logout")
    public ApiResponse<String> logout(@RequestBody @Valid LogOutRequest request, HttpServletResponse response) throws ParseException, JOSEException {
        authenticationService.logOut(request,response);
        return ApiResponse.<String>builder()
                .message("Logout successfully")
                .build();
    }

    @PostMapping("/refresh")
    public ApiResponse<RefreshTokenResponse> refresh(
            @CookieValue(name = "refreshToken", required = false) String refreshToken) throws ParseException, JOSEException {
        return ApiResponse.<RefreshTokenResponse>builder()
                .message("RefreshToken Successfully")
                .result(authenticationService.refreshToken(refreshToken))
                .build();
    }

    @PostMapping("/introspect")
    public ApiResponse<IntrospectResponse> introspect(@RequestBody @Valid IntrospectRequest request) throws ParseException {
        return ApiResponse.<IntrospectResponse>builder()
                .message("Introspect token successfully")
                .result(authenticationService.introspect(request))
                .build();
    }
}
