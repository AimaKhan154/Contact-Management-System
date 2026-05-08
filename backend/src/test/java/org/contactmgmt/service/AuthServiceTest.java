package org.contactmgmt.service;

import org.contactmgmt.dto.ChangePasswordRequest;
import org.contactmgmt.dto.SignupRequest;
import org.contactmgmt.entity.AppUser;
import org.contactmgmt.repository.AppUserRepository;
import org.contactmgmt.security.UserDetailsImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Collections;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @Mock
    private AuthenticationManager authenticationManager;
    @Mock
    private AppUserRepository userRepository;
    @Mock
    private PasswordEncoder encoder;
    @InjectMocks
    private AuthService authService;

    @Test
    void registerUser_shouldThrow_whenEmailAlreadyExists() {
        SignupRequest request = new SignupRequest();
        request.setEmail("test@example.com");
        request.setPassword("password123");

        when(userRepository.existsByEmail("test@example.com")).thenReturn(true);

        RuntimeException ex = assertThrows(RuntimeException.class, () -> authService.registerUser(request));
        assertEquals("Error: Email is already in use!", ex.getMessage());
    }

    @Test
    void changePassword_shouldThrow_whenOldPasswordIsIncorrect() {
        UserDetailsImpl userDetails = new UserDetailsImpl(
                1L, "user@example.com", "user@example.com", "hashed",
                Collections.singletonList(new SimpleGrantedAuthority("ROLE_USER")));
        ChangePasswordRequest request = new ChangePasswordRequest();
        request.setOldPassword("wrong");
        request.setNewPassword("newPassword123");

        AppUser user = new AppUser("user@example.com", null, "storedHash");
        user.setId(1L);

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(encoder.matches("wrong", "storedHash")).thenReturn(false);

        RuntimeException ex = assertThrows(RuntimeException.class, () -> authService.changePassword(request, userDetails));
        assertEquals("Error: Incorrect old password.", ex.getMessage());
    }

    @Test
    void changePassword_shouldUpdatePassword_whenOldPasswordIsCorrect() {
        UserDetailsImpl userDetails = new UserDetailsImpl(
                1L, "user@example.com", "user@example.com", "hashed",
                Collections.singletonList(new SimpleGrantedAuthority("ROLE_USER")));
        ChangePasswordRequest request = new ChangePasswordRequest();
        request.setOldPassword("oldPassword");
        request.setNewPassword("newPassword123");

        AppUser user = new AppUser("user@example.com", null, "storedHash");
        user.setId(1L);

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(encoder.matches("oldPassword", "storedHash")).thenReturn(true);
        when(encoder.encode("newPassword123")).thenReturn("newHash");

        authService.changePassword(request, userDetails);

        assertEquals("newHash", user.getPassword());
        verify(userRepository).save(any(AppUser.class));
    }
}
