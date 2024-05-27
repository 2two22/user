package two.two_user.member.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import two.two_user.domain.Member;
import two.two_user.member.dto.FollowDto;
import two.two_user.member.service.FollowService;

import java.net.URI;
import java.util.List;

@RequiredArgsConstructor
@RestController
@RequestMapping("/users")
public class FollowController {
    private final FollowService followService;

    @PostMapping("/{userId}/follows")
    private ResponseEntity<URI> follow(@PathVariable Long userId,
                                       @AuthenticationPrincipal Member member) {
        followService.follow(userId, member);
        return ResponseEntity.created(URI.create("/users/" + userId)).build();
    }

    @GetMapping("/follows")
    private ResponseEntity<List<FollowDto>> readMyFollowings(@AuthenticationPrincipal Member member) {
        return ResponseEntity.ok(followService.readMyFollowings(member));
    }

    @GetMapping("/{userId}/follows")
    private ResponseEntity<List<FollowDto>> readFollowings(@PathVariable Long userId,
                                                           @AuthenticationPrincipal Member member) {
        return ResponseEntity.ok(followService.readFollowings(userId, member));
    }

    @GetMapping("/followers")
    private ResponseEntity<List<FollowDto>> readMyFollowers(@AuthenticationPrincipal Member member) {
        return ResponseEntity.ok(followService.readMyFollowers(member));
    }

    @GetMapping("/{userId}/followers")
    private ResponseEntity<List<FollowDto>> readFollowers(@PathVariable Long userId,
                                                          @AuthenticationPrincipal Member member) {
        return ResponseEntity.ok(followService.readFollowers(userId, member));
    }
}
