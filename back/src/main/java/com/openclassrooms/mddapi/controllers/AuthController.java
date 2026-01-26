package com.openclassrooms.mddapi.controllers;

import com.openclassrooms.mddapi.dto.JwtResponse;
import com.openclassrooms.mddapi.dto.LoginRequest;
import com.openclassrooms.mddapi.dto.RegisterRequest;
import com.openclassrooms.mddapi.dto.UpdateProfileRequest;
import com.openclassrooms.mddapi.dto.UserDto;
import com.openclassrooms.mddapi.entities.User;
import com.openclassrooms.mddapi.services.auth.RegisterService;
import com.openclassrooms.mddapi.services.auth.LoginService;
import com.openclassrooms.mddapi.services.auth.CurrentUserService;
import com.openclassrooms.mddapi.services.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = "*")
@Tag(name = "Authentification", description = "Endpoints pour l'authentification et la gestion des utilisateurs")
public class AuthController {

    @Autowired
    private RegisterService registerService;

    @Autowired
    private LoginService loginService;

    @Autowired
    private CurrentUserService currentUserService;

    @Autowired
    private UserService userService;

    /**
     * Register a new user
     * 
     * @param registerRequest the registration request data
     * @return JWT token and user information
     */
    @Operation(summary = "Inscription d'un nouvel utilisateur", description = "Crée un nouveau compte utilisateur et retourne un token JWT")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Inscription réussie", content = @Content(schema = @Schema(implementation = JwtResponse.class))),
            @ApiResponse(responseCode = "400", description = "Données invalides", content = @Content)
    })
    @PostMapping("/register")
    public ResponseEntity<JwtResponse> registerUser(@Valid @RequestBody RegisterRequest registerRequest) {
        JwtResponse response = registerService.registerUser(registerRequest);
        return ResponseEntity.ok(response);
    }

    /**
     * Authenticate user login
     * 
     * @param loginRequest the login request data
     * @return JWT token and user information
     */
    @Operation(summary = "Connexion d'un utilisateur", description = "Authentifie un utilisateur et retourne un token JWT")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Connexion réussie", content = @Content(schema = @Schema(implementation = JwtResponse.class))),
            @ApiResponse(responseCode = "400", description = "Identifiants incorrects", content = @Content)
    })
    @PostMapping("/login")
    public ResponseEntity<JwtResponse> authenticateUser(@Valid @RequestBody LoginRequest loginRequest) {
        JwtResponse response = loginService.authenticateUser(loginRequest);
        return ResponseEntity.ok(response);
    }

    /**
     * Get current authenticated user information
     * 
     * @return user information
     */
    @Operation(summary = "Récupération du profil utilisateur", description = "Retourne les informations de l'utilisateur actuellement connecté", security = @SecurityRequirement(name = "bearerAuth"))
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Profil récupéré", content = @Content(schema = @Schema(implementation = User.class))),
            @ApiResponse(responseCode = "401", description = "Non autorisé", content = @Content)
    })
    @GetMapping("/me")
    public ResponseEntity<User> getCurrentUser() {
        User user = currentUserService.getCurrentUser();
        return ResponseEntity.ok(user);
    }

    /**
     * Logout endpoint
     * 
     * @return success message
     */
    @Operation(summary = "Déconnexion", description = "Déconnecte l'utilisateur (endpoint informatif, le token reste valide côté client)", security = @SecurityRequirement(name = "bearerAuth"))
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Déconnexion réussie")
    })
    @PostMapping("/logout")
    public ResponseEntity<Map<String, String>> logout() {
        return ResponseEntity.ok(Map.of("message", "Logged out successfully"));
    }

    /**
     * Update user profile
     * 
     * @param updateRequest the update request data
     * @return updated user information
     */
    @Operation(summary = "Mise à jour du profil", description = "Met à jour le profil de l'utilisateur connecté", security = @SecurityRequirement(name = "bearerAuth"))
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Profil mis à jour", content = @Content(schema = @Schema(implementation = UserDto.class))),
            @ApiResponse(responseCode = "400", description = "Données invalides", content = @Content),
            @ApiResponse(responseCode = "401", description = "Non autorisé", content = @Content)
    })
    @PutMapping("/me")
    public ResponseEntity<UserDto> updateProfile(@Valid @RequestBody UpdateProfileRequest updateRequest) {
        User currentUser = currentUserService.getCurrentUser();
        UserDto updatedUser = userService.updateProfile(currentUser.getId(), updateRequest);
        return ResponseEntity.ok(updatedUser);
    }
}