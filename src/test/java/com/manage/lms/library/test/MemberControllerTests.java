package com.manage.lms.library.test;


import com.manage.lms.library.api.controller.MemberController;
import com.manage.lms.library.application.service.MemberService;
import com.manage.lms.library.domain.model.Member;
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


@WebMvcTest(MemberController.class)
@Import({
        SecurityConfig.class,
        JwtAuthenticationFilter.class
})
public class MemberControllerTests {


    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private MemberService memberService;

    @MockitoBean
    private JwtService jwtService;

    @MockitoBean
    private UserDetailsService userDetailsService;


    @Test
    void adminCanCreateMember() throws Exception {

        Member member =
                new Member(
                        "Twilight",
                        "Sparkle"
                );

        member.setId(1);

        when(memberService.createMember(
                "Twilight",
                "Sparkle"
        )).thenReturn(member);

        mockMvc.perform(
                        post("/api/members")
                                .with(user("celestia")
                                        .roles("ADMIN"))
                                .contentType("application/json")
                                .content("""
                                        {
                                            "firstName": "Twilight",
                                            "lastName": "Sparkle"
                                        }
                                        """)
                )
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.firstName")
                        .value("Twilight"))
                .andExpect(jsonPath("$.lastName")
                        .value("Sparkle"));

        verify(memberService).createMember(
                "Twilight",
                "Sparkle"
        );
    }

    @Test
    void getMembersReturnsMembers() throws Exception {
        Member twilight = new Member("Twilight", "Sparkle");
        twilight.setId(1);
        Member rarity = new Member("Rarity", "Belle");
        rarity.setId(2);
        when(memberService.getMembers())
                .thenReturn(List.of(twilight, rarity));
        mockMvc.perform(
                        get("/api/members")
                                .with(user("twilight")
                                        .roles("USER")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].firstName").value("Twilight"))
                .andExpect(jsonPath("$[0].lastName").value("Sparkle"))
                .andExpect(jsonPath("$[1].id").value(2))
                .andExpect(jsonPath("$[1].firstName").value("Rarity"))
                .andExpect(jsonPath("$[1].lastName").value("Belle"));
        verify(memberService).getMembers();
    }

    @Test
    void getMemberReturnsMember() throws Exception {
        Member member = new Member("Twilight", "Sparkle");
        member.setId(1);

        when(memberService.getMember(1)).thenReturn(member);
        mockMvc.perform(
                        get("/api/members/1")
                                .with(user("twilight")
                                        .roles("USER"))
                ).andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.firstName").value("Twilight"))
                .andExpect(jsonPath("$.lastName").value("Sparkle"));
        verify(memberService).getMember(1);
    }

    @Test
    void userCannotCreateMember() throws Exception {
        mockMvc.perform(
                        post("/api/members")
                                .with(user("twilight")
                                        .roles("USER"))
                                .contentType("application/json")
                                .content("""
                                        { "firstName": "Twilight",
                                         "lastName": "Sparkle" }
                                        """))
                .andExpect(status().isForbidden());
        verify(memberService, never()).createMember(anyString(), anyString());
    }

    @Test
    void adminCanUpdateMember() throws Exception {
        Member member = new Member("Twilight", "Sparkle");
        member.setId(1);
        when(memberService.updateMember(
                1, "Twilight", "Sparkle"))
                .thenReturn(member);
        mockMvc.perform(
                        put("/api/members/1")
                                .with(user("celestia")
                                        .roles("ADMIN"))
                                .contentType("application/json")
                                .content("""
                                        { "firstName": "Twilight",
                                         "lastName": "Sparkle" }
                                        """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.firstName").value("Twilight"))
                .andExpect(jsonPath("$.lastName").value("Sparkle"));
        verify(memberService).updateMember(1, "Twilight", "Sparkle");
    }

    @Test
    void userCannotUpdateMember() throws Exception {
        mockMvc.perform(
                put("/api/members/1")
                        .with(user("twilight")
                                .roles("USER"))
                        .contentType("application/json")
                        .content(""" 
                                { "firstName": "Twilight",
                                 "lastName": "Sparkle" } 
                                 """))
                .andExpect(status().isForbidden());
        verify(memberService, never()).updateMember(anyInt(), anyString(), anyString());
    }

    @Test
    void adminCanDeleteMember() throws Exception {
        mockMvc.perform(
                delete("/api/members/1")
                        .with(user("celestia")
                                .roles("ADMIN")))
                .andExpect(status().isNoContent());
        verify(memberService).deleteMember(1);
    }

    @Test
    void userCannotDeleteMember() throws Exception {
        mockMvc.perform(
                delete("/api/members/1")
                        .with(user("twilight")
                                .roles("USER")) )
                .andExpect(status().isForbidden());
        verify(memberService, never()) .deleteMember(anyInt());
    }

    @Test
    void searchByNameReturnsMembers() throws Exception {
        Member member = new Member( "Twilight", "Sparkle" );
        member.setId(1);
        when(
                memberService.searchByName("Twilight"))
                .thenReturn(List.of(member));
        mockMvc.perform(
                get("/api/members/search")
                        .param("name", "Twilight")
                        .with(user("twilight")
                                .roles("USER")) )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].firstName") .value("Twilight"))
                .andExpect(jsonPath("$[0].lastName") .value("Sparkle"));
        verify(memberService).searchByName("Twilight");
    }

    @Test
    void unauthenticatedUserCannotGetMembers() throws Exception {
        mockMvc.perform(
                get("/api/members") )
                .andExpect(status()
                        .isForbidden());
        verify(memberService, never()).getMembers();
    }
}
