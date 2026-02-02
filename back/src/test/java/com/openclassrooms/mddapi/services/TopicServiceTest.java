package com.openclassrooms.mddapi.services;

import com.openclassrooms.mddapi.dto.CreateTopicRequest;
import com.openclassrooms.mddapi.dto.TopicResponse;
import com.openclassrooms.mddapi.entities.Topic;
import com.openclassrooms.mddapi.entities.User;
import com.openclassrooms.mddapi.exceptions.BadRequestException;
import com.openclassrooms.mddapi.exceptions.ResourceNotFoundException;
import com.openclassrooms.mddapi.repositories.TopicRepository;
import com.openclassrooms.mddapi.repositories.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TopicServiceTest {

    @Mock
    private TopicRepository topicRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private TopicService topicService;

    @Test
    void getAllTopics_ShouldReturnAllTopics() {
        Topic topic1 = new Topic("Java", "Java programming");
        topic1.setId(1L);
        Topic topic2 = new Topic("Python", "Python programming");
        topic2.setId(2L);

        when(topicRepository.findAll()).thenReturn(Arrays.asList(topic1, topic2));

        List<TopicResponse> result = topicService.getAllTopics();

        assertEquals(2, result.size());
        assertEquals("Java", result.get(0).getName());
        assertEquals("Python", result.get(1).getName());
        verify(topicRepository, times(1)).findAll();
    }

    @Test
    void getTopicById_WhenTopicExists_ShouldReturnTopic() {
        Topic topic = new Topic("Java", "Java programming");
        topic.setId(1L);

        when(topicRepository.findById(1L)).thenReturn(Optional.of(topic));

        TopicResponse result = topicService.getTopicById(1L);

        assertNotNull(result);
        assertEquals("Java", result.getName());
        assertEquals("Java programming", result.getDescription());
        verify(topicRepository, times(1)).findById(1L);
    }

    @Test
    void getTopicById_WhenTopicDoesNotExist_ShouldThrowException() {
        when(topicRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> topicService.getTopicById(1L));
        verify(topicRepository, times(1)).findById(1L);
    }

    @Test
    void createTopic_WhenTopicNameIsUnique_ShouldCreateTopic() {
        CreateTopicRequest request = new CreateTopicRequest();
        request.setName("Java");
        request.setDescription("Java programming");

        Topic savedTopic = new Topic("Java", "Java programming");
        savedTopic.setId(1L);

        when(topicRepository.findByName("Java")).thenReturn(Optional.empty());
        when(topicRepository.save(any(Topic.class))).thenReturn(savedTopic);

        TopicResponse result = topicService.createTopic(request);

        assertNotNull(result);
        assertEquals("Java", result.getName());
        verify(topicRepository, times(1)).findByName("Java");
        verify(topicRepository, times(1)).save(any(Topic.class));
    }

    @Test
    void createTopic_WhenTopicNameExists_ShouldThrowException() {
        CreateTopicRequest request = new CreateTopicRequest();
        request.setName("Java");
        request.setDescription("Java programming");

        Topic existingTopic = new Topic("Java", "Existing Java topic");
        existingTopic.setId(1L);

        when(topicRepository.findByName("Java")).thenReturn(Optional.of(existingTopic));

        assertThrows(BadRequestException.class, () -> topicService.createTopic(request));
        verify(topicRepository, times(1)).findByName("Java");
        verify(topicRepository, never()).save(any(Topic.class));
    }

    @Test
    void updateTopic_WhenTopicExists_ShouldUpdateTopic() {
        CreateTopicRequest request = new CreateTopicRequest();
        request.setName("Updated Java");
        request.setDescription("Updated description");

        Topic existingTopic = new Topic("Java", "Java programming");
        existingTopic.setId(1L);

        Topic updatedTopic = new Topic("Updated Java", "Updated description");
        updatedTopic.setId(1L);

        when(topicRepository.findById(1L)).thenReturn(Optional.of(existingTopic));
        when(topicRepository.findByName("Updated Java")).thenReturn(Optional.empty());
        when(topicRepository.save(any(Topic.class))).thenReturn(updatedTopic);

        TopicResponse result = topicService.updateTopic(1L, request);

        assertNotNull(result);
        assertEquals("Updated Java", result.getName());
        assertEquals("Updated description", result.getDescription());
        verify(topicRepository, times(1)).findById(1L);
        verify(topicRepository, times(1)).save(any(Topic.class));
    }

    @Test
    void updateTopic_WhenTopicDoesNotExist_ShouldThrowException() {
        CreateTopicRequest request = new CreateTopicRequest();
        request.setName("Java");
        request.setDescription("Description");

        when(topicRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> topicService.updateTopic(1L, request));
        verify(topicRepository, times(1)).findById(1L);
        verify(topicRepository, never()).save(any(Topic.class));
    }

    @Test
    void updateTopic_WhenNewNameAlreadyExists_ShouldThrowException() {
        CreateTopicRequest request = new CreateTopicRequest();
        request.setName("Python");
        request.setDescription("Description");

        Topic existingTopic = new Topic("Java", "Java programming");
        existingTopic.setId(1L);

        Topic anotherTopic = new Topic("Python", "Python programming");
        anotherTopic.setId(2L);

        when(topicRepository.findById(1L)).thenReturn(Optional.of(existingTopic));
        when(topicRepository.findByName("Python")).thenReturn(Optional.of(anotherTopic));

        assertThrows(BadRequestException.class, () -> topicService.updateTopic(1L, request));
        verify(topicRepository, times(1)).findById(1L);
        verify(topicRepository, never()).save(any(Topic.class));
    }

    @Test
    void deleteTopic_WhenTopicExists_ShouldDeleteTopic() {
        Topic topic = new Topic("Java", "Java programming");
        topic.setId(1L);

        when(topicRepository.findById(1L)).thenReturn(Optional.of(topic));

        topicService.deleteTopic(1L);

        verify(topicRepository, times(1)).findById(1L);
        verify(topicRepository, times(1)).delete(topic);
    }

    @Test
    void deleteTopic_WhenTopicDoesNotExist_ShouldThrowException() {
        when(topicRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> topicService.deleteTopic(1L));
        verify(topicRepository, times(1)).findById(1L);
        verify(topicRepository, never()).delete(any(Topic.class));
    }

    @Test
    void subscribeToTopic_WhenValidData_ShouldSubscribe() {
        Topic topic = new Topic("Java", "Java programming");
        topic.setId(1L);

        User user = new User();
        user.setId(1L);
        user.setSubscriptions(new ArrayList<>());

        when(topicRepository.findById(1L)).thenReturn(Optional.of(topic));
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(userRepository.save(any(User.class))).thenReturn(user);

        topicService.subscribeToTopic(1L, 1L);

        assertTrue(user.getSubscriptions().contains(topic));
        verify(topicRepository, times(1)).findById(1L);
        verify(userRepository, times(1)).findById(1L);
        verify(userRepository, times(1)).save(user);
    }

    @Test
    void subscribeToTopic_WhenAlreadySubscribed_ShouldThrowException() {
        Topic topic = new Topic("Java", "Java programming");
        topic.setId(1L);

        User user = new User();
        user.setId(1L);
        user.setSubscriptions(new ArrayList<>(Arrays.asList(topic)));

        when(topicRepository.findById(1L)).thenReturn(Optional.of(topic));
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));

        assertThrows(BadRequestException.class, () -> topicService.subscribeToTopic(1L, 1L));
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void subscribeToTopic_WhenTopicDoesNotExist_ShouldThrowException() {
        when(topicRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> topicService.subscribeToTopic(1L, 1L));
        verify(userRepository, never()).findById(any());
    }

    @Test
    void subscribeToTopic_WhenUserDoesNotExist_ShouldThrowException() {
        Topic topic = new Topic("Java", "Java programming");
        topic.setId(1L);

        when(topicRepository.findById(1L)).thenReturn(Optional.of(topic));
        when(userRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> topicService.subscribeToTopic(1L, 1L));
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void unsubscribeFromTopic_WhenSubscribed_ShouldUnsubscribe() {
        Topic topic = new Topic("Java", "Java programming");
        topic.setId(1L);

        User user = new User();
        user.setId(1L);
        user.setSubscriptions(new ArrayList<>(Arrays.asList(topic)));

        when(topicRepository.findById(1L)).thenReturn(Optional.of(topic));
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(userRepository.save(any(User.class))).thenReturn(user);

        topicService.unsubscribeFromTopic(1L, 1L);

        assertFalse(user.getSubscriptions().contains(topic));
        verify(topicRepository, times(1)).findById(1L);
        verify(userRepository, times(1)).findById(1L);
        verify(userRepository, times(1)).save(user);
    }

    @Test
    void unsubscribeFromTopic_WhenNotSubscribed_ShouldThrowException() {
        Topic topic = new Topic("Java", "Java programming");
        topic.setId(1L);

        User user = new User();
        user.setId(1L);
        user.setSubscriptions(new ArrayList<>());

        when(topicRepository.findById(1L)).thenReturn(Optional.of(topic));
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));

        assertThrows(BadRequestException.class, () -> topicService.unsubscribeFromTopic(1L, 1L));
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void getUserSubscriptions_WhenUserHasSubscriptions_ShouldReturnSubscriptions() {
        Topic topic1 = new Topic("Java", "Java programming");
        topic1.setId(1L);
        Topic topic2 = new Topic("Python", "Python programming");
        topic2.setId(2L);

        User user = new User();
        user.setId(1L);
        user.setSubscriptions(Arrays.asList(topic1, topic2));

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));

        List<TopicResponse> result = topicService.getUserSubscriptions(1L);

        assertEquals(2, result.size());
        assertEquals("Java", result.get(0).getName());
        assertEquals("Python", result.get(1).getName());
        verify(userRepository, times(1)).findById(1L);
    }

    @Test
    void getUserSubscriptions_WhenUserDoesNotExist_ShouldThrowException() {
        when(userRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> topicService.getUserSubscriptions(1L));
        verify(userRepository, times(1)).findById(1L);
    }
}
