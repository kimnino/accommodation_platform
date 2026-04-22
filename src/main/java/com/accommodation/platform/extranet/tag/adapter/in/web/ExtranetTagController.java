package com.accommodation.platform.extranet.tag.adapter.in.web;

import java.util.List;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;

import com.accommodation.platform.common.response.TagGroupResponse;
import com.accommodation.platform.common.response.TagResponse;
import com.accommodation.platform.common.response.ApiResponse;
import com.accommodation.platform.extranet.tag.application.port.in.ExtranetGetAvailableTagQuery;
import com.accommodation.platform.extranet.tag.application.port.in.ExtranetManageAccommodationTagUseCase;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/extranet/accommodations/{accommodationId}/tags")
public class ExtranetTagController {

    private final ExtranetGetAvailableTagQuery getAvailableTagQuery;
    private final ExtranetManageAccommodationTagUseCase manageTagUseCase;

    @GetMapping("/groups")
    public ApiResponse<List<TagGroupResponse>> getAvailableTagGroups(
            @PathVariable Long accommodationId,
            @RequestHeader("X-Partner-Id") Long partnerId,
            @RequestParam(defaultValue = "ACCOMMODATION") String targetType) {

        List<TagGroupResponse> responses = getAvailableTagQuery
                .getAvailableTagGroups(accommodationId, partnerId, targetType)
                .stream()
                .map(TagGroupResponse::from)
                .toList();
        return ApiResponse.success(responses);
    }

    @GetMapping("/groups/{tagGroupId}")
    public ApiResponse<List<TagResponse>> getTagsByGroup(
            @PathVariable Long accommodationId,
            @PathVariable Long tagGroupId,
            @RequestHeader("X-Partner-Id") Long partnerId) {

        List<TagResponse> responses = getAvailableTagQuery.getTagsByGroupId(tagGroupId)
                .stream()
                .map(TagResponse::from)
                .toList();
        return ApiResponse.success(responses);
    }

    @GetMapping
    public ApiResponse<List<Long>> getAccommodationTags(
            @PathVariable Long accommodationId,
            @RequestHeader("X-Partner-Id") Long partnerId) {

        List<Long> tagIds = manageTagUseCase.getTagIds(accommodationId, partnerId);
        return ApiResponse.success(tagIds);
    }

    @PostMapping
    public ApiResponse<Void> addTags(
            @PathVariable Long accommodationId,
            @RequestHeader("X-Partner-Id") Long partnerId,
            @RequestBody ManageTagsRequest request) {

        manageTagUseCase.addTags(accommodationId, partnerId, request.tagIds());
        return ApiResponse.success(null);
    }

    @DeleteMapping
    public ApiResponse<Void> removeTags(
            @PathVariable Long accommodationId,
            @RequestHeader("X-Partner-Id") Long partnerId,
            @RequestBody ManageTagsRequest request) {

        manageTagUseCase.removeTags(accommodationId, partnerId, request.tagIds());
        return ApiResponse.success(null);
    }

    public record ManageTagsRequest(List<Long> tagIds) {
    }
}
