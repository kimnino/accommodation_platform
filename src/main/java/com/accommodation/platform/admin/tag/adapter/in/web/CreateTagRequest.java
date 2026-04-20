package com.accommodation.platform.admin.tag.adapter.in.web;

import jakarta.validation.constraints.NotBlank;

import com.accommodation.platform.admin.tag.application.port.in.AdminManageTagUseCase.CreateTagCommand;

public record CreateTagRequest(
        @NotBlank(message = "태그명은 필수입니다.") String name,
        int displayOrder
) {

    public CreateTagCommand toCommand() {

        return new CreateTagCommand(name, displayOrder);
    }
}
