package com.bside.mzoffice.common.domain;

public enum ResponseCode {
    // success
    SUCCESS(false),

    // auth
    UNAUTHORIZED(true),
    FORBIDDEN(true),
    SERVER_ERROR(true);

    public final boolean isFatality;

    ResponseCode(boolean isFatality) {
        this.isFatality = isFatality;
    }
}
