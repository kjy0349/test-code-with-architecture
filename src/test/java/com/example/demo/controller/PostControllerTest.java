package com.example.demo.controller;

import com.example.demo.model.dto.PostUpdateDto;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.persistence.PostUpdate;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.jdbc.SqlGroup;
import org.springframework.test.web.servlet.MockMvc;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@AutoConfigureTestDatabase
@SqlGroup({
        @Sql(value = "/sql/delete-all-data.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD),
        @Sql(value = "/sql/post-test-data.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
})
class PostControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;


    @Test
    void ID로_게시글을_불러올_수_있다() throws Exception{
        // given
        // when
        // then
        mockMvc.perform(get("/api/posts/1"))
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.content").value("내용 입니다."));
    }

    @Test
    void 사용자는_존재하지_않는_게시글을_조회할_경우_에러가_난다() throws Exception{
        // given
        // when
        // then
        mockMvc.perform(get("/api/posts/112312312"))
                .andExpect(status().isNotFound())
                .andExpect(content().string("Posts에서 ID 112312312를 찾을 수 없습니다."));
    }

    @Test
    void 사용자는_게시글을_수정할_수_있다() throws Exception{
        // given
        PostUpdateDto postUpdateDto = PostUpdateDto.builder()
                .content("modifiedContent")
                .build();

        // when
        // then
        mockMvc.perform(put("/api/posts/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(postUpdateDto)))
                .andExpect(jsonPath("$.content").value("modifiedContent"))
                .andExpect(jsonPath("$.id").value(1));
    }
}