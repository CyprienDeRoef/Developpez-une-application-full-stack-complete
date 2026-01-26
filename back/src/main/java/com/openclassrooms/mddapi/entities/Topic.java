package com.openclassrooms.mddapi.entities;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Schema(description = "Entité thématique/sujet")
@Entity
@Table(name = "topics")
public class Topic {

    @Schema(description = "Identifiant unique du sujet", example = "1")
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Schema(description = "Nom du sujet", example = "Science")
    @Column(name = "name", nullable = false, unique = true)
    private String name;

    @Schema(description = "Description du sujet", example = "Articles scientifiques et découvertes")
    @Column(name = "description", columnDefinition = "TEXT")
    private String description;

    @Schema(description = "Date de création", example = "2024-01-15T10:30:00")
    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @Schema(description = "Date de dernière mise à jour", example = "2024-01-20T14:45:00")
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    @Schema(hidden = true)
    @ManyToMany(mappedBy = "topics")
    private List<Post> posts = new ArrayList<>();

    @Schema(hidden = true)
    @ManyToMany(mappedBy = "subscriptions")
    private List<User> subscribers = new ArrayList<>();

    public Topic() {
    }

    public Topic(String name, String description) {
        this.name = name;
        this.description = description;
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }

    // Getters and Setters
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

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
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

    public List<Post> getPosts() {
        return posts;
    }

    public void setPosts(List<Post> posts) {
        this.posts = posts;
    }

    public List<User> getSubscribers() {
        return subscribers;
    }

    public void setSubscribers(List<User> subscribers) {
        this.subscribers = subscribers;
    }
}
