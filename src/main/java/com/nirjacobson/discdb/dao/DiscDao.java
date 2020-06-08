package com.nirjacobson.discdb.dao;

import com.mongodb.BasicDBObject;
import com.nirjacobson.discdb.model.Disc;
import java.util.Optional;
import javax.inject.Singleton;
import org.bson.codecs.configuration.CodecRegistries;
import org.bson.codecs.configuration.CodecRegistry;
import org.bson.types.ObjectId;

@Singleton
public class DiscDao extends BaseTDao<Disc> {

  protected DiscDao() {
    super(Disc.DB_NAME, Disc.COLLECTION_NAME);
  }

  @Override
  protected CodecRegistry initializeCodecRegistry() {
    final CodecRegistry codecRegistry = super.initializeCodecRegistry();

    return CodecRegistries.fromRegistries(
        CodecRegistries.fromCodecs(new Disc.DiscCodec(codecRegistry)),
        codecRegistry);
  }

  public void create(final Disc pDisc) {
    insertMajority(pDisc);
  }

  public Optional<Disc> find(final ObjectId pId) {
    return Optional.ofNullable(
        getCollection().find(new BasicDBObject().append(Disc.FieldDefs.ID, pId)).first());
  }

  public Optional<Disc> find(final Disc pDisc) {
    return Optional.ofNullable(
        getCollection().find(flatten(pDisc.toDBObject())).first());
  }

}
