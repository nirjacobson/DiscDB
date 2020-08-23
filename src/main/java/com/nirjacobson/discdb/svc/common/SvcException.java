package com.nirjacobson.discdb.svc.common;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import lombok.Getter;

public class SvcException extends Exception {
  @Getter
  private final ErrorCode _errorCode;

  public SvcException(final ErrorCode pErrorCode) {
    super(pErrorCode.getMessage());
    _errorCode = pErrorCode;
  }

  public SvcException(final ErrorCode pErrorCode, final Object... pParams) {
    super(pErrorCode.formatMessage(pParams));
    _errorCode = pErrorCode;
  }
}
