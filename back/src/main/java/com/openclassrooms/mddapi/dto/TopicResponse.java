package com.openclassrooms.mddapi.dto;

import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDateTime;

@Schema(description = "Réponse contenant les informations d'un sujet")
public class TopicResponse {

    @Schema(description = "Identifiant du sujet", example = "1")
    private Long id;

    @Schema(description = "Nom du sujet", example = "Science")
    private String name;

    @Schema(description = "Description du sujet", example = "Articles scientifiques et découvertes")
    private String description;

    @Schema(description = "Date de création", example = "2024-01-15T10:30:00")
    private LocalDateTime createdAt;

    @Schema(description = "Date de dernière mise à jour", example = "2024-01-20T14:45:00")
    private LocalDateTime updatedAt;

    public TopicResponse() {
    }

    public TopicResponse(Long id, String name, String description, LocalDateTime createdAt, LocalDateTime updatedAt) {
        this.id = id;
        this.name = name;
        this.description = description;
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
}
