package com.tasty.masiottae.common.aws;

import java.util.UUID;

public abstract class ImageFileNameResolver {

    public static String getFullFileName(String fileName, ImageDirectory imageDirectory) {
        String extension = getExtension(fileName);
        return imageDirectory.getS3Directory() + UUID.randomUUID() + extension;
    }

    private static String getExtension(String fileName) {
        int extensionIndex = fileName.lastIndexOf(".");
        if (extensionIndex == -1) {
            return "";
        }
        return fileName.substring(extensionIndex);
    }
}
