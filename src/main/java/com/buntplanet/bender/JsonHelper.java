package com.buntplanet.bender;

import javaslang.monad.Try;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.type.TypeReference;

import java.util.Map;

class JsonHelper {
  private static final ObjectMapper mapper = new ObjectMapper();

  public static Map<String, Object> deserialize(String json) {
    return Try.<Map<String, Object>>of(() -> mapper.readValue(json, new StringObjectMap()))
        .orElseThrow(t -> new RuntimeException("Error deserializing JSON string", t));
  }

  public static String serialize(Object object) {
    return Try.of(() -> mapper.writeValueAsString(object))
        .orElseThrow(t -> new RuntimeException("Error serializing object", t));
  }

  private static class StringObjectMap extends TypeReference<Map<String, Object>> {
  }
}
