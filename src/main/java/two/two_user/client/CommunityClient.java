package two.two_user.client;

import jakarta.validation.Valid;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.HttpHeaders;
import org.springframework.web.bind.annotation.*;
import two.two_user.client.dto.request.ProfileRequest;

@FeignClient(name = "communityClient", url = "${feign.community}")
public interface CommunityClient {
    @GetMapping(value = "/posts/{userId}/count")
    Long getUsersPostCount(@PathVariable Long userId);
    @PutMapping(value = "/profile")
    void updateWriterProfile(@RequestBody @Valid ProfileRequest form, @RequestHeader(value = HttpHeaders.AUTHORIZATION) String token);
}
