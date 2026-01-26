package com.openclassrooms.mddapi.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDateTime;

@Schema(description = "Réponse contenant les informations d'un commentaire")
public class CommentResponse {

    @Schema(description = "Identifiant unique du commentaire", example = "1")
    private Long id;

    @Schema(description = "Contenu du commentaire")
    private String content;

    @Schema(description = "Identifiant de l'auteur", example = "1")
    private Long authorId;

    @Schema(description = "Nom de l'auteur", example = "Jean Dupont")
    private String authorName;

    @Schema(description = "Identifiant du post", example = "1")
    private Long postId;

    @Schema(description = "Date de création", example = "2024-01-15T10:30:00")
    private LocalDateTime createdAt;

    public CommentResponse() {
    }

    public CommentResponse(Long id, String content, Long authorId, String authorName, Long postId,
            LocalDateTime createdAt) {
        this.id = id;
        this.content = content;
        this.authorId = authorId;
        this.authorName = authorName;
        this.postId = postId;
        this.createdAt = createdAt;
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public Long getAuthorId() {
        return authorId;
    }

    public void setAuthorId(Long authorId) {
        this.authorId = authorId;
    }

    public String getAuthorName() {
        return authorName;
    }

    public void setAuthorName(String authorName) {
        this.authorName = authorName;
    }

    public Long getPostId() {
        return postId;
    }

    public void setPostId(Long postId) {
        this.postId = postId;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}
