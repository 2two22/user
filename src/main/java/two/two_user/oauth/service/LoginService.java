package two.two_user.oauth.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.util.ObjectUtils;
import org.springframework.web.client.RestTemplate;
import two.two_user.client.GithubClient;
import two.two_user.client.S3Client;
import two.two_user.client.dto.request.GithubInfoRegisterRequest;
import two.two_user.domain.Member;
import two.two_user.domain.repository.MemberRepository;
import two.two_user.exception.BudException;
import two.two_user.exception.ErrorCode;
import two.two_user.jwt.TokenProvider;
import two.two_user.jwt.dto.JwtDto;

import java.util.*;

@Slf4j
@Service
@RequiredArgsConstructor
public class LoginService {

    private final static String IS_ADD_INFO = "isAddInfo";
    private final TokenProvider tokenProvider;
    private final MemberRepository memberRepository;
    private final S3Client s3Client;
    private final GithubClient githubClient;

    @Value("${spring.security.oauth2.client.registration.github.client-id}")
    String client_id;

    @Value("${spring.security.oauth2.client.registration.github.client-secret}")
    String client_secret;

    @Transactional
    public List<String> codeToJwt(String code) {
        HttpHeaders tokenHeaders = new HttpHeaders();
        RestTemplate tokenTemplate = new RestTemplate();
        tokenHeaders.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        // code 를 통해 github token 발급
        MultiValueMap<String, String> tokenParam = new LinkedMultiValueMap<>();
        tokenParam.add("code", code);
        tokenParam.add("client_id", client_id);
        tokenParam.add("client_secret", client_secret);
        HttpEntity<MultiValueMap<String, String>> oAuthTokenRequest = new HttpEntity<>(tokenParam, tokenHeaders);
        ResponseEntity<String> tokenResponse = tokenTemplate.postForEntity("https://github.com/login/oauth/access_token", oAuthTokenRequest, String.class);
        if (ObjectUtils.isEmpty(tokenResponse.getBody()) || !tokenResponse.getBody().contains("access_token") || tokenResponse.getBody().contains("error"))
            return null;
        // github 로부터 온 response 에서 토큰 추출
        String OAuthAccessToken = tokenResponse.getBody().split("&")[0].replace("access_token=", "");
        HttpHeaders userHeaders = new HttpHeaders();
        RestTemplate userTemplate = new RestTemplate();
        MultiValueMap<String, String> userParam = new LinkedMultiValueMap<>();
        userHeaders.add(HttpHeaders.AUTHORIZATION, "Bearer " + OAuthAccessToken);
        HttpEntity<MultiValueMap<String, String>> getUserInfoRequest = new HttpEntity<>(userParam, userHeaders);
        ResponseEntity<Map> userResponse = userTemplate.exchange("https://api.github.com/user", HttpMethod.GET, getUserInfoRequest, Map.class);
        if (ObjectUtils.isEmpty(userResponse)) return null;

        Member member = saveOrUpdate(userResponse.getBody());
        List<String> response = setTokenInfo(member);
        // github 서버에 github info 정보 저장 request
        githubClient.registerUserToken(response.get(0), GithubInfoRegisterRequest.builder()
                .memberId(member.getId())
                .username(member.getUsername())
                .accessToken(OAuthAccessToken)
                .build());
        return response;
    }

    private Member saveOrUpdate(Map userResponse) {
        Optional<Member> optionalMember = memberRepository.findByUserId(userResponse.get("login").toString());
        Member member;

        String userId = userResponse.get("login").toString();
        String userCode = userResponse.get("id").toString();
        String nickname;

        if (ObjectUtils.isEmpty(userResponse.get("name"))) {
            nickname = userId;
        } else {
            nickname = userResponse.get("name").toString();
        }

        if (optionalMember.isEmpty()) {
            Random random = new Random();
            int randNum = random.nextInt(30) + 1;
            String imageUrl = "profiles/basic/" + randNum + ".png";

            member = Member.register(userId, userCode, imageUrl);
        }
        else {
            member = optionalMember.get();
            member.update(userCode);
        }
        memberRepository.save(member);

        return member;
    }


    public List<String> tokenRefresh(Member member) {
        if (!tokenProvider.validateToken(member.getRefreshToken())) {
            throw new BudException(ErrorCode.INVALID_TOKEN);
        }

        return setTokenInfo(member);
    }

    private List<String> setTokenInfo(Member member) {
        List<String> tokenInfo = new ArrayList<>();
        JwtDto token = tokenProvider.generateToken(member.getUserId());
        tokenInfo.add(token.getGrantType() + token.getAccessToken());
        tokenInfo.add(member.getUserId());
        tokenInfo.add(String.valueOf(token.getAccessTokenExpiresTime()));
        member.setRefreshToken(token.getRefreshToken());
        memberRepository.save(member);

        return tokenInfo;
    }

    public Map<String, Boolean> isAddInfo(Member member) {
        return Map.of(IS_ADD_INFO, member.isAddInfoYn());

    }
}
