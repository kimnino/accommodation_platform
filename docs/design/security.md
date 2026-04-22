# 인증/인가 설계 (Security Design)

## 1. 개요

Spring Security 기반 JWT 인증 + RBAC(Role-Based Access Control) 구조.  
채널별로 역할을 분리하고, URL 레벨에서 1차 접근 제어를 수행한다.

---

## 2. 역할 체계

| 역할 | 설명 | 접근 채널 |
|------|------|-----------|
| `ADMIN` | 플랫폼 운영자 | `/api/v1/admin/**` |
| `PARTNER` | 숙소 파트너 | `/api/v1/extranet/**` |
| `CUSTOMER` | 일반 고객 | `/api/v1/customer/**`, `/api/v1/reservations/**` |
| (없음) | 비인증 | `/api/v1/accommodations/**`, `/api/v1/auth/**` |

---

## 3. JWT 토큰 구조

```json
{
  "sub": "partner:1",
  "role": "PARTNER",
  "partnerId": 1,
  "iat": 1776843661,
  "exp": 1776930061
}
```

- `role` 클레임에 `ADMIN` / `PARTNER` / `CUSTOMER` 중 하나 저장
- `partnerId` — PARTNER 역할일 때만 포함
- `memberId` — CUSTOMER 역할일 때만 포함

### 토큰 발급 (Demo용)

```http
POST /api/v1/auth/token
Content-Type: application/json

{
  "role": "PARTNER",
  "partner_id": 1,
  "member_id": null
}
```

> 실제 서비스에서는 로그인(이메일+비밀번호) → 서버가 인증 후 토큰 발급 구조로 교체한다.

---

## 4. 인증 처리 흐름

```
요청 → JwtAuthenticationFilter
         ├── Authorization 헤더에서 Bearer 토큰 추출
         ├── 토큰 검증 (서명, 만료)
         ├── Claims에서 role, partnerId, memberId 파싱
         ├── SecurityContext에 Authentication 등록
         │   └── authority = "ROLE_" + role  (예: ROLE_PARTNER)
         └── 요청 래핑: X-Partner-Id / X-Member-Id 헤더 자동 주입
```

`JwtAuthenticationFilter`는 PARTNER 토큰이면 `X-Partner-Id`, CUSTOMER 토큰이면 `X-Member-Id` 헤더를 요청에 자동으로 추가한다. 컨트롤러에서 `@RequestHeader`로 별도 처리 없이 파트너/회원 식별이 가능하다.

---

## 5. URL 접근 제어 (SecurityConfig)

```java
.authorizeHttpRequests(auth -> auth
    .requestMatchers("/api/v1/auth/**").permitAll()
    .requestMatchers("/api/v1/accommodations/**").permitAll()
    .requestMatchers("/api/v1/customer/accommodations/**").permitAll()
    .requestMatchers("/api/v1/tags/**").permitAll()
    .requestMatchers("/api/v1/regions/**").permitAll()
    .requestMatchers("/api/v1/admin/**").hasRole("ADMIN")
    .requestMatchers("/api/v1/extranet/**").hasRole("PARTNER")
    .requestMatchers("/api/v1/customer/reservations/**").hasRole("CUSTOMER")
    .requestMatchers("/api/v1/reservations/**").hasRole("CUSTOMER")
    .requestMatchers("/api/v1/supplier/**").hasRole("ADMIN")
    .anyRequest().authenticated()
)
```

---

## 6. 복수 역할 허용 패턴

특정 URL에 여러 역할이 접근해야 하는 경우 `hasAnyRole()`을 사용한다.

```java
// 예: ADMIN도 extranet API 대리 편집 가능
.requestMatchers("/api/v1/extranet/**").hasAnyRole("PARTNER", "ADMIN")
```

### URL 레벨 vs 서비스 레벨 제어 구분

| 레벨 | 역할 | 예시 |
|------|------|------|
| URL (SecurityConfig) | 역할 보유 여부만 체크 | PARTNER 또는 ADMIN만 접근 허용 |
| Service | 데이터 소유권 체크 | PARTNER는 자신의 숙소만, ADMIN은 전체 가능 |

```java
// Service 레이어 소유권 검증 예시
public Accommodation getAccommodation(Long accommodationId, Long requestingPartnerId, String role) {
    Accommodation acc = loadAccommodationPort.findById(accommodationId)
            .orElseThrow(() -> new BusinessException(ErrorCode.ACCOMMODATION_NOT_FOUND));

    if ("PARTNER".equals(role) && !acc.getPartnerId().equals(requestingPartnerId)) {
        throw new BusinessException(ErrorCode.FORBIDDEN);
    }
    return acc;
}
```

---

## 7. 에러 응답

| 상황 | HTTP | 에러 코드 |
|------|------|-----------|
| 토큰 없음 / 만료 / 유효하지 않음 | 401 | `UNAUTHORIZED` |
| 인증은 됐지만 권한 없음 | 403 | `FORBIDDEN` |

```json
// 401
{ "status": "ERROR", "error": { "code": "UNAUTHORIZED", "message": "인증이 필요합니다." } }

// 403
{ "status": "ERROR", "error": { "code": "FORBIDDEN", "message": "접근 권한이 없습니다." } }
```

---

## 8. 향후 고려사항

- **실제 로그인 연동**: `MemberRepository` 기반 이메일+비밀번호 인증으로 교체
- **토큰 갱신(Refresh Token)**: Access Token 만료 시 Refresh Token으로 재발급
- **Redis 블랙리스트**: 로그아웃 시 토큰 무효화
- **메서드 레벨 보안**: `@PreAuthorize("hasRole('ADMIN')")` 활용 검토
