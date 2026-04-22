package com.accommodation.platform.common.security;

import com.accommodation.platform.common.response.ApiResponse;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.context.annotation.Profile;
import org.springframework.web.bind.annotation.RestController;

/**
 * Demo용 토큰 발급 엔드포인트.
 * 인증 없이 역할별 JWT 토큰을 발급한다.
 * 운영 환경에서는 반드시 제거하거나 실제 인증 로직으로 교체해야 한다.
 */
@Slf4j
@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
@Profile("!test")
public class AuthController {

    private final JwtTokenProvider jwtTokenProvider;

    @PostMapping("/token")
    public ResponseEntity<ApiResponse<TokenResponse>> issueToken(@Valid @RequestBody TokenRequest request) {

        log.info("Token issuance requested for role: {}", request.role());

        String token = jwtTokenProvider.generateToken(
                request.role(),
                request.partnerId(),
                request.memberId()
        );

        TokenResponse tokenResponse = new TokenResponse(
                token,
                request.role(),
                jwtTokenProvider.getExpirationSeconds()
        );

        return ResponseEntity.ok(ApiResponse.success(tokenResponse));
    }

    public record TokenRequest(
            @NotBlank(message = "역할(role)은 필수입니다.") String role,
            Long partnerId,
            Long memberId
    ) {
    }

    public record TokenResponse(
            String token,
            String role,
            long expiresIn
    ) {
    }
}
