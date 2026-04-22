package com.accommodation.platform.admin.tag.adapter.in.web;

import com.accommodation.platform.admin.tag.application.port.in.AdminManageTagGroupUseCase.UpdateTagGroupCommand;
import com.accommodation.platform.core.accommodation.domain.enums.AccommodationType;

public record UpdateTagGroupRequest(
        String name,
        Integer displayOrder,
        AccommodationType accommodationType
) {

    public UpdateTagGroupCommand toCommand() {

        return new UpdateTagGroupCommand(name, displayOrder, accommodationType);
    }
}
