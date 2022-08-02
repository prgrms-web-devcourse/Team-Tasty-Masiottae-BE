package com.tasty.masiottae.account.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;
import static org.springframework.http.HttpHeaders.ACCEPT;
import static org.springframework.http.HttpHeaders.CONTENT_TYPE;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;
import static org.springframework.restdocs.headers.HeaderDocumentation.headerWithName;
import static org.springframework.restdocs.headers.HeaderDocumentation.requestHeaders;
import static org.springframework.restdocs.headers.HeaderDocumentation.responseHeaders;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.delete;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.get;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.multipart;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.patch;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.requestFields;
import static org.springframework.restdocs.payload.PayloadDocumentation.requestPartFields;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.partWithName;
import static org.springframework.restdocs.request.RequestDocumentation.pathParameters;
import static org.springframework.restdocs.request.RequestDocumentation.requestParameters;
import static org.springframework.restdocs.request.RequestDocumentation.requestParts;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.tasty.masiottae.account.dto.AccountCreateRequest;
import com.tasty.masiottae.account.dto.AccountDuplicatedResponse;
import com.tasty.masiottae.account.dto.AccountFindResponse;
import com.tasty.masiottae.account.dto.AccountImageUpdateResponse;
import com.tasty.masiottae.account.dto.AccountNickNameUpdateRequest;
import com.tasty.masiottae.account.dto.AccountNickNameUpdateResponse;
import com.tasty.masiottae.account.dto.AccountPasswordUpdateRequest;
import com.tasty.masiottae.account.dto.AccountSnsUpdateRequest;
import com.tasty.masiottae.account.dto.AccountSnsUpdateResponse;
import com.tasty.masiottae.account.service.AccountService;
import com.tasty.masiottae.common.util.AwsS3Service;
import com.tasty.masiottae.config.RestDocsConfiguration;
import com.tasty.masiottae.security.config.SecurityConfig;
import com.tasty.masiottae.security.jwt.JwtToken;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Date;
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
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.restdocs.payload.JsonFieldType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

@AutoConfigureRestDocs
@Import(RestDocsConfiguration.class)
@WebMvcTest(value = AccountController.class, excludeFilters = {
    @ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, classes = SecurityConfig.class)})
@WithMockUser
class AccountControllerTest {

    @Autowired
    MockMvc mockMvc;

    @Autowired
    ObjectMapper objectMapper;

    @MockBean
    AccountService accountService;

    @MockBean
    AwsS3Service awsS3Service;

    @Test
    @DisplayName("회원가입을 한다.")
    void testSaveAccount() throws Exception {
        AccountCreateRequest accountCreateRequest =
                new AccountCreateRequest("test@naver.com", "password", "nickName", "sns");

        MockMultipartFile request = new MockMultipartFile("request", "", "application/json",
                objectMapper.writeValueAsBytes(accountCreateRequest));

        MockMultipartFile imageFile = new MockMultipartFile("image", "image.png",
                "image/png", "sample image".getBytes());

        JwtToken token = new JwtToken(
                "bearer eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9"
                        + ".eyJzdWIiOiJ0ZXN0MjBAbmF2ZXIuY29tIiwicm9sZXMiO"
                        + "lsiUk9MRV9BQ0NPVU5UIl0sImV4cCI6MTY1OTQzMTI5Nn0."
                        + "-cEvT2fbrz5mMpa_3Z0x4TASOEQFgk1-sT0lWU3IPR4",
                new Date());

        when(accountService.saveAccount(any(), any())).thenReturn(token);

        mockMvc.perform(multipart("/signup")
                        .file(request)
                        .file(imageFile)
                        .contentType(MediaType.MULTIPART_FORM_DATA_VALUE)
                        .accept(MediaType.APPLICATION_JSON_VALUE)
                        .with(csrf().asHeader()))
                .andDo(print())
                .andExpect(status().isOk())
                .andDo(document("sign-up",
                        requestHeaders(
                                headerWithName(CONTENT_TYPE).description(
                                        MediaType.MULTIPART_FORM_DATA_VALUE),
                                headerWithName(ACCEPT).description(
                                        MediaType.APPLICATION_JSON_VALUE)
                        ),
                        requestParts(
                                partWithName("image").description("계정 프로필 이미지").optional(),
                                partWithName("request").description("계정 생성 요청 정보")

                        ),
                        requestPartFields("request",
                                fieldWithPath("email").type(JsonFieldType.STRING)
                                        .description("이메일"),
                                fieldWithPath("password").type(JsonFieldType.STRING)
                                        .description("비밀번호"),
                                fieldWithPath("nickName").type(JsonFieldType.STRING)
                                        .description("닉네임"),
                                fieldWithPath("snsAccount").type(JsonFieldType.STRING)
                                        .description("SNS 계정")
                        ),
                        responseHeaders(
                                headerWithName(CONTENT_TYPE).description(APPLICATION_JSON_VALUE)
                        ),
                        responseFields(
                                fieldWithPath("accessToken").type(JsonFieldType.STRING)
                                        .description("토큰 값"),
                                fieldWithPath("expirationTime").type(JsonFieldType.STRING)
                                        .description("토큰 만료 기간"))));
    }

    @Test
    @DisplayName("전체 계정 조회를 한다.")
    void testFindAllAccounts() throws Exception {
        List<AccountFindResponse> list = new ArrayList<>();
        list.add(new AccountFindResponse(1L, "image", "nickname", "example@naver.com",
                "snsTest", LocalDateTime.now(), 0));

        PageRequest pageable = PageRequest.of(0, 10);

        final int start = (int) pageable.getOffset();
        final int end = Math.min((start + pageable.getPageSize()), list.size());
        final Page<AccountFindResponse> pages =
                new PageImpl<>(list.subList(start, end), pageable, list.size());

        when(accountService.findAllAccounts(any())).thenReturn(pages);

        MvcResult mvcResult = mockMvc.perform(get("/accounts")
                        .param("page", String.valueOf(1))
                        .param("size", String.valueOf(10))
                        .accept(APPLICATION_JSON))
                .andExpect(status().isOk())
                .andDo(print())
                .andDo(document("find-all-accounts(paging)",
                        requestHeaders(
                                headerWithName(ACCEPT).description(
                                        APPLICATION_JSON_VALUE)
                        ),
                        requestParameters(
                                parameterWithName("page").description("요청 페이지"),
                                parameterWithName("size").description("페이지 당 사이즈")),
                        responseHeaders(
                                headerWithName(CONTENT_TYPE).description(APPLICATION_JSON_VALUE)
                        ),
                        responseFields(
                                fieldWithPath("content[].id").type(JsonFieldType.NUMBER)
                                        .description("식별자"),
                                fieldWithPath("content[].nickName").type(JsonFieldType.STRING)
                                        .description("닉네임"),
                                fieldWithPath("content[].email").type(JsonFieldType.STRING)
                                        .description("이메일"),
                                fieldWithPath("content[].image").type(JsonFieldType.STRING)
                                        .description("프로필 이미지 url"),
                                fieldWithPath("content[].snsAccount").type(JsonFieldType.STRING)
                                        .description("SNS 계정"),
                                fieldWithPath("content[].createdAt").type(JsonFieldType.STRING)
                                        .description("가입 일자"),
                                fieldWithPath("content[].menuCount").type(JsonFieldType.NUMBER)
                                        .description("보유 메뉴 개수"),
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
                                        .description("페이징 속성 존재 여부"))))
                .andReturn();
    }

    @Test
    @DisplayName("하나의 계정 조회를 한다.")
    void testFindOneAccount() throws Exception {
        AccountFindResponse accountFindResponse = new AccountFindResponse(1L, "image", "nickname",
                "example@naver.com",
                "snsTest", LocalDateTime.now(), 0);

        when(accountService.findOneAccount(anyLong())).thenReturn(accountFindResponse);

        MvcResult mvcResult = mockMvc.perform(
                        get("/accounts/{id}", 1L)
                                .accept(APPLICATION_JSON))
                .andExpect(status().isOk())
                .andDo(print())
                .andDo(document("find-one-account",
                        requestHeaders(
                                headerWithName(ACCEPT).description(APPLICATION_JSON_VALUE)
                        ),
                        pathParameters(
                                parameterWithName("id").description("조회하려는 계정의 식별자")
                        ),
                        responseHeaders(
                                headerWithName(CONTENT_TYPE).description(APPLICATION_JSON_VALUE)
                        ),
                        responseFields(
                                fieldWithPath("id").type(JsonFieldType.NUMBER).description("식별자"),
                                fieldWithPath("image").type(JsonFieldType.STRING)
                                        .description("프로필 이미지 url"),
                                fieldWithPath("nickName").type(JsonFieldType.STRING)
                                        .description("닉네임"),
                                fieldWithPath("email").type(JsonFieldType.STRING)
                                        .description("이메일"),
                                fieldWithPath("snsAccount").type(JsonFieldType.STRING)
                                        .description("SNS 계정"),
                                fieldWithPath("createdAt").type(JsonFieldType.STRING)
                                        .description("가입 일자"),
                                fieldWithPath("menuCount").type(JsonFieldType.NUMBER)
                                        .description("보유 메뉴 개수"))))
                .andReturn();
    }

    @Test
    @DisplayName("계정의 비밀번호를 변경한다.")
    void testUpdatePassword() throws Exception {
        AccountPasswordUpdateRequest request
                = new AccountPasswordUpdateRequest("abcdefgh1234");

        MvcResult mvcResult = mockMvc.perform(
                        patch("/accounts/{id}/password", 1L)
                                .with(csrf().asHeader())
                                .contentType(APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isNoContent())
                .andDo(print())
                .andDo(document("update-password",
                        requestHeaders(
                                headerWithName(CONTENT_TYPE).description(APPLICATION_JSON_VALUE)
                        ),
                        pathParameters(
                                parameterWithName("id").description("조회하려는 계정의 식별자")
                        ),
                        requestFields(
                                fieldWithPath("password").type(JsonFieldType.STRING)
                                        .description("새로운 비밀번호")
                        ))).andReturn();
    }

    @Test
    @DisplayName("계정의 닉네임을 변경한다.")
    void testUpdateNickName() throws Exception {
        AccountNickNameUpdateRequest request
                = new AccountNickNameUpdateRequest("변경할 닉네임");
        AccountNickNameUpdateResponse response
                = new AccountNickNameUpdateResponse("변경된 닉네임");

        when(accountService.updateNickName(any(), any())).thenReturn(response);

        MvcResult mvcResult = mockMvc.perform(patch("/accounts/{id}/nick-name", 1L)
                        .with(csrf().asHeader())
                        .accept(APPLICATION_JSON)
                        .contentType(APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andDo(print())
                .andDo(document("update-nickName",
                        requestHeaders(
                                headerWithName(CONTENT_TYPE).description(APPLICATION_JSON_VALUE)
                        ),
                        pathParameters(
                                parameterWithName("id").description("조회하려는 계정의 식별자")
                        ),
                        requestFields(
                                fieldWithPath("nickName").type(JsonFieldType.STRING)
                                        .description("변경할 닉네임")),
                        responseHeaders(
                                headerWithName(CONTENT_TYPE).description(APPLICATION_JSON_VALUE)
                        ),
                        responseFields(
                                fieldWithPath("updatedNickName").type(JsonFieldType.STRING)
                                        .description("성공적으로 변경된 닉네임")
                        )
                )).andReturn();
    }

    @Test
    @DisplayName("계정의 sns 계정을 변경한다.")
    void testUpdateSnsAccount() throws Exception {
        AccountSnsUpdateRequest request
                = new AccountSnsUpdateRequest("변경할 SNS 계정");
        AccountSnsUpdateResponse response
                = new AccountSnsUpdateResponse("변경된 SNS 닉네임");

        when(accountService.updateSnsAccount(any(), any())).thenReturn(response);

        MvcResult mvcResult = mockMvc.perform(patch("/accounts/{id}/sns", 1L)
                        .with(csrf().asHeader())
                        .accept(APPLICATION_JSON)
                        .contentType(APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andDo(print())
                .andDo(document("update-sns",
                        requestHeaders(
                                headerWithName(CONTENT_TYPE).description(APPLICATION_JSON_VALUE)
                        ),
                        pathParameters(
                                parameterWithName("id").description("조회하려는 계정의 식별자")
                        ),
                        requestFields(
                                fieldWithPath("snsAccount").type(JsonFieldType.STRING)
                                        .description("새로운 SNS 계정")),
                        responseHeaders(
                                headerWithName(CONTENT_TYPE).description(APPLICATION_JSON_VALUE)
                        ),
                        responseFields(
                                fieldWithPath("updatedSnsAccount").type(JsonFieldType.STRING)
                                        .description("성공적으로 변경된 SNS 계정")
                        )
                )).andReturn();
    }

    @Test
    @DisplayName("계정의 프로필 사진을 변경한다.")
    void testUpdateImage() throws Exception {
        AccountImageUpdateResponse accountImageUpdateResponse =
                new AccountImageUpdateResponse("https://localhost/abcdefg.jpeg");

        when(accountService.updateImage(anyLong(), any())).thenReturn(accountImageUpdateResponse);

        MockMultipartFile imageFile = new MockMultipartFile("image", "image.png",
                "image/png", "sample image".getBytes());

        mockMvc.perform(multipart("/accounts/{id}/image", 1L)
                        .file(imageFile)
                        .contentType(MediaType.MULTIPART_FORM_DATA_VALUE)
                        .accept(MediaType.APPLICATION_JSON_VALUE)
                        .with(csrf().asHeader()))
                .andDo(print())
                .andExpect(status().isOk())
                .andDo(document("update-image",
                        requestHeaders(
                                headerWithName(CONTENT_TYPE).description(
                                        MediaType.MULTIPART_FORM_DATA_VALUE),
                                headerWithName(ACCEPT).description(
                                        MediaType.APPLICATION_JSON_VALUE)
                        ),
                        requestParts(
                                partWithName("image").description("새로운 프로필 이미지")
                        ),
                        responseHeaders(
                                headerWithName(CONTENT_TYPE).description(
                                        MediaType.APPLICATION_JSON_VALUE)
                        ),
                        responseFields(
                                fieldWithPath("image").type(JsonFieldType.STRING)
                                        .description("새로운 프로필 이미지 url")
                        )
                ));
    }

    @Test
    @DisplayName("계정을 삭제한다.")
    void testDeleteAccount() throws Exception {
        MvcResult mvcResult = mockMvc.perform(
                        delete("/accounts/{id}", 1L)
                                .with(csrf().asHeader()))
                .andExpect(status().isNoContent())
                .andDo(print())
                .andDo(document("delete-one-account",
                        pathParameters(
                                parameterWithName("id").description("삭제하려는 계정의 식별자")
                        )))
                .andReturn();
    }

    @Test
    @DisplayName("속성 중복 여부를 체크한다.")
    void testCheckDuplicateProperty() throws Exception {
        String property = "email";
        String mockedEmail = "example@naver.com";

        AccountDuplicatedResponse mockedResponse = new AccountDuplicatedResponse(false,
                Optional.empty());

        when(accountService.checkDuplicateByEmail(mockedEmail)).thenReturn(mockedResponse);

        MvcResult mvcResult = mockMvc.perform(get("/accounts/check")
                        .param("property", property)
                        .param("value", mockedEmail)
                        .accept(APPLICATION_JSON))
                .andExpect(status().isOk())
                .andDo(print())
                .andDo(document("check-account",
                        requestHeaders(
                                headerWithName(ACCEPT).description(APPLICATION_JSON_VALUE)
                        ),
                        requestParameters(
                                parameterWithName("property").description(
                                        "중복 여부를 확인할 속성: 이메일(email) 또는 닉네임(nickName)"),
                                parameterWithName("value").description("구체적인 속성 값")),
                        responseHeaders(
                                headerWithName(CONTENT_TYPE).description(APPLICATION_JSON_VALUE)
                        ),
                        responseFields(
                                fieldWithPath("isDuplicated").type(JsonFieldType.BOOLEAN)
                                        .description("중복 여부"),
                                fieldWithPath("errorMessage").type(JsonFieldType.STRING)
                                        .description("예외 발생 이유").optional())))
                .andReturn();
    }

}
