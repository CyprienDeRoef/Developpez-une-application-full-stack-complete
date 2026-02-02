package com.openclassrooms.mddapi.controllers;

import com.openclassrooms.mddapi.dto.CommentResponse;
import com.openclassrooms.mddapi.dto.CreateCommentRequest;
import com.openclassrooms.mddapi.entities.User;
import com.openclassrooms.mddapi.services.CommentService;
import com.openclassrooms.mddapi.services.auth.CurrentUserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CommentControllerTest {

    @Mock
    private CommentService commentService;

    @Mock
    private CurrentUserService currentUserService;

    @InjectMocks
    private CommentController commentController;

    private CommentResponse commentResponse;
    private CreateCommentRequest createCommentRequest;
    private User currentUser;

    @BeforeEach
    void setUp() {
        commentResponse = new CommentResponse();
        commentResponse.setId(1L);
        commentResponse.setContent("Test Comment");
        commentResponse.setAuthorId(1L);
        commentResponse.setAuthorName("Test User");
        commentResponse.setPostId(1L);
        commentResponse.setCreatedAt(LocalDateTime.now());

        createCommentRequest = new CreateCommentRequest();
        createCommentRequest.setContent("Test Comment");

        currentUser = new User();
        currentUser.setId(1L);
        currentUser.setEmail("test@example.com");
    }

    @Test
    void getCommentsByPostId_ShouldReturnListOfComments() {
        List<CommentResponse> comments = Arrays.asList(commentResponse);
        when(commentService.getCommentsByPostId(1L)).thenReturn(comments);

        ResponseEntity<List<CommentResponse>> response = commentController.getCommentsByPostId(1L);

        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(1, response.getBody().size());
        assertEquals("Test Comment", response.getBody().get(0).getContent());
        verify(commentService, times(1)).getCommentsByPostId(1L);
    }

    @Test
    void createComment_ShouldReturnCreatedComment() {
        when(currentUserService.getCurrentUser()).thenReturn(currentUser);
        when(commentService.createComment(eq(1L), any(CreateCommentRequest.class), eq(1L)))
                .thenReturn(commentResponse);

        ResponseEntity<CommentResponse> response = commentController.createComment(1L, createCommentRequest);

        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("Test Comment", response.getBody().getContent());
        assertEquals(1L, response.getBody().getPostId());
        verify(currentUserService, times(1)).getCurrentUser();
        verify(commentService, times(1)).createComment(eq(1L), any(CreateCommentRequest.class), eq(1L));
    }

    @Test
    void createComment_ShouldUseCurrentUserId() {
        when(currentUserService.getCurrentUser()).thenReturn(currentUser);
        when(commentService.createComment(eq(1L), any(CreateCommentRequest.class), eq(1L)))
                .thenReturn(commentResponse);

        commentController.createComment(1L, createCommentRequest);

        verify(commentService, times(1)).createComment(eq(1L), any(CreateCommentRequest.class), eq(1L));
    }
}
