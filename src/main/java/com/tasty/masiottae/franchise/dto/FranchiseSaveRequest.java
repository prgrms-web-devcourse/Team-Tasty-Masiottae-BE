package com.tasty.masiottae.franchise.dto;

import javax.validation.constraints.NotBlank;
import org.springframework.web.multipart.MultipartFile;

public record FranchiseSaveRequest(@NotBlank(message = "프랜차이즈명을 입력해주세요.") String name,
                                   MultipartFile multipartFile) {

}
