package com.nirjacobson.discdb.res;

import com.nirjacobson.discdb.model.Disc;
import com.nirjacobson.discdb.res.exception.ApiException;
import com.nirjacobson.discdb.res.exception.DiscApiErrorCode;
import com.nirjacobson.discdb.svc.DiscSvc;
import com.nirjacobson.discdb.svc.exception.DiscErrorCode;
import com.nirjacobson.discdb.svc.exception.SvcException;
import com.nirjacobson.discdb.view.DiscView;
import com.nirjacobson.discdb.view.FindResultsView;
import java.util.List;
import java.util.stream.Collectors;
import javafx.util.Pair;
import javax.inject.Inject;
import javax.inject.Singleton;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import org.bson.types.ObjectId;

@Singleton
@Path("/api/v1.0")
public class DiscResource {
  @Inject private DiscSvc _discSvc;

  private Response create(final Disc pDisc) throws Exception {
    try {
      return Response.status(HttpServletResponse.SC_CREATED)
          .entity(new DiscView(_discSvc.create(pDisc)))
          .build();
    } catch (final SvcException pE) {
      if (pE.getErrorCode().equals(DiscErrorCode.INCORRECT_DISC_ID)) {
        throw new ApiException(
            DiscApiErrorCode.INCORRECT_DISC_ID, pDisc.getDiscId(), pDisc.calculateDiscId());
      } else if (pE.getErrorCode().equals(DiscErrorCode.DUPLICATE_DISC)) {
        throw new ApiException(DiscApiErrorCode.DUPLICATE_DISC, pDisc.getId());
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
        throw new ApiException(DiscApiErrorCode.MALFORMED_XMCD);
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

  @GET
  @Path("/{id}")
  @Produces(MediaType.APPLICATION_JSON)
  public Response find(@PathParam("id") final ObjectId pId) {
    final DiscView discView =
        _discSvc
            .find(pId)
            .map(DiscView::new)
            .orElseThrow(() -> new ApiException(DiscApiErrorCode.DISC_NOT_FOUND, pId));

    return Response.status(HttpServletResponse.SC_OK).entity(discView).build();
  }

  @POST
  @Path("/find")
  @Consumes(MediaType.APPLICATION_JSON)
  @Produces(MediaType.APPLICATION_JSON)
  public Response find(final DiscView pDiscView) {
    final DiscView discView =
        _discSvc
            .find(pDiscView.toBasicDisc())
            .map(DiscView::new)
            .orElseThrow(() -> new ApiException(DiscApiErrorCode.NO_MATCH));

    return Response.status(HttpServletResponse.SC_OK).entity(discView).build();
  }

  @GET
  @Path("/find")
  @Produces(MediaType.APPLICATION_JSON)
  public Response find(
      @QueryParam("artist") final String pArtist,
      @QueryParam("title") final String pTitle,
      @QueryParam("genre") final String pGenre,
      @QueryParam("year") final Integer pYear,
      @QueryParam("page") final Integer pPage) {

    final Pair<List<Disc>, Integer> results = _discSvc.find(pArtist, pTitle, pGenre, pYear, pPage);

    final FindResultsView resultsView =
        new FindResultsView(
            results.getKey().stream().map(DiscView::new).collect(Collectors.toList()),
            results.getValue());

    return Response.status(HttpServletResponse.SC_OK).entity(resultsView).build();
  }
}
