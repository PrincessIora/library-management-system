package com.manage.lms.library.test;

import com.manage.lms.library.application.service.MemberService;
import com.manage.lms.library.domain.exception.ValidationException;
import com.manage.lms.library.domain.model.Member;
import com.manage.lms.library.domain.repository.MemberRepository;
import org.junit.jupiter.api.*;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;


public class MemberTests {
    @Test
    void createMemberCreatesMember() {
        MemberService service =
                new MemberService(new InMemoryMemberRepository());

        Member member =
                service.createMember("Twilight", "Sparkle");

        assertEquals("Twilight", member.getFirstName());
        assertEquals("Sparkle", member.getLastName());
    }
    @Test
    void createMemberRejectsMissingFirstName() {
        MemberService service =
                new MemberService(new InMemoryMemberRepository());

        assertThrows(
                ValidationException.class,
                () -> service.createMember("", "Sparkle")
        );
    }

    @Test
    void createMemberRejectsMissingLastName() {
        MemberService service =
                new MemberService(new InMemoryMemberRepository());

        assertThrows(
                ValidationException.class,
                () -> service.createMember("Twilight", "")
        );
    }

    @Test
    void createMemberRejectsNullFirstName() {
        MemberService service =
                new MemberService(new InMemoryMemberRepository());

        assertThrows(
                ValidationException.class,
                () -> service.createMember(null, "Sparkle")
        );
    }

    @Test
    void createMemberRejectsNullLastName() {
        MemberService service =
                new MemberService(new InMemoryMemberRepository());

        assertThrows(
                ValidationException.class,
                () -> service.createMember("Twilight", null)
        );
    }

    @Test
    void getMembersReturnsAllMembers() {

        MemberRepository repository =
                new InMemoryMemberRepository();

        MemberService service =
                new MemberService(repository);

        service.createMember("Twilight", "Sparkle");
        service.createMember("Rarity", "Belle");

        List<Member> members = service.getMembers();

        assertEquals(2, members.size());
    }

    @Test
    void getMemberReturnsMember() {

        MemberRepository repository =
                new InMemoryMemberRepository();

        MemberService service =
                new MemberService(repository);

        Member created =
                service.createMember("Twilight", "Sparkle");

        Member found =
                service.getMember(created.getId());

        assertEquals("Twilight", found.getFirstName());
        assertEquals("Sparkle", found.getLastName());
    }

    @Test
    void getMemberRejectsUnknownId() {

        MemberService service =
                new MemberService(new InMemoryMemberRepository());

        assertThrows(
                ValidationException.class,
                () -> service.getMember(999)
        );
    }


    @Test
    void createMembersHaveUniqueIds() {

        MemberService service =
                new MemberService(new InMemoryMemberRepository());

        Member first =
                service.createMember("Twilight", "Sparkle");

        Member second =
                service.createMember("Rarity", "Belle");

        assertNotEquals(first.getId(), second.getId());
    }

    @Test
    void memberFullNameCombinesFirstAndLastName() {

        MemberService service =
                new MemberService(new InMemoryMemberRepository());

        Member member =
                service.createMember("Twilight", "Sparkle");

        assertEquals("Twilight Sparkle", member.getFullName());
    }
}
