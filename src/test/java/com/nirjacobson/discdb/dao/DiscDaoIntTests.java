package com.nirjacobson.discdb.dao;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import com.nirjacobson.discdb.model.Disc;
import com.nirjacobson.discdb.svc.MongoSvc;
import java.util.Collections;
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
    final Disc disc =
        new Disc.Builder()
            .id(ObjectId.get())
            .discId(4)
            .length(110)
            .tracks(Collections.emptyList())
            .build();

    // Create
    assertEquals(0, _discDao.findAll().size());

    _discDao.create(disc);

    assertEquals(1, _discDao.findAll().size());

    // Find(ID)
    assertTrue(_discDao.find(disc.getId()).isPresent());

    // Find(Disc)
    final Disc findMatch =
        new Disc.Builder().discId(4).length(110).tracks(Collections.emptyList()).build();

    final Disc findNonmatch =
        new Disc.Builder().discId(5).length(110).tracks(Collections.emptyList()).build();

    assertTrue(_discDao.find(findMatch).isPresent());
    assertFalse(_discDao.find(findNonmatch).isPresent());
  }
}
