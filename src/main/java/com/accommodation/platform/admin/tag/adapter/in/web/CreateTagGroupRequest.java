package com.accommodation.platform.admin.tag.adapter.in.web;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import com.accommodation.platform.admin.tag.application.port.in.AdminManageTagGroupUseCase.CreateTagGroupCommand;
import com.accommodation.platform.core.accommodation.domain.enums.AccommodationType;
import com.accommodation.platform.core.tag.domain.enums.TagTarget;

public record CreateTagGroupRequest(
        @NotBlank(message = "태그 그룹명은 필수입니다.") String name,
        int displayOrder,
        @NotNull(message = "태그 대상 유형은 필수입니다.") TagTarget targetType,
        AccommodationType accommodationType,
        Long supplierId
) {

    public CreateTagGroupCommand toCommand() {

        return new CreateTagGroupCommand(name, displayOrder, targetType, accommodationType, supplierId);
    }
}
