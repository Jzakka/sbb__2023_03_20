package com.mysite.sbb.user;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import java.security.Principal;
import java.time.LocalDateTime;
import java.util.Collection;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class OAuthGoogleUserService extends DefaultOAuth2UserService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        OAuth2User oAuth2User = super.loadUser(userRequest);

        String username = oAuth2User.getAttribute("name");

        String password = passwordEncoder.encode(UUID.randomUUID().toString().substring(0, 6));

        String email = oAuth2User.getAttribute("email");

        Optional<SiteUser> userOptional = userRepository.findByName(username);
        if (userOptional.isEmpty()) {
            SiteUser googleUser = new SiteUser();
            googleUser.setName(username);
            googleUser.setPassword(password);
            googleUser.setEmail(email);
            googleUser.setCreatedAt(LocalDateTime.now());
            googleUser.setLastModifiedAt(LocalDateTime.now());

            userRepository.save(googleUser);
        }

        return new OAuth2User() {
            private String name = username;

            @Override
            public Map<String, Object> getAttributes() {
                return oAuth2User.getAttributes();
            }

            @Override
            public Collection<? extends GrantedAuthority> getAuthorities() {
                return null;
            }

            @Override
            public String getName() {
                return name;
            }

            public String getUsername() {
                System.out.println("OAuthGoogleUserService.getUsername");
                return getName();
            }
        };
    }
}
