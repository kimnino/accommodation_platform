package com.accommodation.platform.admin.tag.adapter.in.web;

import com.accommodation.platform.admin.tag.application.port.in.AdminManageTagGroupUseCase.UpdateTagGroupCommand;

public record UpdateTagGroupRequest(
        String name,
        int displayOrder,
        String accommodationType
) {

    public UpdateTagGroupCommand toCommand() {

        return new UpdateTagGroupCommand(name, displayOrder, accommodationType);
    }
}
