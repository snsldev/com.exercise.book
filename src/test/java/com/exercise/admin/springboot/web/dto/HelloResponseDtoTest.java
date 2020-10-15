package com.exercise.admin.springboot.web.dto;

import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;/*jUnit 껏도 있지만 추가 라이브러리가 있어야해서 assertj꺼를 쓴다*/
public class HelloResponseDtoTest {

    @Test
    public void 롬북기능테스트(){
        //given
        String name = "이름";
        int amount = 10;

        //when
        HelloResponseDto helloResponseDto = new HelloResponseDto(name, amount);

        //then
        assertThat(helloResponseDto.getName()).isEqualTo(name);
        assertThat(helloResponseDto.getAmount()).isEqualTo(amount);

    }
}
