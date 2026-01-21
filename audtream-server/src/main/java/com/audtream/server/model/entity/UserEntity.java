package com.audtream.server.model.entity;

import jakarta.persistence.*;
import java.time.LocalDate;

@Entity
@Table(name = "users")
public class UserEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String email;

    @Column(unique = true, nullable = false, length = 100)
    private String username;

    @Column(nullable = false, length = 255)
    private String password;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, columnDefinition = "ENUM('user', 'artist', 'admin') DEFAULT 'user'")
    private Role role = Role.USER;

    @Column(name = "is_verified")
    private Boolean isVerified = false;

    @Column(columnDefinition = "TEXT")
    private String biography;

    @Enumerated(EnumType.STRING)
    @Column(name = "subscription_type", columnDefinition = "ENUM('free', 'premium') DEFAULT 'free'")
    private SubscriptionType subscriptionType = SubscriptionType.FREE;

    @Column(name = "country_code", length = 2)
    private String countryCode;

    @Column(name = "birth_date")
    private LocalDate birthDate;

    // Constructors
    public UserEntity() {}

    public UserEntity(String email, String username, String password, Role role) {
        this.email = email;
        this.username = username;
        this.password = password;
        this.role = role;
    }

    // Enums
    public enum Role {
        USER, ARTIST, ADMIN
    }

    public enum SubscriptionType {
        FREE, PREMIUM
    }

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }

    public Role getRole() { return role; }
    public void setRole(Role role) { this.role = role; }

    public Boolean getVerified() { return isVerified; }
    public void setVerified(Boolean verified) { isVerified = verified; }

    public String getBiography() { return biography; }
    public void setBiography(String biography) { this.biography = biography; }

    public SubscriptionType getSubscriptionType() { return subscriptionType; }
    public void setSubscriptionType(SubscriptionType subscriptionType) { this.subscriptionType = subscriptionType; }

    public String getCountryCode() { return countryCode; }
    public void setCountryCode(String countryCode) { this.countryCode = countryCode; }

    public LocalDate getBirthDate() { return birthDate; }
    public void setBirthDate(LocalDate birthDate) { this.birthDate = birthDate; }
}
