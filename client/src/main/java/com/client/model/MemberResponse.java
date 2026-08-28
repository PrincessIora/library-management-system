package com.client.model;

public class MemberResponse {

    private int id;
    private String firstName;
    private String lastName;
    private String fullName;

    public MemberResponse() {
    }

    public MemberResponse(
            int id,
            String firstName,
            String lastName,
            String fullName
    ) {
        this.id = id;
        this.firstName = firstName;
        this.lastName = lastName;
        this.fullName = fullName;
    }

    public int getId() {
        return id;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public String getFullName() {
        return fullName;
    }
}