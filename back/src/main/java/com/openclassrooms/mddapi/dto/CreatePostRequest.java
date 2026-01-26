package com.openclassrooms.mddapi.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;

import java.util.List;

@Schema(description = "Requête de création d'un post")
public class CreatePostRequest {

    @Schema(description = "Titre du post", example = "Les avancées en IA", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "Title is required")
    @Size(min = 3, max = 200, message = "Title must be between 3 and 200 characters")
    private String title;

    @Schema(description = "Contenu du post", example = "Le contenu détaillé...", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "Content is required")
    @Size(min = 10, message = "Content must be at least 10 characters")
    private String content;

    @Schema(description = "IDs des sujets associés", example = "[1, 2]", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotEmpty(message = "At least one topic is required")
    private List<Long> topicIds;

    public CreatePostRequest() {
    }

    public CreatePostRequest(String title, String content, List<Long> topicIds) {
        this.title = title;
        this.content = content;
        this.topicIds = topicIds;
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

    public List<Long> getTopicIds() {
        return topicIds;
    }

    public void setTopicIds(List<Long> topicIds) {
        this.topicIds = topicIds;
    }
}
