package com.openclassrooms.mddapi.entities;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Schema(description = "Entité post")
@Entity
@Table(name = "posts")
public class Post {

    @Schema(description = "Identifiant unique du post", example = "1")
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Schema(description = "Titre du post", example = "Les dernières avancées en intelligence artificielle")
    @Column(name = "title", nullable = false)
    private String title;

    @Schema(description = "Contenu du post", example = "L'intelligence artificielle continue de progresser...")
    @Column(name = "content", nullable = false, columnDefinition = "TEXT")
    private String content;

    @Schema(description = "Auteur du post")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User author;

    @Schema(description = "Sujets du post")
    @ManyToMany(fetch = FetchType.LAZY, cascade = { CascadeType.PERSIST, CascadeType.MERGE })
    @JoinTable(name = "post_topics", joinColumns = @JoinColumn(name = "post_id"), inverseJoinColumns = @JoinColumn(name = "topic_id"))
    private List<Topic> topics = new ArrayList<>();

    @Schema(description = "Date de création", example = "2024-01-15T10:30:00")
    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @Schema(description = "Date de dernière mise à jour", example = "2024-01-20T14:45:00")
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    public Post() {
    }

    public Post(String title, String content, User author, List<Topic> topics) {
        this.title = title;
        this.content = content;
        this.author = author;
        this.topics = topics;
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

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public User getAuthor() {
        return author;
    }

    public void setAuthor(User author) {
        this.author = author;
    }

    public List<Topic> getTopics() {
        return topics;
    }

    public void setTopics(List<Topic> topics) {
        this.topics = topics;
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
}
