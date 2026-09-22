package com.project.phone_shop.Entity;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.Date;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@Entity
@Table(name = "invalidated_tokens")
public class InvalidatedToken {
    @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "invalidated_token_id")
    Long id;
    @Column(name = "token", nullable = false)
    Date expiryTime;
}
