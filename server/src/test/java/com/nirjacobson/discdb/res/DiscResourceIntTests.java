package com.nirjacobson.discdb.res;

import static org.junit.Assert.assertEquals;

import com.nirjacobson.discdb.dao.DiscDao;
import com.nirjacobson.discdb.model.Disc;
import com.nirjacobson.discdb.res.exception.ApiErrorCode;
import com.nirjacobson.discdb.res.exception.DiscApiErrorCode;
import com.nirjacobson.discdb.svc.DiscSvc;
import com.nirjacobson.discdb.svc.exception.SvcException;
import com.nirjacobson.discdb.util.TestFactory;
import com.nirjacobson.discdb.view.ApiErrorView;
import com.nirjacobson.discdb.view.DiscView;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.IntStream;
import javax.inject.Inject;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import org.bson.types.ObjectId;
import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.container.test.api.RunAsClient;
import org.jboss.arquillian.extension.rest.client.ArquillianResteasyResource;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.shrinkwrap.api.Filters;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.asset.EmptyAsset;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.json.JSONArray;
import org.json.JSONObject;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(Arquillian.class)
public class DiscResourceIntTests {
  @Deployment
  public static WebArchive createDeployment() {
    return ShrinkWrap.create(WebArchive.class)
        .addPackages(true, Filters.exclude(".*Tests.java"), "com.nirjacobson.discdb")
        .addAsWebInfResource(EmptyAsset.INSTANCE, "beans.xml");
  }

  @Before
  public void setup() {
    _discDao.dropCollection();
  }

  @Inject private DiscDao _discDao;
  @Inject private DiscSvc _discSvc;

  @Test
  @RunAsClient
  public void testCreate(@ArquillianResteasyResource("") final WebTarget pWebTarget) {
    final Disc disc = TestFactory.getDisc();
    final JSONObject discJson = TestFactory.getDiscJson();
    final JSONObject discJsonBadDiscId =
        TestFactory.getDiscJson().put(DiscView.FieldDefs.DISC_ID, 1);

    // Bad disc ID
    {
      final JSONObject response = doJsonPost(pWebTarget, "/api/v1.0", discJsonBadDiscId);

      verifyApiError(DiscApiErrorCode.INCORRECT_DISC_ID, response);
    }

    // Success
    {
      final JSONObject response = doJsonPost(pWebTarget, "/api/v1.0", discJson);

      verifyDiscJson(disc, response, true);
    }

    // Duplicate
    {
      final JSONObject response = doJsonPost(pWebTarget, "/api/v1.0", discJson);

      verifyApiError(DiscApiErrorCode.DUPLICATE_DISC, response);
    }
  }

  @Test
  @RunAsClient
  public void testCreateFromXMCD(@ArquillianResteasyResource("") final WebTarget pWebTarget)
      throws SvcException {
    final String xmcd = TestFactory.getXMCD();
    final Disc disc = TestFactory.getDisc();

    // Bad XMCD
    {
      final JSONObject response = doBytesPost(pWebTarget, "/api/v1.0/xmcd", "This is not XMCD");

      verifyApiError(DiscApiErrorCode.MALFORMED_XMCD, response);
    }

    // Success
    {
      final JSONObject response = doBytesPost(pWebTarget, "/api/v1.0/xmcd", xmcd);

      verifyDiscJson(disc, response, false);
    }
  }

  @Test
  @RunAsClient
  public void testFindById(@ArquillianResteasyResource("") final WebTarget pWebTarget)
      throws SvcException {
    final Disc disc = TestFactory.getDisc();

    {
      final JSONObject response =
          doJsonGet(pWebTarget, String.format("/api/v1.0/%s", disc.getId()));

      verifyApiError(DiscApiErrorCode.DISC_NOT_FOUND, response);
    }

    _discDao.create(disc);

    {
      final JSONObject response =
          doJsonGet(pWebTarget, String.format("/api/v1.0/%s", disc.getId()));

      verifyDiscJson(disc, response, true);
    }
  }

  @Test
  @RunAsClient
  public void testFindByFields(@ArquillianResteasyResource("") final WebTarget pWebTarget)
      throws SvcException {
    final Disc disc = TestFactory.getDisc();
    final JSONObject discQueryJson = TestFactory.getBasicDiscJson();

    {
      final JSONObject response = doJsonPost(pWebTarget, "/api/v1.0/find", discQueryJson);

      verifyApiError(DiscApiErrorCode.NO_MATCH, response);
    }

    _discDao.create(disc);

    {
      final JSONObject response = doJsonPost(pWebTarget, "/api/v1.0/find", discQueryJson);

      verifyDiscJson(disc, response, true);
    }
  }

  @Test
  @RunAsClient
  public void testFindMany(@ArquillianResteasyResource("") final WebTarget pWebTarget)
      throws SvcException {
    // Before creation
    {
      final JSONObject response =
          doJsonGet(
              pWebTarget,
              "/api/v1.0/find",
              new HashMap<String, Object>() {
                {
                  put("artist", "rippingtons");
                  put("title", "curves");
                }
              });

      assertEquals(0, response.getJSONArray("results").length());
      assertEquals(0, response.getInt("pages"));
    }

    IntStream.range(0, DiscDao.PAGE_SIZE + 3)
        .mapToObj(
            i -> new Disc.Builder(TestFactory.getDisc().toDBObject()).id(ObjectId.get()).build())
        .forEach(disc -> _discDao.create(disc));

    // After creation
    {
      final JSONObject response =
          doJsonGet(
              pWebTarget,
              "/api/v1.0/find",
              new HashMap<String, Object>() {
                {
                  put("artist", "rippingtons");
                  put("title", "curves");
                }
              });

      assertEquals(DiscDao.PAGE_SIZE, response.getJSONArray("results").length());
      assertEquals(2, response.getInt("pages"));
    }

    {
      final JSONObject response =
          doJsonGet(
              pWebTarget,
              "/api/v1.0/find",
              new HashMap<String, Object>() {
                {
                  put("artist", "rippingtons");
                  put("title", "curves");
                  put("page", 2);
                }
              });

      assertEquals(3, response.getJSONArray("results").length());
      assertEquals(2, response.getInt("pages"));
    }
  }

  private void verifyApiError(final ApiErrorCode pApiErrorCode, final JSONObject pJson) {
    assertEquals(pApiErrorCode.getStatus(), pJson.getInt(ApiErrorView.FieldDefs.ERROR));
    assertEquals(pApiErrorCode.getReason(), pJson.getString(ApiErrorView.FieldDefs.REASON));
    assertEquals(pApiErrorCode.name(), pJson.getString(ApiErrorView.FieldDefs.ERROR_CODE));
  }

  private void verifyDiscJson(final Disc pDisc, final JSONObject pJson, final boolean pVerifyId) {
    if (pVerifyId) {
      assertEquals(pDisc.getId().toString(), pJson.getString(DiscView.FieldDefs.ID));
    }

    assertEquals(
        pDisc.getDiscId(), Long.parseLong(pJson.getString(DiscView.FieldDefs.DISC_ID), 16));
    assertEquals(pDisc.getArtist(), pJson.getString(DiscView.FieldDefs.ARTIST));
    assertEquals(pDisc.getTitle(), pJson.getString(DiscView.FieldDefs.TITLE));
    assertEquals(pDisc.getYear(), pJson.get(DiscView.FieldDefs.YEAR));
    assertEquals(pDisc.getGenre(), pJson.getString(DiscView.FieldDefs.GENRE));
    assertEquals(pDisc.getLength(), pJson.getInt(DiscView.FieldDefs.LENGTH));

    final JSONArray tracksArray = pJson.getJSONArray(DiscView.FieldDefs.TRACKS);

    assertEquals(pDisc.getTracks().size(), tracksArray.length());
    IntStream.range(0, tracksArray.length())
        .forEach(
            idx -> {
              final Disc.Track track = pDisc.getTracks().get(idx);
              final JSONObject trackJson = tracksArray.getJSONObject(idx);

              assertEquals(
                  track.getFrameOffset(),
                  trackJson.getInt(DiscView.TrackView.FieldDefs.FRAME_OFFSET));
              assertEquals(
                  track.getTitle(), trackJson.getString(DiscView.TrackView.FieldDefs.TITLE));
              assertEquals(
                  track.getExtendedData(),
                  trackJson.getString(DiscView.TrackView.FieldDefs.EXTENDED_DATA));
            });

    assertEquals(pDisc.getExtendedData(), pJson.getString(DiscView.FieldDefs.EXTENDED_DATA));
    assertEquals(pDisc.getPlayOrder(), pJson.getJSONArray(DiscView.FieldDefs.PLAY_ORDER).toList());
  }

  private JSONObject doJsonGet(final WebTarget pWebTarget, final String pPath) {
    final Response response = pWebTarget.path(pPath).request(MediaType.APPLICATION_JSON).get();

    return new JSONObject(response.readEntity(String.class));
  }

  private JSONObject doJsonGet(
      final WebTarget pWebTarget, final String pPath, final Map<String, Object> queryParams) {
    WebTarget webTarget = pWebTarget.path(pPath);

    for (final Map.Entry<String, Object> entry : queryParams.entrySet()) {
      webTarget = webTarget.queryParam(entry.getKey(), entry.getValue());
    }

    final Response response = webTarget.request(MediaType.APPLICATION_JSON).get();

    return new JSONObject(response.readEntity(String.class));
  }

  private JSONObject doJsonPost(
      final WebTarget pWebTarget, final String pPath, final JSONObject pData) {
    final Response response =
        pWebTarget
            .path(pPath)
            .request(MediaType.APPLICATION_JSON)
            .post(Entity.json(pData.toString()));

    return new JSONObject(response.readEntity(String.class));
  }

  private JSONObject doBytesPost(
      final WebTarget pWebTarget, final String pPath, final String pData) {
    final Response response =
        pWebTarget
            .path(pPath)
            .request(MediaType.APPLICATION_JSON)
            .post(Entity.entity(pData.getBytes(), MediaType.APPLICATION_OCTET_STREAM));

    return new JSONObject(response.readEntity(String.class));
  }
}
