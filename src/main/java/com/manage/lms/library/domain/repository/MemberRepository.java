package com.manage.lms.library.domain.repository;

import com.manage.lms.library.domain.model.Member;

import java.util.List;
import java.util.Optional;

public interface MemberRepository {
    Member save(Member member);

    Optional<Member> findById(int id);

    List<Member> findAll();

    Member update(Member member);

    void deleteById(int id);

    List<Member> searchByName(String name);

}
