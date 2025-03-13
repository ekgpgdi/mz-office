package com.bside.mzoffice.common.enums;

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
    NOT_FOUND_CHAT("The requested chat ID could not be found", true),
    NOT_FOUND_CHAT_SESSION("The requested chat session ID could not be found", true),

    // chat
    CHAT_JSON_PARSE_ERROR("Error occurred while parsing the chat JSON request body", true),

    // ai
    AI_REQUEST_JSON_SERIALIZATION_ERROR("Error occurred while serializing the request body", true),

    //voca
    NOT_FOUND_WORD("Not Found Word", true);

    public final boolean isFatality;
    public final String message;

    ResponseCode(String message, boolean isFatality) {
        this.message = message;
        this.isFatality = isFatality;
    }
}
