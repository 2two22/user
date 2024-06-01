package two.two_user.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import two.two_user.client.dto.request.GithubInfoRegisterRequest;

@FeignClient(value = "githubClient", url = "${feign.github}")
public interface GithubClient {
    @PostMapping(value = "/api/saveToken")
    ResponseEntity<Void> registerUserToken(@RequestHeader(value = HttpHeaders.AUTHORIZATION) String token, @RequestBody GithubInfoRegisterRequest request);
}
