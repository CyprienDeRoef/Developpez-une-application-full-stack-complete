package com.openclassrooms.mddapi.services;

import com.openclassrooms.mddapi.dto.CommentResponse;
import com.openclassrooms.mddapi.dto.CreateCommentRequest;
import com.openclassrooms.mddapi.entities.Comment;
import com.openclassrooms.mddapi.entities.Post;
import com.openclassrooms.mddapi.entities.User;
import com.openclassrooms.mddapi.exceptions.ResourceNotFoundException;
import com.openclassrooms.mddapi.repositories.CommentRepository;
import com.openclassrooms.mddapi.repositories.PostRepository;
import com.openclassrooms.mddapi.repositories.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class CommentService {

    private final CommentRepository commentRepository;
    private final UserRepository userRepository;
    private final PostRepository postRepository;

    public CommentService(CommentRepository commentRepository, UserRepository userRepository,
            PostRepository postRepository) {
        this.commentRepository = commentRepository;
        this.userRepository = userRepository;
        this.postRepository = postRepository;
    }

    /**
     * Get all comments for a post
     */
    public List<CommentResponse> getCommentsByPostId(Long postId) {
        if (!postRepository.existsById(postId)) {
            throw new ResourceNotFoundException("Post not found with id: " + postId);
        }
        return commentRepository.findByPostIdOrderByCreatedAtAsc(postId).stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }

    /**
     * Create a new comment
     */
    @Transactional
    public CommentResponse createComment(Long postId, CreateCommentRequest request, Long authorId) {
        User author = userRepository.findById(authorId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + authorId));

        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new ResourceNotFoundException("Post not found with id: " + postId));

        Comment comment = new Comment();
        comment.setContent(request.getContent());
        comment.setAuthor(author);
        comment.setPost(post);

        Comment savedComment = commentRepository.save(comment);
        return convertToResponse(savedComment);
    }

    /**
     * Convert Comment entity to CommentResponse DTO
     */
    private CommentResponse convertToResponse(Comment comment) {
        return new CommentResponse(
                comment.getId(),
                comment.getContent(),
                comment.getAuthor().getId(),
                comment.getAuthor().getName(),
                comment.getPost().getId(),
                comment.getCreatedAt());
    }
}
