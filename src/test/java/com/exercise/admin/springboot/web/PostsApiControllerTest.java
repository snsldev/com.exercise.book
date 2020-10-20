package com.exercise.admin.springboot.web;

import com.exercise.admin.springboot.domain.posts.Posts;
import com.exercise.admin.springboot.domain.posts.PostsRepository;
import com.exercise.admin.springboot.web.dto.PostsSaveRequestDto;
import com.exercise.admin.springboot.web.dto.PostsUpdateRequestDto;
import org.junit.After;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.ImportAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class PostsApiControllerTest {

    @LocalServerPort
    private int port;

//    private MockMvc mvc;
// JPA기능이 동작안한다함 mock mvc는

    @Autowired
    private TestRestTemplate restTemplate;

    @Autowired
    private PostsRepository postsRepository;

    @After
    public void tearDown(){
        postsRepository.deleteAll();
    }

    @Test
    public void Posts_등록된다() throws Exception{
        //given
        String title = "title";
        String content = "content";
        PostsSaveRequestDto requestDto = PostsSaveRequestDto.builder().title(title).content(content).author("author").build();

        String url = "http://localhost:" + port + "/api/v1/posts";

        //when <T>느 bodyType
        ResponseEntity<Long> responseEntity = restTemplate.postForEntity(url, requestDto, Long.class);

        //then
        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(responseEntity.getBody()).isGreaterThan(0L); // 기본 인덱스키보다 커야된다

        List<Posts> all = postsRepository.findAll(); //post 전체 리스트가져오기
        assertThat(all.get(0).getTitle()).isEqualTo(title);
        assertThat(all.get(0).getContent()).isEqualTo(content);

    }

    @Test
    public void Posts_수정된다() throws Exception{
        //given
        Posts savePosts = postsRepository.save(Posts.builder().title("title").content("content").author("author").build());

        Long updatedId =savePosts.getId();
        String expected_title = "바뀔타이틀";
        String expected_content = "바뀔내용";

        PostsUpdateRequestDto requestDto = PostsUpdateRequestDto.builder().title(expected_title).content(expected_content).build();

        String url = "http://localhost:" + port + "/api/v1/posts/"+updatedId;

        HttpEntity<PostsUpdateRequestDto> requestDtoHttpEntity = new HttpEntity<PostsUpdateRequestDto>(requestDto);

        //when
        //put이라(수정) exchange 메소드를 쓰는건가
        ResponseEntity<Long> responseEntity = restTemplate.exchange(url, HttpMethod.PUT, requestDtoHttpEntity, Long.class);

        //then
        List<Posts> all= postsRepository.findAll();

        assertThat(all.get(0).getTitle()).isEqualTo(expected_title);
        assertThat(all.get(0).getContent()).isEqualTo(expected_content);

    }
}
