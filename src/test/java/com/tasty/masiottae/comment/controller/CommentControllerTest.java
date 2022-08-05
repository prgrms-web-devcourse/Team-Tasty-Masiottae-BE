package com.tasty.masiottae.comment.controller;

import static org.mockito.BDDMockito.given;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.get;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.post;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.requestFields;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.pathParameters;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.tasty.masiottae.account.dto.AccountFindResponse;
import com.tasty.masiottae.comment.dto.CommentFindResponse;
import com.tasty.masiottae.comment.dto.CommentSaveRequest;
import com.tasty.masiottae.comment.dto.CommentSaveResponse;
import com.tasty.masiottae.comment.service.CommentService;
import com.tasty.masiottae.security.config.SecurityConfig;
import com.tasty.masiottae.security.filter.JwtAuthenticationFilter;
import com.tasty.masiottae.security.filter.JwtAuthorizationFilter;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.restdocs.payload.JsonFieldType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(controllers = CommentController.class, excludeFilters = {
    @ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, classes = SecurityConfig.class),
    @ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, classes = JwtAuthorizationFilter.class),
    @ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, classes = JwtAuthenticationFilter.class)})
@AutoConfigureRestDocs
@WithMockUser
class CommentControllerTest {

    @Autowired
    private ObjectMapper objectMapper;
    @MockBean
    private CommentService commentService;
    @Autowired
    private MockMvc mockMvc;

    @Test
    @DisplayName("메뉴에 댓글 작성")
    void testSaveComment() throws Exception {
        // given
        CommentSaveRequest request = new CommentSaveRequest(1L, 1L, "댓글내용");
        CommentSaveResponse response = new CommentSaveResponse(request.menuId(),
            request.userId());
        given(commentService.createComment(request)).willReturn(response);

        // expected
        mockMvc.perform(post("/comments").contentType(APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)).with(csrf().asHeader()))
            .andExpect(status().isCreated())
            .andDo(print())
            .andDo(document("comment-save",
                requestFields(
                    fieldWithPath("userId").type(JsonFieldType.NUMBER)
                        .description("유저 ID"),
                    fieldWithPath("menuId").type(JsonFieldType.NUMBER)
                        .description("메뉴 ID"),
                    fieldWithPath("content").type(JsonFieldType.STRING)
                        .description("댓글 내용")
                ),
                responseFields(
                    fieldWithPath("menuId").type(JsonFieldType.NUMBER).description("메뉴 ID"),
                    fieldWithPath("commentId").type(JsonFieldType.NUMBER).description("댓글 ID")
                )));
    }

    @Test
    @DisplayName("메뉴에 달려있는 댓글 조회 기능 추가")
    void testGetAllComment() throws Exception {
        // given
        Long menuId = 1L;
        List<AccountFindResponse> accounts = IntStream.range(1, 11)
            .mapToObj(i -> new AccountFindResponse((long) i, "profile" + i, "nickname" + i,
                "test" + i + "@gmail.com", "sns" + i, LocalDateTime.now(), 10)).toList();
        List<CommentFindResponse> response = IntStream.range(1, 11).mapToObj(
            i -> new CommentFindResponse((long) i, menuId, accounts.get(i - 1), "댓글내용")
        ).collect(Collectors.toList());
        given(commentService.findAllCommentOfOneMenu(menuId)).willReturn(response);

        // expected
        mockMvc.perform(get("/menu/{menuId}/comments", menuId).contentType(APPLICATION_JSON))
            .andExpect(status().isOk())
            .andDo(document("comment-get-all",
                pathParameters(parameterWithName("menuId").description("메뉴 Id")),
                responseFields(
                    fieldWithPath("[].id").type(JsonFieldType.NUMBER).description("댓글 ID"),
                    fieldWithPath("[].menuId").type(JsonFieldType.NUMBER).description("메뉴 ID"),
                    fieldWithPath("[].author.id").type(JsonFieldType.NUMBER).description("유저 ID"),
                    fieldWithPath("[].author.image").type(JsonFieldType.STRING)
                        .description("유저 프로필 이미지"),
                    fieldWithPath("[].author.nickName").type(JsonFieldType.STRING)
                        .description("유저 닉네임"),
                    fieldWithPath("[].author.email").type(JsonFieldType.STRING)
                        .description("유저 이메일"),
                    fieldWithPath("[].author.snsAccount").type(JsonFieldType.STRING)
                        .description("SNS 계정"),
                    fieldWithPath("[].author.createdAt").type(JsonFieldType.STRING)
                        .description("생성일"),
                    fieldWithPath("[].author.menuCount").type(JsonFieldType.NUMBER)
                        .description("유저 생성 메뉴 개수"),
                    fieldWithPath("[].comment").type(JsonFieldType.STRING).description("댓글 내용"))));
    }
}
