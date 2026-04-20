package com.accommodation.platform.core.accommodation.domain.enums;

import java.util.Arrays;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum SupportedLocale {
    KO("ko", "한국어"),
    EN("en", "English"),
    JA("ja", "日本語");

    private final String code;
    private final String displayName;

    public static boolean isValid(String code) {

        return Arrays.stream(values())
                .anyMatch(locale -> locale.code.equals(code.toLowerCase()));
    }
}
