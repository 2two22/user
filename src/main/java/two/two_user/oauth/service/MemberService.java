package two.two_user.oauth.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.ObjectUtils;
import org.springframework.web.multipart.MultipartFile;
import two.two_user.domain.Member;
import two.two_user.domain.MemberStatus;
import two.two_user.domain.repository.MemberRepository;
import two.two_user.exception.BudException;
import two.two_user.exception.ErrorCode;


import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.UUID;

import static two.two_user.oauth.service.MemberConstants.FILE_EXTENSION_PNG;
import static two.two_user.oauth.service.MemberConstants.PROFILE_BASIC_IMAGE_PREFIX;


@Slf4j
@Service
@RequiredArgsConstructor
public class MemberService implements UserDetailsService {
    private final MemberRepository memberRepository;

//    private final AwsS3Api awsS3Api;

    @Override
    public UserDetails loadUserByUsername(String userId) throws UsernameNotFoundException {
        return memberRepository.findByUserId(userId)
                .orElseThrow(() -> new BudException(ErrorCode.NOT_REGISTERED_MEMBER));
    }

    public boolean modifyInfo(Member member, MultipartFile file, String nickname, String introduceMessage, String job, String imagePath) {
        if(!ObjectUtils.isEmpty(nickname))
            member.setNickname(nickname);
        if(!ObjectUtils.isEmpty(introduceMessage))
            member.setIntroduceMessage(introduceMessage);
        if(!ObjectUtils.isEmpty(job))
            member.setJob(job);
//        if(!ObjectUtils.isEmpty(file) && ObjectUtils.isEmpty(imagePath)) {
//            member.setProfileImg(awsS3Api.uploadImage(file, PROFILES));
//        }
        else if(ObjectUtils.isEmpty(file) && !ObjectUtils.isEmpty(imagePath)) {
            member.setProfileImg(imagePath);
        }

        memberRepository.save(member);
        return true;
    }

    public List<String> getLevelImage(Member member) {
//        long level = member.getLevel().getLevelNumber();
        List<String> levelArray = new ArrayList<>();
//
//        for(int i=1; i<=10; i++) {
//            if(i <= level) {
//                levelArray.add(awsS3Api.getImageUrl("levels/lv" + i + ".png"));
//            } else {
//                levelArray.add(awsS3Api.getImageUrl("levels/lv" + i + "L.png"));
//            }
//        }
        return levelArray;
    }

    public String getProfileRandomImage() {
        Random rd = new Random();
        int randomNumber = rd.nextInt(32) + 1;
        return PROFILE_BASIC_IMAGE_PREFIX + randomNumber + FILE_EXTENSION_PNG;
    }

    @Transactional
    public long withdrawMember(Member member) {
        String uuid;
        String withdrawMemberPrefix = "Deleted User ";

//        githubInfoRepository.deleteByMemberId(member.getId());

        do {
            uuid = UUID.randomUUID().toString().substring(0, 8);
        } while (memberRepository.findByUserCode(uuid).isPresent());

        member.setNickname(withdrawMemberPrefix + uuid);
        member.setUserId(uuid);
        member.setUserCode(uuid);
        member.setStatus(MemberStatus.WITHDREW);

        memberRepository.save(member);

        return member.getId();
    }

}
