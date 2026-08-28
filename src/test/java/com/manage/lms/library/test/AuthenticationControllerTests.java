package com.manage.lms.library.test;

import com.manage.lms.library.api.controller.AuthenticationController;
import com.manage.lms.library.api.controller.UserController;
import com.manage.lms.library.application.service.AuthenticationService;
import com.manage.lms.library.domain.model.User;
import com.manage.lms.library.domain.model.UserRole;
import com.manage.lms.library.domain.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;
import java.util.Optional;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;

import com.manage.lms.library.infrastructure.security.JwtService;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;

import com.manage.lms.library.config.SecurityConfig;
import com.manage.lms.library.infrastructure.security.JwtAuthenticationFilter;
import org.springframework.context.annotation.Import;

import org.springframework.security.core.userdetails.UserDetailsService;

@WebMvcTest(AuthenticationController.class)
@Import({
        SecurityConfig.class,
        JwtAuthenticationFilter.class
})
public class AuthenticationControllerTests {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private AuthenticationService authenticationService;

    @MockitoBean
    private JwtService jwtService;

    @MockitoBean
    private UserDetailsService userDetailsService;

    @MockitoBean
    private UserRepository userRepository;

    @Test
    void loginReturnsTokenAndUser() throws Exception {

        User user =
                new User(
                        "twilight",
                        "hashedPassword",
                        UserRole.USER
                );

        user.setId(1);

        when(authenticationService.authenticate(
                "twilight",
                "password123"
        )).thenReturn("fake-jwt-token");

        when(userRepository.findByUsername("twilight"))
                .thenReturn(Optional.of(user));

        mockMvc.perform(
                        post("/api/auth/login")
                                .contentType("application/json")
                                .content("""
                                        {
                                            "username": "twilight",
                                            "password": "password123"
                                        }
                                        """)
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token")
                        .value("fake-jwt-token"))
                .andExpect(jsonPath("$.user.username")
                        .value("twilight"))
                .andExpect(jsonPath("$.user.role")
                        .value("USER"))
                .andExpect(jsonPath("$.user.id")
                        .value(1))
                .andExpect(jsonPath("$.user.password")
                        .doesNotExist());

        verify(authenticationService).authenticate(
                "twilight",
                "password123"
        );

        verify(userRepository).findByUsername(
                "twilight"
        );
    }


    @Test
    void loginTrimsUsernameWhenLookingUpUser() throws Exception {

        User user =
                new User(
                        "twilight",
                        "hashedPassword",
                        UserRole.USER
                );

        user.setId(1);

        when(authenticationService.authenticate(
                "  twilight  ",
                "password123"
        )).thenReturn("fake-jwt-token");

        when(userRepository.findByUsername("twilight"))
                .thenReturn(Optional.of(user));

        mockMvc.perform(
                        post("/api/auth/login")
                                .contentType("application/json")
                                .content("""
                                        {
                                            "username": "  twilight  ",
                                            "password": "password123"
                                        }
                                        """)
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token")
                        .value("fake-jwt-token"))
                .andExpect(jsonPath("$.user.username")
                        .value("twilight"));

        verify(authenticationService).authenticate(
                "  twilight  ",
                "password123"
        );

        verify(userRepository).findByUsername(
                "twilight"
        );
    }


    @Test
    void adminCanLogin() throws Exception {

        User user =
                new User(
                        "celestia",
                        "hashedPassword",
                        UserRole.ADMIN
                );

        user.setId(2);

        when(authenticationService.authenticate(
                "celestia",
                "admin123"
        )).thenReturn("admin-jwt-token");

        when(userRepository.findByUsername("celestia"))
                .thenReturn(Optional.of(user));

        mockMvc.perform(
                        post("/api/auth/login")
                                .contentType("application/json")
                                .content("""
                                        {
                                            "username": "celestia",
                                            "password": "admin123"
                                        }
                                        """)
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token")
                        .value("admin-jwt-token"))
                .andExpect(jsonPath("$.user.username")
                        .value("celestia"))
                .andExpect(jsonPath("$.user.role")
                        .value("ADMIN"))
                .andExpect(jsonPath("$.user.password")
                        .doesNotExist());
    }


    @Test
    void loginRequiresUsername() throws Exception {

        mockMvc.perform(
                        post("/api/auth/login")
                                .contentType("application/json")
                                .content("""
                                        {
                                            "password": "password123"
                                        }
                                        """)
                )
                .andExpect(status().is5xxServerError());
    }


    @Test
    void loginRequiresPassword() throws Exception {

        mockMvc.perform(
                        post("/api/auth/login")
                                .contentType("application/json")
                                .content("""
                                        {
                                            "username": "twilight"
                                        }
                                        """)
                )
                .andExpect(status().is5xxServerError());
    }


    @Test
    void loginRejectsInvalidCredentials() throws Exception {

        when(authenticationService.authenticate(
                "twilight",
                "wrongPassword"
        )).thenThrow(
                new com.manage.lms.library.domain.exception.AuthenticationException(
                        "Invalid username or password"
                )
        );

        mockMvc.perform(
                        post("/api/auth/login")
                                .contentType("application/json")
                                .content("""
                                        {
                                            "username": "twilight",
                                            "password": "wrongPassword"
                                        }
                                        """)
                )
                .andExpect(status().is4xxClientError());

        verify(authenticationService).authenticate(
                "twilight",
                "wrongPassword"
        );
    }
}
