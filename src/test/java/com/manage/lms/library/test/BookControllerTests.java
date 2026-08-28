package com.manage.lms.library.test;

import com.manage.lms.library.application.service.BookService;
import com.manage.lms.library.api.controller.BookController;

import com.manage.lms.library.domain.model.Book;
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

@WebMvcTest(BookController.class)
@Import({
        SecurityConfig.class,
        JwtAuthenticationFilter.class
})
public class BookControllerTests {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private BookService bookService;

    @MockitoBean
    private JwtService jwtService;

    @MockitoBean
    private UserDetailsService userDetailsService;

    @Test
    void getBooksReturnsBooks() throws Exception {

        Book book1 = new Book(
                "The Hobbit",
                "J.R.R. Tolkien",
                1937
        );

        book1.setId(1);

        Book book2 = new Book(
                "1984",
                "George Orwell",
                1949
        );

        book2.setId(2);

        when(bookService.getBooks())
                .thenReturn(List.of(book1, book2));

        mockMvc.perform(
                        get("/api/books")
                                .with(user("twilight")
                                        .roles("USER"))
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].title").value("The Hobbit"))
                .andExpect(jsonPath("$[1].title").value("1984"));
    }

    @Test
    void adminCanGetBooks() throws Exception {

        Book book =
                new Book(
                        "The Hobbit",
                        "J.R.R. Tolkien",
                        1937
                );

        book.setId(1);

        when(bookService.getBooks())
                .thenReturn(List.of(book));

        mockMvc.perform(
                        get("/api/books")
                                .with(user("celestia")
                                        .roles("ADMIN"))
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].title")
                        .value("The Hobbit"));
    }

    @Test
    void userCannotCreateBook() throws Exception {

        mockMvc.perform(
                        post("/api/books")
                                .with(user("twilight")
                                        .roles("USER"))
                                .contentType("application/json")
                                .content("""
                                        {
                                            "title": "The Hobbit",
                                            "author": "J.R.R. Tolkien",
                                            "year": 1937
                                        }
                                        """)
                )
                .andExpect(status().isForbidden());
    }

    @Test
    void adminCanCreateBook() throws Exception {

        Book book =
                new Book(
                        "The Hobbit",
                        "J.R.R. Tolkien",
                        1937
                );

        book.setId(1);

        when(bookService.createBook(
                "The Hobbit",
                "J.R.R. Tolkien",
                1937
        )).thenReturn(book);

        mockMvc.perform(
                        post("/api/books")
                                .with(user("celestia")
                                        .roles("ADMIN"))
                                .contentType("application/json")
                                .content("""
                                        {
                                            "title": "The Hobbit",
                                            "author": "J.R.R. Tolkien",
                                            "year": 1937
                                        }
                                        """)
                )
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.title")
                        .value("The Hobbit"));

        verify(bookService).createBook(
                "The Hobbit",
                "J.R.R. Tolkien",
                1937
        );
    }

    @Test
    void getBookReturnsBook() throws Exception {

        Book book =
                new Book(
                        "The Hobbit",
                        "J.R.R. Tolkien",
                        1937
                );

        book.setId(1);

        when(bookService.getBook(1))
                .thenReturn(book);

        mockMvc.perform(
                        get("/api/books/1")
                                .with(user("twilight")
                                        .roles("USER"))
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.title")
                        .value("The Hobbit"))
                .andExpect(jsonPath("$.author")
                        .value("J.R.R. Tolkien"))
                .andExpect(jsonPath("$.year")
                        .value(1937));

        verify(bookService).getBook(1);
    }

    @Test
    void adminCanUpdateBook() throws Exception {

        Book book =
                new Book(
                        "The Hobbit Updated",
                        "J.R.R. Tolkien",
                        1937
                );

        book.setId(1);

        when(bookService.updateBook(
                1,
                "The Hobbit Updated",
                "J.R.R. Tolkien",
                1937
        )).thenReturn(book);

        mockMvc.perform(
                        put("/api/books/1")
                                .with(user("celestia")
                                        .roles("ADMIN"))
                                .contentType("application/json")
                                .content("""
                                    {
                                        "title": "The Hobbit Updated",
                                        "author": "J.R.R. Tolkien",
                                        "year": 1937
                                    }
                                    """)
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title")
                        .value("The Hobbit Updated"));

        verify(bookService).updateBook(
                1,
                "The Hobbit Updated",
                "J.R.R. Tolkien",
                1937
        );
    }

    @Test
    void userCannotUpdateBook() throws Exception {

        mockMvc.perform(
                        put("/api/books/1")
                                .with(user("twilight")
                                        .roles("USER"))
                                .contentType("application/json")
                                .content("""
                                    {
                                        "title": "The Hobbit Updated",
                                        "author": "J.R.R. Tolkien",
                                        "year": 1937
                                    }
                                    """)
                )
                .andExpect(status().isForbidden());
    }

    @Test
    void adminCanDeleteBook() throws Exception {
        mockMvc.perform(
                        delete("/api/books/1")
                                .with(user("celestia")
                                        .roles("ADMIN"))
                )
                .andExpect(status().isNoContent());

        verify(bookService).deleteBook(1);
    }

    @Test
    void searchByTitleReturnsMatchingBooks() throws Exception {

        Book book =
                new Book(
                        "The Hobbit",
                        "J.R.R. Tolkien",
                        1937
                );

        book.setId(1);

        when(bookService.searchByTitle("Hobbit"))
                .thenReturn(List.of(book));

        mockMvc.perform(
                        get("/api/books/search/title")
                                .param("title", "Hobbit")
                                .with(user("twilight")
                                        .roles("USER"))
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()")
                        .value(1))
                .andExpect(jsonPath("$[0].title")
                        .value("The Hobbit"));

        verify(bookService).searchByTitle("Hobbit");
    }

    @Test
    void searchByAuthorReturnsMatchingBooks() throws Exception {

        Book book =
                new Book(
                        "The Hobbit",
                        "J.R.R. Tolkien",
                        1937
                );

        book.setId(1);

        when(bookService.searchByAuthor("Tolkien"))
                .thenReturn(List.of(book));

        mockMvc.perform(
                        get("/api/books/search/author")
                                .param("author", "Tolkien")
                                .with(user("twilight")
                                        .roles("USER"))
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()")
                        .value(1))
                .andExpect(jsonPath("$[0].author")
                        .value("J.R.R. Tolkien"));
    }

    @Test
    void getBooksOrderedByYearReturnsSortedBooks() throws Exception {

        Book oldBook =
                new Book(
                        "The Hobbit",
                        "J.R.R. Tolkien",
                        1937
                );

        oldBook.setId(1);

        Book newBook =
                new Book(
                        "Harry Potter",
                        "J.K. Rowling",
                        1997
                );

        newBook.setId(2);

        when(bookService.getBooksOrderedByYear())
                .thenReturn(List.of(oldBook, newBook));

        mockMvc.perform(
                        get("/api/books/sort/year")
                                .with(user("twilight")
                                        .roles("USER"))
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].year")
                        .value(1937))
                .andExpect(jsonPath("$[1].year")
                        .value(1997));
    }



}