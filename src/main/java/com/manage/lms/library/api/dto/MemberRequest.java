package com.manage.lms.library.api.dto;

public class MemberRequest {

    private String firstName;
    private String lastName;

    public MemberRequest() {
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }
}