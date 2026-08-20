package com.manage.lms.library.domain.model;

public class Member {
    private int id;
    private String firstName;
    private String lastName;

    public Member(String firstName, String lastName){
        this.firstName = firstName;
        this.lastName = lastName;
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
        return firstName + " " + lastName;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }
}
