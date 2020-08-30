package com.nirjacobson.discdb.model;

import static org.junit.Assert.assertEquals;

import com.mongodb.BasicDBList;
import com.mongodb.BasicDBObject;
import com.nirjacobson.discdb.util.BasicDBListCollector;
import java.util.Arrays;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import org.bson.types.ObjectId;
import org.junit.Test;

public class DiscUnitTests {

  @Test
  public void testConstructWithDbObject() {
    final BasicDBObject dbObject = getDiscDBObject();
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
    final Disc disc = new Disc(getDiscDBObject());

    assertEquals(disc.getDiscId(), disc.calculateDiscId());
  }

  private BasicDBObject getDiscDBObject() {
    return new BasicDBObject()
        .append(Disc.FieldDefs.ID, ObjectId.get())
        .append(Disc.FieldDefs.DISC_ID, 2131446025)
        .append(Disc.FieldDefs.ARTIST, "The Rippingtons")
        .append(Disc.FieldDefs.TITLE, "Curves Ahead")
        .append(Disc.FieldDefs.YEAR, 1991)
        .append(Disc.FieldDefs.GENRE, "Jazz")
        .append(Disc.FieldDefs.LENGTH, 2891)
        .append(
            Disc.FieldDefs.TRACKS,
            Arrays.asList(
                    new BasicDBObject()
                        .append(Disc.Track.FieldDefs.FRAME_OFFSET, 150)
                        .append(Disc.Track.FieldDefs.TITLE, "Curves Ahead")
                        .append(Disc.Track.FieldDefs.EXTENDED_DATA, "Track 01"),
                    new BasicDBObject()
                        .append(Disc.Track.FieldDefs.FRAME_OFFSET, 25722)
                        .append(Disc.Track.FieldDefs.TITLE, "Aspen")
                        .append(Disc.Track.FieldDefs.EXTENDED_DATA, "Track 02"),
                    new BasicDBObject()
                        .append(Disc.Track.FieldDefs.FRAME_OFFSET, 50452)
                        .append(Disc.Track.FieldDefs.TITLE, "Santa Fe Trail")
                        .append(Disc.Track.FieldDefs.EXTENDED_DATA, "Track 03"),
                    new BasicDBObject()
                        .append(Disc.Track.FieldDefs.FRAME_OFFSET, 74055)
                        .append(Disc.Track.FieldDefs.TITLE, "Take Me With You")
                        .append(Disc.Track.FieldDefs.EXTENDED_DATA, "Track 04"),
                    new BasicDBObject()
                        .append(Disc.Track.FieldDefs.FRAME_OFFSET, 99182)
                        .append(Disc.Track.FieldDefs.TITLE, "North Star")
                        .append(Disc.Track.FieldDefs.EXTENDED_DATA, "Track 05"),
                    new BasicDBObject()
                        .append(Disc.Track.FieldDefs.FRAME_OFFSET, 123545)
                        .append(Disc.Track.FieldDefs.TITLE, "Miles Away")
                        .append(Disc.Track.FieldDefs.EXTENDED_DATA, "Track 06"),
                    new BasicDBObject()
                        .append(Disc.Track.FieldDefs.FRAME_OFFSET, 147382)
                        .append(Disc.Track.FieldDefs.TITLE, "Snowbound")
                        .append(Disc.Track.FieldDefs.EXTENDED_DATA, "Track 07"),
                    new BasicDBObject()
                        .append(Disc.Track.FieldDefs.FRAME_OFFSET, 169405)
                        .append(Disc.Track.FieldDefs.TITLE, "Nature of the Beast")
                        .append(Disc.Track.FieldDefs.EXTENDED_DATA, "Track 08"),
                    new BasicDBObject()
                        .append(Disc.Track.FieldDefs.FRAME_OFFSET, 198077)
                        .append(Disc.Track.FieldDefs.TITLE, "Morning Song")
                        .append(Disc.Track.FieldDefs.EXTENDED_DATA, "Track 09"))
                .stream()
                .collect(new BasicDBListCollector<>()))
        .append(Disc.FieldDefs.EXTENDED_DATA, "YEAR: 1991")
        .append(
            Disc.FieldDefs.PLAY_ORDER,
            Arrays.asList(1, 3, 2, 4, 5, 7, 6, 8, 9).stream()
                .collect(new BasicDBListCollector<>()));
  }
}
