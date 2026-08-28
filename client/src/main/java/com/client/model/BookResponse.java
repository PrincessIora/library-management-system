package com.client.model;

public class BookResponse {
    private int id;
    private String title;
    private String author;
    private int year;
    private BookStatus status;

    public BookResponse() {
    }

    public BookResponse(int id, String title, String author, int year, BookStatus status) {
        this.id = id;
        this.title = title;
        this.author = author;
        this.year = year;
        this.status = status;
    }

    public int getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String getAuthor() {
        return author;
    }

    public int getYear() {
        return year;
    }

    public BookStatus getStatus() {
        return status;
    }
}