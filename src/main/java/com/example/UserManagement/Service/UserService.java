package com.example.UserManagement.Service;

import com.example.UserManagement.Dto.Request.ChangePasswordRequest;
import com.example.UserManagement.Dto.Request.UserCreateRequest;
import com.example.UserManagement.Dto.Request.UserUpdateRequest;
import com.example.UserManagement.Dto.Response.PageResponse;
import com.example.UserManagement.Dto.Response.UserResponse;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.concurrent.TimeUnit;

public interface UserService {
    PageResponse<List<UserResponse>> getAllUsersByKeyword(int page,int size,String keyword,String sort);
    UserResponse createUser(UserCreateRequest request);
    UserResponse findById(Long userId);
    UserResponse updateUser(UserUpdateRequest request, MultipartFile file);
    void softDelete(Long userId);
    void unSoftDelete(Long userId);
    void delete(Long userId);
    UserResponse changePassword(ChangePasswordRequest request);
    UserResponse getMyInfo();
}
