package com.openclassrooms.mddapi.services.auth;

import com.openclassrooms.mddapi.entities.User;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CurrentUserServiceTest {

    @InjectMocks
    private CurrentUserService currentUserService;

    @Test
    void getCurrentUser_WhenUserAuthenticated_ShouldReturnUser() {
        User user = new User("John Doe", "john@example.com", "password");
        user.setId(1L);

        SecurityContext securityContext = mock(SecurityContext.class);
        Authentication authentication = mock(Authentication.class);

        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.getPrincipal()).thenReturn(user);

        try (MockedStatic<SecurityContextHolder> mockedStatic = mockStatic(SecurityContextHolder.class)) {
            mockedStatic.when(SecurityContextHolder::getContext).thenReturn(securityContext);

            User result = currentUserService.getCurrentUser();

            assertNotNull(result);
            assertEquals(1L, result.getId());
            assertEquals("John Doe", result.getName());
            assertEquals("john@example.com", result.getEmail());
        }
    }

    @Test
    void getCurrentUser_WhenNoAuthentication_ShouldThrowException() {
        SecurityContext securityContext = mock(SecurityContext.class);

        when(securityContext.getAuthentication()).thenReturn(null);

        try (MockedStatic<SecurityContextHolder> mockedStatic = mockStatic(SecurityContextHolder.class)) {
            mockedStatic.when(SecurityContextHolder::getContext).thenReturn(securityContext);

            assertThrows(RuntimeException.class, () -> currentUserService.getCurrentUser());
        }
    }

    @Test
    void getCurrentUser_WhenPrincipalNotUser_ShouldThrowException() {
        SecurityContext securityContext = mock(SecurityContext.class);
        Authentication authentication = mock(Authentication.class);

        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.getPrincipal()).thenReturn("NotAUserObject");

        try (MockedStatic<SecurityContextHolder> mockedStatic = mockStatic(SecurityContextHolder.class)) {
            mockedStatic.when(SecurityContextHolder::getContext).thenReturn(securityContext);

            assertThrows(RuntimeException.class, () -> currentUserService.getCurrentUser());
        }
    }
}
