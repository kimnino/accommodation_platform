package com.accommodation.platform.core.room.domain.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import lombok.Builder;
import lombok.Getter;

import com.accommodation.platform.common.domain.BaseEntity;
import com.accommodation.platform.core.room.domain.enums.RoomStatus;

@Getter
public class Room extends BaseEntity {

    private final List<Long> tagIds = new ArrayList<>();
    private Long id;
    private final Long accommodationId;
    private String name;
    private String roomTypeName;
    private int standardCapacity;
    private int maxCapacity;
    private RoomStatus status;

    @Builder
    public Room(Long id, Long accommodationId, String name, String roomTypeName,
                int standardCapacity, int maxCapacity) {

        validateRequired(accommodationId, name);
        validateCapacity(standardCapacity, maxCapacity);
        this.id = id;
        this.accommodationId = accommodationId;
        this.name = name;
        this.roomTypeName = roomTypeName;
        this.standardCapacity = standardCapacity;
        this.maxCapacity = maxCapacity;
        this.status = RoomStatus.ACTIVE;
        initTimestamps();
    }

    public void updateInfo(String name, String roomTypeName, int standardCapacity, int maxCapacity) {

        validateCapacity(standardCapacity, maxCapacity);
        if (name != null && !name.isBlank()) {
            this.name = name;
        }
        this.roomTypeName = roomTypeName;
        this.standardCapacity = standardCapacity;
        this.maxCapacity = maxCapacity;
        updateTimestamp();
    }

    public void activate() {

        this.status = RoomStatus.ACTIVE;
        updateTimestamp();
    }

    public void deactivate() {

        this.status = RoomStatus.INACTIVE;
        updateTimestamp();
    }

    public void addTagId(Long tagId) {

        if (!this.tagIds.contains(tagId)) {
            this.tagIds.add(tagId);
            updateTimestamp();
        }
    }

    public void removeTagId(Long tagId) {

        this.tagIds.remove(tagId);
        updateTimestamp();
    }

    public List<Long> getTagIds() {

        return Collections.unmodifiableList(tagIds);
    }

    public void restoreStatus(RoomStatus status) {

        this.status = status;
    }

    void setId(Long id) {

        this.id = id;
    }

    public static Room reconstruct(Long id, Long accommodationId, String name, String roomTypeName,
                                   int standardCapacity, int maxCapacity, RoomStatus status) {

        Room room = Room.builder()
                .id(id)
                .accommodationId(accommodationId)
                .name(name)
                .roomTypeName(roomTypeName)
                .standardCapacity(standardCapacity)
                .maxCapacity(maxCapacity)
                .build();
        room.status = status;
        return room;
    }

    private void validateRequired(Long accommodationId, String name) {

        if (accommodationId == null) {
            throw new IllegalArgumentException("accommodationId는 필수입니다.");
        }
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("객실명은 필수입니다.");
        }
    }

    private void validateCapacity(int standardCapacity, int maxCapacity) {

        if (standardCapacity <= 0) {
            throw new IllegalArgumentException("기준 인원은 1명 이상이어야 합니다.");
        }
        if (maxCapacity < standardCapacity) {
            throw new IllegalArgumentException("최대 인원은 기준 인원 이상이어야 합니다.");
        }
    }
}
