package com.example.demo.service;

import com.example.demo.exception.CertificationCodeNotMatchedException;
import com.example.demo.exception.ResourceNotFoundException;
import com.example.demo.model.UserStatus;
import com.example.demo.model.dto.UserCreateDto;
import com.example.demo.model.dto.UserUpdateDto;
import com.example.demo.repository.UserEntity;
import org.junit.jupiter.api.Test;
import org.mockito.BDDMockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.jdbc.SqlGroup;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;

@SpringBootTest
@SqlGroup({
        @Sql(value = "/sql/testsql.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD),
        @Sql(value = "/sql/delete-all-data.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
})

class UserServiceTest {
    @Autowired
    private UserService userService;
    @MockBean
    private JavaMailSender javaMailSender;

    @Test
    void getByEmail은_ACTIVE_상태인_유저를_찾아올_수_있다() {
        // given
        String email = "test1@naver.com";

        // when
        UserEntity result = userService.getByEmail(email);

        // then
        assertThat(result.getNickname()).isEqualTo("test1");
    }

    @Test
    void getByEmail은_PENDING_상태인_유저를_찾아올_수_없다() {
        // given
        String email = "test2@naver.com";

        // when
        // then
        assertThatThrownBy(() -> {
            UserEntity result = userService.getByEmail(email);
        }).isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    void getById는_ACTIVE_상태인_유저를_찾아올_수_있다() {
        // given
        // when
        UserEntity result = userService.getById(1);

        // then
        assertThat(result.getNickname()).isEqualTo("test1");
    }

    @Test
    void getById는_PENDING_상태인_유저를_찾아올_수_없다() {
        // given
        // when
        // then
        assertThatThrownBy(() -> {
            UserEntity result = userService.getById(2);
        }).isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    void userCreateDto를_이용하여_유저를_생성할_수_있다() {
        // given
        UserCreateDto userCreateDto = UserCreateDto.builder()
                .email("test3@naver.com")
                .address("Seoul")
                .nickname("test-k")
                .build();
        BDDMockito.doNothing().when(javaMailSender).send(any(SimpleMailMessage.class));
        // when
        UserEntity result = userService.create(userCreateDto);
        // then
        assertThat(result.getId()).isNotNull();
        assertThat(result.getStatus()).isEqualTo(UserStatus.PENDING);
//        assertThat(result.getCertificationCode()).isEqualTo("???"); FIXME
    }

    @Test
    void userUpdateDto를_이용하여_유저를_수정할_수_있다() {
        // given
        UserUpdateDto userCreateDto = UserUpdateDto.builder()
                .address("Incheon")
                .nickname("testIncheon")
                .build();
        // when
        userService.update(1, userCreateDto);

        // then
        UserEntity result = userService.getById(1);
        assertThat(result.getAddress()).isEqualTo("Incheon");
        assertThat(result.getNickname()).isEqualTo("testIncheon");
    }

    @Test
    void user를_로그인_시키면_마지막_로그인_시간이_변경된다() {
        // given
        // when
        userService.login(1);

        // then
        UserEntity result = userService.getById(1);
        assertThat(result.getLastLoginAt()).isGreaterThan(0);
        // assertThat(result.getLastLoginAt()).isGreaterThan(???); FIXME
    }

    @Test
    void PENDING_상태의_사용자는_인증_코드로_ACTIVE_시킬_수_있다() {
        // given
        // when
        userService.verifyEmail(2, "aaaa-aaaaa-aaaa");

        // then
        UserEntity result = userService.getById(2);
        assertThat(result.getStatus()).isEqualTo(UserStatus.ACTIVE);
    }

    @Test
    void PENDING_상태의_사용자는_잘못된_인증_코드를_받으면_에러를_던진다() {
        // given
        // when
        // then

        assertThatThrownBy(() -> {
            userService.verifyEmail(2, "zzz");
        }).isInstanceOf(CertificationCodeNotMatchedException.class);
    }
}