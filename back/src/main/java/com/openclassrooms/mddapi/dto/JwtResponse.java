package com.openclassrooms.mddapi.dto;

import io.swagger.v3.oas.annotations.media.Schema;

/**
 * DTO for JWT authentication response
 */
@Schema(description = "Réponse d'authentification avec token JWT")
public class JwtResponse {

    @Schema(description = "Token JWT pour l'authentification", example = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...")
    private String token;

    @Schema(description = "Type de token", example = "Bearer", defaultValue = "Bearer")
    private String type = "Bearer";

    @Schema(description = "Identifiant de l'utilisateur", example = "1")
    private Long id;

    @Schema(description = "Adresse email de l'utilisateur", example = "user@example.com")
    private String email;

    @Schema(description = "Nom de l'utilisateur", example = "John Doe")
    private String name;

    public JwtResponse(String token, Long id, String email, String name) {
        this.token = token;
        this.id = id;
        this.email = email;
        this.name = name;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}