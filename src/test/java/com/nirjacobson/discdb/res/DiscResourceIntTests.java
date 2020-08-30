package com.nirjacobson.discdb.res;

import static org.junit.Assert.assertEquals;

import com.nirjacobson.discdb.dao.DiscDao;
import com.nirjacobson.discdb.model.Disc;
import com.nirjacobson.discdb.res.common.ApiErrorCode;
import com.nirjacobson.discdb.res.common.ApiErrorView;
import com.nirjacobson.discdb.res.common.DiscApiErrorCode;
import com.nirjacobson.discdb.svc.DiscSvc;
import com.nirjacobson.discdb.svc.common.SvcException;
import com.nirjacobson.discdb.view.DiscView;
import java.util.Arrays;
import java.util.stream.Collectors;
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
    final Disc disc = getDisc();
    final JSONObject discJson = getDiscJson(disc);
    final JSONObject discJsonBadDiscId = getDiscJson(disc).put(DiscView.FieldDefs.DISC_ID, 1);

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
    final String xmcd = getXMCD();
    final Disc disc = getDisc();

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
    final Disc disc = getDisc();

    {
      final JSONObject response =
          doJsonGet(pWebTarget, String.format("/api/v1.0/%s", disc.getId()));

      verifyApiError(DiscApiErrorCode.DISC_NOT_FOUND, response);
    }

    _discSvc.create(disc);

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
    final Disc disc = getDisc();
    final JSONObject discQueryJson = getDiscBasicJson(disc);

    {
      final JSONObject response = doJsonPost(pWebTarget, "/api/v1.0/query", discQueryJson);

      verifyApiError(DiscApiErrorCode.NO_MATCH, response);
    }

    _discSvc.create(disc);

    {
      final JSONObject response = doJsonPost(pWebTarget, "/api/v1.0/query", discQueryJson);

      verifyDiscJson(disc, response, true);
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

    assertEquals(pDisc.getDiscId(), pJson.getLong(DiscView.FieldDefs.DISC_ID));
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

  private Disc getDisc() {
    return new Disc.Builder()
        .id(ObjectId.get())
        .discId(2131446025)
        .artist("The Rippingtons")
        .title("Curves Ahead")
        .year(1991)
        .genre("Jazz")
        .length(2891)
        .tracks(
            Arrays.asList(
                new Disc.Track.Builder()
                    .frameOffset(150)
                    .title("Curves Ahead")
                    .extendedData("Track 01")
                    .build(),
                new Disc.Track.Builder()
                    .frameOffset(25722)
                    .title("Aspen")
                    .extendedData("Track 02")
                    .build(),
                new Disc.Track.Builder()
                    .frameOffset(50452)
                    .title("Santa Fe Trail")
                    .extendedData("Track 03")
                    .build(),
                new Disc.Track.Builder()
                    .frameOffset(74055)
                    .title("Take Me With You")
                    .extendedData("Track 04")
                    .build(),
                new Disc.Track.Builder()
                    .frameOffset(99182)
                    .title("North Star")
                    .extendedData("Track 05")
                    .build(),
                new Disc.Track.Builder()
                    .frameOffset(123545)
                    .title("Miles Away")
                    .extendedData("Track 06")
                    .build(),
                new Disc.Track.Builder()
                    .frameOffset(147382)
                    .title("Snowbound")
                    .extendedData("Track 07")
                    .build(),
                new Disc.Track.Builder()
                    .frameOffset(169405)
                    .title("Nature of the Beast")
                    .extendedData("Track 08")
                    .build(),
                new Disc.Track.Builder()
                    .frameOffset(198077)
                    .title("Morning Song")
                    .extendedData("Track 09")
                    .build()))
        .extendedData("YEAR: 1991")
        .playOrder(Arrays.asList(1, 3, 2, 4, 5, 7, 6, 8, 9))
        .build();
  }

  private JSONObject getDiscBasicJson(final Disc pDisc) {
    return new JSONObject()
        .put(DiscView.FieldDefs.DISC_ID, pDisc.getDiscId())
        .put(DiscView.FieldDefs.LENGTH, pDisc.getLength())
        .put(
            DiscView.FieldDefs.TRACKS,
            new JSONArray(
                pDisc.getTracks().stream()
                    .map(
                        track ->
                            new JSONObject()
                                .put(
                                    DiscView.TrackView.FieldDefs.FRAME_OFFSET,
                                    track.getFrameOffset())
                                .put(DiscView.TrackView.FieldDefs.TITLE, track.getTitle())
                                .put(
                                    DiscView.TrackView.FieldDefs.EXTENDED_DATA,
                                    track.getExtendedData()))
                    .collect(Collectors.toList())));
  }

  private JSONObject getDiscJson(final Disc pDisc) {
    return getDiscBasicJson(pDisc)
        .put(DiscView.FieldDefs.ID, pDisc.getId())
        .put(DiscView.FieldDefs.ARTIST, pDisc.getArtist())
        .put(DiscView.FieldDefs.TITLE, pDisc.getTitle())
        .put(DiscView.FieldDefs.YEAR, pDisc.getYear())
        .put(DiscView.FieldDefs.GENRE, pDisc.getGenre())
        .put(DiscView.FieldDefs.EXTENDED_DATA, pDisc.getExtendedData())
        .put(DiscView.FieldDefs.PLAY_ORDER, new JSONArray(pDisc.getPlayOrder()));
  }

  private String getXMCD() {
    return Arrays.asList(
            "# xmcd 1.3 CD database file",
            "# Copyright (C) 1994 Ti Kan",
            "#",
            "# Track frame offsets:",
            "#     150",
            "#     25722",
            "#     50452",
            "#     74055",
            "#     99182",
            "#     123545",
            "#     147382",
            "#     169405",
            "#     198077",
            "#",
            "# Disc length: 2891",
            "#",
            "DISCID=7F0B4909",
            "DTITLE=The Rippingtons / Curves Ahead",
            "DYEAR=1991",
            "DGENRE=Jazz",
            "TTITLE0=Curves Ahead",
            "TTITLE1=Aspen",
            "TTITLE2=Santa Fe Trail",
            "TTITLE3=Take Me With You",
            "TTITLE4=North Star",
            "TTITLE5=Miles Away",
            "TTITLE6=Snowbound",
            "TTITLE7=Nature of the Beast",
            "TTITLE8=Morning Song",
            "EXTD=YEAR: 1991",
            "EXTT0=Track 01",
            "EXTT1=Track 02",
            "EXTT2=Track 03",
            "EXTT3=Track 04",
            "EXTT4=Track 05",
            "EXTT5=Track 06",
            "EXTT6=Track 07",
            "EXTT7=Track 08",
            "EXTT8=Track 09",
            "PLAYORDER=1, 3, 2, 4, 5, 7, 6, 8, 9")
        .stream()
        .collect(Collectors.joining("\r\n"));
  }
}
