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
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CommentServiceTest {

    @Mock
    private CommentRepository commentRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private PostRepository postRepository;

    @InjectMocks
    private CommentService commentService;

    @Test
    void getCommentsByPostId_WhenPostExists_ShouldReturnComments() {
        User author = new User();
        author.setId(1L);
        author.setName("John");

        Post post = new Post();
        post.setId(1L);

        Comment comment1 = new Comment();
        comment1.setId(1L);
        comment1.setContent("Comment 1");
        comment1.setAuthor(author);
        comment1.setPost(post);

        Comment comment2 = new Comment();
        comment2.setId(2L);
        comment2.setContent("Comment 2");
        comment2.setAuthor(author);
        comment2.setPost(post);

        when(postRepository.existsById(1L)).thenReturn(true);
        when(commentRepository.findByPostIdOrderByCreatedAtAsc(1L))
                .thenReturn(Arrays.asList(comment1, comment2));

        List<CommentResponse> result = commentService.getCommentsByPostId(1L);

        assertEquals(2, result.size());
        assertEquals("Comment 1", result.get(0).getContent());
        assertEquals("Comment 2", result.get(1).getContent());
        verify(postRepository, times(1)).existsById(1L);
        verify(commentRepository, times(1)).findByPostIdOrderByCreatedAtAsc(1L);
    }

    @Test
    void getCommentsByPostId_WhenPostDoesNotExist_ShouldThrowException() {
        when(postRepository.existsById(1L)).thenReturn(false);

        assertThrows(ResourceNotFoundException.class, () -> commentService.getCommentsByPostId(1L));
        verify(postRepository, times(1)).existsById(1L);
        verify(commentRepository, never()).findByPostIdOrderByCreatedAtAsc(any());
    }

    @Test
    void createComment_WhenValidData_ShouldCreateComment() {
        CreateCommentRequest request = new CreateCommentRequest();
        request.setContent("New comment");

        User author = new User();
        author.setId(1L);
        author.setName("John");

        Post post = new Post();
        post.setId(1L);

        Comment savedComment = new Comment();
        savedComment.setId(1L);
        savedComment.setContent("New comment");
        savedComment.setAuthor(author);
        savedComment.setPost(post);

        when(userRepository.findById(1L)).thenReturn(Optional.of(author));
        when(postRepository.findById(1L)).thenReturn(Optional.of(post));
        when(commentRepository.save(any(Comment.class))).thenReturn(savedComment);

        CommentResponse result = commentService.createComment(1L, request, 1L);

        assertNotNull(result);
        assertEquals("New comment", result.getContent());
        assertEquals(1L, result.getAuthorId());
        assertEquals("John", result.getAuthorName());
        verify(userRepository, times(1)).findById(1L);
        verify(postRepository, times(1)).findById(1L);
        verify(commentRepository, times(1)).save(any(Comment.class));
    }

    @Test
    void createComment_WhenUserDoesNotExist_ShouldThrowException() {
        CreateCommentRequest request = new CreateCommentRequest();
        request.setContent("New comment");

        when(userRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> commentService.createComment(1L, request, 1L));
        verify(userRepository, times(1)).findById(1L);
        verify(commentRepository, never()).save(any(Comment.class));
    }

    @Test
    void createComment_WhenPostDoesNotExist_ShouldThrowException() {
        CreateCommentRequest request = new CreateCommentRequest();
        request.setContent("New comment");

        User author = new User();
        author.setId(1L);

        when(userRepository.findById(1L)).thenReturn(Optional.of(author));
        when(postRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> commentService.createComment(1L, request, 1L));
        verify(userRepository, times(1)).findById(1L);
        verify(postRepository, times(1)).findById(1L);
        verify(commentRepository, never()).save(any(Comment.class));
    }
}
