package com.bside.mzoffice.common.exception.customException;

import com.bside.mzoffice.common.domain.ResponseCode;

public class AuthLoginException extends RuntimeException {
    public AuthLoginException(ResponseCode responseCode) {
        super(responseCode.toString());
    }
}
