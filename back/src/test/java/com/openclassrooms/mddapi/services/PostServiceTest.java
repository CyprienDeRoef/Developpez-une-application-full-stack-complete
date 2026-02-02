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
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PostServiceTest {

    @Mock
    private PostRepository postRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private TopicRepository topicRepository;

    @InjectMocks
    private PostService postService;

    @Test
    void getAllPosts_ShouldReturnAllPosts() {
        User author = new User();
        author.setId(1L);
        author.setName("John");

        Topic topic = new Topic("Java", "Java programming");
        topic.setId(1L);

        Post post1 = new Post();
        post1.setId(1L);
        post1.setTitle("Post 1");
        post1.setContent("Content 1");
        post1.setAuthor(author);
        post1.setTopics(Arrays.asList(topic));

        Post post2 = new Post();
        post2.setId(2L);
        post2.setTitle("Post 2");
        post2.setContent("Content 2");
        post2.setAuthor(author);
        post2.setTopics(Arrays.asList(topic));

        when(postRepository.findAll()).thenReturn(Arrays.asList(post1, post2));

        List<PostResponse> result = postService.getAllPosts();

        assertEquals(2, result.size());
        assertEquals("Post 1", result.get(0).getTitle());
        assertEquals("Post 2", result.get(1).getTitle());
        verify(postRepository, times(1)).findAll();
    }

    @Test
    void getPostById_WhenPostExists_ShouldReturnPost() {
        User author = new User();
        author.setId(1L);
        author.setName("John");

        Topic topic = new Topic("Java", "Java programming");
        topic.setId(1L);

        Post post = new Post();
        post.setId(1L);
        post.setTitle("Post 1");
        post.setContent("Content 1");
        post.setAuthor(author);
        post.setTopics(Arrays.asList(topic));

        when(postRepository.findById(1L)).thenReturn(Optional.of(post));

        PostResponse result = postService.getPostById(1L);

        assertNotNull(result);
        assertEquals("Post 1", result.getTitle());
        assertEquals("Content 1", result.getContent());
        verify(postRepository, times(1)).findById(1L);
    }

    @Test
    void getPostById_WhenPostDoesNotExist_ShouldThrowException() {
        when(postRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> postService.getPostById(1L));
        verify(postRepository, times(1)).findById(1L);
    }

    @Test
    void createPost_WhenValidData_ShouldCreatePost() {
        CreatePostRequest request = new CreatePostRequest();
        request.setTitle("New Post");
        request.setContent("New Content");
        request.setTopicIds(Arrays.asList(1L));

        User author = new User();
        author.setId(1L);
        author.setName("John");

        Topic topic = new Topic("Java", "Java programming");
        topic.setId(1L);

        Post savedPost = new Post();
        savedPost.setId(1L);
        savedPost.setTitle("New Post");
        savedPost.setContent("New Content");
        savedPost.setAuthor(author);
        savedPost.setTopics(Arrays.asList(topic));

        when(userRepository.findById(1L)).thenReturn(Optional.of(author));
        when(topicRepository.findAllById(anyList())).thenReturn(Arrays.asList(topic));
        when(postRepository.save(any(Post.class))).thenReturn(savedPost);

        PostResponse result = postService.createPost(request, 1L);

        assertNotNull(result);
        assertEquals("New Post", result.getTitle());
        assertEquals("New Content", result.getContent());
        verify(userRepository, times(1)).findById(1L);
        verify(topicRepository, times(1)).findAllById(anyList());
        verify(postRepository, times(1)).save(any(Post.class));
    }

    @Test
    void createPost_WhenUserDoesNotExist_ShouldThrowException() {
        CreatePostRequest request = new CreatePostRequest();
        request.setTitle("New Post");
        request.setContent("New Content");
        request.setTopicIds(Arrays.asList(1L));

        when(userRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> postService.createPost(request, 1L));
        verify(userRepository, times(1)).findById(1L);
        verify(postRepository, never()).save(any(Post.class));
    }

    @Test
    void createPost_WhenNoValidTopics_ShouldThrowException() {
        CreatePostRequest request = new CreatePostRequest();
        request.setTitle("New Post");
        request.setContent("New Content");
        request.setTopicIds(Arrays.asList(1L));

        User author = new User();
        author.setId(1L);

        when(userRepository.findById(1L)).thenReturn(Optional.of(author));
        when(topicRepository.findAllById(anyList())).thenReturn(Collections.emptyList());

        assertThrows(BadRequestException.class, () -> postService.createPost(request, 1L));
        verify(postRepository, never()).save(any(Post.class));
    }

    @Test
    void updatePost_WhenValidData_ShouldUpdatePost() {
        CreatePostRequest request = new CreatePostRequest();
        request.setTitle("Updated Post");
        request.setContent("Updated Content");
        request.setTopicIds(Arrays.asList(1L));

        User author = new User();
        author.setId(1L);
        author.setName("John");

        Topic topic = new Topic("Java", "Java programming");
        topic.setId(1L);

        Post existingPost = new Post();
        existingPost.setId(1L);
        existingPost.setTitle("Old Post");
        existingPost.setContent("Old Content");
        existingPost.setAuthor(author);
        existingPost.setTopics(Arrays.asList(topic));

        when(postRepository.findById(1L)).thenReturn(Optional.of(existingPost));
        when(topicRepository.findAllById(anyList())).thenReturn(Arrays.asList(topic));
        when(postRepository.save(any(Post.class))).thenReturn(existingPost);

        PostResponse result = postService.updatePost(1L, request, 1L);

        assertNotNull(result);
        assertEquals("Updated Post", existingPost.getTitle());
        assertEquals("Updated Content", existingPost.getContent());
        verify(postRepository, times(1)).findById(1L);
        verify(postRepository, times(1)).save(any(Post.class));
    }

    @Test
    void updatePost_WhenPostDoesNotExist_ShouldThrowException() {
        CreatePostRequest request = new CreatePostRequest();
        request.setTitle("Updated Post");
        request.setContent("Updated Content");
        request.setTopicIds(Arrays.asList(1L));

        when(postRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> postService.updatePost(1L, request, 1L));
        verify(postRepository, times(1)).findById(1L);
        verify(postRepository, never()).save(any(Post.class));
    }

    @Test
    void updatePost_WhenUserIsNotAuthor_ShouldThrowException() {
        CreatePostRequest request = new CreatePostRequest();
        request.setTitle("Updated Post");
        request.setContent("Updated Content");
        request.setTopicIds(Arrays.asList(1L));

        User author = new User();
        author.setId(1L);

        Post existingPost = new Post();
        existingPost.setId(1L);
        existingPost.setAuthor(author);

        when(postRepository.findById(1L)).thenReturn(Optional.of(existingPost));

        assertThrows(UnauthorizedException.class, () -> postService.updatePost(1L, request, 2L));
        verify(postRepository, times(1)).findById(1L);
        verify(postRepository, never()).save(any(Post.class));
    }

    @Test
    void updatePost_WhenNoValidTopics_ShouldThrowException() {
        CreatePostRequest request = new CreatePostRequest();
        request.setTitle("Updated Post");
        request.setContent("Updated Content");
        request.setTopicIds(Arrays.asList(1L));

        User author = new User();
        author.setId(1L);

        Post existingPost = new Post();
        existingPost.setId(1L);
        existingPost.setAuthor(author);

        when(postRepository.findById(1L)).thenReturn(Optional.of(existingPost));
        when(topicRepository.findAllById(anyList())).thenReturn(Collections.emptyList());

        assertThrows(BadRequestException.class, () -> postService.updatePost(1L, request, 1L));
        verify(postRepository, never()).save(any(Post.class));
    }

    @Test
    void deletePost_WhenValidData_ShouldDeletePost() {
        User author = new User();
        author.setId(1L);

        Post post = new Post();
        post.setId(1L);
        post.setAuthor(author);

        when(postRepository.findById(1L)).thenReturn(Optional.of(post));

        postService.deletePost(1L, 1L);

        verify(postRepository, times(1)).findById(1L);
        verify(postRepository, times(1)).delete(post);
    }

    @Test
    void deletePost_WhenPostDoesNotExist_ShouldThrowException() {
        when(postRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> postService.deletePost(1L, 1L));
        verify(postRepository, times(1)).findById(1L);
        verify(postRepository, never()).delete(any(Post.class));
    }

    @Test
    void deletePost_WhenUserIsNotAuthor_ShouldThrowException() {
        User author = new User();
        author.setId(1L);

        Post post = new Post();
        post.setId(1L);
        post.setAuthor(author);

        when(postRepository.findById(1L)).thenReturn(Optional.of(post));

        assertThrows(UnauthorizedException.class, () -> postService.deletePost(1L, 2L));
        verify(postRepository, times(1)).findById(1L);
        verify(postRepository, never()).delete(any(Post.class));
    }

    @Test
    void getPostsByAuthor_ShouldReturnPosts() {
        User author = new User();
        author.setId(1L);
        author.setName("John");

        Topic topic = new Topic("Java", "Java programming");
        topic.setId(1L);

        Post post = new Post();
        post.setId(1L);
        post.setTitle("Post 1");
        post.setContent("Content 1");
        post.setAuthor(author);
        post.setTopics(Arrays.asList(topic));

        when(postRepository.findByAuthorId(1L)).thenReturn(Arrays.asList(post));

        List<PostResponse> result = postService.getPostsByAuthor(1L);

        assertEquals(1, result.size());
        assertEquals("Post 1", result.get(0).getTitle());
        verify(postRepository, times(1)).findByAuthorId(1L);
    }

    @Test
    void getPostsByTopic_ShouldReturnPosts() {
        User author = new User();
        author.setId(1L);
        author.setName("John");

        Topic topic = new Topic("Java", "Java programming");
        topic.setId(1L);

        Post post = new Post();
        post.setId(1L);
        post.setTitle("Post 1");
        post.setContent("Content 1");
        post.setAuthor(author);
        post.setTopics(Arrays.asList(topic));

        when(postRepository.findByTopicsId(1L)).thenReturn(Arrays.asList(post));

        List<PostResponse> result = postService.getPostsByTopic(1L);

        assertEquals(1, result.size());
        assertEquals("Post 1", result.get(0).getTitle());
        verify(postRepository, times(1)).findByTopicsId(1L);
    }
}
