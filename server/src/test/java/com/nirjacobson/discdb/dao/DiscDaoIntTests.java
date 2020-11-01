package com.nirjacobson.discdb.dao;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import com.nirjacobson.discdb.model.Disc;
import com.nirjacobson.discdb.svc.MongoSvc;
import com.nirjacobson.discdb.util.TestFactory;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import javafx.util.Pair;
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
public class DiscDaoIntTests {
  @Deployment
  public static JavaArchive createDeployment() {
    return ShrinkWrap.create(JavaArchive.class)
        .addClass(MongoSvc.class)
        .addClass(DiscDao.class)
        .addAsManifestResource(EmptyAsset.INSTANCE, "beans.xml");
  }

  @Before
  public void setup() {
    _discDao.dropCollection();
  }

  @Inject private DiscDao _discDao;

  @Test
  public void testCreateAndFind() {
    final Disc disc = TestFactory.getDisc();

    // Create
    assertEquals(0, _discDao.findAll().size());

    _discDao.create(disc);

    assertEquals(1, _discDao.findAll().size());

    // Find(ID)
    assertTrue(_discDao.find(disc.getId()).isPresent());

    // Find(Disc)
    final Disc findMatch =
        new Disc.Builder()
            .discId(disc.getDiscId())
            .length(disc.getLength())
            .tracks(
                disc.getTracks().stream()
                    .map(
                        track ->
                            new Disc.Track.Builder().frameOffset(track.getFrameOffset()).build())
                    .collect(Collectors.toList()))
            .build();

    final Disc findNonmatch =
        new Disc.Builder()
            .discId(disc.getDiscId() + 1)
            .length(disc.getLength())
            .tracks(
                disc.getTracks().stream()
                    .map(
                        track ->
                            new Disc.Track.Builder().frameOffset(track.getFrameOffset()).build())
                    .collect(Collectors.toList()))
            .build();

    assertTrue(_discDao.find(findMatch).isPresent());
    assertFalse(_discDao.find(findNonmatch).isPresent());
  }

  @Test
  public void testFindMany() {
    {
      final Pair<List<Disc>, Integer> results =
          _discDao.find("rippingtons", "curves", null, null, 1);
      assertEquals(0, results.getKey().size());
      assertEquals(0, results.getValue().intValue());
    }

    IntStream.range(0, DiscDao.PAGE_SIZE + 1)
        .mapToObj(
            i -> new Disc.Builder(TestFactory.getDisc().toDBObject()).id(ObjectId.get()).build())
        .forEach(disc -> _discDao.create(disc));

    {
      final Pair<List<Disc>, Integer> resultsPage1 =
          _discDao.find("rippingtons", "curves", null, null, 1);
      assertEquals(DiscDao.PAGE_SIZE, resultsPage1.getKey().size());
      assertEquals(2, resultsPage1.getValue().intValue());

      final Pair<List<Disc>, Integer> resultsPage2 =
          _discDao.find("rippingtons", "curves", null, null, 2);
      assertEquals(1, resultsPage2.getKey().size());
      assertEquals(2, resultsPage2.getValue().intValue());
    }
  }
}
