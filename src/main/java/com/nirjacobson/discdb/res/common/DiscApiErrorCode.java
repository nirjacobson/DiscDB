package com.nirjacobson.discdb.res.common;

import java.util.Arrays;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response;
import lombok.Getter;

public enum DiscApiErrorCode implements ApiErrorCode {
  MALFORMED_XMCD(
      HttpServletResponse.SC_BAD_REQUEST,
      "Received XMCD is malformed."
  ),
  DISC_NOT_FOUND(
      HttpServletResponse.SC_NOT_FOUND,
      "Disc not found.",
      "Disc with ID %s not found."
  ),
  DUPLICATE_DISC(
      HttpServletResponse.SC_CONFLICT,
      "Disc already exists.",
      "Disc with ID %s already exists."
  ),
  INCORRECT_DISC_ID(
      HttpServletResponse.SC_BAD_REQUEST,
      "The disc ID for this disc is incorrect.",
      "The disc ID for this disc (%08X) does not match its expected disc ID (%08X)."
  ),
  NO_MATCH(
      HttpServletResponse.SC_NOT_FOUND,
      "No match found for the given disc."
  );

  @Getter
  private final int _status;
  private final String _message;
  private final String _messageFormat;

  DiscApiErrorCode(final int pStatus, final String pMessage, final String pMessageFormat) {
    _status = pStatus;
    _message = pMessage;
    _messageFormat = pMessageFormat;
  }

  DiscApiErrorCode(final int pStatus, final String pMessage) {
    this(pStatus, pMessage, null);
  }

  @Override
  public String getMessage() {
    return _message == null ? name() : _message;
  }

  @Override
  public String formatMessage(final Object... pParams) {
    return _messageFormat == null ? getMessage() : String.format(_messageFormat, pParams);
  }
}
