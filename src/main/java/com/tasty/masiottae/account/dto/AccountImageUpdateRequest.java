package com.tasty.masiottae.account.dto;

import org.springframework.web.multipart.MultipartFile;

public record AccountImageUpdateRequest(
        MultipartFile image
) {

}
