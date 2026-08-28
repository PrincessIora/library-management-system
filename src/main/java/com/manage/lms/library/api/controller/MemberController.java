package com.manage.lms.library.api.controller;

import com.manage.lms.library.api.dto.MemberRequest;
import com.manage.lms.library.api.dto.MemberResponse;
import com.manage.lms.library.application.service.MemberService;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/members")
public class MemberController {

    private final MemberService memberService;

    public MemberController(MemberService memberService) {
        this.memberService = memberService;
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    public List<MemberResponse> getMembers() {
        return memberService.getMembers()
                .stream()
                .map(MemberResponse::from)
                .toList();
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    public MemberResponse getMember(
            @PathVariable int id
    ) {
        return MemberResponse.from(
                memberService.getMember(id));
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("hasAnyRole('ADMIN')")
    public MemberResponse createMember(
            @RequestBody MemberRequest request
    ) {
        return MemberResponse.from(
                memberService.createMember(
                request.getFirstName(),
                request.getLastName()
                )
        );
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public MemberResponse updateMember(@PathVariable int id, @RequestBody MemberRequest request) {
        return MemberResponse.from(
                memberService.updateMember(
                        id,
                        request.getFirstName(),
                        request.getLastName()
                )
        );
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteMember(@PathVariable int id) {
        memberService.deleteMember(id);
    }

    @GetMapping("/search")
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    public List<MemberResponse> searchByName(@RequestParam String name) {
        return memberService.searchByName(name)
                .stream()
                .map(MemberResponse::from)
                .toList();
    }
}