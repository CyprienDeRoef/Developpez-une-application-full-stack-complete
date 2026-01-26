package com.openclassrooms.mddapi.entities;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import com.fasterxml.jackson.annotation.JsonIgnore;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

@Schema(description = "Entité utilisateur")
@Entity
@Table(name = "users", uniqueConstraints = {
        @UniqueConstraint(columnNames = "email")
})
public class User implements UserDetails {

    @Schema(description = "Identifiant unique de l'utilisateur", example = "1")
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Schema(description = "Nom de l'utilisateur", example = "John Doe")
    @Column(name = "name", nullable = false)
    private String name;

    @Schema(description = "Adresse email de l'utilisateur", example = "user@example.com")
    @Column(name = "email", nullable = false, unique = true)
    private String email;

    @Schema(hidden = true)
    @JsonIgnore
    @Column(name = "password", nullable = false)
    private String password;

    @Schema(description = "Date de création du compte", example = "2024-01-15T10:30:00")
    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @Schema(description = "Date de dernière mise à jour", example = "2024-01-20T14:45:00")
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    @Schema(hidden = true)
    @JsonIgnore
    @ManyToMany
    @JoinTable(name = "user_subscriptions", joinColumns = @JoinColumn(name = "user_id"), inverseJoinColumns = @JoinColumn(name = "topic_id"))
    private List<Topic> subscriptions = new ArrayList<>();

    public User() {
    }

    public User(String name, String email, String password) {
        this.name = name;
        this.email = email;
        this.password = password;
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }

    // Getters and setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }

    public List<Topic> getSubscriptions() {
        return subscriptions;
    }

    public void setSubscriptions(List<Topic> subscriptions) {
        this.subscriptions = subscriptions;
    }

    // UserDetails implementation
    @Schema(hidden = true)
    @JsonIgnore
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return Collections.emptyList();
    }

    @Schema(hidden = true)
    @JsonIgnore
    @Override
    public String getUsername() {
        return email;
    }

    @Schema(hidden = true)
    @JsonIgnore
    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Schema(hidden = true)
    @JsonIgnore
    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Schema(hidden = true)
    @JsonIgnore
    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Schema(hidden = true)
    @JsonIgnore
    @Override
    public boolean isEnabled() {
        return true;
    }
}