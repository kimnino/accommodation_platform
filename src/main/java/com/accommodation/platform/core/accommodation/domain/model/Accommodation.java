package com.accommodation.platform.core.accommodation.domain.model;

import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import lombok.Builder;
import lombok.Getter;

import com.accommodation.platform.common.domain.BaseEntity;
import com.accommodation.platform.core.accommodation.domain.enums.AccommodationStatus;
import com.accommodation.platform.core.accommodation.domain.enums.AccommodationType;

@Getter
public class Accommodation extends BaseEntity {

    private final List<Long> tagIds = new ArrayList<>();
    private final List<AccommodationImage> images = new ArrayList<>();
    private Long id;
    private final Long partnerId;
    private String name;
    private final AccommodationType type;
    private String fullAddress;
    private double latitude;
    private double longitude;
    private String locationDescription;
    private AccommodationStatus status;
    private LocalTime checkInTime;
    private LocalTime checkOutTime;

    @Builder
    public Accommodation(Long id, Long partnerId, String name, AccommodationType type,
                         String fullAddress, double latitude, double longitude,
                         String locationDescription, LocalTime checkInTime, LocalTime checkOutTime) {

        validateRequired(partnerId, name, type, fullAddress);
        this.id = id;
        this.partnerId = partnerId;
        this.name = name;
        this.type = type;
        this.fullAddress = fullAddress;
        this.latitude = latitude;
        this.longitude = longitude;
        this.locationDescription = locationDescription;
        this.status = AccommodationStatus.PENDING;
        this.checkInTime = checkInTime;
        this.checkOutTime = checkOutTime;
        initTimestamps();
    }

    public void activate() {

        if (this.status != AccommodationStatus.PENDING && this.status != AccommodationStatus.SUSPENDED) {
            throw new IllegalStateException("PENDING 또는 SUSPENDED 상태에서만 활성화할 수 있습니다.");
        }
        this.status = AccommodationStatus.ACTIVE;
        updateTimestamp();
    }

    public void suspend() {

        if (this.status != AccommodationStatus.ACTIVE) {
            throw new IllegalStateException("ACTIVE 상태에서만 정지할 수 있습니다.");
        }
        this.status = AccommodationStatus.SUSPENDED;
        updateTimestamp();
    }

    public void close() {

        if (this.status == AccommodationStatus.CLOSED) {
            throw new IllegalStateException("이미 폐쇄된 숙소입니다.");
        }
        this.status = AccommodationStatus.CLOSED;
        updateTimestamp();
    }

    public void updateInfo(String name, String fullAddress, double latitude, double longitude,
                           String locationDescription, LocalTime checkInTime, LocalTime checkOutTime) {

        if (name != null && !name.isBlank()) {
            this.name = name;
        }
        if (fullAddress != null && !fullAddress.isBlank()) {
            this.fullAddress = fullAddress;
        }
        this.latitude = latitude;
        this.longitude = longitude;
        this.locationDescription = locationDescription;
        this.checkInTime = checkInTime;
        this.checkOutTime = checkOutTime;
        updateTimestamp();
    }

    public void addImage(AccommodationImage image) {

        this.images.add(image);
        updateTimestamp();
    }

    public void removeImage(String relativePath) {

        this.images.removeIf(image -> image.relativePath().equals(relativePath));
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

    public List<AccommodationImage> getImages() {

        return Collections.unmodifiableList(images);
    }

    void setId(Long id) {

        this.id = id;
    }

    public void restoreStatus(AccommodationStatus status) {

        this.status = status;
    }

    private void validateRequired(Long partnerId, String name, AccommodationType type, String fullAddress) {

        if (partnerId == null) {
            throw new IllegalArgumentException("partnerId는 필수입니다.");
        }
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("숙소명은 필수입니다.");
        }
        if (type == null) {
            throw new IllegalArgumentException("숙소 유형은 필수입니다.");
        }
        if (fullAddress == null || fullAddress.isBlank()) {
            throw new IllegalArgumentException("주소는 필수입니다.");
        }
    }
}
