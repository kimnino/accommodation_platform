package com.accommodation.platform.common.domain;

import java.time.Instant;

import lombok.Getter;

@Getter
public abstract class BaseEntity {

    private Instant createdAt;
    private Instant updatedAt;

    protected void initTimestamps() {
        Instant now = Instant.now();
        this.createdAt = now;
        this.updatedAt = now;
    }

    protected void updateTimestamp() {
        this.updatedAt = Instant.now();
    }

    protected void setCreatedAt(Instant createdAt) {
        this.createdAt = createdAt;
    }

    protected void setUpdatedAt(Instant updatedAt) {
        this.updatedAt = updatedAt;
    }
}
