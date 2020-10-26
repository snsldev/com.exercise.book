package com.exercise.admin.springboot.web;

import com.exercise.admin.springboot.domain.posts.Posts;
import com.exercise.admin.springboot.domain.posts.PostsRepository;
import com.exercise.admin.springboot.web.dto.PostsSaveRequestDto;
import com.exercise.admin.springboot.web.dto.PostsUpdateRequestDto;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.ImportAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.http.*;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.util.List;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class PostsApiControllerTest {

    @LocalServerPort
    private int port;

    private MockMvc mvc;// JPA기능이 동작안한다함 mock mvc는
    @Autowired
    private WebApplicationContext context;//mock mvc와 함께 추가됨


    @Autowired
    private TestRestTemplate restTemplate;
    @Autowired
    private PostsRepository postsRepository;

    @After
    public void tearDown(){
        postsRepository.deleteAll();
    }

    @Before//시작되기전에 mock mvc 인스턴스 생성 mock mvc와 함께 추가됨
    public void setup() {
        mvc = MockMvcBuilders
                .webAppContextSetup(context)
                .apply(springSecurity())
                .build();
    }


    @Test
    @WithMockUser(roles = "USER") //spring 시큐리티 테스트 넣어야 쓸수있음. MockMVC에서 만동작된다 springboot test는 동작안함
    public void Posts_등록된다() throws Exception{
        //given
        String title = "title";
        String content = "content";
        PostsSaveRequestDto requestDto = PostsSaveRequestDto.builder().title(title).content(content).author("author").build();

        String url = "http://localhost:" + port + "/api/v1/posts";

        //when
        mvc.perform(post(url)
                .contentType(MediaType.APPLICATION_JSON_UTF8)
                .content(new ObjectMapper().writeValueAsString(requestDto)))
                .andExpect(status().isOk());
//        ResponseEntity<Long> responseEntity = restTemplate.postForEntity(url, requestDto, Long.class);

        //then
//        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.OK);
//        assertThat(responseEntity.getBody()).isGreaterThan(0L); // 기본 인덱스키보다 커야된다

        List<Posts> all = postsRepository.findAll(); //post 전체 리스트가져오기
        assertThat(all.get(0).getTitle()).isEqualTo(title);
        assertThat(all.get(0).getContent()).isEqualTo(content);

    }

    @Test
    @WithMockUser(roles = "USER")
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
//        ResponseEntity<Long> responseEntity = restTemplate.exchange(url, HttpMethod.PUT, requestDtoHttpEntity, Long.class);
        mvc.perform(put(url)
                .contentType(MediaType.APPLICATION_JSON_UTF8)
                .content(new ObjectMapper().writeValueAsString(requestDto)))
                .andExpect(status().isOk());
        //then
        List<Posts> all= postsRepository.findAll();

        assertThat(all.get(0).getTitle()).isEqualTo(expected_title);
        assertThat(all.get(0).getContent()).isEqualTo(expected_content);

    }

    @Test
    @WithMockUser(roles = "USER")
    public void Posts_삭제() throws Exception{

        //given
        String post_url = "http://localhost:" + port + "/api/v1/posts/";
//        ResponseEntity<Long> responseEntity = restTemplate.postForEntity(post_url, PostsSaveRequestDto.builder().title("타이틀").content("컨텐츠").author("작성자").build(), Long.class);

        PostsSaveRequestDto requestDto = PostsSaveRequestDto.builder().title("타이틀").content("컨텐츠").author("작성자").build();
        MvcResult result =mvc.perform(post(post_url)
                .contentType(MediaType.APPLICATION_JSON_UTF8)
                .content(new ObjectMapper().writeValueAsString(requestDto)))
                .andExpect(status().isOk()).andReturn();

        long id = Long.parseLong(result.getResponse().getContentAsString());
        System.out.println("id: "+id);
//        long id = (long)responseEntity.getBody();

        //when
//        restTemplate.delete("/api/v1/posts/"+id);
        String delete_url = "http://localhost:" + port + "/api/v1/posts/"+id;
        mvc.perform(delete(delete_url)).andExpect(status().isOk()).andExpect(content().string(Long.toString(id)));

        //then
        assertThat(postsRepository.findById(id)).isEmpty();
    }
}
