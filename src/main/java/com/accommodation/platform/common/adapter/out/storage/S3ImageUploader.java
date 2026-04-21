package com.accommodation.platform.common.adapter.out.storage;

import com.accommodation.platform.common.application.port.out.ImageUploader;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

@Component
@Profile("prod")
public class S3ImageUploader implements ImageUploader {

    /*
     * ── S3 이미지 업로드 설계 ──────────────────────────────────────────────────
     *
     * [의존성]
     *   implementation 'software.amazon.awssdk:s3:2.x.x'
     *
     * [설정] application-prod.yaml
     *   cloud.aws.s3.bucket: stay-platform-images
     *   cloud.aws.s3.region: ap-northeast-2
     *   cloud.aws.credentials: (IAM Role 또는 AccessKey/SecretKey)
     *
     * [업로드 흐름]
     *   1. S3Client.putObject(PutObjectRequest) 호출
     *      - key: "{directory}/{yyyyMMddHHmmss}_{uuid}.{ext}"  (예: accommodation/exterior/20260420102838_abc.jpg)
     *      - ContentType: 파일 확장자에서 추론 (image/jpeg, image/png, image/webp)
     *      - ACL: private (CloudFront 경유 서빙)
     *   2. 반환값: 상대경로 "/accommodation/exterior/20260420102838_abc.jpg"
     *      - base URL은 애플리케이션 설정에서 주입 (CloudFront 도메인 교체 시 코드 변경 불필요)
     *
     * [삭제 흐름]
     *   S3Client.deleteObject(DeleteObjectRequest) — relativePath를 key로 사용
     *
     * [CloudFront 연동]
     *   - S3 버킷은 퍼블릭 접근 차단, CloudFront OAC(Origin Access Control)로 서빙
     *   - 이미지 URL = "https://{cloudfront-domain}{relativePath}"
     *   - base URL은 application.yaml의 app.image.base-url 로 외부화
     *
     * [용량/형식 제한]
     *   - 최대 10MB, 허용 형식: jpg/jpeg/png/webp
     *   - 업로드 전 Controller 또는 UseCase에서 검증 (S3ImageUploader 책임 아님)
     * ──────────────────────────────────────────────────────────────────────────
     */

    // @Value("${cloud.aws.s3.bucket}") private String bucket;
    // @Value("${app.image.base-url}") private String baseUrl;
    // private final S3Client s3Client;

    @Override
    public String upload(String directory, String originalFilename, byte[] content) {
        // TODO: S3Client.putObject 구현
        throw new UnsupportedOperationException("S3 업로드 미구현 — 설계 주석 참조");
    }

    @Override
    public void delete(String relativePath) {
        // TODO: S3Client.deleteObject 구현
        throw new UnsupportedOperationException("S3 삭제 미구현 — 설계 주석 참조");
    }
}
