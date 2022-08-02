package com.tasty.masiottae.account.dto;

import org.springframework.web.multipart.MultipartFile;

public record AccountCreateRequest(
        String email,
        String password,
        String nickName,
//        MultipartFile image,
        String snsAccount) {

}
