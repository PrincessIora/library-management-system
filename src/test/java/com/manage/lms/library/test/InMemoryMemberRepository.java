package com.manage.lms.library.test;

import com.manage.lms.library.domain.model.Member;
import com.manage.lms.library.domain.repository.MemberRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class InMemoryMemberRepository implements MemberRepository {

    private final List<Member> members = new ArrayList<>();
    private int nextId = 1;

    @Override
    public Member save(Member member) {
        member.setId(nextId++);
        members.add(member);
        return member;
    }

    @Override
    public Optional<Member> findById(int id) {
        return members.stream()
                .filter(member -> member.getId() == id)
                .findFirst();
    }

    @Override
    public List<Member> findAll() {
        return new ArrayList<>(members);
    }
}