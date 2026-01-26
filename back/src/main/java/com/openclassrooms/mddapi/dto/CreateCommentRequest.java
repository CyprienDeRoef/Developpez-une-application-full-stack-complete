package com.openclassrooms.mddapi.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;

@Schema(description = "Requête pour créer un commentaire")
public class CreateCommentRequest {

    @Schema(description = "Contenu du commentaire", example = "Très intéressant!")
    @NotBlank(message = "Le contenu ne peut pas être vide")
    private String content;

    public CreateCommentRequest() {
    }

    public CreateCommentRequest(String content) {
        this.content = content;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }
}
