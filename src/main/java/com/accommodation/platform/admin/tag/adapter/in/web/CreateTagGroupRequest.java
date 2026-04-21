package com.accommodation.platform.admin.tag.adapter.in.web;

import jakarta.validation.constraints.NotBlank;

import com.accommodation.platform.admin.tag.application.port.in.AdminManageTagGroupUseCase.CreateTagGroupCommand;

public record CreateTagGroupRequest(
        @NotBlank(message = "태그 그룹명은 필수입니다.") String name,
        int displayOrder,
        @NotBlank(message = "태그 대상 유형은 필수입니다.") String targetType,
        String accommodationType,
        Long supplierId
) {

    public CreateTagGroupCommand toCommand() {

        return new CreateTagGroupCommand(name, displayOrder, targetType, accommodationType, supplierId);
    }
}
