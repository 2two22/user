package two.two_user.member.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import two.two_user.domain.Follow;
import two.two_user.domain.Member;
import two.two_user.domain.MemberStatus;
import two.two_user.domain.repository.MemberRepository;
import two.two_user.exception.BudException;
import two.two_user.exception.ErrorCode;
import two.two_user.member.dto.FollowDto;
import two.two_user.member.repository.FollowRepository;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
public class FollowService {
    private final FollowRepository followRepository;

    private final MemberRepository memberRepository;

    @Transactional
    public Long follow(Long memberId, Member member) {
        Member targetMember = memberRepository.findByIdAndStatus(memberId, MemberStatus.VERIFIED)
                .orElseThrow(() -> new BudException(ErrorCode.NOT_REGISTERED_MEMBER));

        if (Objects.equals(member.getId(), targetMember.getId())) {
            throw new BudException(ErrorCode.CANNOT_FOLLOW_YOURSELF);
        }

        followRepository.findByTargetAndMember(targetMember, member)
                .ifPresentOrElse(followRepository::delete,
                        () -> saveFollow(member, targetMember)
                );

        return targetMember.getId();
    }

    private void saveFollow(Member member, Member targetMember) {
        followRepository.save(Follow.builder()
                .target(targetMember)
                .member(member)
                .build());
    }

    public List<FollowDto> readMyFollowings(Member member) {
        return followRepository.findByMemberAndMemberStatus(member, MemberStatus.VERIFIED).stream()
                .map(follow -> FollowDto.of(follow.getTarget(), true))
                .collect(Collectors.toList());
    }

    public List<FollowDto> readMyFollowers(Member member) {
        return followRepository.findByTargetAndMemberStatus(member, MemberStatus.VERIFIED).stream()
                .map(follow -> FollowDto.of(follow.getMember(),
                        followRepository.existsByTargetAndMember(follow.getMember(), member)))
                .collect(Collectors.toList());
    }

    public List<FollowDto> readFollowings(Long userId, Member reader) {
        Member member = memberRepository.findById(userId)
                .orElseThrow(() -> new BudException(ErrorCode.NOT_REGISTERED_MEMBER));

        return followRepository.findByMemberAndMemberStatus(member, MemberStatus.VERIFIED).stream()
                .map(follow -> toFollowDto(reader, follow.getTarget()))
                .collect(Collectors.toList());
    }

    public List<FollowDto> readFollowers(Long userId, Member reader) {
        Member member = memberRepository.findById(userId)
                .orElseThrow(() -> new BudException(ErrorCode.NOT_REGISTERED_MEMBER));

        return followRepository.findByTargetAndMemberStatus(member, MemberStatus.VERIFIED).stream()
                .map(follow -> toFollowDto(reader, follow.getMember()))
                .collect(Collectors.toList());
    }

    private FollowDto toFollowDto(Member reader, Member profileMember) {
        return FollowDto.of(profileMember, Objects.equals(reader.getId(), profileMember.getId()),
                followRepository.existsByTargetAndMember(profileMember, reader));
    }

}
