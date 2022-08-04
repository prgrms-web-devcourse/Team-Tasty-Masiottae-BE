package com.tasty.masiottae.menu.controller;

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
import static org.springframework.restdocs.request.RequestDocumentation.requestParts;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.tasty.masiottae.account.dto.AccountFindResponse;
import com.tasty.masiottae.config.RestDocsConfiguration;
import com.tasty.masiottae.franchise.dto.FranchiseFindResponse;
import com.tasty.masiottae.menu.dto.MenuFindResponse;
import com.tasty.masiottae.menu.dto.MenuSaveResponse;
import com.tasty.masiottae.menu.dto.MenuSaveUpdateRequest;
import com.tasty.masiottae.menu.dto.TasteFindResponse;
import com.tasty.masiottae.menu.service.MenuService;
import com.tasty.masiottae.option.dto.OptionFindResponse;
import com.tasty.masiottae.option.dto.OptionSaveRequest;
import com.tasty.masiottae.security.config.SecurityConfig;
import com.tasty.masiottae.security.filter.JwtAuthenticationFilter;
import com.tasty.masiottae.security.filter.JwtAuthorizationFilter;
import java.time.LocalDateTime;
import java.util.List;
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
@WithMockUser
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
                10), "내용", 100, 5000,
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
                        fieldWithPath("franchise.logoUrl").type(JsonFieldType.STRING)
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
                        fieldWithPath("expectedPrice").type(JsonFieldType.NUMBER)
                            .description("예상가격"),
                        fieldWithPath("optionList[].optionName").type(JsonFieldType.STRING)
                            .description("옵션명"),
                        fieldWithPath("optionList[].optionDescription").type(JsonFieldType.STRING)
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

        MenuSaveUpdateRequest menuSaveUpdateRequest = new MenuSaveUpdateRequest(1L, 1L, "슈렉 프라푸치노",
            "맛있습니다.",
            "그린티 프라푸치노", 10000, optionSaveRequests, tasteIds);

        MockMultipartFile data = new MockMultipartFile("data", "", "application/json",
            objectMapper.writeValueAsBytes(menuSaveUpdateRequest));

        MockMultipartFile imageFile = new MockMultipartFile("image", "image.png", "image/png",
            "sample image".getBytes());

        given(menuService.createMenu(menuSaveUpdateRequest, imageFile)).willReturn(
            new MenuSaveResponse(1L));

        // When // Then
        mockMvc.perform(multipart("/menu").file(data).file(imageFile)
                .contentType(MediaType.MULTIPART_FORM_DATA_VALUE)
                .accept(MediaType.APPLICATION_JSON_VALUE).with(csrf().asHeader())).andDo(print())
            .andExpect(status().isCreated()).andDo(document("create-menu", requestHeaders(
                    headerWithName(HttpHeaders.CONTENT_TYPE).description(
                        MediaType.MULTIPART_FORM_DATA_VALUE),
                    headerWithName(HttpHeaders.ACCEPT).description(MediaType.APPLICATION_JSON_VALUE)),
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
                        .description("맛 ID 목록")), responseHeaders(
                    headerWithName(HttpHeaders.CONTENT_TYPE).description(
                        MediaType.APPLICATION_JSON_VALUE)), responseFields(
                    fieldWithPath("menuId").type(JsonFieldType.NUMBER)
                        .description("생성된 메뉴 ID"))));

        then(menuService).should().createMenu(menuSaveUpdateRequest, imageFile);
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

        MenuSaveUpdateRequest menuSaveUpdateRequest = new MenuSaveUpdateRequest(1L, 1L, "슈렉 프라푸치노",
            "맛있습니다.",
            "그린티 프라푸치노", 10000, optionSaveRequests,
            tasteIds);

        MockMultipartFile data = new MockMultipartFile("data", "", "application/json",
            objectMapper.writeValueAsBytes(menuSaveUpdateRequest));

        MockMultipartFile imageFile = new MockMultipartFile("image", "image.png",
            "image/png", "sample image".getBytes());

        given(menuService.createMenu(menuSaveUpdateRequest, imageFile))
            .willReturn(new MenuSaveResponse(1L));

        mockMvc.perform(multipart("/menu/{menuId}", 1)
                .file(data)
                .file(imageFile)
                .contentType(MediaType.MULTIPART_FORM_DATA_VALUE)
                .accept(MediaType.APPLICATION_JSON_VALUE)
                .with(csrf().asHeader())
            ).andExpect(status().isOk())
            .andDo(print())
            .andDo(document("update-menu",
                requestHeaders(
                    headerWithName(HttpHeaders.CONTENT_TYPE).description(
                        MediaType.MULTIPART_FORM_DATA_VALUE),
                    headerWithName(HttpHeaders.ACCEPT).description(
                        MediaType.APPLICATION_JSON_VALUE)
                ),
                pathParameters(
                    parameterWithName("menuId").description("수정할 메뉴 ID")
                ),
                requestParts(
                    partWithName("image").description("메뉴 이미지"),
                    partWithName("data").description("메뉴 정보")

                ),
                requestPartFields("data",
                    fieldWithPath("userId").type(JsonFieldType.NUMBER)
                        .description("회원 ID"),
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
                        .description("맛 ID 목록")
                )
            ));
    }

    @Test
    @DisplayName("메뉴를 삭제한다.")
    public void deleteMenuTest() throws Exception {
        Long menuId = 1L;
        mockMvc.perform(delete("/menu/{menuId}", menuId)
                .with(csrf().asHeader())
            ).andExpect(status().isOk())
            .andDo(print())
            .andDo(document("delete-menu",
                pathParameters(
                    parameterWithName("menuId").description("삭제할 메뉴 ID")
                )
            ));

        then(menuService).should().delete(menuId);
    }
}
