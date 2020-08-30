package com.nirjacobson.discdb.util;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import com.nirjacobson.discdb.model.Disc;
import java.util.Arrays;
import java.util.stream.Collectors;
import org.junit.Test;

public class XMCDParserUnitTests {

  @Test
  public void testParse() {
    final Disc disc = getDisc();
    final String xmcd = getXMCD();

    try {
      assertEquals(disc, XMCDParser.parse(xmcd));
    } catch (final Exception pE) {
      fail();
    }

    try {
      XMCDParser.parse("This is not XMCD");
      fail();
    } catch (final Exception pE) {
      assertTrue(pE instanceof XMCDParser.ParseException);
    }
  }

  private Disc getDisc() {
    return new Disc.Builder()
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
