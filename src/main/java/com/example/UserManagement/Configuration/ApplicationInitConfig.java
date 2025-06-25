package com.example.UserManagement.Configuration;

import com.example.UserManagement.Entity.Roles;
import com.example.UserManagement.Entity.User;
import com.example.UserManagement.Entity.UserHasRole;
import com.example.UserManagement.Enums.Role;
import com.example.UserManagement.Repository.RolesRepository;
import com.example.UserManagement.Repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

@RequiredArgsConstructor
@Configuration
@Slf4j
public class ApplicationInitConfig {
    private final RolesRepository rolesRepository;
    private final PasswordEncoder passwordEncoder;
    private final UserRepository userRepository;
    @Bean
    @Transactional
    @ConditionalOnProperty(
            prefix = "spring",
            value = "datasource.driver-class-name",
            havingValue = "com.mysql.cj.jdbc.Driver")
    ApplicationRunner initApplication() {
        log.info("Initializing application.....");
        return args -> {
            Optional<Roles> roleUser = rolesRepository.findByName(String.valueOf(Role.USER));
            if(roleUser.isEmpty()) {
                rolesRepository.save(Roles.builder()
                        .name(String.valueOf(Role.USER))
                        .description("User role")
                        .build());
            }

            Optional<Roles> roleAuthor = rolesRepository.findByName(String.valueOf(Role.MANAGER));
            if(roleAuthor.isEmpty()) {
                rolesRepository.save(Roles.builder()
                        .name(String.valueOf(Role.MANAGER))
                        .description("Manager role")
                        .build());
            }
            Optional<Roles> roleAdmin = rolesRepository.findByName(String.valueOf(Role.ADMIN));
            if (roleAdmin.isEmpty()) {
                // Tạo role ADMIN nếu chưa có
                Roles rolesAdmin = Roles.builder()
                        .name(String.valueOf(Role.ADMIN))
                        .description("Admin role")
                        .build();
                rolesRepository.save(rolesAdmin);

                Optional<User> existingAdmin = (userRepository.findByEmail("admin@gmail.com"));
                if (existingAdmin.isEmpty()) {
                    // Tạo user admin
                    User user = User.builder()
                            .email("admin@gmail.com")
                            .password(passwordEncoder.encode("Admin@123"))
                            .isActive(true)
                            .otp("000000")
                            .build();

                    Roles adminRoleEntity = rolesRepository.findByName(Role.ADMIN.name())
                            .orElseThrow(() -> new RuntimeException("Role ADMIN not found"));
                    Roles userRoleEntity = rolesRepository.findByName(Role.USER.name())
                            .orElseThrow(() -> new RuntimeException("Role USER not found"));
                    Roles authorRoleEntity = rolesRepository.findByName(Role.MANAGER.name())
                            .orElseThrow(() -> new RuntimeException("Role AUTHOR not found"));


                    Set<UserHasRole> userHasRoles = new HashSet<>();
                    userHasRoles.add(UserHasRole.builder().user(user).role(adminRoleEntity).build());
                    userHasRoles.add(UserHasRole.builder().user(user).role(userRoleEntity).build());
                    userHasRoles.add(UserHasRole.builder().user(user).role(authorRoleEntity).build());

                    user.setUserHasRoles(userHasRoles);
                    userRepository.save(user);
                    log.info("Admin account has been created.");
                } else {
                    log.info("Admin user already exists.");
                }
            }

            log.info("Application initialization completed .....");
        };
    }
}
