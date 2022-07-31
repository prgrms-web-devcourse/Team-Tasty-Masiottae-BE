package com.tasty.masiottae.menu.controller;

import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.restdocs.headers.HeaderDocumentation.headerWithName;
import static org.springframework.restdocs.headers.HeaderDocumentation.requestHeaders;
import static org.springframework.restdocs.headers.HeaderDocumentation.responseHeaders;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
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
import com.tasty.masiottae.config.RestDocsConfiguration;
import com.tasty.masiottae.menu.dto.MenuFindResponse;
import com.tasty.masiottae.menu.dto.MenuSaveRequest;
import com.tasty.masiottae.menu.dto.MenuSaveResponse;
import com.tasty.masiottae.menu.dto.MenuTasteFindResponse;
import com.tasty.masiottae.menu.service.MenuService;
import com.tasty.masiottae.option.dto.OptionFindResponse;
import com.tasty.masiottae.option.dto.OptionSaveRequest;
import com.tasty.masiottae.security.config.SecurityConfig;
import com.tasty.masiottae.security.filter.JwtAuthenticationFilter;
import com.tasty.masiottae.security.filter.JwtAuthorizationFilter;
import java.util.List;
import java.util.Set;
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
    @ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, classes = JwtAuthenticationFilter.class)})
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
        MenuFindResponse menuFindResponse = new MenuFindResponse(
            1L, "starbucks", "img.png", "커스텀제목",
            "원래제목", "user", "내용", 100,
            5000,
            List.of(new OptionFindResponse("옵션명1", "설명1"), new OptionFindResponse("옵션명", "설명")),
            Set.of(new MenuTasteFindResponse(1L, 1L), new MenuTasteFindResponse(1L, 2L)));

        given(menuService.findOneMenu(1L)).willReturn(menuFindResponse);

        // expected
        mockMvc.perform(get("/menu/{menuId}", menuId)
                .contentType(APPLICATION_JSON))
            .andExpect(status().isOk())
            .andDo(print())
            .andDo(document("menu-findOne",
                pathParameters(
                    parameterWithName("menuId").description("메뉴 Id")),
                responseFields(
                    fieldWithPath("id").type(JsonFieldType.NUMBER).description("메뉴 ID"),
                    fieldWithPath("franchise").type(JsonFieldType.STRING)
                        .description("프랜차이즈명"),
                    fieldWithPath("image").type(JsonFieldType.STRING)
                        .description("이미지경로"),
                    fieldWithPath("title").type(JsonFieldType.STRING)
                        .description("메뉴명"),
                    fieldWithPath("originalTitle").type(JsonFieldType.STRING)
                        .description("실제 메뉴명"),
                    fieldWithPath("author").type(JsonFieldType.STRING)
                        .description("유저명"),
                    fieldWithPath("content").type(JsonFieldType.STRING)
                        .description("설명"),
                    fieldWithPath("likes").type(JsonFieldType.NUMBER)
                        .description("좋아요 수"),
                    fieldWithPath("expectedPrice").type(JsonFieldType.NUMBER)
                        .description("예상가격"),
                    fieldWithPath("options[].optionName").type(JsonFieldType.STRING)
                        .description("옵션명"),
                    fieldWithPath("options[].optionDescription").type(
                            JsonFieldType.STRING)
                        .description("옵션 설명"),
                    fieldWithPath("menuTaste[].menuId").type(JsonFieldType.NUMBER)
                        .description("메뉴 ID"),
                    fieldWithPath("menuTaste[].tasteId").type(JsonFieldType.NUMBER)
                        .description("맛 ID")
                )));
    }

    @Test
    @DisplayName("메뉴를 생성한다.")
    void createMenuTest() throws Exception {
        // Given
        List<OptionSaveRequest> optionSaveRequests = List.of(
                new OptionSaveRequest("에스프레소 샷", "1샷"),
                new OptionSaveRequest("간 자바칩", "1개"),
                new OptionSaveRequest("통 자바칩", "1개"),
                new OptionSaveRequest("카라멜드리즐", "1개")
        );

        List<Long> tasteIds = List.of(1L, 2L, 3L);

        MenuSaveRequest menuSaveRequest = new MenuSaveRequest(1L, 1L, "슈렉 프라푸치노", "맛있습니다.",
                "그린티 프라푸치노", 10000, optionSaveRequests,
                tasteIds);

        MockMultipartFile data = new MockMultipartFile("data", "", "application/json",
                objectMapper.writeValueAsBytes(menuSaveRequest));

        MockMultipartFile imageFile = new MockMultipartFile("image", "image.png",
                "image/png", "sample image".getBytes());

        given(menuService.createMenu(menuSaveRequest, imageFile))
                .willReturn(new MenuSaveResponse(1L));

        // When // Then
        mockMvc.perform(multipart("/menu")
                        .file(data)
                        .file(imageFile)
                        .contentType(MediaType.MULTIPART_FORM_DATA_VALUE)
                        .accept(MediaType.APPLICATION_JSON_VALUE)
                        .with(csrf().asHeader()))
                .andDo(print())
                .andExpect(status().isCreated())
                .andDo(document("create-menu",
                        requestHeaders(
                                headerWithName(HttpHeaders.CONTENT_TYPE).description(
                                        MediaType.MULTIPART_FORM_DATA_VALUE),
                                headerWithName(HttpHeaders.ACCEPT).description(
                                        MediaType.APPLICATION_JSON_VALUE)
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
                        ),
                        responseHeaders(
                                headerWithName(HttpHeaders.CONTENT_TYPE).description(
                                        MediaType.APPLICATION_JSON_VALUE)
                        ),
                        responseFields(
                                fieldWithPath("menuId").type(JsonFieldType.NUMBER)
                                        .description("생성된 메뉴 ID")
                        )
                ));

        then(menuService).should().createMenu(menuSaveRequest, imageFile);
    }
}
