package two.two_user.config;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserService;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import two.two_user.domain.Member;
import two.two_user.domain.repository.MemberRepository;
import two.two_user.oauth.dto.OAuthAttribute;

import java.util.Collections;
import java.util.Optional;
import java.util.Random;

@Slf4j
@Component
@RequiredArgsConstructor
@Service
public class CustomOAuth2UserService implements OAuth2UserService<OAuth2UserRequest, OAuth2User> {
    private final MemberRepository memberRepository;

    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        OAuth2UserService<OAuth2UserRequest, OAuth2User> delegate = new DefaultOAuth2UserService();
        OAuth2User oAuth2User = delegate.loadUser(userRequest);

        String registrationId = userRequest.getClientRegistration().getRegistrationId();
        String oAuthAccessToken = userRequest.getAccessToken().getTokenValue();

        String userCode = oAuth2User.getName();
        String userNameAttributeName = userRequest
                .getClientRegistration().getProviderDetails()
                .getUserInfoEndpoint().getUserNameAttributeName();
        OAuthAttribute attributes = OAuthAttribute.of(userNameAttributeName, oAuth2User.getAttributes(), oAuthAccessToken, userCode);
        Member member = saveOrUpdate(attributes);

        return new DefaultOAuth2User(Collections.singleton(new SimpleGrantedAuthority(member.getStatus().getKey())),
                attributes.getAttributes(),
                attributes.getNameAttributeKey());
    }

    private Member saveOrUpdate(OAuthAttribute attributes) {
        Optional<Member> optionalMember = memberRepository.findByUserId(attributes.getUserId());
        Member member;
        if (optionalMember.isEmpty()) {
            Random random = new Random();
            int randNum = random.nextInt(30) + 1;
            String imageUrl = null;

            member = attributes.toEntity(imageUrl);
        } else {
            member = optionalMember.get();
            member.update(attributes.getUserCode());

        }
        memberRepository.save(member);
        return member;
    }

}
