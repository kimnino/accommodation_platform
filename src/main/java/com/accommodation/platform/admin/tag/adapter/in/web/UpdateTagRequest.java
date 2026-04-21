package com.accommodation.platform.admin.tag.adapter.in.web;

import com.accommodation.platform.admin.tag.application.port.in.AdminManageTagUseCase.UpdateTagCommand;

public record UpdateTagRequest(String name, Integer displayOrder) {

    public UpdateTagCommand toCommand() {

        return new UpdateTagCommand(name, displayOrder);
    }
}
