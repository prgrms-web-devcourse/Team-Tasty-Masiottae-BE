package com.tasty.masiottae.menu.controller;

import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.springframework.restdocs.headers.HeaderDocumentation.headerWithName;
import static org.springframework.restdocs.headers.HeaderDocumentation.requestHeaders;
import static org.springframework.restdocs.headers.HeaderDocumentation.responseHeaders;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.get;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.post;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.requestFields;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.tasty.masiottae.config.RestDocsConfiguration;
import com.tasty.masiottae.menu.dto.TasteFindResponse;
import com.tasty.masiottae.menu.dto.TasteSaveRequest;
import com.tasty.masiottae.menu.dto.TasteSaveResponse;
import com.tasty.masiottae.menu.service.TasteService;
import com.tasty.masiottae.security.config.SecurityConfig;
import com.tasty.masiottae.security.filter.JwtAuthenticationFilter;
import com.tasty.masiottae.security.filter.JwtAuthorizationFilter;
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
import org.springframework.restdocs.payload.JsonFieldType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(controllers = TasteController.class, excludeFilters = {
        @ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, classes = SecurityConfig.class),
        @ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, classes = JwtAuthorizationFilter.class),
        @ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, classes = JwtAuthenticationFilter.class)})
@AutoConfigureRestDocs
@WithMockUser
@Import(RestDocsConfiguration.class)
class TasteControllerTest {

    @Autowired
    ObjectMapper objectMapper;
    @MockBean
    TasteService tasteService;
    @Autowired
    private MockMvc mockMvc;

    @Test
    @DisplayName("맛을 생성한다.")
    void createTasteTest() throws Exception {
        // Given
        Long tasteId = 1L;
        TasteSaveRequest request = new TasteSaveRequest("매운맛", "#ff0000");
        given(tasteService.createTaste(request)).willReturn(new TasteSaveResponse(tasteId));

        // When // Then
        mockMvc.perform(post("/tastes")
                        .accept(MediaType.APPLICATION_JSON_VALUE)
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(objectMapper.writeValueAsString(request))
                        .with(csrf().asHeader()))
                .andExpect(status().isCreated())
                .andDo(print())
                .andDo(document("create-taste",
                        requestHeaders(
                                headerWithName(HttpHeaders.CONTENT_TYPE).description(
                                        MediaType.APPLICATION_JSON_VALUE),
                                headerWithName(HttpHeaders.ACCEPT).description(
                                        MediaType.APPLICATION_JSON_VALUE)
                        ),
                        requestFields(
                               fieldWithPath("tasteName").type(JsonFieldType.STRING).description("맛 이름"),
                               fieldWithPath("tasteColor").type(JsonFieldType.STRING).description("맛 색상 코드(16진수, 알파벳은 소문자), 예: #ff0000")
                        ),
                        responseHeaders(
                                headerWithName(HttpHeaders.CONTENT_TYPE).description(MediaType.APPLICATION_JSON_VALUE)
                        ),
                        responseFields(
                                fieldWithPath("tasteId").type(JsonFieldType.NUMBER).description("생성된 맛 ID")
                        )
                ));

        then(tasteService).should().createTaste(request);
    }

    @Test
    @DisplayName("모든 맛을 조회한다.")
    void findAllTasteTest() throws Exception {
        // Given
        given(tasteService.findAllTaste()).willReturn(List.of(
                new TasteFindResponse(1L, "매운맛", "#FF0000"),
                new TasteFindResponse(2L, "단맛", "#FFFFF"),
                new TasteFindResponse(3L, "쓴맛", "#000000")
        ));

        // When // Then
        mockMvc.perform(get("/tastes")
                        .accept(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(status().isOk())
                .andDo(print())
                .andDo(document("find-all-taste",
                        requestHeaders(
                                headerWithName(HttpHeaders.ACCEPT).description(
                                        MediaType.APPLICATION_JSON_VALUE)
                        ),
                        responseHeaders(
                                headerWithName(HttpHeaders.CONTENT_TYPE).description(
                                        MediaType.APPLICATION_JSON_VALUE)
                        ),
                        responseFields(
                                fieldWithPath("[].tasteId").type(JsonFieldType.NUMBER).description("맛 ID"),
                                fieldWithPath("[].tasteName").type(JsonFieldType.STRING).description("맛 이름"),
                                fieldWithPath("[].tasteColor").type(JsonFieldType.STRING).description("맛 색생 코드(16진수)")
                        )
                ));
    }
}