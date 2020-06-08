package com.nirjacobson.discdb.res;

import com.nirjacobson.discdb.model.Disc;
import com.nirjacobson.discdb.res.common.ApiErrorCode;
import com.nirjacobson.discdb.svc.DiscSvc;
import com.nirjacobson.discdb.svc.common.DiscErrorCode;
import com.nirjacobson.discdb.svc.common.SvcException;
import com.nirjacobson.discdb.view.DiscView;
import javax.inject.Inject;
import javax.inject.Singleton;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import org.bson.types.ObjectId;

@Singleton
@Path("/api/v1.0")
public class DiscDBResource {
  @Inject private DiscSvc _discSvc;

  private Response create(final Disc pDisc) throws Exception {
    try {
      return Response.ok(new DiscView(_discSvc.create(pDisc))).build();
    } catch (final SvcException pE) {
      if (pE.getErrorCode().equals(DiscErrorCode.INCORRECT_DISC_ID)) {
        throw ApiErrorCode.INCORRECT_DISC_ID.exception(pDisc.getDiscId(), pDisc.calculateDiscId());
      }

      throw pE;
    }
  }

  @POST
  @Path("/xmcd")
  @Consumes(MediaType.APPLICATION_OCTET_STREAM)
  @Produces(MediaType.APPLICATION_JSON)
  public Response createFromXMCD(final String pXMCD) throws Exception {
    try {
      return create(_discSvc.fromXMCD(pXMCD));
    } catch (final SvcException pE) {
      if (pE.getErrorCode().equals(DiscErrorCode.MALFORMED_XMCD)) {
        throw ApiErrorCode.MALFORMED_XMCD.exception();
      }

      throw pE;
    }
  }

  @POST
  @Consumes(MediaType.APPLICATION_JSON)
  @Produces(MediaType.APPLICATION_JSON)
  public Response create(final DiscView pDiscView) throws Exception {
    return create(pDiscView.toDisc());
  }

  @POST
  @Path("/query")
  @Consumes(MediaType.APPLICATION_JSON)
  @Produces(MediaType.APPLICATION_JSON)
  public Response query(final DiscView pDiscView) {
    final DiscView discView = _discSvc.find(pDiscView.toBasicDisc())
        .map(DiscView::new)
        .orElseThrow(() -> ApiErrorCode.NO_MATCH.exception());

    return Response.ok(discView).build();
  }

  @GET
  @Path("/{id}")
  @Produces(MediaType.APPLICATION_JSON)
  public Response getDisc(@PathParam("id") final ObjectId pId) {
    try {
      final DiscView discView = _discSvc.find(pId)
          .map(DiscView::new)
          .orElseThrow(() -> ApiErrorCode.DISC_NOT_FOUND.exception(pId));

      return Response.ok(discView).build();
    } catch (final IllegalArgumentException pE) {
      throw ApiErrorCode.INVALID_DISC_ID.exception(pId);
    }
  }
}
