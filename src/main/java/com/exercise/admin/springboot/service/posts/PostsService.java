package com.exercise.admin.springboot.service.posts;


import com.exercise.admin.springboot.domain.posts.Posts;
import com.exercise.admin.springboot.domain.posts.PostsRepository;
import com.exercise.admin.springboot.web.dto.PostsListResponseDto;
import com.exercise.admin.springboot.web.dto.PostsResponseDto;
import com.exercise.admin.springboot.web.dto.PostsSaveRequestDto;
import com.exercise.admin.springboot.web.dto.PostsUpdateRequestDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
public class PostsService {

    private final PostsRepository postsRepository;

    @Transactional
    public Long save(PostsSaveRequestDto requestDto) {
        return postsRepository.save(requestDto.toEntity()).getId();
    }

    @Transactional
    public Long update(Long id, PostsUpdateRequestDto requestDto) {
        Posts posts = postsRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("해당 사용자가 없습니다. id=" + id));
//                .orElseThrow(new Supplier<IllegalArgumentException>(){
//                                 @Override
//                                 public IllegalArgumentException get() {
//                                     return new IllegalArgumentException("해당 사용자가 없습니다. id=" + id);;
//                                 }
//
//                }

        posts.update(requestDto.getTitle(), requestDto.getContent());
        //쿼리를 날리는 부분없이 됨 더티체킹, 엔티티 영속성
        return id;
    }

    public PostsResponseDto findById(Long id){
        Posts entity = postsRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("해당 글이 없습니다.  id="+id));

        return new PostsResponseDto(entity);
    }

    @Transactional(readOnly = true)
    public List<PostsListResponseDto> findAllDesc(){
        return postsRepository.findAllDesc().stream().map(PostsListResponseDto::new).collect(Collectors.toList());
//        return postsRepository.findAllDesc().stream().map(posts -> {new PostsListResponseDto(posts)}).collect(Collectors.toList());
    }

    @Transactional
    public void delete (Long id) {
        Posts posts = postsRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("해당 사용자가 없습니다. id=" + id));

        postsRepository.delete(posts);
        //postsRepository.deleteById(id); 로 사용해도된다
    }
}