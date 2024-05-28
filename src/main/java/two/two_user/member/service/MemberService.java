package two.two_user.member.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.ObjectUtils;
import org.springframework.web.multipart.MultipartFile;
import two.two_user.client.CommunityClient;
import two.two_user.client.S3Client;
import two.two_user.domain.Domain;
import two.two_user.domain.Member;
import two.two_user.domain.MemberStatus;
import two.two_user.domain.repository.MemberRepository;
import two.two_user.exception.BudException;
import two.two_user.exception.ErrorCode;
import two.two_user.member.dto.FollowDto;
import two.two_user.member.dto.UserDto;
import two.two_user.member.repository.FollowRepository;


import java.util.*;
import java.util.stream.Collectors;

import static two.two_user.oauth.service.MemberConstants.FILE_EXTENSION_PNG;
import static two.two_user.oauth.service.MemberConstants.PROFILE_BASIC_IMAGE_PREFIX;


@Slf4j
@Service
@RequiredArgsConstructor
public class MemberService implements UserDetailsService {
    private final MemberRepository memberRepository;
    private final FollowRepository followRepository;
    private final S3Client s3Client;
    private final CommunityClient communityClient;

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
        if(!ObjectUtils.isEmpty(file) && ObjectUtils.isEmpty(imagePath)) {
            member.setProfileImg(s3Client.upload(file, Domain.PROFILE));
        }
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

    public UserDto readMyProfile(Member member) {
        Long numberOfFollowers = followRepository.countByTarget(member);
        Long numberOfFollows = followRepository.countByMember(member);
        Long numberOfPosts = communityClient.getUsersPostCount(member.getId());

        return UserDto.of(member, numberOfFollowers, numberOfFollows, numberOfPosts);
    }

    public UserDto readProfile(Long userId, Member member) {
        Member targetMember = memberRepository.findById(userId)
                .orElseThrow(() -> new BudException(ErrorCode.NOT_REGISTERED_MEMBER));

        Long numberOfFollowers = followRepository.countByTarget(targetMember);
        Long numberOfFollows = followRepository.countByMember(targetMember);
        Long numberOfPosts = communityClient.getUsersPostCount(userId);
        boolean isFollowing = followRepository.existsByTargetAndMember(targetMember, member);

        return UserDto.of(targetMember, Objects.equals(member.getId(), targetMember.getId()),
                isFollowing, numberOfFollowers, numberOfFollows, numberOfPosts , targetMember.getStatus());
    }

}
