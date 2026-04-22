package com.accommodation.platform.customer.tag.adapter.in.web;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.accommodation.platform.common.response.ApiResponse;
import com.accommodation.platform.core.accommodation.domain.enums.AccommodationType;
import com.accommodation.platform.core.tag.domain.enums.TagTarget;
import com.accommodation.platform.customer.tag.application.port.in.CustomerGetTagsQuery;
import com.accommodation.platform.customer.tag.application.port.in.CustomerGetTagsQuery.TagGroupResponse;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/tags")
public class CustomerTagController {

    private final CustomerGetTagsQuery getTagsQuery;

    @GetMapping
    public ApiResponse<List<TagGroupResponse>> getTags(
            @RequestParam(required = false) TagTarget targetType,
            @RequestParam(required = false) AccommodationType accommodationType) {

        return ApiResponse.success(getTagsQuery.getTagGroups(targetType, accommodationType));
    }
}
