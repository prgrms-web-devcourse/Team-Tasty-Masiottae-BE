package com.tasty.masiottae.common.aws;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public enum ImageDirectory {
    ACCOUNT("account/"), MENU("menu/"), FRANCHISE("franchise/");

    private final String s3Directory;
}
