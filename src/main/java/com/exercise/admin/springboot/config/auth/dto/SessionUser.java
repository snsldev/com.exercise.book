package com.exercise.admin.springboot.config.auth.dto;

import com.exercise.admin.springboot.domain.user.User;
import lombok.Getter;

import java.io.Serializable;

@Getter
public class SessionUser implements Serializable {
    //엔티티를 직렬화하면 부수적인 필드까지 된다
    //따로 직렬화 dto를 만든다
    private String name;
    private String email;
    private String picture;

    public SessionUser(User user) {
        this.name = user.getName();
        this.email = user.getEmail();
        this.picture = user.getPicture();
    }
}
