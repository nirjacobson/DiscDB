package com.nirjacobson.discdb.res.common;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import lombok.Getter;

@Getter
public class ApiErrorView {

  @JsonProperty(FieldDefs.ERROR)
  private final int _error;

  @JsonProperty(FieldDefs.REASON)
  private final String _reason;

  @JsonProperty(FieldDefs.DETAIL)
  private final String _detail;

  @JsonProperty(FieldDefs.ERROR_CODE)
  private final ApiErrorCode _errorCode;

  public ApiErrorView(final ApiErrorCode pErrorCode) {
    _error = pErrorCode.getStatus();
    _reason = pErrorCode.getReason();
    _detail = pErrorCode.getMessage();
    _errorCode = pErrorCode;
  }

  public ApiErrorView(
      final ApiErrorCode pErrorCode,
      final Object... pParameters) {
    _error = pErrorCode.getStatus();
    _reason = pErrorCode.getReason();
    _detail = pErrorCode.formatMessage(pParameters);
    _errorCode = pErrorCode;
  }
  
  public class FieldDefs {
    public static final String ERROR = "error";
    public static final String REASON = "reason";
    public static final String DETAIL = "detail";
    public static final String ERROR_CODE = "errorCode";
    public static final String PARAMETERS = "parameters";
  }
}
