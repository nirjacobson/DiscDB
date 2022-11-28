package com.nirjacobson.discdb.svc.exception;

public interface ErrorCode {
    String name();

    String getMessage();

    String formatMessage(final Object... pParams);
}
