package com.tasty.masiottae.menu.controller;

import static org.mockito.BDDMockito.given;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.get;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.pathParameters;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.tasty.masiottae.menu.dto.MenuFindResponse;
import com.tasty.masiottae.menu.dto.MenuTasteFindResponse;
import com.tasty.masiottae.menu.service.MenuService;
import com.tasty.masiottae.option.dto.OptionFindResponse;
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
import org.springframework.restdocs.payload.JsonFieldType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(controllers = MenuController.class, excludeFilters = {
        @ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, classes = SecurityConfig.class),
        @ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, classes = JwtAuthorizationFilter.class),
        @ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, classes = JwtAuthenticationFilter.class)})
@AutoConfigureRestDocs
class MenuControllerTest {

    @Autowired
    ObjectMapper objectMapper;
    @MockBean
    MenuService menuService;
    @Autowired
    private MockMvc mockMvc;

    @Test
    @DisplayName("메뉴 단건 조회")
    @WithMockUser(roles = "ADMIN")
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

}
