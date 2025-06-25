package com.example.UserManagement.Enums;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public enum ErrorCode {
    UNCATEGORIZED_EXCEPTION(9999,"Uncategorized exception",HttpStatus.INTERNAL_SERVER_ERROR),
    INVALID_EMAIL(1000,"Invalid email address",HttpStatus.BAD_REQUEST),
    USER_NOT_FOUND(1001,"User not found",HttpStatus.NOT_FOUND),
    UNAUTHENTICATED(1002,"Unauthenticated",HttpStatus.UNAUTHORIZED),
    UNAUTHORIZED(1003,"You do not have permission",HttpStatus.FORBIDDEN),
    EMAIL_NOT_MATCH(1004,"Email does not match",HttpStatus.BAD_REQUEST),
    PASSWORD_NOT_MACTH(1005,"Password does not macth",HttpStatus.BAD_REQUEST),
    TOKEN_NOT_VALID(1006,"Token not valid",HttpStatus.BAD_REQUEST);

    private int code;
    private String message;
    private HttpStatus statusCode;

    ErrorCode(int code,String message,HttpStatus statusCode){
        this.code = code;
        this.message = message;
        this.statusCode = statusCode;
    }
}
