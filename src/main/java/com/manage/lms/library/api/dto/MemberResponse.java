package com.manage.lms.library.api.dto;

import com.manage.lms.library.domain.model.Member;

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

    public static MemberResponse from(Member member) {
        return new MemberResponse(
                member.getId(),
                member.getFirstName(),
                member.getLastName(),
                member.getFullName()
        );
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