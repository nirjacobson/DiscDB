package com.nirjacobson.discdb.svc;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import com.nirjacobson.discdb.dao.DiscDao;
import com.nirjacobson.discdb.model.Disc;
import com.nirjacobson.discdb.svc.exception.DiscErrorCode;
import com.nirjacobson.discdb.svc.exception.SvcException;
import com.nirjacobson.discdb.util.TestFactory;
import java.util.Arrays;
import java.util.Optional;
import javax.inject.Inject;
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
    final Disc disc = TestFactory.getDisc();

    assertFalse(_discSvc.find(disc.getId()).isPresent());

    _discSvc.create(disc);

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

    _discSvc.create(disc);

    assertTrue(_discSvc.find(matchingDisc).isPresent());
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
