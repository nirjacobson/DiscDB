package com.nirjacobson.discdb.res.common;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;
import lombok.Getter;

@Getter
public class ApiError {

  @JsonProperty(FieldDefs.ERROR)
  private final int _error;

  @JsonProperty(FieldDefs.REASON)
  private final String _reason;

  @JsonProperty(FieldDefs.DETAIL)
  private final String _detail;

  @JsonProperty(FieldDefs.ERROR_CODE)
  private final ApiErrorCode _errorCode;

  @JsonProperty(FieldDefs.PARAMETERS)
  private final List<Object> _parameters;

  public ApiError(
      final int pError,
      final String pReason,
      final String pDetail,
      final ApiErrorCode pErrorCode,
      final List<Object> pParameters) {
    _error = pError;
    _reason = pReason;
    _detail = pDetail;
    _errorCode = pErrorCode;
    _parameters = pParameters;
  }
  
  public class FieldDefs {
    public static final String ERROR = "error";
    public static final String REASON = "reason";
    public static final String DETAIL = "detail";
    public static final String ERROR_CODE = "errorCode";
    public static final String PARAMETERS = "parameters";
  }
}
