package two.two_user.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(value = "newsClient", url = "${feign.news}")
public interface NewsClient {
    @PostMapping(value = "/news/bookmark/{userId}")
    int saveBookmark(@RequestHeader(value = HttpHeaders.AUTHORIZATION) String token, @RequestParam("newsId") Long newsId);
}
