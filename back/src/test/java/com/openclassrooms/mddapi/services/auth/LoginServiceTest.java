package com.openclassrooms.mddapi.services.auth;

import com.openclassrooms.mddapi.dto.JwtResponse;
import com.openclassrooms.mddapi.dto.LoginRequest;
import com.openclassrooms.mddapi.entities.User;
import com.openclassrooms.mddapi.util.JwtUtils;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class LoginServiceTest {

    @Mock
    private AuthenticationManager authenticationManager;

    @Mock
    private JwtUtils jwtUtils;

    @Mock
    private Authentication authentication;

    @InjectMocks
    private LoginService loginService;

    @Test
    void authenticateUser_WhenValidCredentials_ShouldReturnJwtResponse() {
        LoginRequest request = new LoginRequest("john@example.com", "password123");

        User user = new User("John Doe", "john@example.com", "encodedPassword");
        user.setId(1L);

        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenReturn(authentication);
        when(authentication.getPrincipal()).thenReturn(user);
        when(jwtUtils.generateJwtToken(authentication)).thenReturn("jwt-token");

        JwtResponse result = loginService.authenticateUser(request);

        assertNotNull(result);
        assertEquals("jwt-token", result.getToken());
        assertEquals(1L, result.getId());
        assertEquals("john@example.com", result.getEmail());
        assertEquals("John Doe", result.getName());
        verify(authenticationManager, times(1)).authenticate(any(UsernamePasswordAuthenticationToken.class));
        verify(jwtUtils, times(1)).generateJwtToken(authentication);
    }
}
