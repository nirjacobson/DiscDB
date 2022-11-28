package com.nirjacobson.discdb.svc.exception;

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
