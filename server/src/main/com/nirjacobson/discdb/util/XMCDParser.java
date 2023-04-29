package com.nirjacobson.discdb.util;

import com.nirjacobson.discdb.model.Disc;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class XMCDParser {

  public static Disc parse(final String pXMCD) throws ParseException {
    final Disc.Builder discBuilder = new Disc.Builder();

    boolean gettingFrameOffsets = false;

    final List<Integer> trackFrameOffsets = new ArrayList<>();
    final List<String> trackTitles = new ArrayList<>();
    final List<String> trackExtendedData = new ArrayList<>();

    final List<Long> discIds = new ArrayList<>();
    String discTitle = "";
    String discExtendedData = "";
    List<Integer> playOrder = new ArrayList<>();

    final Scanner scanner = new Scanner(pXMCD);

    final String firstLine = scanner.nextLine();
    if (!Patterns.XMCD.matcher(firstLine).find()) {
      throw new ParseException();
    }

    while (scanner.hasNextLine()) {
      final String line = scanner.nextLine();

      if (Patterns.BLANK.matcher(line).find()) {
        continue;
      }

      if (Patterns.TRACK_FRAME_OFFSETS.matcher(line).find()) {
        gettingFrameOffsets = true;
        continue;
      }

      if (gettingFrameOffsets) {
        final Matcher matcher = Patterns.TRACK_FRAME_OFFSET.matcher(line);
        if (matcher.find()) {
          trackFrameOffsets.add(Integer.parseInt(matcher.group(1)));
          continue;
        } else {
          gettingFrameOffsets = false;
        }
      }

      final Matcher discLengthMatcher = Patterns.DISC_LENGTH.matcher(line);
      if (discLengthMatcher.find()) {
        discBuilder.length(Integer.parseInt(discLengthMatcher.group(1)));
        continue;
      }

      final Matcher keyValueMatcher = Patterns.KEY_VALUE.matcher(line);
      if (keyValueMatcher.find()) {
        final String key = keyValueMatcher.group(1);
        final String value = keyValueMatcher.group(2);

        if (!value.isEmpty()) {
          switch (key) {
            case Keys.DISCID:
              final Matcher matcher = Patterns.DISCID.matcher(value.trim());
              while (matcher.find()) discIds.add(Long.parseLong(matcher.group(0), 16));
              break;
            case Keys.DTITLE:
              String newValue = value;
              if (newValue.startsWith("/")) newValue = " " + newValue;
              if (newValue.endsWith("/")) newValue = newValue + " ";

              discTitle += newValue;

              break;
            case Keys.DYEAR:
              discBuilder.year(Integer.parseInt(value.trim()));
              break;
            case Keys.DGENRE:
              discBuilder.genre(value.trim());
              break;
            case Keys.EXTD:
              discExtendedData += value;
              break;
            case Keys.PLAYORDER:
              playOrder =
                  Arrays.asList(value.split("\\s*,\\s*")).stream()
                      .map(track -> Integer.parseInt(track))
                      .collect(Collectors.toList());
            default:
              break;
          }

          {
            final Matcher matcher = Patterns.TTITLE.matcher(key);
            if (matcher.find()) {
              final int index = Integer.parseInt(matcher.group(1));
              appendAtIndex(trackTitles, index, value);
            }
          }

          {
            final Matcher matcher = Patterns.EXTT.matcher(key);
            if (matcher.find()) {
              final int index = Integer.parseInt(matcher.group(1));
              appendAtIndex(trackExtendedData, index, value);
            }
          }
        }
      }
    }

    scanner.close();

    final List<String> artistAndTitle =
        Arrays.asList(discTitle.split("\\s+/\\s+")).stream()
            .map(str -> str.trim())
            .collect(Collectors.toList());
    if (artistAndTitle.size() > 0) discBuilder.artist(artistAndTitle.get(0));
    if (artistAndTitle.size() > 1) discBuilder.title(artistAndTitle.get(1));

    final List<Disc.Track> tracks =
        IntStream.range(0, trackFrameOffsets.size())
            .mapToObj(
                i -> {
                  final Disc.Track.Builder trackBuilder =
                      new Disc.Track.Builder().frameOffset(trackFrameOffsets.get(i));

                  if (i < trackTitles.size())
                    Optional.ofNullable(trackTitles.get(i))
                        .ifPresent(trackTitle -> trackBuilder.title(trackTitle.trim()));

                  if (i < trackExtendedData.size())
                    Optional.ofNullable(trackExtendedData.get(i))
                        .map(extendedData -> unescape(extendedData))
                        .ifPresent(extendedData -> trackBuilder.extendedData(extendedData.trim()));

                  return trackBuilder.build();
                })
            .collect(Collectors.toList());

    discBuilder.tracks(tracks);

    if (!discExtendedData.isEmpty()) discBuilder.extendedData(unescape(discExtendedData).trim());

    if (!playOrder.isEmpty()) discBuilder.playOrder(playOrder);

    final Disc disc = discBuilder.discId(0).build();
    final long discID = disc.calculateDiscId();

    if (!discIds.contains(discID)) throw new ParseException();

    return new Disc.Builder(disc.toDBObject()).discId(discID).build();
  }

  private static void appendAtIndex(List<String> pList, final int pIndex, final String pValue) {
    final int size = pList.size();

    if (size <= pIndex) for (int i = 0; i < (pIndex - size + 1); i++) pList.add(null);

    pList.set(pIndex, Optional.ofNullable(pList.get(pIndex)).orElse("") + pValue);
  }

  private static String unescape(final String input) {
    final StringBuilder stringBuilder = new StringBuilder();

    for (int i = 0; i < input.length(); i++) {
      int cp = input.codePointAt(i);

      if (cp == '\\') {
        int cp1 = (i < input.length() - 1) ? input.codePointAt(++i) : 'n';

        switch (cp1) {
          case 'n':
            stringBuilder.append('\n');
            break;
          case 't':
            stringBuilder.append('\t');
            break;
          case '\\':
            stringBuilder.append('\\');
          default:
            break;
        }
      } else {
        stringBuilder.append((Character.toChars(cp)));
      }
    }

    return stringBuilder.toString();
  }

  private static class Patterns {
    public static final Pattern XMCD = Pattern.compile("^#\\s+xmcd");
    public static final Pattern BLANK = Pattern.compile("^#\\s*$");
    public static final Pattern TRACK_FRAME_OFFSETS = Pattern.compile("^#\\s*Track frame offsets:");
    public static final Pattern TRACK_FRAME_OFFSET = Pattern.compile("^#\\s*(\\d+)");
    public static final Pattern DISC_LENGTH = Pattern.compile("^#\\s*Disc length:\\s*(\\d+)");
    public static final Pattern KEY_VALUE = Pattern.compile("^([A-Z0-9]+)=(.*)$");
    public static final Pattern TTITLE = Pattern.compile("^TTITLE(\\d{1,2})$");
    public static final Pattern EXTT = Pattern.compile("^EXTT(\\d{1,2})$");
    public static final Pattern DISCID = Pattern.compile("([0-9A-Fa-f]{8})");
  }

  private static class Keys {
    private static final String DISCID = "DISCID";
    private static final String DTITLE = "DTITLE";
    private static final String DYEAR = "DYEAR";
    private static final String DGENRE = "DGENRE";
    private static final String PLAYORDER = "PLAYORDER";
    private static final String EXTD = "EXTD";
  }

  public static class ParseException extends Exception {}
}
