package com.nirjacobson.discdb.svc.common;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import lombok.Getter;

public class SvcException extends Exception {
  @Getter
  private final ErrorCode _errorCode;

  private final List<Object> _messageParams;

  public SvcException(final ErrorCode pErrorCode) {
    super(pErrorCode.getMessage() == null ? pErrorCode.name() : pErrorCode.getMessage());
    _errorCode = pErrorCode;
    _messageParams = Collections.emptyList();
  }

  public SvcException(final ErrorCode pErrorCode, final Object... pParams) {
    super(pErrorCode.formatMessage(pParams));
    _errorCode = pErrorCode;
    _messageParams = toList(pParams);
  }

  private List<Object> toList(final Object[] pParams) {
    return Optional.ofNullable(pParams).map(Arrays::asList).orElse(Collections.emptyList());
  }
}
