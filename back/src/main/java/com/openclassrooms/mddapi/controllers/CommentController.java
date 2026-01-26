package com.openclassrooms.mddapi.controllers;

import com.openclassrooms.mddapi.dto.CommentResponse;
import com.openclassrooms.mddapi.dto.CreateCommentRequest;
import com.openclassrooms.mddapi.services.CommentService;
import com.openclassrooms.mddapi.services.auth.CurrentUserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/posts/{postId}/comments")
@CrossOrigin(origins = "*")
@Tag(name = "Comments", description = "Endpoints pour la gestion des commentaires")
public class CommentController {

    private final CommentService commentService;
    private final CurrentUserService currentUserService;

    public CommentController(CommentService commentService, CurrentUserService currentUserService) {
        this.commentService = commentService;
        this.currentUserService = currentUserService;
    }

    /**
     * Get all comments for a post
     */
    @Operation(summary = "Récupérer tous les commentaires d'un post", description = "Retourne la liste de tous les commentaires d'un post")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Liste des commentaires récupérée", content = @Content(schema = @Schema(implementation = CommentResponse.class))),
            @ApiResponse(responseCode = "404", description = "Post non trouvé", content = @Content)
    })
    @GetMapping
    public ResponseEntity<List<CommentResponse>> getCommentsByPostId(@PathVariable Long postId) {
        List<CommentResponse> comments = commentService.getCommentsByPostId(postId);
        return ResponseEntity.ok(comments);
    }

    /**
     * Create a new comment
     */
    @Operation(summary = "Créer un nouveau commentaire", description = "Crée un nouveau commentaire pour le post", security = @SecurityRequirement(name = "bearerAuth"))
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Commentaire créé", content = @Content(schema = @Schema(implementation = CommentResponse.class))),
            @ApiResponse(responseCode = "400", description = "Données invalides", content = @Content),
            @ApiResponse(responseCode = "401", description = "Non autorisé", content = @Content),
            @ApiResponse(responseCode = "404", description = "Post non trouvé", content = @Content)
    })
    @PostMapping
    public ResponseEntity<CommentResponse> createComment(
            @PathVariable Long postId,
            @Valid @RequestBody CreateCommentRequest request) {
        Long userId = currentUserService.getCurrentUser().getId();
        CommentResponse comment = commentService.createComment(postId, request, userId);
        return ResponseEntity.ok(comment);
    }
}
