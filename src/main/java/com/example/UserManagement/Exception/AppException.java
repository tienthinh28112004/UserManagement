package com.example.UserManagement.Exception;

import com.example.UserManagement.Enums.ErrorCode;
import lombok.Getter;

@Getter
public class AppException extends RuntimeException{
    private ErrorCode errorCode;
    public AppException(ErrorCode errorCode){
        super(errorCode.getMessage());
        this.errorCode = errorCode;
    }
}
