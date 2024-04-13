package com.example.demo.service;

import com.example.demo.model.dto.PostCreateDto;
import com.example.demo.model.dto.PostUpdateDto;
import com.example.demo.repository.PostEntity;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.jdbc.SqlGroup;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@SpringBootTest
@SqlGroup({
        @Sql(value = "/sql/post-test-data.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD),
        @Sql(value = "/sql/delete-all-data.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
})
class PostServiceTest {
    @Autowired
    PostService postService;
    @Test
    void getById는_존재하는_게시물을_조회한다() {
        // given
        // when
        PostEntity postEntity = postService.getById(1);
        // then
        assertThat(postEntity.getContent()).isEqualTo("내용 입니다.");
        assertThat(postEntity.getWriter().getEmail()).isEqualTo("test1@naver.com");
    }

    @Test
    void postCreateDto를_이용해_게시글을_등록할_수_있다() {
        // given
        PostCreateDto postCreateDto = PostCreateDto.builder()
                .writerId(1)
                .content("second")
                .build();
        // when
        PostEntity postEntity = postService.create(postCreateDto);

        // then
        assertThat(postEntity.getWriter().getId()).isEqualTo(1);
        assertThat(postEntity.getContent()).isEqualTo("second");
    }

    @Test
    void postCreateDto를_이용해_게시글을_수정할_수_있다() {
        // given
        PostUpdateDto postUpdateDto = PostUpdateDto.builder()
                .content("third")
                .build();
        // when
        postService.update(1, postUpdateDto);

        // then
        PostEntity postEntity = postService.getById(1);
        assertThat(postEntity.getContent()).isEqualTo("third");
    }
}