package com.example.UserManagement.Repository;

import com.example.UserManagement.Entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User,Long> {
    Optional<User> findByEmail(String email);

    @Query("SELECT u FROM User u WHERE u.fullName LIKE %:keyword% OR u.email LIKE %:keyword%")
    Page<User> findAllByKeyword(Pageable pageable,@Param("keyword") String keyword);
}
