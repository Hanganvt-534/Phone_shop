package com.project.phone_shop.Entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.util.Set;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Table(name = "users")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_id",unique = true, nullable = false)
    private Long id;
    @Column(name = "username",unique = true, nullable = false)
    private String username;
    @Column(name = "password",nullable = false)
    private String password;
    @Column(name = "email",unique = true, nullable = false)
    private String email;
    @Column(name = "phone",unique = true, nullable = false)
    private String phone;
    @ManyToMany
    Set<Role> roles;
    @ManyToMany
    Set<Permission> permissions;
}
