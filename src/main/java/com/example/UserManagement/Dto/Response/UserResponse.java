package com.example.UserManagement.Dto.Response;

import com.example.UserManagement.Entity.User;
import lombok.*;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserResponse {
    private Long userId;
    private String fullName;
    private String email;
    private String phoneNumber;
    private boolean isActive;
    private LocalDate dob;
    private String avatarUrl;
    private LocalDate createdAt;
    private List<String> roles;

    public static UserResponse convert(User user){
        List<String> roles=new ArrayList<>();
        user.getUserHasRoles().stream().map(userHasRole -> userHasRole.getRole().getName()).forEach(roles::add);
        return UserResponse.builder()
                .userId(user.getId())
                .fullName(user.getFullName())
                .email(user.getEmail())
                .dob(user.getDob())
                .phoneNumber(user.getPhoneNumber()!=null? user.getPhoneNumber() : "N/A")
                .avatarUrl(user.getAvatarUrl())
                .roles(roles)
                .build();
    }
}
