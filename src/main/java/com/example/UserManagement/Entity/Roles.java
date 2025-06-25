package com.example.UserManagement.Entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.*;

import java.util.Set;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity(name = "Roles")
@Table(name = "roles")
public class Roles extends AbstractEntity<Long>{
    @Column(name = "name",nullable = false,unique = true)
    private String name;

    @Column(name = "description")
    private String description;

    @OneToMany(mappedBy = "role")
    private Set<UserHasRole> userHasRoles;
}
