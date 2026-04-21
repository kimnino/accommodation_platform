package com.accommodation.platform.common.adapter.out.storage;

import com.accommodation.platform.common.application.port.out.ImageUploader;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Slf4j
@Component
@Profile("local")
public class LocalImageUploader implements ImageUploader {

    private static final DateTimeFormatter FILENAME_FORMATTER = DateTimeFormatter.ofPattern("yyyyMMddHHmmss");
    private static final String BASE_DIR = System.getProperty("user.home") + "/ota-images";

    @Override
    public String upload(String directory, String originalFilename, byte[] content) {
        String timestamp = LocalDateTime.now().format(FILENAME_FORMATTER);
        String filename = timestamp + "_" + originalFilename;
        Path targetDir = Paths.get(BASE_DIR, directory);
        Path targetFile = targetDir.resolve(filename);

        try {
            Files.createDirectories(targetDir);
            Files.write(targetFile, content);
        } catch (IOException e) {
            log.error("로컬 이미지 저장 실패: {}", targetFile, e);
            throw new UncheckedIOException(e);
        }

        String relativePath = "/" + directory + "/" + filename;
        log.info("이미지 저장 완료: {}", relativePath);
        return relativePath;
    }

    @Override
    public void delete(String relativePath) {
        Path filePath = Paths.get(BASE_DIR + relativePath);
        try {
            boolean deleted = Files.deleteIfExists(filePath);
            if (deleted) {
                log.info("이미지 삭제 완료: {}", relativePath);
            } else {
                log.warn("삭제할 이미지가 존재하지 않음: {}", relativePath);
            }
        } catch (IOException e) {
            log.error("로컬 이미지 삭제 실패: {}", relativePath, e);
            throw new UncheckedIOException(e);
        }
    }
}
