package com.nirjacobson.discdb.util;

import com.mongodb.BasicDBObject;
import com.nirjacobson.discdb.model.Disc;
import com.nirjacobson.discdb.view.DiscView;
import org.bson.types.ObjectId;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.Arrays;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class TestFactory {

    private static final ObjectId ID = ObjectId.get();

    public static BasicDBObject getDiscDBObject() {
        return new BasicDBObject()
                .append(Disc.FieldDefs.ID, ID)
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
                        IntStream.range(1, 10).boxed().collect(new BasicDBListCollector<>()));
    }

    public static Disc getDisc() {
        return getDisc(true);
    }

    public static Disc getDisc(final boolean pWithId) {
        final Disc.Builder builder =
                new Disc.Builder()
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
                        .playOrder(IntStream.range(1, 10).boxed().collect(Collectors.toList()));

        if (pWithId) {
            builder.id(ID);
        }

        return builder.build();
    }

    public static String getXMCD() {
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
                        "PLAYORDER=1, 2, 3, 4, 5, 6, 7, 8, 9")
                .stream()
                .collect(Collectors.joining("\r\n"));
    }

    public static JSONObject getBasicDiscJson() {
        return new JSONObject()
                .put(DiscView.FieldDefs.DISC_ID, Long.toHexString(2131446025))
                .put(DiscView.FieldDefs.LENGTH, 2891)
                .put(
                        DiscView.FieldDefs.TRACKS,
                        new JSONArray(
                                Arrays.asList(
                                        new JSONObject().put(DiscView.TrackView.FieldDefs.FRAME_OFFSET, 150),
                                        new JSONObject().put(DiscView.TrackView.FieldDefs.FRAME_OFFSET, 25722),
                                        new JSONObject().put(DiscView.TrackView.FieldDefs.FRAME_OFFSET, 50452),
                                        new JSONObject().put(DiscView.TrackView.FieldDefs.FRAME_OFFSET, 74055),
                                        new JSONObject().put(DiscView.TrackView.FieldDefs.FRAME_OFFSET, 99182),
                                        new JSONObject().put(DiscView.TrackView.FieldDefs.FRAME_OFFSET, 123545),
                                        new JSONObject().put(DiscView.TrackView.FieldDefs.FRAME_OFFSET, 147382),
                                        new JSONObject().put(DiscView.TrackView.FieldDefs.FRAME_OFFSET, 169405),
                                        new JSONObject().put(DiscView.TrackView.FieldDefs.FRAME_OFFSET, 198077))));
    }

    public static JSONObject getDiscJson() {
        return new JSONObject()
                .put(DiscView.FieldDefs.ID, ID.toString())
                .put(DiscView.FieldDefs.DISC_ID, Long.toHexString(2131446025))
                .put(DiscView.FieldDefs.ARTIST, "The Rippingtons")
                .put(DiscView.FieldDefs.TITLE, "Curves Ahead")
                .put(DiscView.FieldDefs.YEAR, 1991)
                .put(DiscView.FieldDefs.GENRE, "Jazz")
                .put(DiscView.FieldDefs.LENGTH, 2891)
                .put(
                        DiscView.FieldDefs.TRACKS,
                        new JSONArray(
                                Arrays.asList(
                                        new JSONObject()
                                                .put(DiscView.TrackView.FieldDefs.FRAME_OFFSET, 150)
                                                .put(Disc.Track.FieldDefs.TITLE, "Curves Ahead")
                                                .put(Disc.Track.FieldDefs.EXTENDED_DATA, "Track 01"),
                                        new JSONObject()
                                                .put(DiscView.TrackView.FieldDefs.FRAME_OFFSET, 25722)
                                                .put(Disc.Track.FieldDefs.TITLE, "Aspen")
                                                .put(Disc.Track.FieldDefs.EXTENDED_DATA, "Track 02"),
                                        new JSONObject()
                                                .put(DiscView.TrackView.FieldDefs.FRAME_OFFSET, 50452)
                                                .put(Disc.Track.FieldDefs.TITLE, "Santa Fe Trail")
                                                .put(Disc.Track.FieldDefs.EXTENDED_DATA, "Track 03"),
                                        new JSONObject()
                                                .put(DiscView.TrackView.FieldDefs.FRAME_OFFSET, 74055)
                                                .put(Disc.Track.FieldDefs.TITLE, "Take Me With You")
                                                .put(Disc.Track.FieldDefs.EXTENDED_DATA, "Track 04"),
                                        new JSONObject()
                                                .put(DiscView.TrackView.FieldDefs.FRAME_OFFSET, 99182)
                                                .put(Disc.Track.FieldDefs.TITLE, "North Star")
                                                .put(Disc.Track.FieldDefs.EXTENDED_DATA, "Track 05"),
                                        new JSONObject()
                                                .put(DiscView.TrackView.FieldDefs.FRAME_OFFSET, 123545)
                                                .put(Disc.Track.FieldDefs.TITLE, "Miles Away")
                                                .put(Disc.Track.FieldDefs.EXTENDED_DATA, "Track 06"),
                                        new JSONObject()
                                                .put(DiscView.TrackView.FieldDefs.FRAME_OFFSET, 147382)
                                                .put(Disc.Track.FieldDefs.TITLE, "Snowbound")
                                                .put(Disc.Track.FieldDefs.EXTENDED_DATA, "Track 07"),
                                        new JSONObject()
                                                .put(DiscView.TrackView.FieldDefs.FRAME_OFFSET, 169405)
                                                .put(Disc.Track.FieldDefs.TITLE, "Nature of the Beast")
                                                .put(Disc.Track.FieldDefs.EXTENDED_DATA, "Track 08"),
                                        new JSONObject()
                                                .put(DiscView.TrackView.FieldDefs.FRAME_OFFSET, 198077)
                                                .put(Disc.Track.FieldDefs.TITLE, "Morning Song")
                                                .put(Disc.Track.FieldDefs.EXTENDED_DATA, "Track 09"))))
                .put(DiscView.FieldDefs.EXTENDED_DATA, "YEAR: 1991")
                .put(
                        DiscView.FieldDefs.PLAY_ORDER,
                        new JSONArray(IntStream.range(1, 10).boxed().collect(Collectors.toList())));
    }
}
