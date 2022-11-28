package com.nirjacobson.discdb.res;

import com.nirjacobson.discdb.model.Disc;
import com.nirjacobson.discdb.res.exception.ApiException;
import com.nirjacobson.discdb.res.exception.DiscApiErrorCode;
import com.nirjacobson.discdb.svc.DiscSvc;
import com.nirjacobson.discdb.svc.exception.DiscErrorCode;
import com.nirjacobson.discdb.svc.exception.SvcException;
import com.nirjacobson.discdb.view.DiscView;
import com.nirjacobson.discdb.view.FindResultsView;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.bson.types.ObjectId;

@ApplicationScoped
@Path("/api/v1.0")
public class DiscResource {
    @Inject
    private DiscSvc _discSvc;

    private Response create(final Disc pDisc) throws Exception {
        try {
            return Response.status(HttpServletResponse.SC_CREATED).entity(new DiscView(_discSvc.create(pDisc))).build();
        } catch (final SvcException pE) {
            if (pE.getErrorCode().equals(DiscErrorCode.INCORRECT_DISC_ID)) {
                throw new ApiException(DiscApiErrorCode.INCORRECT_DISC_ID, pDisc.getDiscId(), pDisc.calculateDiscId());
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
        final DiscView discView = _discSvc.find(pId).map(DiscView::new).orElseThrow(() -> new ApiException(DiscApiErrorCode.DISC_NOT_FOUND, pId));

        return Response.status(HttpServletResponse.SC_OK).entity(discView).build();
    }

    @POST
    @Path("/find")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response find(final DiscView pDiscView) {
        final DiscView discView = _discSvc.find(pDiscView.toBasicDisc()).map(DiscView::new).orElseThrow(() -> new ApiException(DiscApiErrorCode.NO_MATCH));

        return Response.status(HttpServletResponse.SC_OK).entity(discView).build();
    }

    @GET
    @Path("/find")
    @Produces(MediaType.APPLICATION_JSON)
    public Response find(@QueryParam("artist") final String pArtist, @QueryParam("title") final String pTitle, @QueryParam("genre") final String pGenre, @QueryParam("year") final Integer pYear, @QueryParam("page") final Integer pPage) {

        final FindResultsView resultsView = _discSvc.find(pArtist, pTitle, pGenre, pYear, pPage);

        return Response.status(HttpServletResponse.SC_OK).entity(resultsView).build();
    }
}
