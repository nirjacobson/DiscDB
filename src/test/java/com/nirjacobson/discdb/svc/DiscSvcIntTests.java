package com.nirjacobson.discdb.svc;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import com.nirjacobson.discdb.dao.DiscDao;
import com.nirjacobson.discdb.model.Disc;
import com.nirjacobson.discdb.svc.common.DiscErrorCode;
import com.nirjacobson.discdb.svc.common.SvcException;
import java.util.Arrays;
import java.util.Optional;
import java.util.stream.Collectors;
import javax.inject.Inject;
import org.bson.types.ObjectId;
import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.asset.EmptyAsset;
import org.jboss.shrinkwrap.api.spec.JavaArchive;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(Arquillian.class)
public class DiscSvcIntTests {
  @Deployment
  public static JavaArchive createDeployment() {
    return ShrinkWrap.create(JavaArchive.class)
        .addClass(MongoSvc.class)
        .addClass(DiscDao.class)
        .addClass(DiscSvc.class)
        .addAsManifestResource(EmptyAsset.INSTANCE, "beans.xml");
  }

  @Before
  public void setup() {
    _discDao.dropCollection();
  }

  @Inject private DiscDao _discDao;
  @Inject private DiscSvc _discSvc;

  @Test
  public void testCreate() throws SvcException {
    final Disc disc = getDisc();
    final Disc discWithBadDiscId =
        new Disc.Builder(disc.toDBObject()).discId(disc.getDiscId() + 1).build();
    final Disc discWithNoId = new Disc.Builder(disc.toDBObject()).id(null).build();

    // Bad disc ID
    try {
      _discSvc.create(discWithBadDiscId);
      fail();
    } catch (final SvcException pE) {
      assertEquals(DiscErrorCode.INCORRECT_DISC_ID, pE.getErrorCode());
    }

    // Disc with no ID - ID is generated
    final Disc discCreatedWithGeneratedId = _discSvc.create(discWithNoId);
    assertNotNull(discCreatedWithGeneratedId.getId());
    assertNotEquals(disc.getId(), discCreatedWithGeneratedId.getId());

    // Disc with ID
    final Disc discCreatedWithId = _discSvc.create(disc);
    assertEquals(disc, discCreatedWithId);

    // Duplicate disc
    try {
      _discSvc.create(disc);
      fail();
    } catch (final SvcException pE) {
      assertEquals(DiscErrorCode.DUPLICATE_DISC, pE.getErrorCode());
    }
  }

  @Test
  public void testFindById() throws SvcException {
    final Disc disc = getDisc();

    assertFalse(_discSvc.find(disc.getId()).isPresent());

    _discSvc.create(disc);

    final Optional<Disc> discById = _discSvc.find(disc.getId());
    assertTrue(discById.isPresent());
    assertEquals(disc, discById.get());
  }

  @Test
  public void testFindByFields() throws SvcException {
    final Disc disc = getDisc();
    final Disc matchingDisc =
        new Disc.Builder()
            .discId(2131446025)
            .length(2891)
            .tracks(
                Arrays.asList(
                    new Disc.Track.Builder().frameOffset(150).build(),
                    new Disc.Track.Builder().frameOffset(25722).build(),
                    new Disc.Track.Builder().frameOffset(50452).build(),
                    new Disc.Track.Builder().frameOffset(74055).build(),
                    new Disc.Track.Builder().frameOffset(99182).build(),
                    new Disc.Track.Builder().frameOffset(123545).build(),
                    new Disc.Track.Builder().frameOffset(147382).build(),
                    new Disc.Track.Builder().frameOffset(169405).build(),
                    new Disc.Track.Builder().frameOffset(198077).build()))
            .build();

    assertFalse(_discSvc.find(matchingDisc).isPresent());

    _discSvc.create(disc);

    assertTrue(_discSvc.find(matchingDisc).isPresent());
  }

  @Test
  public void testFromXMCD() throws SvcException {
    final String xmcd = getXMCD();

    try {
      _discSvc.fromXMCD("This is not XMCD");
      fail();
    } catch (final SvcException pE) {
      assertEquals(DiscErrorCode.MALFORMED_XMCD, pE.getErrorCode());
    }

    assertNotNull(_discSvc.fromXMCD(xmcd));
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

  private String getXMCD() {
    return Arrays.asList(
            "# xmcd 1.3 CD database file",
            "# Copyright (C) 2020 Nir Jacobson",
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
