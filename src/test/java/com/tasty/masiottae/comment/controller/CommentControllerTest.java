package com.tasty.masiottae.comment.controller;

import static org.mockito.BDDMockito.given;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.restdocs.headers.HeaderDocumentation.headerWithName;
import static org.springframework.restdocs.headers.HeaderDocumentation.requestHeaders;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.delete;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.get;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.patch;
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
import com.tasty.masiottae.comment.dto.CommentUpdateRequest;
import com.tasty.masiottae.comment.service.CommentService;
import com.tasty.masiottae.config.WithMockAccount;
import com.tasty.masiottae.security.config.SecurityConfig;
import com.tasty.masiottae.security.filter.JwtAuthenticationFilter;
import com.tasty.masiottae.security.filter.JwtAuthorizationFilter;
import com.tasty.masiottae.security.jwt.JwtAccessToken;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.LongStream;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.http.HttpHeaders;
import org.springframework.restdocs.payload.JsonFieldType;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(controllers = CommentController.class, excludeFilters = {
    @ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, classes = SecurityConfig.class),
    @ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, classes = JwtAuthorizationFilter.class),
    @ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, classes = JwtAuthenticationFilter.class)})
@AutoConfigureRestDocs
@WithMockAccount
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
            request.userId(), request.comment());
        given(commentService.createComment(request)).willReturn(response);

        // expected
        mockMvc.perform(post("/comments").contentType(APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)).with(csrf().asHeader()))
            .andExpect(status().isCreated())
            .andDo(document("comment-save",
                requestFields(
                    fieldWithPath("userId").type(JsonFieldType.NUMBER)
                        .description("유저 ID"),
                    fieldWithPath("menuId").type(JsonFieldType.NUMBER)
                        .description("메뉴 ID"),
                    fieldWithPath("comment").type(JsonFieldType.STRING)
                        .description("댓글 내용")
                ),
                responseFields(
                    fieldWithPath("menuId").type(JsonFieldType.NUMBER).description("메뉴 ID"),
                    fieldWithPath("commentId").type(JsonFieldType.NUMBER).description("댓글 ID"),
                    fieldWithPath("comment").type(JsonFieldType.STRING).description("댓글 내용")
                )));
    }

    @Test
    @DisplayName("메뉴에 댓글 작성 실패 : 댓글 내용 Empty")
    void testSaveCommentFailedByEmptyComment() throws Exception {
        // given
        CommentSaveRequest request = new CommentSaveRequest(1L, 1L, "");
        CommentSaveResponse response = new CommentSaveResponse(request.menuId(),
            request.userId(), request.comment());
        given(commentService.createComment(request)).willReturn(response);

        // expected
        mockMvc.perform(post("/comments").contentType(APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)).with(csrf().asHeader()))
            .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("메뉴에 댓글 작성 실패 : 댓글 내용 Empty")
    void testSaveCommentFailedByBlankComment() throws Exception {
        // given
        CommentSaveRequest request = new CommentSaveRequest(1L, 1L, " ");
        CommentSaveResponse response = new CommentSaveResponse(request.menuId(),
            request.userId(), request.comment());
        given(commentService.createComment(request)).willReturn(response);

        // expected
        mockMvc.perform(post("/comments").contentType(APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)).with(csrf().asHeader()))
            .andExpect(status().isBadRequest());
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
            i -> new CommentFindResponse((long) i, menuId, accounts.get(i - 1), "댓글내용",
                LocalDateTime.now(), LocalDateTime.now())
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
                    fieldWithPath("[].comment").type(JsonFieldType.STRING).description("댓글 내용"),
                    fieldWithPath("[].createdAt").type(JsonFieldType.STRING).description("생성일"),
                    fieldWithPath("[].updatedAt").type(JsonFieldType.STRING).description("갱신일"))));
    }

    @Test
    @DisplayName("자신이 쓴 댓글 수정")
    @WithMockAccount
    void testUpdateComment() throws Exception {
        Long commentId = 1L;
        JwtAccessToken token = new JwtAccessToken("bearer eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9"
            + ".eyJzdWIiOiJ0ZXN0MjBAbmF2ZXIuY29tIiwicm9sZXMiO"
            + "lsiUk9MRV9BQ0NPVU5UIl0sImV4cCI6MTY1OTQzMTI5Nn0."
            + "-cEvT2fbrz5mMpa_3Z0x4TASOEQFgk1-sT0lWU3IPR4", new Date());
        mockMvc.perform(patch("/comments/{commentId}", commentId)
                .header("Authorization", token)
                .accept(APPLICATION_JSON)
                .contentType(APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(new CommentUpdateRequest("새로운 댓글 내용")))
                .with(csrf().asHeader())
            )
            .andExpect(status().isNoContent())
            .andDo(document("comment-update",
                pathParameters(
                    parameterWithName("commentId").description("댓글 ID")
                ),
                requestHeaders(
                    headerWithName(HttpHeaders.ACCEPT).description(APPLICATION_JSON),
                    headerWithName("Authorization").description("JWT Access 토큰")
                ),
                requestFields(
                    fieldWithPath("comment").type(JsonFieldType.STRING).description("댓글 내용")
                )
            ));
    }

    @Test
    @DisplayName("자신이 쓴 댓글 삭제")
    @WithMockAccount
    void testRemoveComment() throws Exception {
        Long commentId = 1L;
        JwtAccessToken token = new JwtAccessToken("bearer eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9"
            + ".eyJzdWIiOiJ0ZXN0MjBAbmF2ZXIuY29tIiwicm9sZXMiO"
            + "lsiUk9MRV9BQ0NPVU5UIl0sImV4cCI6MTY1OTQzMTI5Nn0."
            + "-cEvT2fbrz5mMpa_3Z0x4TASOEQFgk1-sT0lWU3IPR4", new Date());

        mockMvc.perform(delete("/comments/{commentId}", commentId)
                .header("Authorization", token)
                .accept(APPLICATION_JSON)
                .with(csrf().asHeader())
            )
            .andExpect(status().isNoContent())
            .andDo(document("comment-delete",
                pathParameters(
                    parameterWithName("commentId").description("댓글 ID")
                ),
                requestHeaders(
                    headerWithName(HttpHeaders.ACCEPT).description(APPLICATION_JSON),
                    headerWithName("Authorization").description("JWT Access 토큰")
                )));
    }
}
