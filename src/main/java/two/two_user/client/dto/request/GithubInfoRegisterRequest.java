package two.two_user.client.dto.request;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class GithubInfoRegisterRequest {
    private final String token;
}
