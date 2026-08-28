package com.client.service;
import com.client.model.BookRequest;
import com.client.model.BookResponse;


public class BookService {

    private final ApiClient apiClient;

    public BookService() {
        apiClient = new ApiClient();
    }

    public BookResponse[] getBooks()
            throws Exception {

        return apiClient.get(
                "/books",
                BookResponse[].class
        );
    }

    public BookResponse getBook(int id)
            throws Exception {

        return apiClient.get(
                "/books/" + id,
                BookResponse.class
        );
    }

    public BookResponse createBook(
            String title,
            String author,
            int year
    ) throws Exception {

        BookRequest request =
                new BookRequest(
                        title,
                        author,
                        year
                );

        return apiClient.post(
                "/books",
                request,
                BookResponse.class
        );
    }

    public BookResponse updateBook(
            int id,
            String title,
            String author,
            int year
    ) throws Exception {

        BookRequest request =
                new BookRequest(
                        title,
                        author,
                        year
                );

        return apiClient.put(
                "/books/" + id,
                request,
                BookResponse.class
        );
    }

    public void deleteBook(int id)
            throws Exception {

        apiClient.delete(
                "/books/" + id
        );
    }

    public BookResponse[] searchByTitle(
            String title
    ) throws Exception {

        return apiClient.get(
                "/books/search/title?title="
                        + java.net.URLEncoder.encode(
                        title,
                        java.nio.charset.StandardCharsets.UTF_8
                ),
                BookResponse[].class
        );
    }

    public BookResponse[] searchByAuthor(
            String author
    ) throws Exception {

        return apiClient.get(
                "/books/search/author?author="
                        + java.net.URLEncoder.encode(
                        author,
                        java.nio.charset.StandardCharsets.UTF_8
                ),
                BookResponse[].class
        );
    }

    public BookResponse[] getBooksOrderedByYear()
            throws Exception {

        return apiClient.get(
                "/books/sort/year",
                BookResponse[].class
        );
    }
}