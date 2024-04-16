package com.example.demo.controller;

import com.example.demo.model.dto.PostCreateDto;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.jdbc.SqlGroup;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

@SpringBootTest
@AutoConfigureMockMvc
@AutoConfigureTestDatabase
@SqlGroup({
        @Sql(value = "/sql/delete-all-data.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD),
        @Sql(value = "/sql/user-test-data.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
})
class PostCreateControllerTest {
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;

    @Test
    public void 사용자는_게시글을_등록할_수_있다 () throws Exception {
        // given
        PostCreateDto postCreateDto = PostCreateDto.builder()
                .content("postCreateTestContent")
                .writerId(1)
                .build();
        // when
        // then
        mockMvc.perform(post("/api/posts")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(postCreateDto)))
                .andExpect(jsonPath("$.writer.id").value(1))
                .andExpect(jsonPath("$.content").value("postCreateTestContent"));
    }
}