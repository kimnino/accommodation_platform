package com.accommodation.platform.common.response;

import java.util.List;

public record ErrorDetail(String code, String message, List<FieldError> fieldErrors) {

    public static ErrorDetail of(String code, String message) {

        return new ErrorDetail(code, message, null);
    }

    public static ErrorDetail withFieldErrors(String code, String message, List<FieldError> fieldErrors) {

        return new ErrorDetail(code, message, fieldErrors);
    }
}
