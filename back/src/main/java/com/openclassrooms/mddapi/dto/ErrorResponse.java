package com.openclassrooms.mddapi.dto;

import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDateTime;

@Schema(description = "Réponse d'erreur")
public class ErrorResponse {

    @Schema(description = "Message d'erreur", example = "Resource not found")
    private String message;

    @Schema(description = "Timestamp de l'erreur", example = "2024-01-15T10:30:00")
    private LocalDateTime timestamp;

    @Schema(description = "Code de statut HTTP", example = "404")
    private int status;

    public ErrorResponse() {
        this.timestamp = LocalDateTime.now();
    }

    public ErrorResponse(String message) {
        this.message = message;
        this.timestamp = LocalDateTime.now();
    }

    public ErrorResponse(String message, int status) {
        this.message = message;
        this.status = status;
        this.timestamp = LocalDateTime.now();
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }
}
