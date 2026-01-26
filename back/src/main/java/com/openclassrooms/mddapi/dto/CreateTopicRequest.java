package com.openclassrooms.mddapi.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

@Schema(description = "Requête de création d'un sujet")
public class CreateTopicRequest {

    @Schema(description = "Nom du sujet", example = "Science", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "Name is required")
    @Size(min = 2, max = 50, message = "Name must be between 2 and 50 characters")
    private String name;

    @Schema(description = "Description du sujet", example = "Articles scientifiques et découvertes")
    @Size(max = 500, message = "Description must not exceed 500 characters")
    private String description;

    public CreateTopicRequest() {
    }

    public CreateTopicRequest(String name, String description) {
        this.name = name;
        this.description = description;
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
}
