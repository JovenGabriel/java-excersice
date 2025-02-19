package com.threeit.users.entities;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Data
@Entity
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "AppUser")
public class User {

    @Id
    @GeneratedValue
    @Column(columnDefinition = "UUID")
    private UUID id;
    @Column(nullable = false)
    private String name;
    @Email
    @Column(nullable = false, unique = true)
    private String email;
    @Column(nullable = false)
    private String password;
    @OneToMany
    private List<Phone> phones;
    private LocalDateTime lastLogin;
    private String token;
    private boolean isActive;
    @CreationTimestamp
    private LocalDateTime createdAt;
    @UpdateTimestamp
    private LocalDateTime  updatedAt;
}
