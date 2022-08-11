package com.tasty.masiottae.menu.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.restdocs.headers.HeaderDocumentation.headerWithName;
import static org.springframework.restdocs.headers.HeaderDocumentation.requestHeaders;
import static org.springframework.restdocs.headers.HeaderDocumentation.responseHeaders;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.delete;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.get;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.multipart;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
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
import com.tasty.masiottae.account.dto.AccountFindResponse;
import com.tasty.masiottae.config.RestDocsConfiguration;
import com.tasty.masiottae.config.WithMockAccount;
import com.tasty.masiottae.franchise.dto.FranchiseFindResponse;
import com.tasty.masiottae.menu.dto.*;
import com.tasty.masiottae.menu.service.MenuService;
import com.tasty.masiottae.option.dto.OptionFindResponse;
import com.tasty.masiottae.option.dto.OptionSaveRequest;
import com.tasty.masiottae.security.config.SecurityConfig;
import com.tasty.masiottae.security.filter.JwtAuthenticationFilter;
import com.tasty.masiottae.security.filter.JwtAuthorizationFilter;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;

import com.tasty.masiottae.security.jwt.JwtAccessToken;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.restdocs.payload.JsonFieldType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(controllers = MenuController.class, excludeFilters = {
    @ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, classes = SecurityConfig.class),
    @ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, classes = JwtAuthorizationFilter.class),
    @ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, classes = JwtAuthenticationFilter.class)}
)
@AutoConfigureRestDocs
@WithMockAccount
@Import(RestDocsConfiguration.class)
class MenuControllerTest {

    @Autowired
    ObjectMapper objectMapper;
    @MockBean
    MenuService menuService;
    @Autowired
    private MockMvc mockMvc;

    @Test
    @DisplayName("메뉴 단건 조회")
    void testGetOneMenu() throws Exception {
        // given
        Long menuId = 1L;

        MenuFindResponse menuFindResponse = new MenuFindResponse(1L,
                new FranchiseFindResponse(1L, "logo", "스타벅스"), "img.png", "커스텀제목", "원래제목",
                new AccountFindResponse(1L, "이미지 url", "닉네임", "유저이름", "이메일", LocalDateTime.now(),
                        10), "내용", 100, 0, 5000,
                List.of(new OptionFindResponse("옵션명1", "설명1"), new OptionFindResponse("옵션명", "설명")),
                List.of(new TasteFindResponse(1L, "빨간맛", "빨간색"),
                        new TasteFindResponse(2L, "파란맛", "파란색")), LocalDateTime.now(),
                LocalDateTime.now());

        given(menuService.findOneMenu(1L)).willReturn(menuFindResponse);

        // expected
        mockMvc.perform(get("/menu/{menuId}", menuId).contentType(APPLICATION_JSON))
                .andExpect(status().isOk()).andDo(print()).andDo(document("menu-findOne",
                        pathParameters(parameterWithName("menuId").description("메뉴 Id")),
                        responseFields(fieldWithPath("id").type(JsonFieldType.NUMBER).description("메뉴 ID"),
                                fieldWithPath("franchise.id").type(JsonFieldType.NUMBER)
                                        .description("프랜차이즈 id"),
                                fieldWithPath("franchise.image").type(JsonFieldType.STRING)
                                        .description("프랜차이즈 로고"),
                                fieldWithPath("franchise.name").type(JsonFieldType.STRING)
                                        .description("프랜차이즈명"),
                                fieldWithPath("image").type(JsonFieldType.STRING).description("이미지경로"),
                                fieldWithPath("title").type(JsonFieldType.STRING).description("메뉴명"),
                                fieldWithPath("originalTitle").type(JsonFieldType.STRING)
                                        .description("실제 메뉴명"),
                                fieldWithPath("author.id").type(JsonFieldType.NUMBER).description("유저 ID"),
                                fieldWithPath("author.nickName").type(JsonFieldType.STRING)
                                        .description("닉네임"),
                                fieldWithPath("author.image").type(JsonFieldType.STRING)
                                        .description("유저 프로필사진"),
                                fieldWithPath("author.email").type(JsonFieldType.STRING)
                                        .description("유저 이메일"),
                                fieldWithPath("author.createdAt").type(JsonFieldType.STRING)
                                        .description("생성일"),
                                fieldWithPath("author.snsAccount").type(JsonFieldType.STRING)
                                        .description("유저 SNS 계정"),
                                fieldWithPath("author.menuCount").type(JsonFieldType.NUMBER)
                                        .description("해당 유저의 생성메뉴 수"),
                                fieldWithPath("content").type(JsonFieldType.STRING).description("설명"),
                                fieldWithPath("likes").type(JsonFieldType.NUMBER).description("좋아요 수"),
                                fieldWithPath("comments").type(JsonFieldType.NUMBER).description("댓글 개수"),
                                fieldWithPath("expectedPrice").type(JsonFieldType.NUMBER)
                                        .description("예상가격"),
                                fieldWithPath("optionList[].name").type(JsonFieldType.STRING)
                                        .description("옵션명"),
                                fieldWithPath("optionList[].description").type(JsonFieldType.STRING)
                                        .description("옵션 설명"),
                                fieldWithPath("tasteList[].id").type(JsonFieldType.NUMBER)
                                        .description("맛 ID"),
                                fieldWithPath("tasteList[].name").type(JsonFieldType.STRING)
                                        .description("맛 이름"),
                                fieldWithPath("tasteList[].color").type(JsonFieldType.STRING)
                                        .description("맛 태그 컬러"),
                                fieldWithPath("createdAt").type(JsonFieldType.STRING).description("생성일"),
                                fieldWithPath("updatedAt").type(JsonFieldType.STRING).description("갱신일"))));
    }

    @Test
    @DisplayName("메뉴를 생성한다.")
    void createMenuTest() throws Exception {
        // Given

        List<OptionSaveRequest> optionSaveRequests = List.of(new OptionSaveRequest("에스프레소 샷", "1샷"),
                new OptionSaveRequest("간 자바칩", "1개"), new OptionSaveRequest("통 자바칩", "1개"),
                new OptionSaveRequest("카라멜드리즐", "1개"));

        List<Long> tasteIds = List.of(1L, 2L, 3L);

        MenuSaveRequest menuSaveRequest = new MenuSaveRequest(1L, 1L, "슈렉 프라푸치노",
                "맛있습니다.",
                "그린티 프라푸치노", 10000, optionSaveRequests, tasteIds);

        MockMultipartFile data = new MockMultipartFile("data", "", "application/json",
                objectMapper.writeValueAsBytes(menuSaveRequest));

        MockMultipartFile imageFile = new MockMultipartFile("image", "image.png", "image/png",
                "sample image".getBytes());

        given(menuService.createMenu(menuSaveRequest, imageFile)).willReturn(
                new MenuSaveResponse(1L));



        // When // Then
        mockMvc.perform(multipart("/menu").file(data).file(imageFile)
                        .contentType(MediaType.MULTIPART_FORM_DATA_VALUE)
                        .accept(MediaType.APPLICATION_JSON_VALUE).with(csrf().asHeader())).andDo(print())
                .andExpect(status().isCreated()).andDo(document("create-menu", requestHeaders(
                                headerWithName(HttpHeaders.CONTENT_TYPE).description(
                                        MediaType.MULTIPART_FORM_DATA_VALUE),
                                headerWithName(HttpHeaders.ACCEPT).description(MediaType.APPLICATION_JSON_VALUE)
                        ),
                        requestParts(partWithName("image").description("메뉴 이미지"),
                                partWithName("data").description("메뉴 정보")

                        ), requestPartFields("data",
                                fieldWithPath("userId").type(JsonFieldType.NUMBER).description("회원 ID"),
                                fieldWithPath("franchiseId").type(JsonFieldType.NUMBER)
                                        .description("프렌차이즈 ID"),
                                fieldWithPath("title").type(JsonFieldType.STRING).description("메뉴명"),
                                fieldWithPath("content").type(JsonFieldType.STRING).description("메뉴 설명"),
                                fieldWithPath("originalTitle").type(JsonFieldType.STRING)
                                        .description("실제 메뉴명"),
                                fieldWithPath("expectedPrice").type(JsonFieldType.NUMBER).description("가격"),
                                fieldWithPath("optionList[]").type(JsonFieldType.ARRAY)
                                        .description("옵션 목록"),
                                fieldWithPath("optionList[].name").type(JsonFieldType.STRING)
                                        .description("옵션명"),
                                fieldWithPath("optionList[].description").type(JsonFieldType.STRING)
                                        .description("옵션 설명"),
                                fieldWithPath("tasteIdList[]").type(JsonFieldType.ARRAY)
                                        .description("맛 ID 목록")
                        ), responseHeaders(
                                headerWithName(HttpHeaders.CONTENT_TYPE).description(
                                        MediaType.APPLICATION_JSON_VALUE)), responseFields(
                                fieldWithPath("menuId").type(JsonFieldType.NUMBER)
                                        .description("생성된 메뉴 ID"))));

        then(menuService).should().createMenu(menuSaveRequest, imageFile);
    }

    @Test
    @DisplayName("메뉴를 수정한다.")
    void updateMenuTest() throws Exception {
        // Given
        List<OptionSaveRequest> optionSaveRequests = List.of(
                new OptionSaveRequest("에스프레소 샷", "1샷"),
                new OptionSaveRequest("간 자바칩", "1개"),
                new OptionSaveRequest("통 자바칩", "1개"),
                new OptionSaveRequest("카라멜드리즐", "1개")
        );

        List<Long> tasteIds = List.of(1L, 2L, 3L);

        MenuUpdateRequest menuUpdateRequest = new MenuUpdateRequest(1L, "슈렉 프라푸치노",
                "맛있습니다.",
                "그린티 프라푸치노", 10000, optionSaveRequests,
                tasteIds, false);

        MockMultipartFile data = new MockMultipartFile("data", "", "application/json",
                objectMapper.writeValueAsBytes(menuUpdateRequest));

        MockMultipartFile imageFile = new MockMultipartFile("image", "image.png",
                "image/png", "sample image".getBytes());

        JwtAccessToken token = new JwtAccessToken(
                "bearer eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9"
                        + ".eyJzdWIiOiJ0ZXN0MjBAbmF2ZXIuY29tIiwicm9sZXMiO"
                        + "lsiUk9MRV9BQ0NPVU5UIl0sImV4cCI6MTY1OTQzMTI5Nn0."
                        + "-cEvT2fbrz5mMpa_3Z0x4TASOEQFgk1-sT0lWU3IPR4",
                new Date());

        given(menuService.createMenu(any(), any()))
                .willReturn(new MenuSaveResponse(1L));

        mockMvc.perform(multipart("/menu/{menuId}", 1)
                        .file(data)
                        .file(imageFile)
                        .contentType(MediaType.MULTIPART_FORM_DATA_VALUE)
                        .accept(MediaType.APPLICATION_JSON_VALUE)
                        .header("Authorization", token)
                        .with(csrf().asHeader())
                ).andExpect(status().isOk())
                .andDo(print())
                .andDo(document("update-menu",
                        requestHeaders(
                                headerWithName(HttpHeaders.CONTENT_TYPE).description(
                                        MediaType.MULTIPART_FORM_DATA_VALUE),
                                headerWithName(HttpHeaders.ACCEPT).description(
                                        MediaType.APPLICATION_JSON_VALUE),
                                headerWithName("Authorization").description("JWT Access 토큰")
                        ),
                        pathParameters(
                                parameterWithName("menuId").description("수정할 메뉴 ID")
                        ),
                        requestParts(
                                partWithName("image").description("메뉴 이미지"),
                                partWithName("data").description("메뉴 정보")

                        ),
                        requestPartFields("data",
                                fieldWithPath("franchiseId").type(JsonFieldType.NUMBER)
                                        .description("프렌차이즈 ID"),
                                fieldWithPath("title").type(JsonFieldType.STRING)
                                        .description("메뉴명"),
                                fieldWithPath("content").type(JsonFieldType.STRING)
                                        .description("메뉴 설명"),
                                fieldWithPath("originalTitle").type(JsonFieldType.STRING)
                                        .description("실제 메뉴명"),
                                fieldWithPath("expectedPrice").type(JsonFieldType.NUMBER)
                                        .description("가격"),
                                fieldWithPath("optionList[]").type(JsonFieldType.ARRAY)
                                        .description("옵션 목록"),
                                fieldWithPath("optionList[].name").type(JsonFieldType.STRING)
                                        .description("옵션명"),
                                fieldWithPath("optionList[].description").type(JsonFieldType.STRING)
                                        .description("옵션 설명"),
                                fieldWithPath("tasteIdList[]").type(JsonFieldType.ARRAY)
                                        .description("맛 ID 목록"),
                                fieldWithPath("isRemoveImage").type(JsonFieldType.BOOLEAN).description("이미지가 제거 유무.")
                        )
                ));
    }

    @Test
    @DisplayName("메뉴를 삭제한다.")
    public void deleteMenuTest() throws Exception {
        JwtAccessToken token = new JwtAccessToken(
                "bearer eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9"
                        + ".eyJzdWIiOiJ0ZXN0MjBAbmF2ZXIuY29tIiwicm9sZXMiO"
                        + "lsiUk9MRV9BQ0NPVU5UIl0sImV4cCI6MTY1OTQzMTI5Nn0."
                        + "-cEvT2fbrz5mMpa_3Z0x4TASOEQFgk1-sT0lWU3IPR4",
                new Date());
        Long menuId = 1L;
        mockMvc.perform(delete("/menu/{menuId}", menuId)
                        .with(csrf().asHeader())
                        .header("Authorization", token)
                        .accept(MediaType.APPLICATION_JSON)
                ).andExpect(status().isOk())
                .andDo(print())
                .andDo(document("delete-menu",
                        requestHeaders(
                                headerWithName(HttpHeaders.ACCEPT).description(
                                        MediaType.APPLICATION_JSON_VALUE),
                                headerWithName("Authorization").description("JWT Access 토큰")
                        ),
                        pathParameters(
                                parameterWithName("menuId").description("삭제할 메뉴 ID")
                        )
                ));

        then(menuService).should().delete(any(), any());
    }

    @Test
    @DisplayName("나의 메뉴를 검색한다.")
    void searchMyMenuTest() throws Exception {
        List<Long> tasteIds = List.of(1L, 2L, 3L);

        AccountFindResponse author = new AccountFindResponse(1L, "user1.com", "programmers",
                "prgrms@gmail.com", "prgrms123", LocalDateTime.now(), 1);
        FranchiseFindResponse franchise = new FranchiseFindResponse(1L, "starbucks.com", "스타벅스");

        List<OptionFindResponse> optionFindResponses = List.of(
                new OptionFindResponse("간 자바칩", "1개"),
                new OptionFindResponse("통 자바칩", "1개"),
                new OptionFindResponse("카라멜드리즐", "1개")
        );

        List<TasteFindResponse> tasteFindResponses = List.of(
                new TasteFindResponse(1L, "단맛", "#000000"),
                new TasteFindResponse(2L, "매운맛", "#000000"),
                new TasteFindResponse(3L, "쓴맛", "#000000")
        );

        List<MenuFindResponse> menuFindResponses = List.of(
                new MenuFindResponse(1L, franchise, "menu1.com",
                        "슈렉 프라푸치노", "그린티 프라푸치노",
                        author, "맛있습니다.", 0, 0, 8000, optionFindResponses, tasteFindResponses,
                        LocalDateTime.now(), LocalDateTime.now()),
                new MenuFindResponse(2L, franchise, "menu1.com",
                        "몬스터 프라푸치노", "자바칩 프라푸치노",
                        author, "맛있습니다.", 0, 0, 8000, optionFindResponses, tasteFindResponses,
                        LocalDateTime.now(), LocalDateTime.now())
        );

        Long accountId = 1L;
        SearchMyMenuRequest request = new SearchMyMenuRequest(0, 1, "프라푸치노", "recent",
                List.of(1L, 2L, 3L));

        given(menuService.searchMyMenu(accountId, request)).willReturn(
                new SearchMenuResponse(menuFindResponses));

        mockMvc.perform(get("/accounts/{accountId}/menu", accountId)
                        .accept(MediaType.APPLICATION_JSON_VALUE)
                        .param("keyword", request.keyword())
                        .param("sort", request.sort())
                        .param("tasteIdList", "1,2,3")
                        .param("offset", String.valueOf(request.offset()))
                        .param("limit", String.valueOf(request.limit())))
                .andExpect(status().isOk())
                .andDo(print())
                .andDo(document("search-my-menu",
                        requestHeaders(
                                headerWithName(HttpHeaders.ACCEPT).description(
                                        MediaType.APPLICATION_JSON_VALUE)
                        ),
                        pathParameters(
                                parameterWithName("accountId").description("회원 ID")
                        ),
                        requestParameters(
                                parameterWithName("keyword").description("검색어"),
                                parameterWithName("sort").description(
                                        "정렬 조건(recent, like, comment)"),
                                parameterWithName("tasteIdList").description("맛 ID 조건(예: 1,2,3)"),
                                parameterWithName("offset").description("페이징 offset"),
                                parameterWithName("limit").description("패이징 limit")
                        ),
                        responseHeaders(
                                headerWithName(HttpHeaders.CONTENT_TYPE).description(
                                        MediaType.APPLICATION_JSON_VALUE)
                        ),
                        responseFields(
                                fieldWithPath("menu[]").type(JsonFieldType.ARRAY)
                                        .description("메뉴 배열"),
                                fieldWithPath("menu[].id").type(JsonFieldType.NUMBER)
                                        .description("메뉴 ID"),
                                fieldWithPath("menu[].image").type(JsonFieldType.STRING)
                                        .description("이미지 URL"),
                                fieldWithPath("menu[].title").type(JsonFieldType.STRING)
                                        .description("메뉴명"),
                                fieldWithPath("menu[].originalTitle").type(JsonFieldType.STRING)
                                        .description("기존 메뉴명"),
                                fieldWithPath("menu[].content").type(JsonFieldType.STRING)
                                        .description("메뉴 설명"),
                                fieldWithPath("menu[].likes").type(JsonFieldType.NUMBER)
                                        .description("메뉴 좋아요 개수"),
                                fieldWithPath("menu[].comments").type(JsonFieldType.NUMBER)
                                        .description("메뉴 댓글 개수"),
                                fieldWithPath("menu[].expectedPrice").type(JsonFieldType.NUMBER)
                                        .description("메뉴 가격"),
                                fieldWithPath("menu[].createdAt").type(JsonFieldType.STRING)
                                        .description("메뉴 생성 일시"),
                                fieldWithPath("menu[].updatedAt").type(JsonFieldType.STRING)
                                        .description("최종 메뉴 수정 일시"),
                                fieldWithPath("menu[].franchise").type(JsonFieldType.OBJECT)
                                        .description("프렌차이즈 정보"),
                                fieldWithPath("menu[].franchise.id").type(JsonFieldType.NUMBER)
                                        .description("프렌차이즈 ID"),
                                fieldWithPath("menu[].franchise.image").type(JsonFieldType.STRING)
                                        .description("프렌차이즈 이미지 URL"),
                                fieldWithPath("menu[].franchise.name").type(JsonFieldType.STRING)
                                        .description("프렌차이즈명"),
                                fieldWithPath("menu[].author").type(JsonFieldType.OBJECT)
                                        .description("회원 정보"),
                                fieldWithPath("menu[].author.id").type(JsonFieldType.NUMBER)
                                        .description("회원 ID"),
                                fieldWithPath("menu[].author.image").type(JsonFieldType.STRING)
                                        .description("회원 이미지 URL"),
                                fieldWithPath("menu[].author.nickName").type(JsonFieldType.STRING)
                                        .description("회원 닉네임"),
                                fieldWithPath("menu[].author.email").type(JsonFieldType.STRING)
                                        .description("회원 이메일"),
                                fieldWithPath("menu[].author.snsAccount").type(JsonFieldType.STRING)
                                        .description("회원 SNS 계정"),
                                fieldWithPath("menu[].author.createdAt").type(JsonFieldType.STRING)
                                        .description("회원 생성 일시"),
                                fieldWithPath("menu[].author.menuCount").type(JsonFieldType.NUMBER)
                                        .description("해당 회원이 생성한 메뉴 개수"),
                                fieldWithPath("menu[].optionList[]").type(JsonFieldType.ARRAY)
                                        .description("메뉴 옵션 배열"),
                                fieldWithPath("menu[].optionList[].name").type(
                                        JsonFieldType.STRING).description("메뉴 옵션명"),
                                fieldWithPath("menu[].optionList[].description").type(
                                        JsonFieldType.STRING).description("메뉴 옵션 설명"),
                                fieldWithPath("menu[].tasteList[]").type(JsonFieldType.ARRAY)
                                        .description("메뉴 맛 배열"),
                                fieldWithPath("menu[].tasteList[].id").type(JsonFieldType.NUMBER)
                                        .description("맛 ID"),
                                fieldWithPath("menu[].tasteList[].name").type(JsonFieldType.STRING)
                                        .description("맛 이름"),
                                fieldWithPath("menu[].tasteList[].color").type(JsonFieldType.STRING)
                                        .description("맛 색상 코드")
                        )
                ));
    }

    @Test
    @DisplayName("메뉴를 검색한다.")
    void searchMenuTest() throws Exception {
        AccountFindResponse author = new AccountFindResponse(1L, "user1.com", "programmers",
                "prgrms@gmail.com", "prgrms123", LocalDateTime.now(), 1);
        FranchiseFindResponse franchise = new FranchiseFindResponse(1L, "starbucks.com", "스타벅스");

        List<OptionFindResponse> optionFindResponses = List.of(
                new OptionFindResponse("간 자바칩", "1개"),
                new OptionFindResponse("통 자바칩", "1개"),
                new OptionFindResponse("카라멜드리즐", "1개")
        );

        List<TasteFindResponse> tasteFindResponses = List.of(
                new TasteFindResponse(1L, "단맛", "#000000"),
                new TasteFindResponse(2L, "매운맛", "#000000"),
                new TasteFindResponse(3L, "쓴맛", "#000000")
        );

        List<MenuFindResponse> menuFindResponses = List.of(
                new MenuFindResponse(1L, franchise, "menu1.com",
                        "슈렉 프라푸치노", "그린티 프라푸치노",
                        author, "맛있습니다.", 0, 0, 8000, optionFindResponses, tasteFindResponses,
                        LocalDateTime.now(), LocalDateTime.now()),
                new MenuFindResponse(2L, franchise, "menu1.com",
                        "몬스터 프라푸치노", "자바칩 프라푸치노",
                        author, "맛있습니다.", 0, 0, 8000, optionFindResponses, tasteFindResponses,
                        LocalDateTime.now(), LocalDateTime.now())
        );

        SearchMenuRequest request = new SearchMenuRequest(0, 1, "프라푸치노", "recent", 1L,
                List.of(1L, 2L, 3L));

        given(menuService.searchAllMenu(request)).willReturn(
                new SearchMenuResponse(menuFindResponses));

        mockMvc.perform(get("/menu")
                        .accept(MediaType.APPLICATION_JSON_VALUE)
                        .param("keyword", request.keyword())
                        .param("sort", request.sort())
                        .param("tasteIdList", "1,2,3")
                        .param("franchiseId", "1")
                        .param("offset", String.valueOf(request.offset()))
                        .param("limit", String.valueOf(request.limit())))
                .andExpect(status().isOk())
                .andDo(print())
                .andDo(document("search-menu",
                        requestHeaders(
                                headerWithName(HttpHeaders.ACCEPT).description(
                                        MediaType.APPLICATION_JSON_VALUE)
                        ),
                        requestParameters(
                                parameterWithName("keyword").description("검색어"),
                                parameterWithName("sort").description(
                                        "정렬 조건(recent, like, comment)"),
                                parameterWithName("tasteIdList").description("맛 ID 조건(예: 1,2,3)"),
                                parameterWithName("offset").description("페이징 offset"),
                                parameterWithName("limit").description("패이징 limit"),
                                parameterWithName("franchiseId").description(
                                        "프랜차이즈 ID(null 전달시 전체 프렌차이즈 조회)")
                        ),
                        responseHeaders(
                                headerWithName(HttpHeaders.CONTENT_TYPE).description(
                                        MediaType.APPLICATION_JSON_VALUE)
                        ),
                        responseFields(
                                fieldWithPath("menu[]").type(JsonFieldType.ARRAY)
                                        .description("메뉴 배열"),
                                fieldWithPath("menu[].id").type(JsonFieldType.NUMBER)
                                        .description("메뉴 ID"),
                                fieldWithPath("menu[].image").type(JsonFieldType.STRING)
                                        .description("이미지 URL"),
                                fieldWithPath("menu[].title").type(JsonFieldType.STRING)
                                        .description("메뉴명"),
                                fieldWithPath("menu[].originalTitle").type(JsonFieldType.STRING)
                                        .description("기존 메뉴명"),
                                fieldWithPath("menu[].content").type(JsonFieldType.STRING)
                                        .description("메뉴 설명"),
                                fieldWithPath("menu[].likes").type(JsonFieldType.NUMBER)
                                        .description("메뉴 좋아요 개수"),
                                fieldWithPath("menu[].comments").type(JsonFieldType.NUMBER)
                                        .description("메뉴 댓글 개수"),
                                fieldWithPath("menu[].expectedPrice").type(JsonFieldType.NUMBER)
                                        .description("메뉴 가격"),
                                fieldWithPath("menu[].createdAt").type(JsonFieldType.STRING)
                                        .description("메뉴 생성 일시"),
                                fieldWithPath("menu[].updatedAt").type(JsonFieldType.STRING)
                                        .description("최종 메뉴 수정 일시"),
                                fieldWithPath("menu[].franchise").type(JsonFieldType.OBJECT)
                                        .description("프렌차이즈 정보"),
                                fieldWithPath("menu[].franchise.id").type(JsonFieldType.NUMBER)
                                        .description("프렌차이즈 ID"),
                                fieldWithPath("menu[].franchise.image").type(JsonFieldType.STRING)
                                        .description("프렌차이즈 이미지 URL"),
                                fieldWithPath("menu[].franchise.name").type(JsonFieldType.STRING)
                                        .description("프렌차이즈명"),
                                fieldWithPath("menu[].author").type(JsonFieldType.OBJECT)
                                        .description("회원 정보"),
                                fieldWithPath("menu[].author.id").type(JsonFieldType.NUMBER)
                                        .description("회원 ID"),
                                fieldWithPath("menu[].author.image").type(JsonFieldType.STRING)
                                        .description("회원 이미지 URL"),
                                fieldWithPath("menu[].author.nickName").type(JsonFieldType.STRING)
                                        .description("회원 닉네임"),
                                fieldWithPath("menu[].author.email").type(JsonFieldType.STRING)
                                        .description("회원 이메일"),
                                fieldWithPath("menu[].author.snsAccount").type(JsonFieldType.STRING)
                                        .description("회원 SNS 계정"),
                                fieldWithPath("menu[].author.createdAt").type(JsonFieldType.STRING)
                                        .description("회원 생성 일시"),
                                fieldWithPath("menu[].author.menuCount").type(JsonFieldType.NUMBER)
                                        .description("해당 회원이 생성한 메뉴 개수"),
                                fieldWithPath("menu[].optionList[]").type(JsonFieldType.ARRAY)
                                        .description("메뉴 옵션 배열"),
                                fieldWithPath("menu[].optionList[].name").type(
                                        JsonFieldType.STRING).description("메뉴 옵션명"),
                                fieldWithPath("menu[].optionList[].description").type(
                                        JsonFieldType.STRING).description("메뉴 옵션 설명"),
                                fieldWithPath("menu[].tasteList[]").type(JsonFieldType.ARRAY)
                                        .description("메뉴 맛 배열"),
                                fieldWithPath("menu[].tasteList[].id").type(JsonFieldType.NUMBER)
                                        .description("맛 ID"),
                                fieldWithPath("menu[].tasteList[].name").type(JsonFieldType.STRING)
                                        .description("맛 이름"),
                                fieldWithPath("menu[].tasteList[].color").type(JsonFieldType.STRING)
                                        .description("맛 색상 코드")
                        )
                ));
    }
}
