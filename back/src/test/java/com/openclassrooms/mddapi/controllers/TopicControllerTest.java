package com.openclassrooms.mddapi.controllers;

import com.openclassrooms.mddapi.dto.CreateTopicRequest;
import com.openclassrooms.mddapi.dto.TopicResponse;
import com.openclassrooms.mddapi.entities.User;
import com.openclassrooms.mddapi.services.TopicService;
import com.openclassrooms.mddapi.services.auth.CurrentUserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TopicControllerTest {

    @Mock
    private TopicService topicService;

    @Mock
    private CurrentUserService currentUserService;

    @InjectMocks
    private TopicController topicController;

    private TopicResponse topicResponse;
    private CreateTopicRequest createTopicRequest;
    private User currentUser;

    @BeforeEach
    void setUp() {
        topicResponse = new TopicResponse();
        topicResponse.setId(1L);
        topicResponse.setName("Java");
        topicResponse.setDescription("Java programming");

        createTopicRequest = new CreateTopicRequest();
        createTopicRequest.setName("Java");
        createTopicRequest.setDescription("Java programming");

        currentUser = new User();
        currentUser.setId(1L);
        currentUser.setEmail("test@example.com");
    }

    @Test
    void getAllTopics_ShouldReturnListOfTopics() {
        List<TopicResponse> topics = Arrays.asList(topicResponse);
        when(topicService.getAllTopics()).thenReturn(topics);

        ResponseEntity<List<TopicResponse>> response = topicController.getAllTopics();

        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(1, response.getBody().size());
        assertEquals("Java", response.getBody().get(0).getName());
        verify(topicService, times(1)).getAllTopics();
    }

    @Test
    void getTopicById_ShouldReturnTopic() {
        when(topicService.getTopicById(1L)).thenReturn(topicResponse);

        ResponseEntity<TopicResponse> response = topicController.getTopicById(1L);

        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(1L, response.getBody().getId());
        assertEquals("Java", response.getBody().getName());
        verify(topicService, times(1)).getTopicById(1L);
    }

    @Test
    void createTopic_ShouldReturnCreatedTopic() {
        when(topicService.createTopic(any(CreateTopicRequest.class))).thenReturn(topicResponse);

        ResponseEntity<TopicResponse> response = topicController.createTopic(createTopicRequest);

        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("Java", response.getBody().getName());
        verify(topicService, times(1)).createTopic(any(CreateTopicRequest.class));
    }

    @Test
    void updateTopic_ShouldReturnUpdatedTopic() {
        when(topicService.updateTopic(eq(1L), any(CreateTopicRequest.class))).thenReturn(topicResponse);

        ResponseEntity<TopicResponse> response = topicController.updateTopic(1L, createTopicRequest);

        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("Java", response.getBody().getName());
        verify(topicService, times(1)).updateTopic(eq(1L), any(CreateTopicRequest.class));
    }

    @Test
    void deleteTopic_ShouldReturnOk() {
        doNothing().when(topicService).deleteTopic(1L);

        ResponseEntity<Void> response = topicController.deleteTopic(1L);

        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        verify(topicService, times(1)).deleteTopic(1L);
    }

    @Test
    void subscribeToTopic_ShouldReturnOk() {
        when(currentUserService.getCurrentUser()).thenReturn(currentUser);
        doNothing().when(topicService).subscribeToTopic(1L, 1L);

        ResponseEntity<Void> response = topicController.subscribeToTopic(1L);

        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        verify(currentUserService, times(1)).getCurrentUser();
        verify(topicService, times(1)).subscribeToTopic(1L, 1L);
    }

    @Test
    void unsubscribeFromTopic_ShouldReturnOk() {
        when(currentUserService.getCurrentUser()).thenReturn(currentUser);
        doNothing().when(topicService).unsubscribeFromTopic(1L, 1L);

        ResponseEntity<Void> response = topicController.unsubscribeFromTopic(1L);

        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        verify(currentUserService, times(1)).getCurrentUser();
        verify(topicService, times(1)).unsubscribeFromTopic(1L, 1L);
    }

    @Test
    void getUserSubscriptions_ShouldReturnListOfTopics() {
        when(currentUserService.getCurrentUser()).thenReturn(currentUser);
        List<TopicResponse> topics = Arrays.asList(topicResponse);
        when(topicService.getUserSubscriptions(1L)).thenReturn(topics);

        ResponseEntity<List<TopicResponse>> response = topicController.getUserSubscriptions();

        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(1, response.getBody().size());
        verify(currentUserService, times(1)).getCurrentUser();
        verify(topicService, times(1)).getUserSubscriptions(1L);
    }
}
