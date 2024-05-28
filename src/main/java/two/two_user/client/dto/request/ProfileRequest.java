package two.two_user.client.dto.request;

import lombok.Builder;
import lombok.Getter;
import two.two_user.domain.Member;

@Getter
@Builder
public class ProfileRequest {
    private Long id;

    private String nickname;

    private String profileUrl;

    public static ProfileRequest from(Member member){
        return ProfileRequest.builder()
                .id(member.getId())
                .nickname(member.getNickname())
                .profileUrl(member.getProfileImg())
                .build();
    }
}
