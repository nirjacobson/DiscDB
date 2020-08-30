package com.nirjacobson.discdb.model;

import static org.junit.Assert.assertEquals;

import com.mongodb.BasicDBList;
import com.mongodb.BasicDBObject;
import com.nirjacobson.discdb.util.TestFactory;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import org.junit.Test;

public class DiscUnitTests {

  @Test
  public void testConstructWithDbObject() {
    final BasicDBObject dbObject = TestFactory.getDiscDBObject();
    final Disc discFromDBObject = new Disc(dbObject);
    final BasicDBObject dbObjectFromDisc = discFromDBObject.toDBObject();

    // Test Disc constructed from BasicDBObject
    assertEquals(dbObject.getObjectId(Disc.FieldDefs.ID), discFromDBObject.getId());
    assertEquals(dbObject.getLong(Disc.FieldDefs.DISC_ID), discFromDBObject.getDiscId());
    assertEquals(dbObject.getString(Disc.FieldDefs.ARTIST), discFromDBObject.getArtist());
    assertEquals(dbObject.getString(Disc.FieldDefs.TITLE), discFromDBObject.getTitle());
    assertEquals(dbObject.get(Disc.FieldDefs.YEAR), discFromDBObject.getYear());
    assertEquals(dbObject.getString(Disc.FieldDefs.GENRE), discFromDBObject.getGenre());
    assertEquals(dbObject.getInt(Disc.FieldDefs.LENGTH), discFromDBObject.getLength());

    final BasicDBList dbObjectTracks = (BasicDBList) dbObject.get(Disc.FieldDefs.TRACKS);
    IntStream.range(0, dbObjectTracks.size())
        .forEach(
            idx -> {
              final BasicDBObject dbObjectTrack = (BasicDBObject) dbObjectTracks.get(idx);
              final Disc.Track discTrackFromDBObject = discFromDBObject.getTracks().get(idx);

              assertEquals(
                  dbObjectTrack.getInt(Disc.Track.FieldDefs.FRAME_OFFSET),
                  discTrackFromDBObject.getFrameOffset());
              assertEquals(
                  dbObjectTrack.getString(Disc.Track.FieldDefs.TITLE),
                  discTrackFromDBObject.getTitle());
              assertEquals(
                  dbObjectTrack.getString(Disc.Track.FieldDefs.EXTENDED_DATA),
                  discTrackFromDBObject.getExtendedData());
            });

    assertEquals(
        dbObject.getString(Disc.FieldDefs.EXTENDED_DATA), discFromDBObject.getExtendedData());

    final BasicDBList dbObjectPlayOrder = (BasicDBList) dbObject.get(Disc.FieldDefs.PLAY_ORDER);
    assertEquals(
        dbObjectPlayOrder.stream().collect(Collectors.toList()), discFromDBObject.getPlayOrder());

    // Test BasicDBObject constructed from Disc
    assertEquals(
        dbObject.getObjectId(Disc.FieldDefs.ID), dbObjectFromDisc.getObjectId(Disc.FieldDefs.ID));
    assertEquals(
        dbObject.getLong(Disc.FieldDefs.DISC_ID), dbObjectFromDisc.getLong(Disc.FieldDefs.DISC_ID));
    assertEquals(
        dbObject.getString(Disc.FieldDefs.ARTIST),
        dbObjectFromDisc.getString(Disc.FieldDefs.ARTIST));
    assertEquals(
        dbObject.getString(Disc.FieldDefs.TITLE), dbObjectFromDisc.getString(Disc.FieldDefs.TITLE));
    assertEquals(dbObject.get(Disc.FieldDefs.YEAR), dbObjectFromDisc.get(Disc.FieldDefs.YEAR));
    assertEquals(
        dbObject.getString(Disc.FieldDefs.GENRE), dbObjectFromDisc.getString(Disc.FieldDefs.GENRE));
    assertEquals(
        dbObject.getInt(Disc.FieldDefs.LENGTH), dbObjectFromDisc.getInt(Disc.FieldDefs.LENGTH));
    assertEquals(dbObject.get(Disc.FieldDefs.TRACKS), dbObjectFromDisc.get(Disc.FieldDefs.TRACKS));
    assertEquals(
        dbObject.getString(Disc.FieldDefs.EXTENDED_DATA),
        dbObjectFromDisc.getString(Disc.FieldDefs.EXTENDED_DATA));
    assertEquals(
        dbObject.get(Disc.FieldDefs.PLAY_ORDER), dbObjectFromDisc.get(Disc.FieldDefs.PLAY_ORDER));
  }

  @Test
  public void testCalculateDiscId() {
    final Disc disc = TestFactory.getDisc();

    assertEquals(disc.getDiscId(), disc.calculateDiscId());
  }
}
