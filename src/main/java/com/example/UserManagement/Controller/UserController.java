package com.example.UserManagement.Controller;

import com.example.UserManagement.Dto.Request.ChangePasswordRequest;
import com.example.UserManagement.Dto.Request.UserCreateRequest;
import com.example.UserManagement.Dto.Request.UserUpdateRequest;
import com.example.UserManagement.Dto.Response.ApiResponse;
import com.example.UserManagement.Dto.Response.PageResponse;
import com.example.UserManagement.Dto.Response.UserResponse;
import com.example.UserManagement.Service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
@Slf4j
@Validated
public class UserController {
    private final UserService userService;

    @PreAuthorize("hasAuthority('ADMIN')")
    @GetMapping("/getAllUsersByKeywords")
    public ApiResponse<PageResponse<List<UserResponse>>> getAllUsersByKeyword(
            @RequestParam(defaultValue = "1", required = false) int page,
            @RequestParam(defaultValue = "10", required = false) int size,
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) String sorts) {
        log.info("Request get all of users with sort by multiple columns");
        return ApiResponse.<PageResponse<List<UserResponse>>>builder()
                .message("list users")
                .result(userService.getAllUsersByKeyword(page, size,keyword, sorts))
                .build();
    }

    @PostMapping("/addUser")
    public ApiResponse<UserResponse> createUser(
            @RequestBody @Valid UserCreateRequest request){
        return ApiResponse.<UserResponse>builder()
                .message("Create user successfully")
                .result(userService.createUser(request))
                .build();
    }

    @PreAuthorize("hasAuthority('ADMIN')")
    @GetMapping("/{userId}")
    public ApiResponse<UserResponse> getUser(
            @PathVariable("userId") final Long userId) {
        return ApiResponse.<UserResponse>builder()
                .message("Detail user")
                .result(userService.findById(userId))
                .build();
    }

    @PatchMapping("/update")
    @PreAuthorize("hasAuthority('USER') or hasAuthority('ADMIN')")
    public ApiResponse<UserResponse> updateUser(
            @RequestPart MultipartFile file,
            @RequestPart UserUpdateRequest request) {
        return ApiResponse.<UserResponse>builder()
                .message("Profile updated successfully")
                .result(userService.updateUser(request,file))
                .build();

    }

    @DeleteMapping("/delete/{userId}")
    @PreAuthorize("hasAuthority('ADMIN')")
    public ApiResponse<String> deleteUser(
            @PathVariable("userId") final Long userId){
        userService.delete(userId);
        return ApiResponse.<String>builder().
                result("User has been deleted").
                build();
    }

    @PatchMapping("/changePassword")
    @PreAuthorize("isAuthenticated()")
    public ApiResponse<UserResponse> changePassword(
            @RequestBody @Valid ChangePasswordRequest request){
        return ApiResponse.<UserResponse>builder()
                .message("Change password successfully")
                .result(userService.changePassword(request))
                .build();
    }

    @GetMapping (value = "/myInfo")
    @PreAuthorize("isAuthenticated()")
    public ApiResponse<UserResponse> getMyInfo () {
        return ApiResponse.<UserResponse>builder()
                .message("User detail")
                .result(userService.getMyInfo())
                .build();
    }
    @PreAuthorize("hasAuthority('ADMIN')")
    @PatchMapping("/softDelete/{userId}")
    public ApiResponse<String> softDelete(
            @PathVariable("userId") final Long userId){
        userService.softDelete(userId);
        return ApiResponse.<String>builder().
                result("User has been deleted").
                build();
    }

    @PreAuthorize("hasAuthority('ADMIN')")
    @PatchMapping("/unSoftDelete/{userId}")
    public ApiResponse<String> unSoftDelete(
            @PathVariable("userId") final Long userId){
        userService.unSoftDelete(userId);
        return ApiResponse.<String>builder().
                result("User has been deleted").
                build();
    }
    @PreAuthorize("hasAuthority('ADMIN')")
    @PatchMapping("/delete/{userId}")
    public ApiResponse<String> delete(
            @PathVariable("userId") final Long userId){
        userService.delete(userId);
        return ApiResponse.<String>builder().
                result("User has been deleted").
                build();
    }
}
