package two.two_user.domain;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Entity(name = "MEMBER")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class Member extends BaseEntity implements UserDetails {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(unique = true)
    private String userId;

    @Column(unique = true)
    private String userCode;

    @Column(unique = true)
    private String nickname;

    private String profileImg;

    private String job;


    @Enumerated(EnumType.STRING)
    private MemberStatus status;
    private String introduceMessage;

    private String refreshToken;

    private boolean addInfoYn;

    public Member update(String userCode, String oauthToken) {
        this.userCode = userCode;
        return this;
    }

    public static Member register(String userId, String userCode, String profileImg) {
        return Member.builder()
                .userId(userId)
                .userCode(userCode)
                .nickname(UUID.randomUUID().toString())
                .profileImg(profileImg)
                .addInfoYn(false)
                .status(MemberStatus.VERIFIED)
                .build();
    }

    public void updateProfileImage(String imagePath) {
        System.out.println(imagePath);
        System.out.println(this.profileImg);
        this.profileImg = imagePath;
    }


    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        List<String> roles = new ArrayList<>();
        roles.add(this.getStatus().getKey());
        return roles.stream()
                .map(SimpleGrantedAuthority::new)
                .collect(Collectors.toList());
    }

    @Override
    public String getPassword() {
        return null;
    }

    @Override
    public String getUsername() {
        return userId;
    }

    @Override
    public boolean isAccountNonExpired() {
        return false;
    }

    @Override
    public boolean isAccountNonLocked() {
        return false;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return false;
    }

    @Override
    public boolean isEnabled() {
        return false;
    }
}
