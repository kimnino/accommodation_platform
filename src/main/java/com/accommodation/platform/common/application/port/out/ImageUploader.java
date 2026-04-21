package com.accommodation.platform.common.application.port.out;

public interface ImageUploader {

    /** 이미지를 업로드하고 상대경로를 반환한다. 예: /accommodation/exterior/20260420102838.png */
    String upload(String directory, String originalFilename, byte[] content);

    void delete(String relativePath);
}
