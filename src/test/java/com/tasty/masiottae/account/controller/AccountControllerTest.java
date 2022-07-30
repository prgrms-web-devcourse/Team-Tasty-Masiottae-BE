package com.tasty.masiottae.account.controller;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.get;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.patch;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.post;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.requestFields;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.pathParameters;
import static org.springframework.restdocs.request.RequestDocumentation.requestParameters;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.tasty.masiottae.RestDocsConfiguration;
import com.tasty.masiottae.account.dto.AccountCreateRequest;
import com.tasty.masiottae.account.dto.AccountDuplicatedResponse;
import com.tasty.masiottae.account.dto.AccountFindResponse;
import com.tasty.masiottae.account.dto.AccountLoginRequest;
import com.tasty.masiottae.account.dto.AccountPasswordUpdateRequest;
import com.tasty.masiottae.account.dto.AccountUpdateRequest;
import com.tasty.masiottae.account.service.AccountService;
import com.tasty.masiottae.security.config.SecurityConfig;
import com.tasty.masiottae.security.filter.JwtAuthenticationFilter;
import com.tasty.masiottae.security.filter.JwtAuthorizationFilter;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.MediaType;
import org.springframework.restdocs.payload.JsonFieldType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

@AutoConfigureRestDocs
@Import(RestDocsConfiguration.class)
@WebMvcTest(value = AccountController.class, excludeFilters = {
    @ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, classes = SecurityConfig.class),
    @ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, classes = JwtAuthorizationFilter.class),
    @ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, classes = JwtAuthenticationFilter.class)})
@WithMockUser
class AccountControllerTest {

    @Autowired
    MockMvc mockMvc;

    @Autowired
    ObjectMapper objectMapper;

    @MockBean
    AccountService accountService;

    @Test
    void findAccountById() throws Exception {

        mockMvc.perform(get("/accounts/{id}", 1L).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk()).andDo(print()).andDo(document("findById-account",
                        responseFields(fieldWithPath("id").description("식별자"),
                                fieldWithPath("nickname").description("닉네임"),
                                fieldWithPath("email").description("이메일"),
                                fieldWithPath("imgUrl").description("프로필 사진 url"),
                                fieldWithPath("createdAt").description("가입 일자"),
                                fieldWithPath("menuCount").description("작성한 메뉴 개수"))));
    }

    @Test
    void findAccountPaging() throws Exception {
        List<AccountFindResponse> list = new ArrayList<>();
        list.add(new AccountFindResponse(1L, "nickname", "imgUrl", "example@naver.com",
                LocalDateTime.now(), 0));

        PageRequest pageable = PageRequest.of(0, 10);

        final int start = (int) pageable.getOffset();
        final int end = Math.min((start + pageable.getPageSize()), list.size());
        final Page<AccountFindResponse> pages = new PageImpl<>(list.subList(start, end), pageable,
                list.size());

        mockMvc.perform(get("/accounts").param("page", String.valueOf(0))
                        .contentType(MediaType.APPLICATION_JSON)).andExpect(status().isOk()).andDo(print())
                .andDo(document("paging-account",
                        requestParameters(parameterWithName("page").description("요청 페이지")),
                        responseFields(fieldWithPath("content[].id").description("식별자"),
                                fieldWithPath("content[].nickname").description("닉네임"),
                                fieldWithPath("content[].email").description("이메일"),
                                fieldWithPath("content[].imgUrl").description("프로필 사진 url"),
                                fieldWithPath("content[].createdAt").description("가입 일자"),
                                fieldWithPath("content[].menuCount").description(
                                        "account가 작성한 메뉴 갯수"),
                                fieldWithPath("pageable.pageNumber").type(JsonFieldType.NUMBER)
                                        .description("페이지 번호"),
                                fieldWithPath("pageable.pageSize").type(JsonFieldType.NUMBER)
                                        .description("페이지 크기"),
                                fieldWithPath("pageable.offset").type(JsonFieldType.NUMBER)
                                        .description("offset"),
                                fieldWithPath("pageable.paged").type(JsonFieldType.BOOLEAN)
                                        .description("Pageable paging 여부"),
                                fieldWithPath("pageable.unpaged").type(JsonFieldType.BOOLEAN)
                                        .description("Pageable not paging 여부"),
                                fieldWithPath("pageable.sort.sorted").type(JsonFieldType.BOOLEAN)
                                        .description("페이지 정렬 여부"),
                                fieldWithPath("pageable.sort.unsorted").type(JsonFieldType.BOOLEAN)
                                        .description("페이지 미정렬 여부"),
                                fieldWithPath("pageable.sort.empty").type(JsonFieldType.BOOLEAN)
                                        .description("페이지 정렬 값의 여부"),
                                fieldWithPath("totalPages").type(JsonFieldType.NUMBER)
                                        .description("전체 페이지 수"),
                                fieldWithPath("totalElements").type(JsonFieldType.NUMBER)
                                        .description("전체 게시글 수"),
                                fieldWithPath("last").type(JsonFieldType.BOOLEAN)
                                        .description("현재 페이지가 마지막 여부"),
                                fieldWithPath("numberOfElements").type(JsonFieldType.NUMBER)
                                        .description("현재 페이지 내의 원소 개수"),
                                fieldWithPath("first").type(JsonFieldType.BOOLEAN)
                                        .description("현재 페이지가 첫번째 페이지인지 여부"),
                                fieldWithPath("size").type(JsonFieldType.NUMBER)
                                        .description("현재 페이지의 크기"),
                                fieldWithPath("number").type(JsonFieldType.NUMBER)
                                        .description("현재 페이지의 index"),
                                fieldWithPath("empty").type(JsonFieldType.BOOLEAN)
                                        .description("페이지 비어있는지 여부"),
                                fieldWithPath("sort").type(JsonFieldType.OBJECT)
                                        .description("페이지 정보"),
                                fieldWithPath("sort.sorted").type(JsonFieldType.BOOLEAN)
                                        .description("페이징 정렬 여부"),
                                fieldWithPath("sort.unsorted").type(JsonFieldType.BOOLEAN)
                                        .description("페이징 미정렬 여부"),
                                fieldWithPath("sort.empty").type(JsonFieldType.BOOLEAN)
                                        .description("페이징 속성 존재 여부"))));
    }

    @Test
    void signAccount() throws Exception {

        AccountCreateRequest accountCreateRequest = new AccountCreateRequest("example@naver.com",
                "testNickname", "testPassword", "testImageUrl", "testInstagramId");
        mockMvc.perform(
                        post("/accounts").contentType(MediaType.APPLICATION_JSON).with(csrf().asHeader())
                                .content(objectMapper.writeValueAsString(accountCreateRequest)))
                .andExpect(status().isOk())
                .andDo(document("sign-account",
                        requestFields(fieldWithPath("nickname").description("닉네임"),
                                fieldWithPath("email").description("이메일"),
                                fieldWithPath("imgUrl").description("프로필 사진 url"),
                                fieldWithPath("password").description("account Password"),
                                fieldWithPath("snsAccount").description("sns 계정명"))));
    }

    @Test
    void updateAccount() throws Exception {
        AccountUpdateRequest accountUpdateRequest = new AccountUpdateRequest("updateNickname",
                "updateImgUrl");
        mockMvc.perform(patch("/accounts/{id}", 1L).with(csrf().asHeader())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(accountUpdateRequest)))
                .andExpect(status().is2xxSuccessful()).andDo(print())
                .andDo(document("update-account",
                        pathParameters(parameterWithName("id").description("식별자")),
                        requestFields(fieldWithPath("nickname").description("닉네임"),
                                fieldWithPath("imgUrl").description("프로필 사진 url"))));
    }

    @Test
    void loginAccount() throws Exception {
        AccountLoginRequest accountLoginRequest = new AccountLoginRequest("testLoginId",
                "testLoginPassword");
        mockMvc.perform(post("/accounts/login").with(csrf().asHeader())
                        .content(objectMapper.writeValueAsString(accountLoginRequest))
                        .contentType(MediaType.APPLICATION_JSON)).andExpect(status().is2xxSuccessful())
                .andDo(print()).andDo(document("login-account",
                        requestFields(fieldWithPath("loginId").description("로그인 ID"),
                                fieldWithPath("loginPassword").description("로그인 비밀번호"))));
    }

    @Test
    void updatePassword() throws Exception {
        AccountPasswordUpdateRequest accountPasswordUpdateRequest = new AccountPasswordUpdateRequest(
                "updatePassword");
        mockMvc.perform(patch("/accounts/{id}/password", 1L).with(csrf().asHeader())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(accountPasswordUpdateRequest)))
                .andExpect(status().is2xxSuccessful()).andDo(print())
                .andDo(document("update-password-account",
                        pathParameters(parameterWithName("id").description("식별자")),
                        requestFields(fieldWithPath("password").description("수정된 비밀번호"))));
    }

    @Test
    @DisplayName("속성 중복 여부를 체크한다.")
    void testIsDuplicatedParam() throws Exception {
        String property = "email";
        String mockedEmail = "example@naver.com";

        AccountDuplicatedResponse mockedResponse = new AccountDuplicatedResponse(false,
                Optional.empty());

        when(accountService.duplicateCheckEmail(mockedEmail)).thenReturn(mockedResponse);

        MvcResult mvcResult = mockMvc.perform(
                        get("/accounts/check").param("property", property).param("value", mockedEmail)
                                .accept(MediaType.APPLICATION_JSON_VALUE).with(csrf().asHeader()))
                .andExpect(status().isOk()).andDo(print()).andDo(document("check-account",
                        requestParameters(parameterWithName("property").description(
                                        "중복 여부를 확인할 속성: 이메일(email) 또는 닉네임(nickname)"),
                                parameterWithName("value").description("속성 값")), responseFields(
                                fieldWithPath("isDuplicated").type(JsonFieldType.BOOLEAN)
                                        .description("중복 여부"),
                                fieldWithPath("errorMessage").type(JsonFieldType.STRING)
                                        .description("예외 발생 이유").optional()))).andReturn();

        verify(accountService, times(1)).duplicateCheckEmail(mockedEmail);
        verify(accountService, never()).duplicateCheckNickname(anyString());
    }

}