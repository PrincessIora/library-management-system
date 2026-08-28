package com.manage.lms.library.test;

import com.manage.lms.library.api.controller.LoanController;
import com.manage.lms.library.application.service.LoanService;
import com.manage.lms.library.config.SecurityConfig;
import com.manage.lms.library.domain.model.Loan;
import com.manage.lms.library.infrastructure.security.JwtAuthenticationFilter;
import com.manage.lms.library.infrastructure.security.JwtService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;


import org.junit.jupiter.api.Test;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;


@WebMvcTest(LoanController.class)
@Import({
        SecurityConfig.class,
        JwtAuthenticationFilter.class
})
public class LoanControllerTests {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private LoanService loanService;

    @MockitoBean
    private JwtService jwtService;

    @MockitoBean
    private UserDetailsService userDetailsService;

    @Test
    void adminCanBorrowBook() throws Exception {

        Loan loan =
                new Loan(
                        1,
                        1,
                        LocalDateTime.now(),
                        LocalDate.now().plusWeeks(2)
                );

        loan.setId(1);

        when(loanService.borrowBook(1, 1))
                .thenReturn(loan);

        mockMvc.perform(
                        post("/api/loans/borrow")
                                .with(user("celestia")
                                        .roles("ADMIN"))
                                .contentType("application/json")
                                .content("""
                                        {
                                            "bookId": 1,
                                            "memberId": 1
                                        }
                                        """)
                )
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.bookId").value(1))
                .andExpect(jsonPath("$.memberId").value(1));

        verify(loanService).borrowBook(1, 1);
    }

    @Test
    void userCannotBorrowBook() throws Exception {

        mockMvc.perform(
                        post("/api/loans/borrow")
                                .with(user("twilight")
                                        .roles("USER"))
                                .contentType("application/json")
                                .content("""
                                        {
                                            "bookId": 1,
                                            "memberId": 1
                                        }
                                        """)
                )
                .andExpect(status().isForbidden());
    }


    @Test
    void adminCanReturnBook() throws Exception {
        Loan loan = new Loan(1, 1, LocalDateTime.now().minusDays(5), LocalDate.now().plusDays(9));
        loan.setId(1);
        loan.setReturnedDate(LocalDateTime.now());
        when(
                loanService.returnBook(1)).thenReturn(loan);
        mockMvc.perform(
                        post("/api/loans/return/1")
                                .with(user("celestia")
                                        .roles("ADMIN")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.bookId").value(1))
                .andExpect(jsonPath("$.memberId").value(1))
                .andExpect(jsonPath("$.returnedDate")
                        .exists());
        verify(loanService).returnBook(1);
    }

    @Test
    void userCannotReturnBook() throws Exception {
        mockMvc.perform(
                        post("/api/loans/return/1")
                                .with(user("twilight")
                                        .roles("USER")))
                .andExpect(status().isForbidden());
        verifyNoInteractions(loanService);
    }


    @Test
    void userCanGetLoanHistory() throws Exception {
        Loan loan1 = new Loan(1, 1, LocalDateTime.now().minusDays(5), LocalDate.now().plusDays(9));
        loan1.setId(1);
        Loan loan2 = new Loan(2, 1, LocalDateTime.now().minusDays(3), LocalDate.now().plusDays(11));
        loan2.setId(2);
        when(
                loanService.getLoanHistory()).thenReturn(List.of(loan1, loan2));
        mockMvc.perform(
                        get("/api/loans")
                                .with(user("twilight")
                                        .roles("USER")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].bookId").value(1))
                .andExpect(jsonPath("$[0].memberId").value(1))
                .andExpect(jsonPath("$[1].id").value(2))
                .andExpect(jsonPath("$[1].bookId").value(2))
                .andExpect(jsonPath("$[1].memberId").value(1));
        verify(loanService).getLoanHistory();
    }

    @Test
    void adminCanGetLoanHistory() throws Exception {
        when(
                loanService.getLoanHistory()).thenReturn(List.of());
        mockMvc.perform(
                        get("/api/loans")
                                .with(user("celestia")
                                        .roles("ADMIN")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(0));
        verify(loanService).getLoanHistory();
    }

    @Test
    void userCanGetActiveLoans() throws Exception {
        Loan loan = new Loan(1, 1, LocalDateTime.now().minusDays(2), LocalDate.now().plusDays(12));
        loan.setId(1);
        when(
                loanService.getActiveLoans()).thenReturn(List.of(loan));
        mockMvc.perform(
                        get("/api/loans/active")
                                .with(user("twilight")
                                        .roles("USER")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].bookId").value(1))
                .andExpect(jsonPath("$[0].memberId").value(1));
        verify(loanService).getActiveLoans();
    }

    @Test
    void adminCanGetActiveLoans() throws Exception {
        when(
                loanService.getActiveLoans()).thenReturn(List.of());
        mockMvc.perform(
                        get("/api/loans/active")
                                .with(user("celestia")
                                        .roles("ADMIN")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(0));
        verify(loanService).getActiveLoans();
    }

    @Test
    void userCanGetOverdueLoans() throws Exception {
        Loan loan = new Loan(1, 1, LocalDateTime.now().minusWeeks(3), LocalDate.now().minusWeeks(1));
        loan.setId(1);
        when(
                loanService.getOverdueLoans()).thenReturn(List.of(loan));
        mockMvc.perform(
                        get("/api/loans/overdue")
                                .with(user("twilight")
                                        .roles("USER")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].bookId").value(1))
                .andExpect(jsonPath("$[0].memberId").value(1));
        verify(loanService).getOverdueLoans();
    }

    @Test
    void adminCanGetOverdueLoans() throws Exception {
        when(
                loanService.getOverdueLoans()).thenReturn(List.of());
        mockMvc.perform(
                        get("/api/loans/overdue")
                                .with(user("celestia")
                                        .roles("ADMIN")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(0));
        verify(loanService).getOverdueLoans();
    }

    @Test
    void userCanGetMemberLoanHistory() throws Exception {
        Loan loan = new Loan(1, 5, LocalDateTime.now().minusDays(4), LocalDate.now().plusDays(10));
        loan.setId(1);
        when(
                loanService.getMemberLoanHistory(5)).thenReturn(List.of(loan));
        mockMvc.perform(
                        get("/api/loans/member/5")
                                .with(user("twilight")
                                        .roles("USER")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].bookId").value(1))
                .andExpect(jsonPath("$[0].memberId").value(5));
        verify(loanService).getMemberLoanHistory(5);
    }

    @Test
    void adminCanGetMemberLoanHistory() throws Exception {
        when(
                loanService.getMemberLoanHistory(5)).thenReturn(List.of());
        mockMvc.perform(
                        get("/api/loans/member/5")
                                .with(user("celestia")
                                        .roles("ADMIN")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(0));
        verify(loanService).getMemberLoanHistory(5);
    }

    @Test
    void userCanGetBookLoanHistory() throws Exception {
        Loan loan = new Loan(7, 1, LocalDateTime.now().minusDays(6), LocalDate.now().plusDays(8));
        loan.setId(1);
        when(
                loanService.getBookLoanHistory(7)).thenReturn(List.of(loan));
        mockMvc.perform(
                        get("/api/loans/book/7")
                                .with(user("twilight")
                                        .roles("USER")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].bookId").value(7))
                .andExpect(jsonPath("$[0].memberId").value(1));
        verify(loanService).getBookLoanHistory(7);
    }

    @Test
    void adminCanGetBookLoanHistory() throws Exception {
        when(
                loanService.getBookLoanHistory(7)).thenReturn(List.of());
        mockMvc.perform(
                        get("/api/loans/book/7")
                                .with(user("celestia")
                                        .roles("ADMIN")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(0));
        verify(loanService).getBookLoanHistory(7);
    }

    @Test
    void unauthenticatedUserCannotGetLoanHistory() throws Exception {
        mockMvc.perform(
                        get("/api/loans"))
                .andExpect(status().isForbidden());
        verifyNoInteractions(loanService);
    }

    @Test
    void unauthenticatedUserCannotBorrowBook() throws Exception {
        mockMvc.perform(
                        post("/api/loans/borrow")
                                .contentType("application/json")
                                .content("""
                                        { "bookId": 1,
                                         "memberId": 1 } """))
                .andExpect(status().isForbidden());
        verifyNoInteractions(loanService);
    }

    @Test
    void unauthenticatedUserCannotReturnBook() throws Exception {
        mockMvc.perform(
                        post("/api/loans/return/1"))
                .andExpect(status().isForbidden());
        verifyNoInteractions(loanService);
    }
}

