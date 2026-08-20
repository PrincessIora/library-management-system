package com.manage.lms.library.application.service;

import com.manage.lms.library.domain.exception.ValidationException;
import com.manage.lms.library.domain.model.Member;
import com.manage.lms.library.domain.repository.MemberRepository;

import java.util.List;

public class MemberService {

    private final MemberRepository memberRepository;

    public MemberService(MemberRepository memberRepository) {
        this.memberRepository = memberRepository;
    }

    public Member createMember(String firstName, String lastName) {

        if (firstName == null || firstName.isBlank()) {
            throw new ValidationException("First Name is required.");
        }

        if (lastName == null || lastName.isBlank()) {
            throw new ValidationException("Last Name is required.");
        }

        Member newMember = new Member(
                firstName.trim(),
                lastName.trim()
        );

        return memberRepository.save(newMember);
    }

    public List<Member> getMembers() {
        return memberRepository.findAll();
    }

    public Member getMember(int id) {
        return memberRepository.findById(id)
                .orElseThrow(() ->
                        new ValidationException(
                                "Member with ID " + id + " does not exist."
                        ));
    }
}