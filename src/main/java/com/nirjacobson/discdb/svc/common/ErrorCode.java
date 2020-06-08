package com.nirjacobson.discdb.svc.common;

public interface ErrorCode {
  String name();
  String getMessage();

  default String formatMessage(final Object... pParams) {
    return getMessageFormat() == null ? getMessage() : String.format(getMessageFormat(), pParams);
  }

  default String getMessageFormat() {
    return null;
  }
}
