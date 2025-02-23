package com.bside.mzoffice.common.domain;

public enum ResponseCode {
    // success
    SUCCESS("Success", false),

    // auth errors
    UNAUTHORIZED("Unauthorized access", true),
    FORBIDDEN("Access forbidden", true),
    SERVER_ERROR("Internal server error", true),

    API_REQUEST_FAILED("API request failed", true),
    EMPTY_RESPONSE_BODY("API response body is empty", true),
    FAILED_UNLINK_ACCOUNT("Failed to unlink account", true),

    // not found
    NOT_FOUND_USER("not found user", true),

    // chat
    CHAT_JSON_PARSE_ERROR("chat json parsing error", true),
    NOT_FOUND_CHAT("chat id not found", true);

    public final boolean isFatality;
    public final String message;

    ResponseCode(String message, boolean isFatality) {
        this.message = message;
        this.isFatality = isFatality;
    }
    }
