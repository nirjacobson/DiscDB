package com.nirjacobson.discdb.res.common;

import java.util.Arrays;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response;
import lombok.Getter;

@Getter
public enum ApiErrorCode {
  MALFORMED_XMCD(
      HttpServletResponse.SC_BAD_REQUEST,
      "Received XMCD is malformed."
  ),
  DISC_NOT_FOUND(
      HttpServletResponse.SC_NOT_FOUND,
      "Disc with ID %s not found."
  ),
  DUPLICATE_DISC(
      HttpServletResponse.SC_CONFLICT,
      "Disc with ID %s already exists."
  ),
  INVALID_DISC_ID(
      HttpServletResponse.SC_BAD_REQUEST,
      "%s is not a valid disc ID."
  ),
  INCORRECT_DISC_ID(
      HttpServletResponse.SC_BAD_REQUEST,
      "The disc ID for this disc (%08X) does not match its expected disc ID (%08X)."
  ),
  NO_MATCH(
      HttpServletResponse.SC_NOT_FOUND,
      "No match found for the given disc."
  );

  private final int _status;
  private final String _message;

  ApiErrorCode(final int pStatus, final String pMessage) {
    _status = pStatus;
    _message = pMessage;
  }

  public Response response(final Object... pParameters) {
    return Response
        .status(getStatus())
        .entity(
            new ApiError(
                getStatus(),
                getReason(getStatus()),
                String.format(getMessage(), pParameters),
                this,
                Arrays.asList(pParameters)))
        .build();
  }

  public WebApplicationException exception(final Object... pParameters) {
    return new WebApplicationException(response(pParameters));
  }

  private static String getReason(final int pStatus) {
    switch (pStatus) {
      case HttpServletResponse.SC_BAD_REQUEST: // HTTP 400
        return "Bad Request";
      case HttpServletResponse.SC_NOT_FOUND: // HTTP 404
        return "Not Found";
      default:
        return null;
    }
  }
}
