package com.openclassrooms.mddapi.controllers;

import com.openclassrooms.mddapi.dto.JwtResponse;
import com.openclassrooms.mddapi.dto.LoginRequest;
import com.openclassrooms.mddapi.dto.RegisterRequest;
import com.openclassrooms.mddapi.dto.UpdateProfileRequest;
import com.openclassrooms.mddapi.dto.UserDto;
import com.openclassrooms.mddapi.entities.User;
import com.openclassrooms.mddapi.services.UserService;
import com.openclassrooms.mddapi.services.auth.CurrentUserService;
import com.openclassrooms.mddapi.services.auth.LoginService;
import com.openclassrooms.mddapi.services.auth.RegisterService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthControllerTest {

    @Mock
    private RegisterService registerService;

    @Mock
    private LoginService loginService;

    @Mock
    private CurrentUserService currentUserService;

    @Mock
    private UserService userService;

    @InjectMocks
    private AuthController authController;

    private RegisterRequest registerRequest;
    private LoginRequest loginRequest;
    private UpdateProfileRequest updateProfileRequest;
    private JwtResponse jwtResponse;
    private User user;
    private UserDto userDto;

    @BeforeEach
    void setUp() {
        registerRequest = new RegisterRequest();
        registerRequest.setEmail("test@example.com");
        registerRequest.setPassword("password123");

        loginRequest = new LoginRequest(null, null);
        loginRequest.setEmail("test@example.com");
        loginRequest.setPassword("password123");

        updateProfileRequest = new UpdateProfileRequest();
        updateProfileRequest.setEmail("updated@example.com");

        jwtResponse = new JwtResponse(null, null, null, null);
        jwtResponse.setToken("jwt-token");

        user = new User();
        user.setId(1L);
        user.setEmail("test@example.com");

        userDto = new UserDto();
        userDto.setId(1L);
        userDto.setEmail("test@example.com");
    }

    @Test
    void registerUser_ShouldReturnJwtResponse() {
        when(registerService.registerUser(any(RegisterRequest.class))).thenReturn(jwtResponse);

        ResponseEntity<JwtResponse> response = authController.registerUser(registerRequest);

        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("jwt-token", response.getBody().getToken());
        verify(registerService, times(1)).registerUser(any(RegisterRequest.class));
    }

    @Test
    void authenticateUser_ShouldReturnJwtResponse() {
        when(loginService.authenticateUser(any(LoginRequest.class))).thenReturn(jwtResponse);

        ResponseEntity<JwtResponse> response = authController.authenticateUser(loginRequest);

        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("jwt-token", response.getBody().getToken());
        verify(loginService, times(1)).authenticateUser(any(LoginRequest.class));
    }

    @Test
    void getCurrentUser_ShouldReturnUser() {
        when(currentUserService.getCurrentUser()).thenReturn(user);

        ResponseEntity<User> response = authController.getCurrentUser();

        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(1L, response.getBody().getId());
        assertEquals("test@example.com", response.getBody().getEmail());
        verify(currentUserService, times(1)).getCurrentUser();
    }

    @Test
    void logout_ShouldReturnSuccessMessage() {
        ResponseEntity<Map<String, String>> response = authController.logout();

        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("Logged out successfully", response.getBody().get("message"));
    }

    @Test
    void updateProfile_ShouldReturnUpdatedUserDto() {
        when(currentUserService.getCurrentUser()).thenReturn(user);
        when(userService.updateProfile(anyLong(), any(UpdateProfileRequest.class))).thenReturn(userDto);

        ResponseEntity<UserDto> response = authController.updateProfile(updateProfileRequest);

        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(1L, response.getBody().getId());
        assertEquals("test@example.com", response.getBody().getEmail());
        verify(currentUserService, times(1)).getCurrentUser();
        verify(userService, times(1)).updateProfile(anyLong(), any(UpdateProfileRequest.class));
    }

    @Test
    void updateProfile_ShouldUseCurrentUserId() {
        when(currentUserService.getCurrentUser()).thenReturn(user);
        when(userService.updateProfile(eq(1L), any(UpdateProfileRequest.class))).thenReturn(userDto);

        authController.updateProfile(updateProfileRequest);

        verify(userService, times(1)).updateProfile(eq(1L), any(UpdateProfileRequest.class));
    }
}