package com.nirjacobson.discdb.res.exception;

import com.nirjacobson.discdb.view.ApiErrorView;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response;

public class ApiException extends WebApplicationException {

  public ApiException(final ApiErrorCode pErrorCode) {
    super(response(pErrorCode));
  }

  public ApiException(final ApiErrorCode pErrorCode, final Object... pParams) {
    super(response(pErrorCode, pParams));
  }

  private static Response response(final ApiErrorCode pErrorCode) {
    return Response.status(pErrorCode.getStatus()).entity(new ApiErrorView(pErrorCode)).build();
  }

  private static Response response(final ApiErrorCode pErrorCode, final Object... pParameters) {
    return Response.status(pErrorCode.getStatus())
        .entity(new ApiErrorView(pErrorCode, pParameters))
        .build();
  }
}
