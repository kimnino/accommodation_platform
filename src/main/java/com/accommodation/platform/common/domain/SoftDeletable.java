package com.accommodation.platform.common.domain;

import java.time.Instant;

public interface SoftDeletable {

    boolean isDeleted();

    Instant getDeletedAt();

    void delete();

    void restore();
}
