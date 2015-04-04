package com.buntplanet.bender;

import javaslang.monad.Try;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.type.TypeReference;

import java.util.Map;

final class JsonHelper {
  private static final ObjectMapper mapper = new ObjectMapper();

  static Map<String, Object> deserialize(final String json) {
    return Try.<Map<String, Object>>of(() -> mapper.readValue(json, new StringObjectMap()))
        .orElseThrow(t -> new RuntimeException("Error deserializing JSON string", t));
  }

  static String serialize(final Object object) {
    return Try.of(() -> mapper.writeValueAsString(object))
        .orElseThrow(t -> new RuntimeException("Error serializing object", t));
  }

  private static final class StringObjectMap extends TypeReference<Map<String, Object>> {
  }
}
