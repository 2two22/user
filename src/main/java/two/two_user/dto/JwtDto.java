package two.two_user.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class JwtDto {
    String grantType;
    String accessToken;
    String refreshToken;
    long accessTokenExpiresTime;
}
