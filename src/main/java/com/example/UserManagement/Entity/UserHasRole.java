package com.example.UserManagement.Entity;


import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.*;

@Setter
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity(name = "UserHasRole")
@Table(name = "user_has_role")
public class UserHasRole extends AbstractEntity<Long>{
    @ManyToOne
    @JoinColumn(name = "user_id",nullable = false)
    private User user;

    @ManyToOne
    @JoinColumn(name = "role_id",nullable = false)
    private Roles role;
}
