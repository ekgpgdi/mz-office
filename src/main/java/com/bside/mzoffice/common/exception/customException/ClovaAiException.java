package com.bside.mzoffice.common.exception.customException;

import com.bside.mzoffice.common.domain.ResponseCode;

public class ClovaAiException extends RuntimeException {
    public ClovaAiException(ResponseCode responseCode) {
        super(responseCode.toString());
    }
}
