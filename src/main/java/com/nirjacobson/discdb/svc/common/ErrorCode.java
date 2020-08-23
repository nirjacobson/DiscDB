package com.nirjacobson.discdb.svc.common;

public interface ErrorCode {
  String name();
  String getMessage();
  String formatMessage(final Object... pParams);
}
