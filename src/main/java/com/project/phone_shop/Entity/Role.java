package com.project.phone_shop.Entity;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.Set;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@Entity
public class Role {
    @Id
    @Column(name = "role_id",  nullable = false)
     String id;
    @Column(name = "name", nullable = false)
    String name;

     @Column(name = "description")
    String description;

    @ManyToMany
    Set<Permission> permissions;
}
