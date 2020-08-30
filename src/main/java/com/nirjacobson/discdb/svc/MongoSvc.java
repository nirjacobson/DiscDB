package com.nirjacobson.discdb.svc;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import java.util.Optional;
import javax.inject.Singleton;

@Singleton
public class MongoSvc implements AutoCloseable {

  private final String MONGO_URI = "%s://%s:%s@%s/%s?retryWrites=true&w=majority";

  private final MongoClient _mongoClient = getMongoClient();

  public MongoClient getMongo() {
    return _mongoClient;
  }

  private MongoClient getMongoClient() {
    final String mongoProtocol =
        System.getenv("DISCDB_MONGO_SRV") == null ? "mongodb" : "mongodb+srv";
    final String mongoUser = System.getenv("DISCDB_MONGO_USER");
    final String mongoPassword = System.getenv("DISCDB_MONGO_PASSWORD");
    final String mongoHost = System.getenv("DISCDB_MONGO_HOST");
    final String mongoAuthDb =
        Optional.ofNullable(System.getenv("DISCDB_MONGO_AUTH_DB")).orElse("");

    if (mongoUser == null || mongoPassword == null || mongoHost == null) {
      throw new IllegalStateException("The mongoDB connection environment variables are not set.");
    }

    return MongoClients.create(
        String.format(MONGO_URI, mongoProtocol, mongoUser, mongoPassword, mongoHost, mongoAuthDb));
  }

  @Override
  public void close() {
    _mongoClient.close();
  }
}
