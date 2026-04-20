package com.accommodation.platform.admin.tag.adapter.in.web;

import java.util.List;

import jakarta.validation.Valid;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;

import com.accommodation.platform.admin.tag.application.port.in.AdminManageTagGroupUseCase;
import com.accommodation.platform.admin.tag.application.port.in.AdminManageTagUseCase;
import com.accommodation.platform.common.response.ApiResponse;
import com.accommodation.platform.core.tag.domain.model.Tag;
import com.accommodation.platform.core.tag.domain.model.TagGroup;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/admin/tag-groups")
public class AdminTagController {

    private final AdminManageTagGroupUseCase tagGroupUseCase;
    private final AdminManageTagUseCase tagUseCase;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ApiResponse<TagGroupResponse> createTagGroup(
            @Valid @RequestBody CreateTagGroupRequest request) {

        TagGroup tagGroup = tagGroupUseCase.create(request.toCommand());
        return ApiResponse.success(TagGroupResponse.from(tagGroup));
    }

    @PutMapping("/{tagGroupId}")
    public ApiResponse<TagGroupResponse> updateTagGroup(
            @PathVariable Long tagGroupId,
            @Valid @RequestBody UpdateTagGroupRequest request) {

        TagGroup tagGroup = tagGroupUseCase.update(tagGroupId, request.toCommand());
        return ApiResponse.success(TagGroupResponse.from(tagGroup));
    }

    @PatchMapping("/{tagGroupId}/deactivate")
    public ApiResponse<Void> deactivateTagGroup(@PathVariable Long tagGroupId) {

        tagGroupUseCase.deactivate(tagGroupId);
        return ApiResponse.success(null);
    }

    @GetMapping
    public ApiResponse<List<TagGroupResponse>> listTagGroups() {

        List<TagGroupResponse> responses = tagGroupUseCase.listAll().stream()
                .map(TagGroupResponse::from)
                .toList();
        return ApiResponse.success(responses);
    }

    @PostMapping("/{tagGroupId}/tags")
    @ResponseStatus(HttpStatus.CREATED)
    public ApiResponse<TagResponse> createTag(
            @PathVariable Long tagGroupId,
            @Valid @RequestBody CreateTagRequest request) {

        Tag tag = tagUseCase.create(tagGroupId, request.toCommand());
        return ApiResponse.success(TagResponse.from(tag));
    }

    @PutMapping("/{tagGroupId}/tags/{tagId}")
    public ApiResponse<TagResponse> updateTag(
            @PathVariable Long tagGroupId,
            @PathVariable Long tagId,
            @Valid @RequestBody UpdateTagRequest request) {

        Tag tag = tagUseCase.update(tagId, request.toCommand());
        return ApiResponse.success(TagResponse.from(tag));
    }

    @PatchMapping("/{tagGroupId}/tags/{tagId}/deactivate")
    public ApiResponse<Void> deactivateTag(
            @PathVariable Long tagGroupId,
            @PathVariable Long tagId) {

        tagUseCase.deactivate(tagId);
        return ApiResponse.success(null);
    }

    @GetMapping("/{tagGroupId}/tags")
    public ApiResponse<List<TagResponse>> listTags(@PathVariable Long tagGroupId) {

        List<TagResponse> responses = tagUseCase.listByTagGroupId(tagGroupId).stream()
                .map(TagResponse::from)
                .toList();
        return ApiResponse.success(responses);
    }
}
