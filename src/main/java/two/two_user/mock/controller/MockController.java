package two.two_user.mock.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import two.two_user.client.NewsClient;

@RequiredArgsConstructor
@RestController
@Slf4j
public class MockController {
    private final NewsClient newsClient;
    @GetMapping("/test")
    public ResponseEntity<Void> mock(){
        return ResponseEntity.ok().build();
    }
}
