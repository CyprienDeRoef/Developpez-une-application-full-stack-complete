package com.openclassrooms.mddapi.services;

import com.openclassrooms.mddapi.dto.UpdateProfileRequest;
import com.openclassrooms.mddapi.dto.UserDto;
import com.openclassrooms.mddapi.entities.User;
import com.openclassrooms.mddapi.exceptions.BadRequestException;
import com.openclassrooms.mddapi.exceptions.ResourceNotFoundException;
import com.openclassrooms.mddapi.repositories.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private UserService userService;

    @Test
    void getUserById_WhenUserExists_ShouldReturnUser() {
        User user = new User();
        user.setId(1L);
        user.setName("John Doe");
        user.setEmail("john@example.com");

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));

        UserDto result = userService.getUserById(1L);

        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals("John Doe", result.getName());
        assertEquals("john@example.com", result.getEmail());
        verify(userRepository, times(1)).findById(1L);
    }

    @Test
    void getUserById_WhenUserDoesNotExist_ShouldThrowException() {
        when(userRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> userService.getUserById(1L));
        verify(userRepository, times(1)).findById(1L);
    }

    @Test
    void updateProfile_WhenUpdatingName_ShouldUpdateName() {
        UpdateProfileRequest request = new UpdateProfileRequest();
        request.setName("Jane Doe");

        User user = new User();
        user.setId(1L);
        user.setName("John Doe");
        user.setEmail("john@example.com");

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(userRepository.save(any(User.class))).thenReturn(user);

        UserDto result = userService.updateProfile(1L, request);

        assertNotNull(result);
        assertEquals("Jane Doe", user.getName());
        verify(userRepository, times(1)).findById(1L);
        verify(userRepository, times(1)).save(user);
    }

    @Test
    void updateProfile_WhenUpdatingEmail_ShouldUpdateEmail() {
        UpdateProfileRequest request = new UpdateProfileRequest();
        request.setEmail("jane@example.com");

        User user = new User();
        user.setId(1L);
        user.setName("John Doe");
        user.setEmail("john@example.com");

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(userRepository.findByEmail("jane@example.com")).thenReturn(Optional.empty());
        when(userRepository.save(any(User.class))).thenReturn(user);

        UserDto result = userService.updateProfile(1L, request);

        assertNotNull(result);
        assertEquals("jane@example.com", user.getEmail());
        verify(userRepository, times(1)).findById(1L);
        verify(userRepository, times(1)).findByEmail("jane@example.com");
        verify(userRepository, times(1)).save(user);
    }

    @Test
    void updateProfile_WhenEmailAlreadyTaken_ShouldThrowException() {
        UpdateProfileRequest request = new UpdateProfileRequest();
        request.setEmail("taken@example.com");

        User user = new User();
        user.setId(1L);
        user.setName("John Doe");
        user.setEmail("john@example.com");

        User anotherUser = new User();
        anotherUser.setId(2L);
        anotherUser.setEmail("taken@example.com");

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(userRepository.findByEmail("taken@example.com")).thenReturn(Optional.of(anotherUser));

        assertThrows(BadRequestException.class, () -> userService.updateProfile(1L, request));
        verify(userRepository, times(1)).findById(1L);
        verify(userRepository, times(1)).findByEmail("taken@example.com");
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void updateProfile_WhenUpdatingPassword_ShouldEncodeAndUpdatePassword() {
        UpdateProfileRequest request = new UpdateProfileRequest();
        request.setPassword("newpassword");

        User user = new User();
        user.setId(1L);
        user.setName("John Doe");
        user.setEmail("john@example.com");
        user.setPassword("oldhashedpassword");

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(passwordEncoder.encode("newpassword")).thenReturn("newhashedpassword");
        when(userRepository.save(any(User.class))).thenReturn(user);

        UserDto result = userService.updateProfile(1L, request);

        assertNotNull(result);
        assertEquals("newhashedpassword", user.getPassword());
        verify(userRepository, times(1)).findById(1L);
        verify(passwordEncoder, times(1)).encode("newpassword");
        verify(userRepository, times(1)).save(user);
    }

    @Test
    void updateProfile_WhenUserDoesNotExist_ShouldThrowException() {
        UpdateProfileRequest request = new UpdateProfileRequest();
        request.setName("Jane Doe");

        when(userRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> userService.updateProfile(1L, request));
        verify(userRepository, times(1)).findById(1L);
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void updateProfile_WhenUpdatingEmailToSameEmail_ShouldNotCheckForDuplicates() {
        UpdateProfileRequest request = new UpdateProfileRequest();
        request.setEmail("john@example.com");

        User user = new User();
        user.setId(1L);
        user.setName("John Doe");
        user.setEmail("john@example.com");

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(userRepository.save(any(User.class))).thenReturn(user);

        UserDto result = userService.updateProfile(1L, request);

        assertNotNull(result);
        verify(userRepository, times(1)).findById(1L);
        verify(userRepository, never()).findByEmail(anyString());
        verify(userRepository, times(1)).save(user);
    }
}
