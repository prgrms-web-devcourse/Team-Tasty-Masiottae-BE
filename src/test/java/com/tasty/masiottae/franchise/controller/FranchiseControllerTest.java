package com.tasty.masiottae.franchise.controller;

import static org.mockito.BDDMockito.given;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.http.MediaType.MULTIPART_FORM_DATA_VALUE;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.get;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.multipart;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.tasty.masiottae.franchise.domain.Franchise;
import com.tasty.masiottae.franchise.dto.FranchiseFindResponse;
import com.tasty.masiottae.franchise.dto.FranchiseSaveRequest;
import com.tasty.masiottae.franchise.dto.FranchiseSaveResponse;
import com.tasty.masiottae.franchise.service.FranchiseService;
import com.tasty.masiottae.security.config.SecurityConfig;
import com.tasty.masiottae.security.filter.JwtAuthenticationFilter;
import com.tasty.masiottae.security.filter.JwtAuthorizationFilter;
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
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.restdocs.payload.JsonFieldType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(controllers = FranchiseController.class, excludeFilters = {
    @ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, classes = SecurityConfig.class),
    @ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, classes = JwtAuthorizationFilter.class),
    @ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, classes = JwtAuthenticationFilter.class)})
@AutoConfigureRestDocs
class FranchiseControllerTest {

    @Autowired
    ObjectMapper objectMapper;
    @MockBean
    private FranchiseService franchiseService;
    @Autowired
    private MockMvc mockMvc;

    @Test
    @DisplayName("프랜차이즈 생성")
    @WithMockUser(roles = "ADMIN")
    void testSaveFranchise() throws Exception {
        // given
        MockMultipartFile multipartFile = new MockMultipartFile("file", "image.png", "img/png",
            "Hello".getBytes());
        FranchiseSaveRequest request = new FranchiseSaveRequest("starbucks", multipartFile);

        FranchiseSaveResponse response = franchiseService.createFranchise(request);
        given(franchiseService.createFranchise(request)).willReturn(response);

        // expected
        mockMvc.perform(multipart("/franchises").file(multipartFile)
                .param("name", request.name())
                .with(csrf().asHeader()).contentType(MULTIPART_FORM_DATA_VALUE))
            .andExpect(status().isCreated());
    }

    @Test
    @DisplayName("프랜차이즈 생성 실패 : 프랜차이즈 이름 미입력")
    @WithMockUser(roles = "ADMIN")
    void testSaveFranchiseFailedByNameEmpty() throws Exception {
        // given
        MockMultipartFile multipartFile = new MockMultipartFile("file", "image.png", "img/png",
            "Hello".getBytes());
        FranchiseSaveRequest request = new FranchiseSaveRequest("", multipartFile);

        FranchiseSaveResponse response = franchiseService.createFranchise(request);
        given(franchiseService.createFranchise(request)).willReturn(response);

        // expected
        mockMvc.perform(multipart("/franchises").file(multipartFile)
                .param("name", request.name())
                .with(csrf().asHeader()).contentType(MULTIPART_FORM_DATA_VALUE))
            .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("프랜차이즈 전체조회")
    @WithMockUser(roles = "ADMIN")
    void testGetAllFranchise() throws Exception {
        // given
        List<Franchise> requestFranchise = IntStream.range(1, 10)
            .mapToObj(i ->
                Franchise.createFranchise("franchise" + i, "logo" + i))
            .toList();

        List<FranchiseFindResponse> response = requestFranchise.stream()
            .map(franchise -> new FranchiseFindResponse((long) ((Math.random() * 10) + 1),
                franchise.getLogoUrl(), franchise.getName()))
            .collect(Collectors.toList());
        given(franchiseService.findAllFranchise()).willReturn(response);

        // expected
        mockMvc.perform(get("/franchises")
                .contentType(APPLICATION_JSON))
            .andExpect(status().isOk())
            .andDo(document("franchise-get-all",
                responseFields(
                    fieldWithPath("[].id").type(JsonFieldType.NUMBER)
                        .description("프랜차이즈 ID"),
                    fieldWithPath("[].image").type(JsonFieldType.STRING)
                        .description("프랜차이즈 로고"),
                    fieldWithPath("[].name").type(JsonFieldType.STRING)
                        .description("프랜차이즈 이름")
                )));
    }
}
