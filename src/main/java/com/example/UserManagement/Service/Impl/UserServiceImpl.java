package com.example.UserManagement.Service.Impl;

import com.example.UserManagement.Dto.Request.ChangePasswordRequest;
import com.example.UserManagement.Dto.Request.UserCreateRequest;
import com.example.UserManagement.Dto.Request.UserUpdateRequest;
import com.example.UserManagement.Dto.Response.PageResponse;
import com.example.UserManagement.Dto.Response.UserResponse;
import com.example.UserManagement.Entity.Roles;
import com.example.UserManagement.Entity.User;
import com.example.UserManagement.Entity.UserHasRole;
import com.example.UserManagement.Enums.ErrorCode;
import com.example.UserManagement.Enums.Role;
import com.example.UserManagement.Exception.AppException;
import com.example.UserManagement.Repository.RolesRepository;
import com.example.UserManagement.Repository.UserRepository;
import com.example.UserManagement.Service.UserService;
import com.example.UserManagement.Utils.SecurityUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
@Slf4j
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final RolesRepository rolesRepository;
    private final CloudinaryService cloudinaryService;
    private final PasswordEncoder passwordEncoder;
    private final UserRepository userRepository;
    private  final MailSenderService mailSenderService;

    @Override
    public PageResponse<List<UserResponse>> getAllUsersByKeyword(int page, int size, String keyword, String sorts) {
        List<Sort.Order> orders=new ArrayList<>();

        if(sorts!=null) {
            log.info("vào đến đấy?");
            Pattern pattern = Pattern.compile("(\\w+?)(:)(.*)");
            Matcher matcher = pattern.matcher(sorts);
            if (matcher.find()) {
                if (matcher.group(3).equalsIgnoreCase("asc")) {
                    orders.add(new Sort.Order(Sort.Direction.ASC, matcher.group(1)));
                } else {
                    orders.add(new Sort.Order(Sort.Direction.DESC, matcher.group(1)));
                }
            }
        }

        //Paging
        Pageable pageable= PageRequest.of(page-1,size,Sort.by(orders));
        Page<User> userPage=null;
        if(StringUtils.hasLength(keyword)&&keyword!=null){
            userPage =userRepository.findAllByKeyword(pageable,keyword);
        }else{
            userPage = userRepository.findAll(pageable);
        }
        List<UserResponse> userList=userPage.stream().map(UserResponse::convert).toList();
        return PageResponse.<List<UserResponse>>builder()
                .currentPage(page)
                .pageSize(size)
                .totalPages(userPage.getTotalPages())
                .items(userList)
                .totalElements((long) userList.size())
                .build();
    }

    @Override
    public UserResponse createUser(UserCreateRequest request) {
        User existingUser = userRepository.findByEmail(request.getEmail()).orElse(null);
        if(existingUser!=null){
            if(existingUser.getOtp() != null){
                throw new AppException(ErrorCode.UNAUTHORIZED);
            }else{
                try {
                    existingUser.setFullName(request.getFullName());
                    existingUser.setPassword(passwordEncoder.encode(request.getPassword()));
                    mailSenderService.sendEmailUser(existingUser);
                    return UserResponse.convert(existingUser);
                } catch (Exception e){
                    throw new AppException(ErrorCode.UNAUTHENTICATED);
                }
            }
        }

        User user=User.builder()
                .fullName(request.getFullName())
                .password(passwordEncoder.encode(request.getPassword()))
                .email(request.getEmail())
                .isActive(false)
                .avatarUrl("https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcSp3ztVtyMtzjiT_yIthons_zqTQ_TNZm4PS0LxFyFO0ozfM2S87W8QoL4&s")
                .build();
        userRepository.save(user);
        Roles role= rolesRepository.findByName(String.valueOf(Role.USER))
                .orElseThrow(()->new AppException(ErrorCode.USER_NOT_FOUND));

        Set<UserHasRole> userHasRoleList=new HashSet<>();
        UserHasRole userHasRole=UserHasRole.builder()
                .role(role)
                .user(user)
                .build();
        userHasRoleList.add(userHasRole);
        user.setUserHasRoles(userHasRoleList);

        userRepository.save(user);
        try {
            mailSenderService.sendEmailUser(user);
            log.info("User được lưu thành công");
        } catch (Exception e){
            throw new RuntimeException("Gửi Email thất bại vui lòng thử lại");
        }
        return UserResponse.convert(user);
    }

    @Override
    public UserResponse findById(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(()->new AppException(ErrorCode.USER_NOT_FOUND));
        return UserResponse.convert(user);
    }

    @Override
    public UserResponse updateUser(UserUpdateRequest request, MultipartFile file) {
        String email = SecurityUtils.getCurrentLogin()
                .orElseThrow(()->new AppException(ErrorCode.INVALID_EMAIL));
        User user = userRepository.findByEmail(email)
                .orElseThrow(()->new AppException(ErrorCode.USER_NOT_FOUND));
        String avatarUrl=null;
        if(file!=null){
            avatarUrl=cloudinaryService.uploadImage(file);
        }
        if(StringUtils.hasText(request.getFullName())&&!request.getFullName().equals(user.getFullName())){
            user.setFullName(request.getFullName());
        }
        if(request.getDob() != null && !Objects.equals(request.getDob(),user.getDob())){
            user.setDob(request.getDob());
        }
        if(StringUtils.hasText(request.getPhoneNumber())&&!request.getPhoneNumber().equals(user.getPhoneNumber())){
            user.setPhoneNumber(request.getPhoneNumber());
        }
        if(StringUtils.hasText(avatarUrl) && !avatarUrl.equals(user.getAvatarUrl())){
            user.setAvatarUrl(avatarUrl);
        }
        userRepository.save(user);
        return UserResponse.convert(user);
    }

    @Override
    public void softDelete(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(()->new AppException(ErrorCode.USER_NOT_FOUND));
        user.setActive(false);
        userRepository.save(user);
    }

    @Override
    public void unSoftDelete(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(()->new AppException(ErrorCode.USER_NOT_FOUND));
        user.setActive(true);
        userRepository.save(user);
    }

    @Override
    public void delete(Long userId) {
        userRepository.deleteById(userId);
    }

    @Override
    public UserResponse changePassword(ChangePasswordRequest request) {
        String email = SecurityUtils.getCurrentLogin()
                .orElseThrow(()->new AppException(ErrorCode.INVALID_EMAIL));
        if(!request.getEmail().equals(email)){
            throw new AppException(ErrorCode.EMAIL_NOT_MATCH);
        }
        if(!request.getNewPassword().equals(request.getNewPasswordConfirm())){
            throw new AppException(ErrorCode.PASSWORD_NOT_MACTH);
        }
        User user = userRepository.findByEmail(email)
                .orElseThrow(()->new AppException(ErrorCode.USER_NOT_FOUND));
        if(!passwordEncoder.matches(request.getPassword(), user.getPassword())){
            throw new AppException(ErrorCode.PASSWORD_NOT_MACTH);
        }
        user.setPassword(passwordEncoder.encode(request.getNewPassword()));
        userRepository.save(user);
        return UserResponse.convert(user);
    }

    @Override
    public UserResponse getMyInfo() {
        String email=SecurityUtils.getCurrentLogin()
                .orElseThrow(()->new AppException(ErrorCode.INVALID_EMAIL));
        User user=userRepository.findByEmail(email)
                .orElseThrow(()->new AppException(ErrorCode.USER_NOT_FOUND));
        return UserResponse.convert(user);
    }
}
