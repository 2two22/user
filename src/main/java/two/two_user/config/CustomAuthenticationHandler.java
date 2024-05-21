package two.two_user.config;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;
import two.two_user.domain.Member;
import two.two_user.domain.repository.MemberRepository;
import two.two_user.jwt.TokenProvider;
import two.two_user.jwt.dto.JwtDto;

import java.io.IOException;
import java.util.Optional;

@Slf4j
@Component
@RequiredArgsConstructor
public class CustomAuthenticationHandler implements AuthenticationSuccessHandler {
    private final TokenProvider tokenProvider;
    private final MemberRepository memberRepository;
    @Override
    public void onAuthenticationSuccess(HttpServletRequest request,
                                        HttpServletResponse response,
                                        Authentication authentication) throws IOException, ServletException {
        Optional<Member> optionalMember = memberRepository.findByUserCode(authentication.getName());
        if(optionalMember.isPresent()) {
            Member member = optionalMember.get();
            JwtDto token = tokenProvider.generateToken(member.getUserId());

            System.out.println(token.getAccessToken());

            Cookie jwtTokenCookie = new Cookie("Bearer", token.getAccessToken());
            jwtTokenCookie.setMaxAge(1000 * 60 * 60);
            jwtTokenCookie.setPath("/");
            jwtTokenCookie.setSecure(true);
            jwtTokenCookie.setHttpOnly(true);
            response.addCookie(jwtTokenCookie);
            if(member.isAddInfoYn()) {
                response.sendRedirect("http://127.0.0.1:5173/");
            }
            else {
                response.sendRedirect("http://127.0.0.1:5173/signUp");
            }
        }
        else {
            log.info("유효하지 않은 아이디입니다.");
            response.sendRedirect("http://127.0.0.1:5173/");
        }
    }
}
