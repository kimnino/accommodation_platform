package com.accommodation.platform.common.security;

import com.accommodation.platform.common.response.ApiResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.context.annotation.Profile;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
@Profile("!test")
public class AuthController {

    private final JwtTokenProvider jwtTokenProvider;

    @PostMapping("/token")
    public ResponseEntity<ApiResponse<TokenResponse>> issueToken(@RequestBody TokenRequest request) {
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
            String role,
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
