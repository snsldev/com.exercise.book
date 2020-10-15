package com.exercise.admin.springboot.web.dto;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor/*final 필드를 포함한  생성자 자동으로 만들어줌*/
public class HelloResponseDto {

    private final String name;
    private final int amount;
}
