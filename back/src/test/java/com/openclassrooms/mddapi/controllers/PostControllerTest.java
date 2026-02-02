package com.openclassrooms.mddapi.controllers;

import com.openclassrooms.mddapi.dto.CreatePostRequest;
import com.openclassrooms.mddapi.dto.PostResponse;
import com.openclassrooms.mddapi.entities.User;
import com.openclassrooms.mddapi.services.PostService;
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
class PostControllerTest {

    @Mock
    private PostService postService;

    @Mock
    private CurrentUserService currentUserService;

    @InjectMocks
    private PostController postController;

    private PostResponse postResponse;
    private CreatePostRequest createPostRequest;
    private User currentUser;

    @BeforeEach
    void setUp() {
        postResponse = new PostResponse();
        postResponse.setId(1L);
        postResponse.setTitle("Test Post");
        postResponse.setContent("Test Content");
        postResponse.setAuthorId(1L);
        postResponse.setAuthorName("Test User");
        postResponse.setTopicIds(Arrays.asList(1L));
        postResponse.setTopicNames(Arrays.asList("Java"));
        postResponse.setCreatedAt(LocalDateTime.now());

        createPostRequest = new CreatePostRequest();
        createPostRequest.setTitle("Test Post");
        createPostRequest.setContent("Test Content");
        createPostRequest.setTopicIds(Arrays.asList(1L));

        currentUser = new User();
        currentUser.setId(1L);
        currentUser.setEmail("test@example.com");
    }

    @Test
    void getAllPosts_ShouldReturnListOfPosts() {
        List<PostResponse> posts = Arrays.asList(postResponse);
        when(postService.getAllPosts()).thenReturn(posts);

        ResponseEntity<List<PostResponse>> response = postController.getAllPosts();

        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(1, response.getBody().size());
        assertEquals("Test Post", response.getBody().get(0).getTitle());
        verify(postService, times(1)).getAllPosts();
    }

    @Test
    void getPostById_ShouldReturnPost() {
        when(postService.getPostById(1L)).thenReturn(postResponse);

        ResponseEntity<PostResponse> response = postController.getPostById(1L);

        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(1L, response.getBody().getId());
        assertEquals("Test Post", response.getBody().getTitle());
        verify(postService, times(1)).getPostById(1L);
    }

    @Test
    void createPost_ShouldReturnCreatedPost() {
        when(currentUserService.getCurrentUser()).thenReturn(currentUser);
        when(postService.createPost(any(CreatePostRequest.class), eq(1L))).thenReturn(postResponse);

        ResponseEntity<PostResponse> response = postController.createPost(createPostRequest);

        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("Test Post", response.getBody().getTitle());
        verify(currentUserService, times(1)).getCurrentUser();
        verify(postService, times(1)).createPost(any(CreatePostRequest.class), eq(1L));
    }

    @Test
    void updatePost_ShouldReturnUpdatedPost() {
        when(currentUserService.getCurrentUser()).thenReturn(currentUser);
        when(postService.updatePost(eq(1L), any(CreatePostRequest.class), eq(1L))).thenReturn(postResponse);

        ResponseEntity<PostResponse> response = postController.updatePost(1L, createPostRequest);

        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("Test Post", response.getBody().getTitle());
        verify(currentUserService, times(1)).getCurrentUser();
        verify(postService, times(1)).updatePost(eq(1L), any(CreatePostRequest.class), eq(1L));
    }

    @Test
    void deletePost_ShouldReturnOk() {
        when(currentUserService.getCurrentUser()).thenReturn(currentUser);
        doNothing().when(postService).deletePost(1L, 1L);

        ResponseEntity<Void> response = postController.deletePost(1L);

        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        verify(currentUserService, times(1)).getCurrentUser();
        verify(postService, times(1)).deletePost(1L, 1L);
    }

    @Test
    void getPostsByAuthor_ShouldReturnListOfPosts() {
        List<PostResponse> posts = Arrays.asList(postResponse);
        when(postService.getPostsByAuthor(1L)).thenReturn(posts);

        ResponseEntity<List<PostResponse>> response = postController.getPostsByAuthor(1L);

        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(1, response.getBody().size());
        verify(postService, times(1)).getPostsByAuthor(1L);
    }

    @Test
    void getPostsByTopic_ShouldReturnListOfPosts() {
        List<PostResponse> posts = Arrays.asList(postResponse);
        when(postService.getPostsByTopic(1L)).thenReturn(posts);

        ResponseEntity<List<PostResponse>> response = postController.getPostsByTopic(1L);

        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(1, response.getBody().size());
        verify(postService, times(1)).getPostsByTopic(1L);
    }
}
