package com.nirjacobson.discdb.dao;

import static com.mongodb.client.model.Filters.eq;
import static org.bson.codecs.configuration.CodecRegistries.fromProviders;
import static org.bson.codecs.configuration.CodecRegistries.fromRegistries;

import com.mongodb.BasicDBList;
import com.mongodb.BasicDBObject;
import com.mongodb.WriteConcern;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.UpdateOptions;
import com.mongodb.client.result.UpdateResult;
import java.lang.reflect.ParameterizedType;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;
import java.util.stream.IntStream;
import org.bson.codecs.configuration.CodecRegistry;
import org.bson.codecs.pojo.ClassModel;
import org.bson.codecs.pojo.Convention;
import org.bson.codecs.pojo.Conventions;
import org.bson.codecs.pojo.PojoCodecProvider;
import org.bson.conversions.Bson;

public abstract class BaseTDao<T> {

  private static final String MONGO_URI = "%s://%s:%s@%s/%s?retryWrites=true&w=majority";

  private static final MongoClient _mongoClient = getMongoClient();

  private final CodecRegistry CODEC_REGISTRY = initializeCodecRegistry();

  private final String _dbName;
  private final String _collectionName;
  private final Class<T> _encoderClass;

  private static MongoClient getMongoClient() {
    final String mongoProtocol = System.getenv("DISCDB_MONGO_SRV") == null ? "mongodb" : "mongodb+srv";
    final String mongoUser = System.getenv("DISCDB_MONGO_USER");
    final String mongoPassword = System.getenv("DISCDB_MONGO_PASSWORD");
    final String mongoHost = System.getenv("DISCDB_MONGO_HOST");
    final String mongoAuthDb = Optional.ofNullable(System.getenv("DISCDB_MONGO_AUTH_DB")).orElse("");

    if (mongoUser == null || mongoPassword == null || mongoHost == null) {
      throw new IllegalStateException(
          "The mongoDB connection environment variables are not set.");
    }

    return MongoClients.create(String.format(MONGO_URI, mongoProtocol, mongoUser, mongoPassword, mongoHost, mongoAuthDb));
  }

  protected BaseTDao(final String pDbName, final String pCollectionName) {
    _dbName = pDbName;
    _collectionName = pCollectionName;
    // unchecked
    // Note: this will only work for direct descendents of BaseTDao
    _encoderClass =
        (Class<T>)
            ((ParameterizedType) getClass().getGenericSuperclass()).getActualTypeArguments()[0];
  }

  private Class<T> getEncoderClass() {
    return _encoderClass;
  }

  public String getDbName() {
    return _dbName;
  }

  public String getCollectionName() {
    return _collectionName;
  }

  public String getNamespace() {
    return _dbName + "." + _collectionName;
  }

  public MongoClient getMongo() {
    return _mongoClient;
  }

  public CodecRegistry getCodecRegistry() {
    return CODEC_REGISTRY;
  }

  protected CodecRegistry initializeCodecRegistry() {
    final List<Convention> conventions = getPojoCodecConventions();

    @SuppressWarnings("rawtypes")
    final ClassModel<Optional> optionalClassModel = ClassModel.builder(Optional.class).build();
    return fromRegistries(
        com.mongodb.MongoClient.getDefaultCodecRegistry(),
        // PojoCodecProvider should be the last register in the list
        fromProviders(
            PojoCodecProvider.builder()
                .automatic(true)
                .register(Optional.class)
                .register(optionalClassModel)
                .conventions(conventions)
                .build()));
  }

  protected List<Convention> getPojoCodecConventions() {
    final ArrayList<Convention> conventions = new ArrayList<>(Conventions.DEFAULT_CONVENTIONS);
    return conventions;
  }

  protected static MongoDatabase getDatabase(
      final MongoClient pClient, final String pDatabaseName) {
    return pClient.getDatabase(pDatabaseName);
  }

  public MongoCollection<T> getCollection() {
    return getDatabase(getMongo(), _dbName)
        .getCollection(_collectionName, getEncoderClass())
        .withCodecRegistry(CODEC_REGISTRY);
  }

  public long countAll() {
    return getCollection().countDocuments();
  }

  public Optional<T> find(final Object pId) {
    return Optional.ofNullable(getCollection().find(eq(pId)).first());
  }

  public Optional<T> findOne(final String pField, final Object pValue) {
    return Optional.ofNullable(getCollection().find(eq(pField, pValue)).first());
  }

  public List<T> findAll() {
    return getCollection().find().into(new ArrayList<>());
  }

  public void insertMajority(final T pDoc) {
    getCollection().withWriteConcern(WriteConcern.MAJORITY).insertOne(pDoc);
  }

  public UpdateResult replaceOneMajority(final Bson pQuery, final T pDoc) {
    return getCollection().withWriteConcern(WriteConcern.MAJORITY).replaceOne(pQuery, pDoc);
  }


  public UpdateResult updateOneMajority(final Bson pQuery, final Bson pUpdate) {
    return updateOneMajority(pQuery, pUpdate, new UpdateOptions());
  }

  public UpdateResult updateOneMajority(
      final Bson pQuery, final Bson pUpdate, final UpdateOptions pOptions) {
    return getCollection()
        .withWriteConcern(WriteConcern.MAJORITY)
        .updateOne(pQuery, pUpdate, pOptions);
  }

  protected void dropCollection() { getCollection().drop(); }

  public static BasicDBObject flatten(final BasicDBObject pDBObject) {
    final BasicDBObject result = new BasicDBObject();

    flatten(pDBObject, "", result);

    return result;
  }

  private static void flatten(
      final BasicDBObject pDBObject, final String prefix, BasicDBObject result) {
    for (final Iterator<String> it = pDBObject.keySet().iterator(); it.hasNext(); ) {
      final String key = it.next();
      final String nextPrefix = (prefix.isEmpty() ? prefix : (prefix + ".")) + key;

      if (pDBObject.get(key) instanceof BasicDBObject) {
        flatten((BasicDBObject) pDBObject.get(key), nextPrefix, result);
      } else if (pDBObject.get(key) instanceof BasicDBList) {
        flatten((BasicDBList) pDBObject.get(key), nextPrefix, result);
      } else {
        result.append(nextPrefix, pDBObject.get(key));
      }
    }
  }

  private static void flatten(
      final BasicDBList pDBList, final String prefix, BasicDBObject result) {
    IntStream.range(0, pDBList.size())
        .forEach(
            idx -> {
              final Object element = pDBList.get(idx);
              final String nextPrefix = (prefix.isEmpty() ? prefix : (prefix + ".")) + idx;

              if (element instanceof BasicDBObject) {
                flatten((BasicDBObject) element, nextPrefix, result);
              } else if (element instanceof BasicDBList) {
                flatten((BasicDBList) element, nextPrefix, result);
              } else {
                result.append(nextPrefix, element);
              }
            });
  }

  public static class UpdateOperators {
    public static final String SET = "$set";
    public static final String UNSET = "$unset";
    public static final String INC = "$inc";
    public static final String PUSH = "$push";
    public static final String PULL = "$pull";
  }

  public static class QueryOperators {
    public static final String LT = "$lt";
    public static final String OR = "$or";
  }
}
