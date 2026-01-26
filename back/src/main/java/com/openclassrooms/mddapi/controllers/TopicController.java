package com.openclassrooms.mddapi.controllers;

import com.openclassrooms.mddapi.dto.CreateTopicRequest;
import com.openclassrooms.mddapi.dto.TopicResponse;
import com.openclassrooms.mddapi.services.TopicService;
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
@RequestMapping("/api/topics")
@CrossOrigin(origins = "*")
@Tag(name = "Topics", description = "Endpoints pour la gestion des sujets/thématiques")
public class TopicController {

    private final TopicService topicService;
    private final CurrentUserService currentUserService;

    public TopicController(TopicService topicService, CurrentUserService currentUserService) {
        this.topicService = topicService;
        this.currentUserService = currentUserService;
    }

    /**
     * Get all topics
     */
    @Operation(summary = "Récupérer tous les sujets", description = "Retourne la liste de tous les sujets disponibles")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Liste des sujets récupérée", content = @Content(schema = @Schema(implementation = TopicResponse.class)))
    })
    @GetMapping
    public ResponseEntity<List<TopicResponse>> getAllTopics() {
        List<TopicResponse> topics = topicService.getAllTopics();
        return ResponseEntity.ok(topics);
    }

    /**
     * Get a topic by ID
     */
    @Operation(summary = "Récupérer un sujet par ID", description = "Retourne un sujet spécifique")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Sujet récupéré", content = @Content(schema = @Schema(implementation = TopicResponse.class))),
            @ApiResponse(responseCode = "404", description = "Sujet non trouvé", content = @Content)
    })
    @GetMapping("/{id}")
    public ResponseEntity<TopicResponse> getTopicById(@PathVariable Long id) {
        TopicResponse topic = topicService.getTopicById(id);
        return ResponseEntity.ok(topic);
    }

    /**
     * Create a new topic
     */
    @Operation(summary = "Créer un nouveau sujet", description = "Crée un nouveau sujet", security = @SecurityRequirement(name = "bearerAuth"))
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Sujet créé", content = @Content(schema = @Schema(implementation = TopicResponse.class))),
            @ApiResponse(responseCode = "400", description = "Données invalides ou sujet déjà existant", content = @Content),
            @ApiResponse(responseCode = "401", description = "Non autorisé", content = @Content)
    })
    @PostMapping
    public ResponseEntity<TopicResponse> createTopic(@Valid @RequestBody CreateTopicRequest request) {
        TopicResponse topic = topicService.createTopic(request);
        return ResponseEntity.ok(topic);
    }

    /**
     * Update a topic
     */
    @Operation(summary = "Mettre à jour un sujet", description = "Met à jour un sujet existant", security = @SecurityRequirement(name = "bearerAuth"))
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Sujet mis à jour", content = @Content(schema = @Schema(implementation = TopicResponse.class))),
            @ApiResponse(responseCode = "400", description = "Données invalides", content = @Content),
            @ApiResponse(responseCode = "401", description = "Non autorisé", content = @Content),
            @ApiResponse(responseCode = "404", description = "Sujet non trouvé", content = @Content)
    })
    @PutMapping("/{id}")
    public ResponseEntity<TopicResponse> updateTopic(@PathVariable Long id,
            @Valid @RequestBody CreateTopicRequest request) {
        TopicResponse topic = topicService.updateTopic(id, request);
        return ResponseEntity.ok(topic);
    }

    /**
     * Delete a topic
     */
    @Operation(summary = "Supprimer un sujet", description = "Supprime un sujet existant", security = @SecurityRequirement(name = "bearerAuth"))
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Sujet supprimé"),
            @ApiResponse(responseCode = "401", description = "Non autorisé", content = @Content),
            @ApiResponse(responseCode = "404", description = "Sujet non trouvé", content = @Content)
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTopic(@PathVariable Long id) {
        topicService.deleteTopic(id);
        return ResponseEntity.ok().build();
    }

    /**
     * Subscribe to a topic
     */
    @Operation(summary = "S'abonner à un sujet", description = "Permet à l'utilisateur connecté de s'abonner à un sujet", security = @SecurityRequirement(name = "bearerAuth"))
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Abonnement réussi"),
            @ApiResponse(responseCode = "400", description = "Déjà abonné", content = @Content),
            @ApiResponse(responseCode = "401", description = "Non autorisé", content = @Content),
            @ApiResponse(responseCode = "404", description = "Sujet non trouvé", content = @Content)
    })
    @PostMapping("/{id}/subscribe")
    public ResponseEntity<Void> subscribeToTopic(@PathVariable Long id) {
        Long userId = currentUserService.getCurrentUser().getId();
        topicService.subscribeToTopic(id, userId);
        return ResponseEntity.ok().build();
    }

    /**
     * Unsubscribe from a topic
     */
    @Operation(summary = "Se désabonner d'un sujet", description = "Permet à l'utilisateur connecté de se désabonner d'un sujet", security = @SecurityRequirement(name = "bearerAuth"))
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Désabonnement réussi"),
            @ApiResponse(responseCode = "400", description = "Pas abonné", content = @Content),
            @ApiResponse(responseCode = "401", description = "Non autorisé", content = @Content),
            @ApiResponse(responseCode = "404", description = "Sujet non trouvé", content = @Content)
    })
    @DeleteMapping("/{id}/subscribe")
    public ResponseEntity<Void> unsubscribeFromTopic(@PathVariable Long id) {
        Long userId = currentUserService.getCurrentUser().getId();
        topicService.unsubscribeFromTopic(id, userId);
        return ResponseEntity.ok().build();
    }

    /**
     * Get user's subscribed topics
     */
    @Operation(summary = "Récupérer les abonnements de l'utilisateur", description = "Retourne tous les sujets auxquels l'utilisateur est abonné", security = @SecurityRequirement(name = "bearerAuth"))
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Abonnements récupérés", content = @Content(schema = @Schema(implementation = TopicResponse.class))),
            @ApiResponse(responseCode = "401", description = "Non autorisé", content = @Content)
    })
    @GetMapping("/subscriptions")
    public ResponseEntity<List<TopicResponse>> getUserSubscriptions() {
        Long userId = currentUserService.getCurrentUser().getId();
        List<TopicResponse> topics = topicService.getUserSubscriptions(userId);
        return ResponseEntity.ok(topics);
    }
}
