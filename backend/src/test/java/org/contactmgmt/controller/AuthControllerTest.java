package org.contactmgmt.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.contactmgmt.repository.AppUserRepository;
import org.contactmgmt.service.AuthService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class AuthControllerTest {

    private MockMvc mockMvc;
    private ObjectMapper objectMapper;
    private AppUserRepository userRepository;

    @BeforeEach
    void setUp() {
        objectMapper = new ObjectMapper();
        userRepository = mock(AppUserRepository.class);
        PasswordEncoder passwordEncoder = mock(PasswordEncoder.class);
        AuthenticationManager authenticationManager = mock(AuthenticationManager.class);

        AuthService authService = new AuthService();
        ReflectionTestUtils.setField(authService, "userRepository", userRepository);
        ReflectionTestUtils.setField(authService, "encoder", passwordEncoder);
        ReflectionTestUtils.setField(authService, "authenticationManager", authenticationManager);

        AuthController authController = new AuthController();
        ReflectionTestUtils.setField(authController, "authService", authService);

        mockMvc = MockMvcBuilders.standaloneSetup(authController).build();
    }

    @Test
    void register_shouldReturnSuccessMessage() throws Exception {
        when(userRepository.existsByEmail(any())).thenReturn(false);
        when(userRepository.existsByPhone(any())).thenReturn(false);

        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "email": "test@example.com",
                                  "password": "password123"
                                }
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("User registered successfully!"));

        verify(userRepository).save(any());
    }
}
