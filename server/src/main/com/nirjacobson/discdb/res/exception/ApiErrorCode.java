package com.nirjacobson.discdb.res.exception;

import jakarta.servlet.http.HttpServletResponse;

public interface ApiErrorCode {
  String name();

  String getMessage();

  String formatMessage(final Object... pParams);

  int getStatus();

  default String getReason() {
    switch (getStatus()) {
      case HttpServletResponse.SC_BAD_REQUEST: // HTTP 400
        return "Bad Request";
      case HttpServletResponse.SC_NOT_FOUND: // HTTP 404
        return "Not Found";
      case HttpServletResponse.SC_CONFLICT: // HTTP 409
        return "Conflict";
      default:
        return null;
    }
  }
}
