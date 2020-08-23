package com.nirjacobson.discdb.res.common;

import com.nirjacobson.discdb.svc.common.ErrorCode;
import javax.servlet.http.HttpServletResponse;

public interface ApiErrorCode extends ErrorCode {
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
