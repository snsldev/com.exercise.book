package com.exercise.admin.springboot.config.auth;

import com.exercise.admin.springboot.config.auth.dto.OAuthAttributes;
import com.exercise.admin.springboot.config.auth.dto.SessionUser;
import com.exercise.admin.springboot.domain.user.User;
import com.exercise.admin.springboot.domain.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserService;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpSession;
import java.util.Collections;

/*구글에서 가져온 사용자 정보를 기반으로 가입, 정보수정, 세션 저장등의 기능을 구현*/
@RequiredArgsConstructor
@Service
public class CustomOAuth2UserService implements OAuth2UserService<OAuth2UserRequest, OAuth2User> {
    private final UserRepository userRepository;
    private final HttpSession httpSession;

    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        OAuth2UserService delegate = new DefaultOAuth2UserService();
        OAuth2User oAuth2User = delegate.loadUser(userRequest);

        String registrationId = userRequest.getClientRegistration().getRegistrationId();//로그인을 진행하는 서비스 구분을 위한 아이디(구글인지 네이버인지)
        String userNameAttributeName = userRequest.getClientRegistration().getProviderDetails()
                .getUserInfoEndpoint().getUserNameAttributeName();//구글 로그인시 키가되는 필드값?, 네이버 카카오등 기본지원 안됨

        OAuthAttributes attributes = OAuthAttributes.of(registrationId, userNameAttributeName, oAuth2User.getAttributes());//user의 attribute를 담을 클래스

        User user = saveOrUpdate(attributes);
        System.out.println("after saveOrUpdate: "+user.getName());
        httpSession.setAttribute("user", new SessionUser(user)); //세션에 사용자 정보를 저장하기위한 클래스

        return new DefaultOAuth2User(
                Collections.singleton(new SimpleGrantedAuthority(user.getRoleKey())),
                attributes.getAttributes(),
                attributes.getNameAttributeKey());
    }


    private User saveOrUpdate(OAuthAttributes attributes) {
        User user = userRepository.findByEmail(attributes.getEmail())
                .map(entity -> entity.update(attributes.getName(), attributes.getPicture()))
                .orElse(attributes.toEntity()); //이메일로 찾아서 없으면 엔티티를 생성한다
        System.out.println("user id:"+user.getId());
        System.out.println("user name:"+user.getName());

        return userRepository.save(user);
    }
}
