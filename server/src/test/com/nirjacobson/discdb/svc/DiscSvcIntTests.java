package com.nirjacobson.discdb.svc;

import static org.junit.jupiter.api.Assertions.*;

import com.nirjacobson.discdb.dao.DiscDao;
import com.nirjacobson.discdb.model.Disc;
import com.nirjacobson.discdb.svc.exception.DiscErrorCode;
import com.nirjacobson.discdb.svc.exception.SvcException;
import com.nirjacobson.discdb.util.TestFactory;
import com.nirjacobson.discdb.view.FindResultsView;
import jakarta.inject.Inject;
import java.util.Arrays;
import java.util.Optional;
import java.util.stream.IntStream;
import org.bson.types.ObjectId;
import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.container.test.api.RunAsClient;
import org.jboss.arquillian.junit5.ArquillianExtension;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.asset.EmptyAsset;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

@ExtendWith(ArquillianExtension.class)
@RunAsClient
public class DiscSvcIntTests {
  @Deployment
  public static WebArchive createDeployment() {
    WebArchive webArchive =
        ShrinkWrap.create(WebArchive.class)
            .addClass(MongoSvc.class)
            .addClass(DiscDao.class)
            .addClass(DiscSvc.class)
            .addAsWebInfResource(EmptyAsset.INSTANCE, "beans.xml");

    return webArchive;
  }

  @BeforeEach
  public void setup() {
    _discDao.dropCollection();
  }

  @Inject private DiscDao _discDao;
  @Inject private DiscSvc _discSvc;

  @Test
  public void testCreate() throws SvcException {
    final Disc disc = TestFactory.getDisc();
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
    final Disc discCreatedWithNoId = _discSvc.create(discWithNoId);
    assertNotNull(discCreatedWithNoId.getId());
    assertNotEquals(disc.getId(), discCreatedWithNoId.getId());
    assertTrue(_discDao.find(discCreatedWithNoId.getId()).isPresent());

    // Disc with ID
    final Disc discCreatedWithId = _discSvc.create(disc);
    assertEquals(disc, discCreatedWithId);
    assertTrue(_discDao.find(discCreatedWithId.getId()).isPresent());

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
    final Disc disc = TestFactory.getDisc();

    assertFalse(_discSvc.find(disc.getId()).isPresent());

    _discDao.create(disc);

    final Optional<Disc> discById = _discSvc.find(disc.getId());
    assertTrue(discById.isPresent());
    assertEquals(disc, discById.get());
  }

  @Test
  public void testFindByFields() throws SvcException {
    final Disc disc = TestFactory.getDisc();
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

    _discDao.create(disc);

    assertTrue(_discSvc.find(matchingDisc).isPresent());
  }

  @Test
  public void testFindMany() {
    {
      final FindResultsView results = _discSvc.find("rippingtons", "curves", null, null, 1);
      assertEquals(0, results.getResults().size());
      assertEquals(0, results.getPages());
    }

    IntStream.range(0, DiscDao.PAGE_SIZE + 3)
        .mapToObj(
            i -> new Disc.Builder(TestFactory.getDisc().toDBObject()).id(ObjectId.get()).build())
        .forEach(disc -> _discDao.create(disc));

    {
      final FindResultsView resultsPage1 = _discSvc.find("rippingtons", "curves", null, null, 1);
      assertEquals(DiscDao.PAGE_SIZE, resultsPage1.getResults().size());
      assertEquals(2, resultsPage1.getPages());

      final FindResultsView resultsPage2 = _discSvc.find("rippingtons", "curves", null, null, 2);
      assertEquals(3, resultsPage2.getResults().size());
      assertEquals(2, resultsPage2.getPages());
    }
  }

  @Test
  public void testFromXMCD() throws SvcException {
    final String xmcd = TestFactory.getXMCD();

    try {
      _discSvc.fromXMCD("This is not XMCD");
      fail();
    } catch (final SvcException pE) {
      assertEquals(DiscErrorCode.MALFORMED_XMCD, pE.getErrorCode());
    }

    assertNotNull(_discSvc.fromXMCD(xmcd));
  }
}
