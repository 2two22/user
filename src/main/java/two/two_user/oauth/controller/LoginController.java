package two.two_user.oauth.controller;

import java.util.Map;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.util.ObjectUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import two.two_user.domain.Member;
import two.two_user.oauth.service.LoginService;
//import org.springframework.web.bind.annotation.*;
//import two.two_user.oauth.service.LoginService;


import java.util.List;

@RestController
@Slf4j
@RequiredArgsConstructor
public class LoginController {
    private final LoginService loginService;

    @GetMapping("/token")
    public ResponseEntity<?> requestCode(@RequestParam(value = "code") String code) {
        List<String> tokenInfo = loginService.codeToJwt(code);
        if(ObjectUtils.isEmpty(tokenInfo.get(0))) return ResponseEntity.ok(null);

        return ResponseEntity.ok()
                .header(HttpHeaders.AUTHORIZATION, tokenInfo.get(0))
                .header("JWT_USER_INFORMATION", tokenInfo.get(1))
                .header("JWT_EXPIRE_TIME", tokenInfo.get(2))
                .build();
    }

    @GetMapping("/refresh")
    public ResponseEntity<?> refreshToken(@AuthenticationPrincipal Member member) {
        List<String> tokenInfo = loginService.tokenRefresh(member);

        return ResponseEntity.ok()
                .header(HttpHeaders.AUTHORIZATION, tokenInfo.get(0))
                .header("JWT_USER_INFORMATION", tokenInfo.get(1))
                .header("JWT_EXPIRE_TIME", tokenInfo.get(2))
                .build();
    }

    @GetMapping("/check")
    public ResponseEntity<Map<String, Boolean>> isAddInfo(@AuthenticationPrincipal Member member) {

        return ResponseEntity.ok().body(loginService.isAddInfo(member));
    }

}
