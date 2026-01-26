package com.openclassrooms.mddapi.controllers;

import com.openclassrooms.mddapi.dto.CreatePostRequest;
import com.openclassrooms.mddapi.dto.PostResponse;
import com.openclassrooms.mddapi.services.PostService;
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
@RequestMapping("/api/posts")
@CrossOrigin(origins = "*")
@Tag(name = "Posts", description = "Endpoints pour la gestion des posts")
public class PostController {

    private final PostService postService;
    private final CurrentUserService currentUserService;

    public PostController(PostService postService, CurrentUserService currentUserService) {
        this.postService = postService;
        this.currentUserService = currentUserService;
    }

    /**
     * Get all posts
     */
    @Operation(summary = "Récupérer tous les posts", description = "Retourne la liste de tous les posts")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Liste des posts récupérée", content = @Content(schema = @Schema(implementation = PostResponse.class)))
    })
    @GetMapping
    public ResponseEntity<List<PostResponse>> getAllPosts() {
        List<PostResponse> posts = postService.getAllPosts();
        return ResponseEntity.ok(posts);
    }

    /**
     * Get a post by ID
     */
    @Operation(summary = "Récupérer un post par ID", description = "Retourne un post spécifique")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Post récupéré", content = @Content(schema = @Schema(implementation = PostResponse.class))),
            @ApiResponse(responseCode = "404", description = "Post non trouvé", content = @Content)
    })
    @GetMapping("/{id}")
    public ResponseEntity<PostResponse> getPostById(@PathVariable Long id) {
        PostResponse post = postService.getPostById(id);
        return ResponseEntity.ok(post);
    }

    /**
     * Create a new post
     */
    @Operation(summary = "Créer un nouveau post", description = "Crée un nouveau post pour l'utilisateur connecté", security = @SecurityRequirement(name = "bearerAuth"))
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Post créé", content = @Content(schema = @Schema(implementation = PostResponse.class))),
            @ApiResponse(responseCode = "400", description = "Données invalides", content = @Content),
            @ApiResponse(responseCode = "401", description = "Non autorisé", content = @Content)
    })
    @PostMapping
    public ResponseEntity<PostResponse> createPost(@Valid @RequestBody CreatePostRequest request) {
        Long userId = currentUserService.getCurrentUser().getId();
        PostResponse post = postService.createPost(request, userId);
        return ResponseEntity.ok(post);
    }

    /**
     * Update a post
     */
    @Operation(summary = "Mettre à jour un post", description = "Met à jour un post existant (réservé à l'auteur)", security = @SecurityRequirement(name = "bearerAuth"))
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Post mis à jour", content = @Content(schema = @Schema(implementation = PostResponse.class))),
            @ApiResponse(responseCode = "400", description = "Données invalides", content = @Content),
            @ApiResponse(responseCode = "401", description = "Non autorisé", content = @Content),
            @ApiResponse(responseCode = "403", description = "Accès refusé", content = @Content),
            @ApiResponse(responseCode = "404", description = "Post non trouvé", content = @Content)
    })
    @PutMapping("/{id}")
    public ResponseEntity<PostResponse> updatePost(@PathVariable Long id,
            @Valid @RequestBody CreatePostRequest request) {
        Long userId = currentUserService.getCurrentUser().getId();
        PostResponse post = postService.updatePost(id, request, userId);
        return ResponseEntity.ok(post);
    }

    /**
     * Delete a post
     */
    @Operation(summary = "Supprimer un post", description = "Supprime un post existant (réservé à l'auteur)", security = @SecurityRequirement(name = "bearerAuth"))
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Post supprimé"),
            @ApiResponse(responseCode = "401", description = "Non autorisé", content = @Content),
            @ApiResponse(responseCode = "403", description = "Accès refusé", content = @Content),
            @ApiResponse(responseCode = "404", description = "Post non trouvé", content = @Content)
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletePost(@PathVariable Long id) {
        Long userId = currentUserService.getCurrentUser().getId();
        postService.deletePost(id, userId);
        return ResponseEntity.ok().build();
    }

    /**
     * Get posts by author
     */
    @Operation(summary = "Récupérer les posts d'un auteur", description = "Retourne tous les posts d'un auteur spécifique")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Posts récupérés", content = @Content(schema = @Schema(implementation = PostResponse.class)))
    })
    @GetMapping("/author/{authorId}")
    public ResponseEntity<List<PostResponse>> getPostsByAuthor(@PathVariable Long authorId) {
        List<PostResponse> posts = postService.getPostsByAuthor(authorId);
        return ResponseEntity.ok(posts);
    }

    /**
     * Get posts by topic
     */
    @Operation(summary = "Récupérer les posts d'un sujet", description = "Retourne tous les posts associés à un sujet spécifique")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Posts récupérés", content = @Content(schema = @Schema(implementation = PostResponse.class)))
    })
    @GetMapping("/topic/{topicId}")
    public ResponseEntity<List<PostResponse>> getPostsByTopic(@PathVariable Long topicId) {
        List<PostResponse> posts = postService.getPostsByTopic(topicId);
        return ResponseEntity.ok(posts);
    }
}
