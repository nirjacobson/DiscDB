package com.nirjacobson.discdb.svc.common;

public enum DiscErrorCode implements ErrorCode {
  MALFORMED_XMCD(
      "XMCD is malformed."
  ),
  DUPLICATE_DISC(
      "An entry for this disc already exists.",
      "An entry for disc with ID %s already exists."
  ),
  INCORRECT_DISC_ID(
      "The disc ID for this disc is incorrect.",
      "The disc ID for this disc (%08X) does not match its expected disc ID (%08X)."
  );

  private final String _message;
  private final String _messageFormat;

  DiscErrorCode(final String pMessage, final String pMessageFormat) {
    _message = pMessage;
    _messageFormat = pMessageFormat;
  }

  DiscErrorCode(final String pMessage) {
    this(pMessage, null);
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
