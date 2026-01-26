package com.openclassrooms.mddapi.services;

import com.openclassrooms.mddapi.dto.CreatePostRequest;
import com.openclassrooms.mddapi.dto.PostResponse;
import com.openclassrooms.mddapi.entities.Post;
import com.openclassrooms.mddapi.entities.Topic;
import com.openclassrooms.mddapi.entities.User;
import com.openclassrooms.mddapi.exceptions.BadRequestException;
import com.openclassrooms.mddapi.exceptions.ResourceNotFoundException;
import com.openclassrooms.mddapi.exceptions.UnauthorizedException;
import com.openclassrooms.mddapi.repositories.PostRepository;
import com.openclassrooms.mddapi.repositories.TopicRepository;
import com.openclassrooms.mddapi.repositories.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class PostService {

    private final PostRepository postRepository;
    private final UserRepository userRepository;
    private final TopicRepository topicRepository;

    public PostService(PostRepository postRepository, UserRepository userRepository, TopicRepository topicRepository) {
        this.postRepository = postRepository;
        this.userRepository = userRepository;
        this.topicRepository = topicRepository;
    }

    /**
     * Get all posts
     */
    public List<PostResponse> getAllPosts() {
        return postRepository.findAll().stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }

    /**
     * Get a post by ID
     */
    public PostResponse getPostById(Long id) {
        Post post = postRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Post not found with id: " + id));
        return convertToResponse(post);
    }

    /**
     * Create a new post
     */
    @Transactional
    public PostResponse createPost(CreatePostRequest request, Long authorId) {
        User author = userRepository.findById(authorId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + authorId));

        List<Topic> topics = topicRepository.findAllById(request.getTopicIds());
        if (topics.isEmpty()) {
            throw new BadRequestException("No valid topics found");
        }

        Post post = new Post(request.getTitle(), request.getContent(), author, topics);
        Post savedPost = postRepository.save(post);
        return convertToResponse(savedPost);
    }

    /**
     * Update a post
     */
    @Transactional
    public PostResponse updatePost(Long id, CreatePostRequest request, Long userId) {
        Post post = postRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Post not found with id: " + id));

        // Check if the user is the author
        if (!post.getAuthor().getId().equals(userId)) {
            throw new UnauthorizedException("You are not authorized to update this post");
        }

        post.setTitle(request.getTitle());
        post.setContent(request.getContent());

        List<Topic> topics = topicRepository.findAllById(request.getTopicIds());
        if (topics.isEmpty()) {
            throw new BadRequestException("No valid topics found");
        }
        post.setTopics(topics);

        Post updatedPost = postRepository.save(post);
        return convertToResponse(updatedPost);
    }

    /**
     * Delete a post
     */
    @Transactional
    public void deletePost(Long id, Long userId) {
        Post post = postRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Post not found with id: " + id));

        // Check if the user is the author
        if (!post.getAuthor().getId().equals(userId)) {
            throw new UnauthorizedException("You are not authorized to delete this post");
        }

        postRepository.delete(post);
    }

    /**
     * Get posts by author
     */
    public List<PostResponse> getPostsByAuthor(Long authorId) {
        return postRepository.findByAuthorId(authorId).stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }

    /**
     * Get posts by topic
     */
    public List<PostResponse> getPostsByTopic(Long topicId) {
        return postRepository.findByTopicsId(topicId).stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }

    /**
     * Convert Post entity to PostResponse DTO
     */
    private PostResponse convertToResponse(Post post) {
        List<Long> topicIds = post.getTopics().stream()
                .map(Topic::getId)
                .collect(Collectors.toList());

        List<String> topicNames = post.getTopics().stream()
                .map(Topic::getName)
                .collect(Collectors.toList());

        return new PostResponse(
                post.getId(),
                post.getTitle(),
                post.getContent(),
                post.getAuthor().getId(),
                post.getAuthor().getName(),
                topicIds,
                topicNames,
                post.getCreatedAt(),
                post.getUpdatedAt());
    }
}
