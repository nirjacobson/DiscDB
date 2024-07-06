package com.nirjacobson.discdb.model;

import com.mongodb.BasicDBList;
import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;
import com.nirjacobson.discdb.util.BasicDBListCollector;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import org.bson.BsonReader;
import org.bson.BsonWriter;
import org.bson.codecs.Codec;
import org.bson.codecs.DecoderContext;
import org.bson.codecs.EncoderContext;
import org.bson.codecs.configuration.CodecRegistry;
import org.bson.types.ObjectId;

@Getter
@EqualsAndHashCode
public class Disc {
  public static final String DB_NAME = "discdb";
  public static final String COLLECTION_NAME = "discs";

  private static final int FRAMES_PER_SECOND = 75;

  public static final int framesToSeconds(final int pFrames) {
    return pFrames / FRAMES_PER_SECOND;
  }

  private final ObjectId _id;
  private final long _discId;
  private final String _artist;
  private final String _title;
  private final Integer _year;
  private final String _genre;
  private final int _length;
  private final List<Track> _tracks;
  private final String _extendedData;
  private final List<Integer> _playOrder;

  public Disc(final BasicDBObject pDBObject) {
    _id = pDBObject.getObjectId(FieldDefs.ID);
    _discId = pDBObject.getLong(FieldDefs.DISC_ID);
    _artist = pDBObject.getString(FieldDefs.ARTIST);
    _title = pDBObject.getString(FieldDefs.TITLE);
    _year = (Integer) pDBObject.get(FieldDefs.YEAR);
    _genre = (String) pDBObject.get(FieldDefs.GENRE);
    _length = pDBObject.getInt(FieldDefs.LENGTH);
    _tracks =
        ((BasicDBList) pDBObject.get(FieldDefs.TRACKS))
            .stream()
                .map(BasicDBObject.class::cast)
                .map(dbObject -> new Track(dbObject))
                .collect(Collectors.toList());
    _extendedData = pDBObject.getString(FieldDefs.EXTENDED_DATA);
    _playOrder =
        Optional.ofNullable((BasicDBList) pDBObject.get(FieldDefs.PLAY_ORDER))
            .map(
                dbList ->
                    dbList.stream().map(track -> (Integer) track).collect(Collectors.toList()))
            .orElse(null);
  }

  public BasicDBObject toDBObject() {
    final BasicDBObject dbObject =
        new BasicDBObject()
            .append(FieldDefs.DISC_ID, _discId)
            .append(FieldDefs.LENGTH, _length)
            .append(
                FieldDefs.TRACKS,
                _tracks.stream()
                    .map(track -> track.toDBObject())
                    .collect(new BasicDBListCollector<>()));

    Optional.ofNullable(_id).ifPresent(id -> dbObject.append(FieldDefs.ID, id));
    Optional.ofNullable(_artist).ifPresent(artist -> dbObject.append(FieldDefs.ARTIST, artist));
    Optional.ofNullable(_title).ifPresent(title -> dbObject.append(FieldDefs.TITLE, title));
    Optional.ofNullable(_year).ifPresent(year -> dbObject.append(FieldDefs.YEAR, year));
    Optional.ofNullable(_genre).ifPresent(genre -> dbObject.append(FieldDefs.GENRE, genre));
    Optional.ofNullable(_extendedData)
        .ifPresent(extendedData -> dbObject.append(FieldDefs.EXTENDED_DATA, extendedData));
    Optional.ofNullable(_playOrder)
        .ifPresent(
            playOrder ->
                dbObject.append(
                    FieldDefs.PLAY_ORDER,
                    playOrder.stream().collect(new BasicDBListCollector<>())));

    return dbObject;
  }

  public long calculateDiscId() {
    if (_tracks.isEmpty()) return 0;

    long result = 0;
    long temp;

    for (final Disc.Track track : _tracks) {
      temp = framesToSeconds(track.getFrameOffset());
      do {
        result += temp % 10;
        temp /= 10;
      } while (temp != 0);
    }

    long discID =
        ((result % 0xff) << 24)
            | ((_length - framesToSeconds(_tracks.get(0).getFrameOffset())) << 8)
            | _tracks.size();

    return discID;
  }

  @Getter
  @EqualsAndHashCode
  public static class Track {
    private final int _frameOffset;
    private final String _title;
    private final String _extendedData;

    public Track(final BasicDBObject pDBObject) {
      _frameOffset = pDBObject.getInt(FieldDefs.FRAME_OFFSET);
      _title = pDBObject.getString(FieldDefs.TITLE);
      _extendedData = pDBObject.getString(FieldDefs.EXTENDED_DATA);
    }

    public BasicDBObject toDBObject() {
      final BasicDBObject dbObject =
          new BasicDBObject().append(FieldDefs.FRAME_OFFSET, _frameOffset);

      Optional.ofNullable(_title).ifPresent(title -> dbObject.append(FieldDefs.TITLE, title));
      Optional.ofNullable(_extendedData)
          .ifPresent(extendedData -> dbObject.append(FieldDefs.EXTENDED_DATA, extendedData));

      return dbObject;
    }

    public class FieldDefs {
      public static final String FRAME_OFFSET = "frameOffset";
      public static final String TITLE = "title";
      public static final String EXTENDED_DATA = "extendedData";
    }

    public static class Builder {
      private final BasicDBObject _dbObject;

      public Builder() {
        _dbObject = new BasicDBObject();
      }

      public Builder title(final String pTitle) {
        _dbObject.append(FieldDefs.TITLE, pTitle);
        return this;
      }

      public Builder frameOffset(final int pFrameOffset) {
        _dbObject.append(FieldDefs.FRAME_OFFSET, pFrameOffset);
        return this;
      }

      public Builder extendedData(final String pExtendedData) {
        _dbObject.append(Disc.FieldDefs.EXTENDED_DATA, pExtendedData);
        return this;
      }

      public Track build() {
        return new Track(_dbObject);
      }
    }
  }

  public class FieldDefs {
    public static final String ID = "_id";
    public static final String DISC_ID = "discId";
    public static final String ARTIST = "artist";
    public static final String TITLE = "title";
    public static final String YEAR = "year";
    public static final String GENRE = "genre";
    public static final String LENGTH = "length";
    public static final String TRACKS = "tracks";
    public static final String EXTENDED_DATA = "extendedData";
    public static final String PLAY_ORDER = "playOrder";
  }

  public static class Builder {
    private final BasicDBObject _dbObject;

    public Builder() {
      _dbObject = new BasicDBObject();
    }

    public Builder(final BasicDBObject pDBObject) {
      _dbObject = pDBObject;
    }

    public Builder id(final ObjectId pId) {
      _dbObject.append(FieldDefs.ID, pId);
      return this;
    }

    public Builder discId(final long pId) {
      _dbObject.append(FieldDefs.DISC_ID, pId);
      return this;
    }

    public Builder artist(final String pArtist) {
      _dbObject.append(FieldDefs.ARTIST, pArtist);
      return this;
    }

    public Builder title(final String pTitle) {
      _dbObject.append(FieldDefs.TITLE, pTitle);
      return this;
    }

    public Builder year(final int pYear) {
      _dbObject.append(FieldDefs.YEAR, pYear);
      return this;
    }

    public Builder genre(final String pGenre) {
      _dbObject.append(FieldDefs.GENRE, pGenre);
      return this;
    }

    public Builder length(final int pLength) {
      _dbObject.append(FieldDefs.LENGTH, pLength);
      return this;
    }

    public Builder tracks(final List<Track> pTracks) {
      _dbObject.append(
          FieldDefs.TRACKS,
          pTracks.stream().map(track -> track.toDBObject()).collect(new BasicDBListCollector<>()));
      return this;
    }

    public Builder extendedData(final String pExtendedData) {
      _dbObject.append(FieldDefs.EXTENDED_DATA, pExtendedData);
      return this;
    }

    public Builder playOrder(final List<Integer> pPlayOrder) {
      _dbObject.append(
          FieldDefs.PLAY_ORDER, (pPlayOrder == null) ? null : pPlayOrder.stream().collect(new BasicDBListCollector<>()));
      return this;
    }

    public Disc build() {
      return new Disc(_dbObject);
    }
  }

  public static class DiscCodec implements Codec<Disc> {

    private final CodecRegistry _codecRegistry;

    public DiscCodec(final CodecRegistry pCodecRegistry) {
      _codecRegistry = pCodecRegistry;
    }

    @Override
    public Disc decode(final BsonReader pBsonReader, final DecoderContext pDecoderContext) {
      final DBObject dbObject =
          _codecRegistry.get(DBObject.class).decode(pBsonReader, pDecoderContext);

      return new Disc((BasicDBObject) dbObject);
    }

    @Override
    public void encode(
        final BsonWriter pBsonWriter, final Disc pDisc, final EncoderContext pEncoderContext) {
      final BasicDBObject dbObject = pDisc.toDBObject();

      _codecRegistry.get(DBObject.class).encode(pBsonWriter, dbObject, pEncoderContext);
    }

    @Override
    public Class<Disc> getEncoderClass() {
      return Disc.class;
    }
  }
}
