package two.two_user.client.dto.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class GithubInfoRegisterRequest {
    private String accessToken;
    private Long memberId;
    private String username;

}
