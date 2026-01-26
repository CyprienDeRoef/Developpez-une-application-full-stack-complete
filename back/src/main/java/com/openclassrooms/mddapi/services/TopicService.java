package com.openclassrooms.mddapi.services;

import com.openclassrooms.mddapi.dto.CreateTopicRequest;
import com.openclassrooms.mddapi.dto.TopicResponse;
import com.openclassrooms.mddapi.entities.Topic;
import com.openclassrooms.mddapi.entities.User;
import com.openclassrooms.mddapi.exceptions.BadRequestException;
import com.openclassrooms.mddapi.exceptions.ResourceNotFoundException;
import com.openclassrooms.mddapi.repositories.TopicRepository;
import com.openclassrooms.mddapi.repositories.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class TopicService {

    private final TopicRepository topicRepository;
    private final UserRepository userRepository;

    public TopicService(TopicRepository topicRepository, UserRepository userRepository) {
        this.topicRepository = topicRepository;
        this.userRepository = userRepository;
    }

    /**
     * Get all topics
     */
    public List<TopicResponse> getAllTopics() {
        return topicRepository.findAll().stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }

    /**
     * Get a topic by ID
     */
    public TopicResponse getTopicById(Long id) {
        Topic topic = topicRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Topic not found with id: " + id));
        return convertToResponse(topic);
    }

    /**
     * Create a new topic
     */
    @Transactional
    public TopicResponse createTopic(CreateTopicRequest request) {
        // Check if topic with same name already exists
        if (topicRepository.findByName(request.getName()).isPresent()) {
            throw new BadRequestException("Topic with name '" + request.getName() + "' already exists");
        }

        Topic topic = new Topic(request.getName(), request.getDescription());
        Topic savedTopic = topicRepository.save(topic);
        return convertToResponse(savedTopic);
    }

    /**
     * Update a topic
     */
    @Transactional
    public TopicResponse updateTopic(Long id, CreateTopicRequest request) {
        Topic topic = topicRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Topic not found with id: " + id));

        // Check if another topic with the same name exists
        topicRepository.findByName(request.getName()).ifPresent(existingTopic -> {
            if (!existingTopic.getId().equals(id)) {
                throw new BadRequestException("Topic with name '" + request.getName() + "' already exists");
            }
        });

        topic.setName(request.getName());
        topic.setDescription(request.getDescription());

        Topic updatedTopic = topicRepository.save(topic);
        return convertToResponse(updatedTopic);
    }

    /**
     * Delete a topic
     */
    @Transactional
    public void deleteTopic(Long id) {
        Topic topic = topicRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Topic not found with id: " + id));
        topicRepository.delete(topic);
    }

    /**
     * Subscribe a user to a topic
     */
    @Transactional
    public void subscribeToTopic(Long topicId, Long userId) {
        Topic topic = topicRepository.findById(topicId)
                .orElseThrow(() -> new ResourceNotFoundException("Topic not found with id: " + topicId));

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + userId));

        if (user.getSubscriptions().contains(topic)) {
            throw new BadRequestException("User is already subscribed to this topic");
        }

        user.getSubscriptions().add(topic);
        userRepository.save(user);
    }

    /**
     * Unsubscribe a user from a topic
     */
    @Transactional
    public void unsubscribeFromTopic(Long topicId, Long userId) {
        Topic topic = topicRepository.findById(topicId)
                .orElseThrow(() -> new ResourceNotFoundException("Topic not found with id: " + topicId));

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + userId));

        if (!user.getSubscriptions().contains(topic)) {
            throw new BadRequestException("User is not subscribed to this topic");
        }

        user.getSubscriptions().remove(topic);
        userRepository.save(user);
    }

    /**
     * Get user's subscribed topics
     */
    public List<TopicResponse> getUserSubscriptions(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + userId));

        return user.getSubscriptions().stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }

    /**
     * Convert Topic entity to TopicResponse DTO
     */
    private TopicResponse convertToResponse(Topic topic) {
        return new TopicResponse(
                topic.getId(),
                topic.getName(),
                topic.getDescription(),
                topic.getCreatedAt(),
                topic.getUpdatedAt());
    }
}
