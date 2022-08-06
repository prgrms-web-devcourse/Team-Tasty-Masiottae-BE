package com.tasty.masiottae.likemenu.controller;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.restdocs.headers.HeaderDocumentation.headerWithName;
import static org.springframework.restdocs.headers.HeaderDocumentation.requestHeaders;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.get;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.post;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.pathParameters;
import static org.springframework.restdocs.request.RequestDocumentation.requestParameters;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.tasty.masiottae.account.dto.AccountFindResponse;
import com.tasty.masiottae.config.RestDocsConfiguration;
import com.tasty.masiottae.config.WithMockAccount;
import com.tasty.masiottae.franchise.dto.FranchiseFindResponse;
import com.tasty.masiottae.likemenu.service.LikeMenuService;
import com.tasty.masiottae.menu.dto.MenuFindResponse;
import com.tasty.masiottae.menu.dto.TasteFindResponse;
import com.tasty.masiottae.option.dto.OptionFindResponse;
import com.tasty.masiottae.security.config.SecurityConfig;
import com.tasty.masiottae.security.filter.JwtAuthenticationFilter;
import com.tasty.masiottae.security.filter.JwtAuthorizationFilter;
import com.tasty.masiottae.security.jwt.JwtToken;
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
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.restdocs.payload.JsonFieldType;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(controllers = LikeMenuController.class, excludeFilters = {
    @ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, classes = SecurityConfig.class),
    @ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, classes = JwtAuthorizationFilter.class),
    @ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, classes = JwtAuthenticationFilter.class)}
)
@AutoConfigureRestDocs
@WithMockAccount
@Import(RestDocsConfiguration.class)
class LikeMenuControllerTest {

    @Autowired
    ObjectMapper objectMapper;
    @MockBean
    LikeMenuService likeMenuService;
    @Autowired
    private MockMvc mockMvc;


    @Test
    @DisplayName("좋아요 변경")
    void changeLike() throws Exception {
        Long menuId = 1L;
        JwtToken token = new JwtToken(
                "bearer eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9"
                        + ".eyJzdWIiOiJ0ZXN0MjBAbmF2ZXIuY29tIiwicm9sZXMiO"
                        + "lsiUk9MRV9BQ0NPVU5UIl0sImV4cCI6MTY1OTQzMTI5Nn0."
                        + "-cEvT2fbrz5mMpa_3Z0x4TASOEQFgk1-sT0lWU3IPR4",
                new Date());
        mockMvc.perform(post("/menu/{menuId}/like", menuId)
                        .header("Authorization", token)
                        .accept(MediaType.APPLICATION_JSON)
                        .with(csrf().asHeader())
                ).andExpect(status().isOk())
                .andDo(print())
                .andDo(document("likeMenu-update",
                        pathParameters(
                                parameterWithName("menuId").description("좋아요한 메뉴 id")
                        ),
                        requestHeaders(
                                headerWithName(HttpHeaders.ACCEPT).description(
                                        MediaType.APPLICATION_JSON),
                                headerWithName("Authorization").description("JWT Access 토큰")
                        )
                ));
    }

    @Test
    @DisplayName("좋아요한 메뉴 보기")
    void getMenuByAccountLike() throws Exception {

        MenuFindResponse menuFindResponse = new MenuFindResponse(1L,
                new FranchiseFindResponse(1L, "logo", "스타벅스"), "img.png", "커스텀제목", "원래제목",
                new AccountFindResponse(1L, "이미지 url", "닉네임", "유저이름", "이메일", LocalDateTime.now(),
                        10), "내용", 100, 5000,
                List.of(new OptionFindResponse("옵션명1", "설명1"), new OptionFindResponse("옵션명", "설명")),
                List.of(new TasteFindResponse(1L, "빨간맛", "빨간색"),
                        new TasteFindResponse(2L, "파란맛", "파란색")), LocalDateTime.now(),
                LocalDateTime.now());

        List<MenuFindResponse> menuFindResponseList = new ArrayList<>();
        menuFindResponseList.add(menuFindResponse);
        PageRequest pageRequest = PageRequest.of(0, 10);
        int start = (int) pageRequest.getOffset();
        int end = Math.min((start + pageRequest.getPageSize()), menuFindResponseList.size());
        Page<MenuFindResponse> pages =
                new PageImpl<>(menuFindResponseList.subList(start, end), pageRequest,
                        menuFindResponseList.size());
        when(likeMenuService.getPageLikeMenuByAccount(any(), any())).thenReturn(pages);

        JwtToken token = new JwtToken(
                "bearer eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9"
                        + ".eyJzdWIiOiJ0ZXN0MjBAbmF2ZXIuY29tIiwicm9sZXMiO"
                        + "lsiUk9MRV9BQ0NPVU5UIl0sImV4cCI6MTY1OTQzMTI5Nn0."
                        + "-cEvT2fbrz5mMpa_3Z0x4TASOEQFgk1-sT0lWU3IPR4",
                new Date());

        mockMvc.perform(get("/accounts/like")
                        .param("page", String.valueOf(0))
                        .param("size", String.valueOf(10)
                        ).header("Authorization", token)
                        .accept(MediaType.APPLICATION_JSON)
                ).andExpect(status().isOk())
                .andDo(print())
                .andDo(document("get-Menu-AccountLike(paging)",
                        requestHeaders(
                                headerWithName(HttpHeaders.ACCEPT).description(
                                        MediaType.APPLICATION_JSON),
                                headerWithName("Authorization").description("JWT Access 토큰")
                        ), requestParameters(
                                parameterWithName("page").description("요청 페이지"),
                                parameterWithName("size").description("페이지 당 사이즈")
                        ),
                        responseFields(
                                fieldWithPath("content[].id").type(JsonFieldType.NUMBER)
                                        .description("메뉴 ID"),
                                fieldWithPath("content[].franchise.id").type(JsonFieldType.NUMBER)
                                        .description("프랜차이즈 id"),
                                fieldWithPath("content[].franchise.logoUrl").type(
                                                JsonFieldType.STRING)
                                        .description("프랜차이즈 로고"),
                                fieldWithPath("content[].franchise.name").type(JsonFieldType.STRING)
                                        .description("프랜차이즈명"),
                                fieldWithPath("content[].image").type(JsonFieldType.STRING)
                                        .description("이미지경로"),
                                fieldWithPath("content[].title").type(JsonFieldType.STRING)
                                        .description("메뉴명"),
                                fieldWithPath("content[].originalTitle").type(JsonFieldType.STRING)
                                        .description("실제 메뉴명"),
                                fieldWithPath("content[].author.id").type(JsonFieldType.NUMBER)
                                        .description("유저 ID"),
                                fieldWithPath("content[].author.nickName").type(
                                                JsonFieldType.STRING)
                                        .description("닉네임"),
                                fieldWithPath("content[].author.image").type(JsonFieldType.STRING)
                                        .description("유저 프로필사진"),
                                fieldWithPath("content[].author.email").type(JsonFieldType.STRING)
                                        .description("유저 이메일"),
                                fieldWithPath("content[].author.createdAt").type(
                                                JsonFieldType.STRING)
                                        .description("생성일"),
                                fieldWithPath("content[].author.snsAccount").type(
                                                JsonFieldType.STRING)
                                        .description("유저 SNS 계정"),
                                fieldWithPath("content[].author.menuCount").type(
                                                JsonFieldType.NUMBER)
                                        .description("해당 유저의 생성메뉴 수"),
                                fieldWithPath("content[].content").type(JsonFieldType.STRING)
                                        .description("설명"),
                                fieldWithPath("content[].likes").type(JsonFieldType.NUMBER)
                                        .description("좋아요 수"),
                                fieldWithPath("content[].expectedPrice").type(JsonFieldType.NUMBER)
                                        .description("예상가격"),
                                fieldWithPath("content[].optionList[].optionName").type(
                                                JsonFieldType.STRING)
                                        .description("옵션명"),
                                fieldWithPath("content[].optionList[].optionDescription").type(
                                                JsonFieldType.STRING)
                                        .description("옵션 설명"),
                                fieldWithPath("content[].tasteList[].tasteId").type(
                                                JsonFieldType.NUMBER)
                                        .description("맛 ID"),
                                fieldWithPath("content[].tasteList[].tasteName").type(
                                                JsonFieldType.STRING)
                                        .description("맛 이름"),
                                fieldWithPath("content[].tasteList[].tasteColor").type(
                                                JsonFieldType.STRING)
                                        .description("맛 태그 컬러"),
                                fieldWithPath("content[].createdAt").type(JsonFieldType.STRING)
                                        .description("생성일"),
                                fieldWithPath("content[].updatedAt").type(JsonFieldType.STRING)
                                        .description("갱신일"),
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
                                        .description("페이징 속성 존재 여부")
                        )
                ));
    }
}