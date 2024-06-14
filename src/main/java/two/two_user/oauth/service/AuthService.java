package two.two_user.oauth.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;
import org.springframework.web.multipart.MultipartFile;
import two.two_user.client.S3Client;
import two.two_user.domain.Domain;
import two.two_user.domain.Member;
import two.two_user.domain.repository.MemberRepository;
import two.two_user.exception.BudException;
import two.two_user.exception.ErrorCode;
import two.two_user.jwt.TokenProvider;
import two.two_user.jwt.dto.JwtDto;
import two.two_user.jwt.dto.RefreshDto;


@Slf4j
@Service
@RequiredArgsConstructor
public class AuthService {
    private final TokenProvider tokenProvider;
    private final MemberRepository memberRepository;
    private final S3Client s3Client;

    public JwtDto login(OAuth2User oAuth2User) {
        return tokenProvider.generateToken(oAuth2User);
    }

    public JwtDto refresh(RefreshDto refreshDto) {
        boolean result = tokenProvider.validateRawToken(refreshDto.getRefreshToken());
        if (!result) {
            log.info("유효하지 않은 토큰입니다.");
            return null;
        }
        Authentication authentication = tokenProvider.getAuthentication(refreshDto.getRefreshToken());

        return tokenProvider.generateToken(authentication.getName());
    }

    public boolean addAdditionalInfo(Member member, MultipartFile file, String nickname, String job, String imagePath) {
        if(memberRepository.findByNickname(nickname).isPresent()) {
            throw new BudException(ErrorCode.INTERNAL_ERROR);
        }
        if(!ObjectUtils.isEmpty(file) && ObjectUtils.isEmpty(imagePath)) {
            member.setProfileImg(s3Client.upload(file, Domain.PROFILE));
        }
        else if(ObjectUtils.isEmpty(file) && !ObjectUtils.isEmpty(imagePath)) {
            member.setProfileImg(imagePath);
        }
        
        member.setNickname(nickname);
        member.setJob(job);
        member.setIntroduceMessage("안녕하세요. " + nickname + "입니다.");
        member.setAddInfoYn(true);
        memberRepository.save(member);
        return true;
    }

    public boolean checkNickname(String nickname) {
        return memberRepository.findByNickname(nickname).isEmpty();
    }
}
