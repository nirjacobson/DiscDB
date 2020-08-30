package com.nirjacobson.discdb.util;

import com.fasterxml.jackson.annotation.JsonView;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.databind.type.TypeFactory;
import com.fasterxml.jackson.datatype.jdk8.Jdk8Module;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Reader;
import java.io.Writer;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;
import java.util.Optional;
import javax.ws.rs.Consumes;
import javax.ws.rs.Produces;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;
import javax.ws.rs.ext.MessageBodyReader;
import javax.ws.rs.ext.MessageBodyWriter;
import javax.ws.rs.ext.Provider;
import org.bson.types.ObjectId;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Provider
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class CustomJacksonJsonProvider
    implements MessageBodyReader<Object>, MessageBodyWriter<Object> {

  private static final Logger LOG = LoggerFactory.getLogger(CustomJacksonJsonProvider.class);

  @Context private UriInfo _uriInfo;

  private static ObjectMapper _mapper = createObjectMapper();

  @Override
  public boolean isReadable(
      Class<?> pType, Type pGenericType, Annotation[] pAnnotations, MediaType pMediaType) {
    if (pType == char[].class
        || pType == byte[].class
        || pType == String.class
        || pType == InputStream.class
        || pType == Reader.class
        || pType == Optional.class) {
      return false;
    }
    return pMediaType.isCompatible(MediaType.APPLICATION_JSON_TYPE)
        && _mapper.canDeserialize(TypeFactory.defaultInstance().constructType(pGenericType));
  }

  @Override
  public Object readFrom(
      Class<Object> aClass,
      Type pType,
      Annotation[] pAnnotations,
      MediaType pMediaType,
      MultivaluedMap<String, String> pMultivaluedMap,
      InputStream pInputStream)
      throws IOException, WebApplicationException {
    try {
      return _mapper
          .readerFor(TypeFactory.defaultInstance().constructType(pType))
          .readValue(pInputStream);
    } catch (final Throwable t) {
      LOG.error("Failed to read object from JSON", t);
      throw t;
    }
  }

  @Override
  public boolean isWriteable(
      final Class<?> pType,
      final Type pGenericType,
      final Annotation[] pAnnotations,
      final MediaType pMediaType) {
    if (pType == char[].class
        || pType == byte[].class
        || pType == String.class
        || pType == Response.class
        || pType == OutputStream.class
        || pType == Writer.class
        || pType == Optional.class) {
      return false;
    }
    return pMediaType.isCompatible(MediaType.APPLICATION_JSON_TYPE) && _mapper.canSerialize(pType);
  }

  @Override
  public void writeTo(
      final Object pObject,
      final Class<?> pType,
      final Type pGenericType,
      final Annotation[] pAnnotations,
      final MediaType pMediaType,
      final MultivaluedMap<String, Object> pHttpHeaders,
      final OutputStream pEntityStream)
      throws IOException {
    try {
      ObjectWriter writer;
      if (_uriInfo.getQueryParameters().containsKey("pretty")
          && Boolean.valueOf(_uriInfo.getQueryParameters().getFirst("pretty"))) {
        writer = _mapper.writer(SerializationFeature.INDENT_OUTPUT);
      } else {
        writer = _mapper.writer();
      }
      writer = applyView(writer, pAnnotations);
      writer.writeValue(pEntityStream, pObject);
    } catch (final Throwable t) {
      LOG.error("Failed to write object to JSON", t);
    }
  }

  private ObjectWriter applyView(ObjectWriter writer, Annotation[] pAnnotations) {
    for (Annotation pAnnotation : pAnnotations) {
      if (pAnnotation instanceof JsonView) {
        Class<?>[] value = ((JsonView) pAnnotation).value();
        return writer.withView(value[0]);
      }
    }

    return writer;
  }

  public static ObjectMapper createObjectMapper() {

    return new ObjectMapper()
        .disable(
            MapperFeature.AUTO_DETECT_GETTERS,
            MapperFeature.AUTO_DETECT_IS_GETTERS,
            MapperFeature.AUTO_DETECT_FIELDS,
            MapperFeature.AUTO_DETECT_SETTERS,
            MapperFeature.USE_GETTERS_AS_SETTERS,
            MapperFeature.INFER_PROPERTY_MUTATORS)
        // Ensure sorted key order: default order is unspecified (based on what JDK gives us, which
        // may be declaration order, but is not guaranteed).
        .enable(MapperFeature.SORT_PROPERTIES_ALPHABETICALLY)
        // Ensure maps are sorted as well - MapperFeature sorting does not apply to non-SortedMap
        .enable(SerializationFeature.ORDER_MAP_ENTRIES_BY_KEYS)
        .registerModule(createSerializationModule())
        .registerModule(new Jdk8Module());
  }

  private static SimpleModule createSerializationModule() {
    // Register Module for MongoDB Driver class conversions (below for now, since it's just
    // ObjectId)
    final SimpleModule module = new SimpleModule("DiscDBModule");
    module.addSerializer(ObjectId.class, new ObjectIdSerializer());
    module.addDeserializer(ObjectId.class, new ObjectIdDeserializer());
    return module;
  }

  private static class ObjectIdSerializer extends JsonSerializer<ObjectId> {
    @Override
    public void serialize(
        final ObjectId value, final JsonGenerator jgen, final SerializerProvider provider)
        throws IOException, JsonProcessingException {
      jgen.writeString(value.toString());
    }
  }

  private static class ObjectIdDeserializer extends JsonDeserializer<ObjectId> {
    @Override
    public ObjectId deserialize(final JsonParser jp, final DeserializationContext ctxt)
        throws IOException, JsonProcessingException {
      return new ObjectId(jp.getText());
    }
  }
}
