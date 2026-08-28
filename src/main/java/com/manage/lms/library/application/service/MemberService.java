package com.manage.lms.library.application.service;

import com.manage.lms.library.domain.exception.ValidationException;
import com.manage.lms.library.domain.model.Book;
import com.manage.lms.library.domain.model.Member;
import com.manage.lms.library.domain.repository.LoanRepository;
import com.manage.lms.library.domain.repository.MemberRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class MemberService {

    private final MemberRepository memberRepository;
    private final LoanRepository loanRepository;

    public MemberService(MemberRepository memberRepository, LoanRepository loanRepository) {
        this.memberRepository = memberRepository;
        this.loanRepository = loanRepository;
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

    public void deleteMember(int id) {

        Member member = memberRepository.findById(id)
                .orElseThrow(() ->
                        new ValidationException(
                                "Member with ID " + id + " was not found."
                        )
                );

        if (!loanRepository.findByMemberId(id).isEmpty()) {
            throw new ValidationException(
                    "Member cannot be deleted because they have loan history."
            );
        }

        memberRepository.deleteById(id);

    }

    public Member updateMember(int id, String firstname, String lastname) {
        Member member = memberRepository.findById(id)
                .orElseThrow(() ->
                        new ValidationException(
                                "Member with ID " + id + " was not found."
                        )
                );

        if (firstname == null || firstname.isBlank()) {
            throw new ValidationException("First Name is required.");
        }

        if (lastname == null || lastname.isBlank()) {
            throw new ValidationException("Last Name is required.");
        }

        member.setFirstName(firstname.trim());
        member.setLastName(lastname.trim());

        return memberRepository.update(member);
    }

    public List<Member> searchByName(String name) {


        if (name == null || name.isBlank()) {
            throw new ValidationException(
                    "Search name cannot be empty."
            );
        }
        return memberRepository.searchByName(name);


    }


}