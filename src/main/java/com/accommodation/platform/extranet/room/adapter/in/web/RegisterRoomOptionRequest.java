package com.accommodation.platform.extranet.room.adapter.in.web;

import java.math.BigDecimal;
import java.util.List;

import jakarta.validation.constraints.NotBlank;

import com.accommodation.platform.extranet.room.application.port.in.ExtranetRegisterRoomOptionUseCase.RegisterRoomOptionCommand;
import com.accommodation.platform.extranet.room.application.port.in.ExtranetRegisterRoomOptionUseCase.TranslationCommand;

public record RegisterRoomOptionRequest(
        @NotBlank(message = "옵션명은 필수입니다.") String name,
        @NotBlank(message = "취소 정책은 필수입니다.") String cancellationPolicy,
        BigDecimal additionalPrice,
        List<TranslationRequest> translations
) {

    public RegisterRoomOptionCommand toCommand() {

        List<TranslationCommand> translationCommands = translations != null
                ? translations.stream()
                        .map(t -> new TranslationCommand(t.locale(), t.name()))
                        .toList()
                : List.of();

        return new RegisterRoomOptionCommand(name, cancellationPolicy, additionalPrice, translationCommands);
    }

    public record TranslationRequest(
            @NotBlank(message = "언어코드는 필수입니다.") String locale,
            @NotBlank(message = "번역 옵션명은 필수입니다.") String name
    ) {
    }
}
