package com.bside.mzoffice.common.exception.customException;

import com.bside.mzoffice.common.domain.ResponseCode;

public class ChatException extends RuntimeException {
    public ChatException(ResponseCode responseCode) {
        super(responseCode.toString());
    }
}
