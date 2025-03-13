package com.bside.mzoffice.common.exception.customException;

import com.bside.mzoffice.common.enums.ResponseCode;

public class ChatException extends RuntimeException {
    public ChatException(ResponseCode responseCode) {
        super(responseCode.toString());
    }
}
