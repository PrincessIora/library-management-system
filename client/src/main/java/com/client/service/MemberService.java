package com.client.service;

import com.client.model.MemberRequest;
import com.client.model.MemberResponse;

public class MemberService {

    private final ApiClient apiClient;

    public MemberService() {
        apiClient = new ApiClient();
    }

    public MemberResponse[] getMembers()
            throws Exception {

        return apiClient.get(
                "/members",
                MemberResponse[].class
        );
    }

    public MemberResponse getMember(
            int id
    ) throws Exception {

        return apiClient.get(
                "/members/" + id,
                MemberResponse.class
        );
    }

    public MemberResponse[] searchByName(
            String name
    ) throws Exception {

        return apiClient.get(
                "/members/search?name="
                        + java.net.URLEncoder.encode(
                        name,
                        java.nio.charset.StandardCharsets.UTF_8
                ),
                MemberResponse[].class
        );
    }

    public MemberResponse createMember(
            String firstName,
            String lastName
    ) throws Exception {

        MemberRequest request =
                new MemberRequest(
                        firstName,
                        lastName
                );

        return apiClient.post(
                "/members",
                request,
                MemberResponse.class
        );
    }

    public MemberResponse updateMember(
            int id,
            String firstName,
            String lastName
    ) throws Exception {

        MemberRequest request =
                new MemberRequest(
                        firstName,
                        lastName
                );

        return apiClient.put(
                "/members/" + id,
                request,
                MemberResponse.class
        );
    }

    public void deleteMember(
            int id
    ) throws Exception {

        apiClient.delete(
                "/members/" + id
        );
    }
}