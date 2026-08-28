package com.manage.lms.library.test;

import com.manage.lms.library.domain.model.Book;
import com.manage.lms.library.domain.model.Member;
import com.manage.lms.library.domain.model.User;
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

    @Override
    public Member update(Member member) {

        for (int i = 0; i < members.size(); i++) {

            if (members.get(i).getId() == member.getId()) {
                members.set(i, member);
                return member;
            }
        }

        return null;
    }

    @Override
    public void deleteById(int id) {
            members.removeIf(
                    member -> member.getId() == id
            );
        }



    @Override
    public List<Member> searchByName(String name){
        return members.stream()
                .filter(member ->
                                (
                        member.getFirstName().equals(name))
                                        || ( member.getLastName().equals(name))
                ).toList();


    }
}