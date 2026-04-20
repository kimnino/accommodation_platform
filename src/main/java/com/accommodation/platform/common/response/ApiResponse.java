package com.accommodation.platform.common.response;

import java.time.Instant;

public record ApiResponse<T>(String status, T data, ErrorDetail error, Instant timestamp) {

    private static final String SUCCESS = "SUCCESS";
    private static final String ERROR = "ERROR";

    public static <T> ApiResponse<T> success(T data) {
        return new ApiResponse<>(SUCCESS, data, null, Instant.now());
    }

    public static <T> ApiResponse<T> error(ErrorDetail error) {
        return new ApiResponse<>(ERROR, null, error, Instant.now());
    }
}
