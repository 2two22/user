package two.two_user.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.HttpHeaders;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import two.two_user.client.dto.request.GithubInfoRegisterRequest;

@FeignClient(name = "githubClient", value = "githubClient")
public interface GithubClient {
    @PostMapping(value = "/github")
    void registerUserToken(@RequestHeader(value = HttpHeaders.AUTHORIZATION) String token, @RequestBody GithubInfoRegisterRequest request);
}