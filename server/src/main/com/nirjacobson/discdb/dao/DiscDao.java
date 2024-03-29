package com.nirjacobson.discdb.dao;

import static com.mongodb.client.model.Filters.and;
import static com.mongodb.client.model.Filters.regex;

import com.mongodb.BasicDBObject;
import com.nirjacobson.discdb.model.Disc;
import com.nirjacobson.discdb.view.DiscView;
import com.nirjacobson.discdb.view.FindResultsView;
import jakarta.enterprise.context.ApplicationScoped;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import org.bson.codecs.configuration.CodecRegistries;
import org.bson.codecs.configuration.CodecRegistry;
import org.bson.conversions.Bson;
import org.bson.types.ObjectId;

@ApplicationScoped
public class DiscDao extends BaseTDao<Disc> {

  public static final int PAGE_SIZE = 10;

  protected DiscDao() {
    super(Disc.DB_NAME, Disc.COLLECTION_NAME);
  }

  @Override
  protected CodecRegistry initializeCodecRegistry() {
    final CodecRegistry codecRegistry = super.initializeCodecRegistry();

    return CodecRegistries.fromRegistries(
        CodecRegistries.fromCodecs(new Disc.DiscCodec(codecRegistry)), codecRegistry);
  }

  public void create(final Disc pDisc) {
    insertMajority(pDisc);
  }

  public Optional<Disc> find(final ObjectId pId) {
    return Optional.ofNullable(
        getCollection().find(new BasicDBObject().append(Disc.FieldDefs.ID, pId)).first());
  }

  public Optional<Disc> find(final Disc pDisc) {
    return Optional.ofNullable(getCollection().find(flatten(pDisc.toDBObject())).first());
  }

  public FindResultsView find(
      final String pArtist,
      final String pTitle,
      final String pGenre,
      final Integer pYear,
      final Integer pPage) {
    final List<Bson> fields = new ArrayList<>();

    if (!(pArtist == null || pArtist.isEmpty())) {
      fields.add(
          regex(
              Disc.FieldDefs.ARTIST,
              Pattern.compile(Pattern.quote(pArtist), Pattern.CASE_INSENSITIVE)));
    }

    if (!(pTitle == null || pTitle.isEmpty())) {
      fields.add(
          regex(
              Disc.FieldDefs.TITLE,
              Pattern.compile(Pattern.quote(pTitle), Pattern.CASE_INSENSITIVE)));
    }

    if (!(pGenre == null || pGenre.isEmpty())) {
      fields.add(
          regex(
              Disc.FieldDefs.GENRE,
              Pattern.compile(Pattern.quote(pGenre), Pattern.CASE_INSENSITIVE)));
    }

    if (pYear != null) {
      fields.add(new BasicDBObject(Disc.FieldDefs.YEAR, pYear));
    }

    final Bson query = fields.isEmpty() ? new BasicDBObject() : and(fields);

    final int pages = (int) Math.ceil((double) getCollection().countDocuments(query) / PAGE_SIZE);

    return new FindResultsView(
        getCollection()
            .find(query)
            .sort(
                new BasicDBObject()
                    .append(Disc.FieldDefs.ARTIST, 1)
                    .append(Disc.FieldDefs.TITLE, 1)
                    .append(Disc.FieldDefs.GENRE, 1)
                    .append(Disc.FieldDefs.YEAR, 1))
            .skip(((pPage == null ? 1 : pPage) - 1) * PAGE_SIZE)
            .limit(PAGE_SIZE)
            .into(new ArrayList<>())
            .stream()
            .map(DiscView::new)
            .collect(Collectors.toList()),
        pages);
  }
}
