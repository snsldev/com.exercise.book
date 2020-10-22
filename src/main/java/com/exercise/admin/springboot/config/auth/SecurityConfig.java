package com.exercise.admin.springboot.config.auth;

import com.exercise.admin.springboot.domain.user.Role;
import lombok.RequiredArgsConstructor;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;

@RequiredArgsConstructor
@EnableWebSecurity
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    private final CustomOAuth2UserService customOAuth2UserService;

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http
                .csrf().disable()
                .headers().frameOptions().disable()//h2console때문에 배활성화
                .and()
                .authorizeRequests()
                .antMatchers("/", "/css/**", "/images/**", "/js/**", "/h2-console/**", "/profile").permitAll() //허락함
                .antMatchers("/api/v1/**").hasRole(Role.USER.name())
                .anyRequest().authenticated()//그외의 요청에 대하여 인증된 사용자만
                .and()
                .logout()
                .logoutSuccessUrl("/")
                .and()
                .oauth2Login()
                .userInfoEndpoint()//oauth2.0 로그인 성공이후 사용자정보 가져올떄 설정
                .userService(customOAuth2UserService); //후속조치를 진행할 인터페이스 구현체체    }
    }
}