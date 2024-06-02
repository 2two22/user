package two.two_user.member.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import two.two_user.domain.Member;
import two.two_user.domain.MemberStatus;


@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UserDto {
    private Long id;
    private String userId;
    private String nickName;
    private String description;
    private Long level;
    private Long numberOfFollowers;
    private Long numberOfFollows;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private Long numberOfPosts;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private Long numberOfScraps;
    private String job;
    private String profileUrl;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private Boolean isReader;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private Boolean isFollowing;
    private MemberStatus memberStatus;

    public static UserDto of(Member member, boolean isReader, boolean isFollowing,
                             Long numberOfFollowrs, Long numberOfFollows,
                             Long numberOfPosts , MemberStatus memberStatus){
        return UserDto.builder()
                .id(member.getId())
                .userId(member.getUserId())
                .nickName(member.getNickname())
                .description(member.getIntroduceMessage())
//                .level(member.getLevel().getLevelNumber())
                .profileUrl(member.getProfileImg())
                .numberOfFollowers(numberOfFollowrs)
                .numberOfFollows(numberOfFollows)
                .numberOfPosts(numberOfPosts)
                .isReader(isReader)
                .isFollowing(isFollowing)
                .job(member.getJob())
                .memberStatus(memberStatus)
                .build();
    }

    public static UserDto of(Member member, Long numberOfFollowrs,
                             Long numberOfFollows, Long numberOfPosts, Long numberOfScraps){
        return UserDto.builder()
                .id(member.getId())
                .userId(member.getUserId())
                .nickName(member.getNickname())
                .description(member.getIntroduceMessage())
//                .level(member.getLevel().getLevelNumber())
                .profileUrl(member.getProfileImg())
                .numberOfFollowers(numberOfFollowrs)
                .numberOfFollows(numberOfFollows)
                .job(member.getJob())
                .numberOfPosts(numberOfPosts)
                .numberOfScraps(numberOfScraps)
                .build();
    }
}
