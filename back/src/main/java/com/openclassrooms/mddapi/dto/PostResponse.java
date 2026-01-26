package com.openclassrooms.mddapi.dto;

import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDateTime;
import java.util.List;

@Schema(description = "Réponse contenant les informations d'un post")
public class PostResponse {

    @Schema(description = "Identifiant du post", example = "1")
    private Long id;

    @Schema(description = "Titre du post", example = "Les avancées en IA")
    private String title;

    @Schema(description = "Contenu du post", example = "Le contenu détaillé...")
    private String content;

    @Schema(description = "Identifiant de l'auteur", example = "1")
    private Long authorId;

    @Schema(description = "Nom de l'auteur", example = "John Doe")
    private String authorName;

    @Schema(description = "IDs des sujets", example = "[1, 2]")
    private List<Long> topicIds;

    @Schema(description = "Noms des sujets", example = "[\"Science\", \"Technology\"]")
    private List<String> topicNames;

    @Schema(description = "Date de création", example = "2024-01-15T10:30:00")
    private LocalDateTime createdAt;

    @Schema(description = "Date de dernière mise à jour", example = "2024-01-20T14:45:00")
    private LocalDateTime updatedAt;

    public PostResponse() {
    }

    public PostResponse(Long id, String title, String content, Long authorId, String authorName,
            List<Long> topicIds, List<String> topicNames, LocalDateTime createdAt, LocalDateTime updatedAt) {
        this.id = id;
        this.title = title;
        this.content = content;
        this.authorId = authorId;
        this.authorName = authorName;
        this.topicIds = topicIds;
        this.topicNames = topicNames;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
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

    public List<Long> getTopicIds() {
        return topicIds;
    }

    public void setTopicIds(List<Long> topicIds) {
        this.topicIds = topicIds;
    }

    public List<String> getTopicNames() {
        return topicNames;
    }

    public void setTopicNames(List<String> topicNames) {
        this.topicNames = topicNames;
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
