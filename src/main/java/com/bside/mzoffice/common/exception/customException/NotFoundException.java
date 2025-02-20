package com.bside.mzoffice.common.exception.customException;

import com.bside.mzoffice.common.domain.ResponseCode;

public class NotFoundException extends RuntimeException {
  public NotFoundException(ResponseCode responseCode) {
    super(responseCode.toString());
  }
}
