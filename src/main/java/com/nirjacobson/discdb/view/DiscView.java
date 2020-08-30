package com.nirjacobson.discdb.view;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.nirjacobson.discdb.model.Disc;
import java.util.List;
import java.util.stream.Collectors;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.bson.types.ObjectId;

@Getter
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_ABSENT)
public class DiscView {
  @JsonProperty(FieldDefs.ID)
  private ObjectId _id;

  @JsonProperty(FieldDefs.DISC_ID)
  private long _discId;

  @JsonProperty(FieldDefs.ARTIST)
  private String _artist;

  @JsonProperty(FieldDefs.TITLE)
  private String _title;

  @JsonProperty(FieldDefs.YEAR)
  private Integer _year;

  @JsonProperty(FieldDefs.GENRE)
  private String _genre;

  @JsonProperty(FieldDefs.LENGTH)
  private int _length;

  @JsonProperty(FieldDefs.TRACKS)
  private List<TrackView> _tracks;

  @JsonProperty(FieldDefs.EXTENDED_DATA)
  private String _extendedData;

  @JsonProperty(FieldDefs.PLAY_ORDER)
  private List<Integer> _playOrder;

  public DiscView(final Disc pDisc) {
    _id = pDisc.getId();
    _discId = pDisc.getDiscId();
    _artist = pDisc.getArtist();
    _title = pDisc.getTitle();
    _year = pDisc.getYear();
    _genre = pDisc.getGenre();
    _length = pDisc.getLength();
    _tracks = pDisc.getTracks().stream().map(TrackView::new).collect(Collectors.toList());
    _extendedData = pDisc.getExtendedData();
    _playOrder = pDisc.getPlayOrder();
  }

  public Disc toBasicDisc() {
    return new Disc.Builder()
        .discId(_discId)
        .length(_length)
        .tracks(_tracks.stream().map(TrackView::toTrack).collect(Collectors.toList()))
        .build();
  }

  public Disc toDisc() {
    return new Disc.Builder(toBasicDisc().toDBObject())
        .id(_id)
        .artist(_artist)
        .title(_title)
        .year(_year)
        .genre(_genre)
        .extendedData(_extendedData)
        .playOrder(_playOrder)
        .build();
  }

  @Getter
  @NoArgsConstructor
  @JsonInclude(JsonInclude.Include.NON_ABSENT)
  public static class TrackView {
    @JsonProperty(FieldDefs.FRAME_OFFSET)
    private int _frameOffset;

    @JsonProperty(FieldDefs.TITLE)
    private String _title;

    @JsonProperty(FieldDefs.EXTENDED_DATA)
    private String _extendedData;

    public TrackView(final Disc.Track pTrack) {
      _frameOffset = pTrack.getFrameOffset();
      _title = pTrack.getTitle();
      _extendedData = pTrack.getExtendedData();
    }

    public Disc.Track toTrack() {
      return new Disc.Track.Builder()
          .frameOffset(_frameOffset)
          .title(_title)
          .extendedData(_extendedData)
          .build();
    }

    public class FieldDefs {
      public static final String FRAME_OFFSET = "frameOffset";
      public static final String TITLE = "title";
      public static final String EXTENDED_DATA = "extendedData";
    }
  }

  public class FieldDefs {
    public static final String ID = "id";
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
}
