package two.two_user.member.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import two.two_user.domain.Member;
import two.two_user.member.dto.UserDto;
import two.two_user.member.service.MemberService;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/member")
@RequiredArgsConstructor
public class MemberController {
    private final MemberService memberService;

    @PostMapping("/modifyInfo")
    public ResponseEntity<Boolean> modifyMemberInfo(@AuthenticationPrincipal Member member,
                                                    @RequestPart(required = false) MultipartFile file,
                                                    @RequestPart(required = false) String nickname,
                                                    @RequestPart(required = false) String introduceMessage,
                                                    @RequestPart(required = false) String imagePath,
                                                    @RequestPart String job,
                                                    @RequestHeader(value = HttpHeaders.AUTHORIZATION) String token) {
        return ResponseEntity.ok(memberService.modifyInfo(member, file, nickname, introduceMessage, job, imagePath, token));
    }

    @GetMapping("/getLevelImage")
    public ResponseEntity<List<String>> getLevelImage(@AuthenticationPrincipal Member member) {
        return ResponseEntity.ok(memberService.getLevelImage(member));
    }

    @GetMapping("/random-image")
    public ResponseEntity<String> getProfileRandomImage() {
        return ResponseEntity.ok(memberService.getProfileRandomImage());
    }

    @PostMapping("/withdraw")
    public ResponseEntity<?> withdrawMember(@AuthenticationPrincipal Member member) {
        return ResponseEntity.ok(memberService.withdrawMember(member));
    }

    @GetMapping("/{userId}")
    private ResponseEntity<UserDto> readProfile(@PathVariable Long userId,
                                                @AuthenticationPrincipal Member member) {
        return ResponseEntity.ok(memberService.readProfile(userId, member));
    }

    @GetMapping
    private ResponseEntity<UserDto> readMyProfile(@AuthenticationPrincipal Member member,
                                                  @RequestHeader(value = HttpHeaders.AUTHORIZATION) String token) {
        return ResponseEntity.ok(memberService.readMyProfile(member, token));
    }
}
