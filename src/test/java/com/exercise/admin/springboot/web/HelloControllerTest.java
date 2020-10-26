package com.exercise.admin.springboot.web;


import com.exercise.admin.springboot.config.auth.SecurityConfig;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@RunWith(SpringRunner.class)
@WebMvcTest(controllers = HelloController.class,
        excludeFilters = {
                @ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, classes = SecurityConfig.class)
        })
//webmvctest는 @Controller만 읽고 스프링시큐리티관련 컴포넌트 안읽는다
//SecurityConfig관련된 클래스 못읽어서 스캔대상에서 거른다

public class HelloControllerTest {
    @Autowired
    private MockMvc mvc;

    @Test
    @WithMockUser
    public void hello가_리턴된다() throws Exception {
        String hello = "hello";

//        mvc.perform(get("/hello"))
//                .andExpect(status().isOk())
//                .andExpect(content().string(hello));

        mvc.perform(get("/hello")).andExpect(status().isOk()).andExpect(content().string(hello));
    }

    @Test
    @WithMockUser
    public void helloDto가_리턴된다() throws Exception {
        String name = "hello";
        int amount = 1000;

        mvc.perform(
                get("/hello/dto")
                        .param("name", name)
                        .param("amount", String.valueOf(amount)))//테스트할땐 문자로 변경해야한다
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name", is(name))) //제이슨할땐 $기준으로 필드
                .andExpect(jsonPath("$.amount").value(amount));
    }
}
