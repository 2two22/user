package two.two_user.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.HttpHeaders;
import org.springframework.web.bind.annotation.*;

@FeignClient(name = "communityClient", url = "${feign.community}")
public interface CommunityClient {
    @GetMapping(value = "/posts/{userId}/count")
    Long getUsersPostCount(@PathVariable Long userId);
}
