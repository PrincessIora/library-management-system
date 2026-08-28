package com.manage.lms.library.test;

import com.manage.lms.library.api.controller.UserController;
import com.manage.lms.library.application.service.UserService;
import com.manage.lms.library.domain.model.User;
import com.manage.lms.library.domain.model.UserRole;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

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

@WebMvcTest(UserController.class)
@Import({
        SecurityConfig.class,
        JwtAuthenticationFilter.class
})
public class UserControllerTests {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private UserService userService;

    @MockitoBean
    private JwtService jwtService;

    @MockitoBean
    private UserDetailsService userDetailsService;

    @Test
    void userCannotGetUsers() throws Exception {

        mockMvc.perform(
                        get("/api/users")
                                .with(user("twilight")
                                        .roles("USER"))
                )
                .andExpect(status().isForbidden());
    }

    @Test
    void adminCanGetUsers() throws Exception {

        User twilight =
                new User(
                        "twilight",
                        "hashedPassword",
                        UserRole.USER
                );

        twilight.setId(1);

        User celestia =
                new User(
                        "celestia",
                        "hashedPassword",
                        UserRole.ADMIN
                );

        celestia.setId(2);

        when(userService.getAllUsers())
                .thenReturn(List.of(twilight, celestia));

        mockMvc.perform(
                        get("/api/users")
                                .with(user("celestia")
                                        .roles("ADMIN"))
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()")
                        .value(2))
                .andExpect(jsonPath("$[0].id")
                        .value(1))
                .andExpect(jsonPath("$[0].username")
                        .value("twilight"))
                .andExpect(jsonPath("$[0].role")
                        .value("USER"))
                .andExpect(jsonPath("$[1].id")
                        .value(2))
                .andExpect(jsonPath("$[1].username")
                        .value("celestia"))
                .andExpect(jsonPath("$[1].role")
                        .value("ADMIN"));

        verify(userService).getAllUsers();
    }

    @Test
    void unauthenticatedUserCannotGetUsers() throws Exception {

        mockMvc.perform(
                        get("/api/users")
                )
                .andExpect(status().isForbidden());
    }

    @Test
    void adminCanGetUserById() throws Exception {

        User user =
                new User(
                        "twilight",
                        "hashedPassword",
                        UserRole.USER
                );

        user.setId(1);

        when(userService.getUserById(1))
                .thenReturn(user);

        mockMvc.perform(
                        get("/api/users/1")
                                .with(user("celestia")
                                        .roles("ADMIN"))
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id")
                        .value(1))
                .andExpect(jsonPath("$.username")
                        .value("twilight"))
                .andExpect(jsonPath("$.role")
                        .value("USER"));

        verify(userService).getUserById(1);
    }


    @Test
    void userCannotGetUserById() throws Exception {

        mockMvc.perform(
                        get("/api/users/1")
                                .with(user("twilight")
                                        .roles("USER"))
                )
                .andExpect(status().isForbidden());
    }


    @Test
    void adminCanCreateUser() throws Exception {

        User createdUser =
                new User(
                        "twilight",
                        "hashedPassword",
                        UserRole.USER
                );

        createdUser.setId(1);

        when(userService.createUser(
                "twilight",
                "password123",
                UserRole.USER
        )).thenReturn(createdUser);

        mockMvc.perform(
                        post("/api/users")
                                .with(user("celestia")
                                        .roles("ADMIN"))
                                .contentType("application/json")
                                .content("""
                                        {
                                            "username": "twilight",
                                            "password": "password123",
                                            "role": "USER"
                                        }
                                        """)
                )
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id")
                        .value(1))
                .andExpect(jsonPath("$.username")
                        .value("twilight"))
                .andExpect(jsonPath("$.role")
                        .value("USER"))
                .andExpect(jsonPath("$.password")
                        .doesNotExist());

        verify(userService).createUser(
                "twilight",
                "password123",
                UserRole.USER
        );
    }


    @Test
    void userCannotCreateUser() throws Exception {

        mockMvc.perform(
                        post("/api/users")
                                .with(user("twilight")
                                        .roles("USER"))
                                .contentType("application/json")
                                .content("""
                                        {
                                            "username": "rarity",
                                            "password": "password123",
                                            "role": "USER"
                                        }
                                        """)
                )
                .andExpect(status().isForbidden());
    }


    @Test
    void adminCanUpdateUser() throws Exception {

        User updatedUser =
                new User(
                        "twilightUpdated",
                        "newHashedPassword",
                        UserRole.USER
                );

        updatedUser.setId(1);

        when(userService.updateUser(
                1,
                "twilightUpdated",
                "newPassword123",
                UserRole.USER
        )).thenReturn(updatedUser);

        mockMvc.perform(
                        put("/api/users/1")
                                .with(user("celestia")
                                        .roles("ADMIN"))
                                .contentType("application/json")
                                .content("""
                                        {
                                            "username": "twilightUpdated",
                                            "password": "newPassword123",
                                            "role": "USER"
                                        }
                                        """)
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id")
                        .value(1))
                .andExpect(jsonPath("$.username")
                        .value("twilightUpdated"))
                .andExpect(jsonPath("$.role")
                        .value("USER"))
                .andExpect(jsonPath("$.password")
                        .doesNotExist());

        verify(userService).updateUser(
                1,
                "twilightUpdated",
                "newPassword123",
                UserRole.USER
        );
    }


    @Test
    void userCannotUpdateUser() throws Exception {

        mockMvc.perform(
                        put("/api/users/1")
                                .with(user("twilight")
                                        .roles("USER"))
                                .contentType("application/json")
                                .content("""
                                        {
                                            "username": "twilightUpdated",
                                            "password": "newPassword123",
                                            "role": "USER"
                                        }
                                        """)
                )
                .andExpect(status().isForbidden());
    }


    @Test
    void adminCanDeleteUser() throws Exception {

        mockMvc.perform(
                        delete("/api/users/1")
                                .with(user("celestia")
                                        .roles("ADMIN"))
                )
                .andExpect(status().isNoContent());

        verify(userService).deleteUser(1);
    }


    @Test
    void userCannotDeleteUser() throws Exception {

        mockMvc.perform(
                        delete("/api/users/1")
                                .with(user("twilight")
                                        .roles("USER"))
                )
                .andExpect(status().isForbidden());
    }


    @Test
    void unauthenticatedUserCannotCreateUser() throws Exception {

        mockMvc.perform(
                        post("/api/users")
                                .contentType("application/json")
                                .content("""
                                        {
                                            "username": "twilight",
                                            "password": "password123",
                                            "role": "USER"
                                        }
                                        """)
                )
                .andExpect(status().isForbidden());
    }


    @Test
    void unauthenticatedUserCannotUpdateUser() throws Exception {

        mockMvc.perform(
                        put("/api/users/1")
                                .contentType("application/json")
                                .content("""
                                        {
                                            "username": "twilightUpdated",
                                            "password": "password123",
                                            "role": "USER"
                                        }
                                        """)
                )
                .andExpect(status().isForbidden());
    }


    @Test
    void unauthenticatedUserCannotDeleteUser() throws Exception {

        mockMvc.perform(
                        delete("/api/users/1")
                )
                .andExpect(status().isForbidden());
    }
}

