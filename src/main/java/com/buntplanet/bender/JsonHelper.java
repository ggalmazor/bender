package com.buntplanet.bender;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import javaslang.monad.Try;

import java.util.Map;

final class JsonHelper {
  private static final ObjectMapper mapper = new ObjectMapper();

  static Map<String, Object> deserialize(final String json) {
    return Try.<Map<String, Object>>of(() -> mapper.readValue(json, new StringObjectMap())).get();
  }

  static String serialize(final Object object) {
    return Try.of(() -> mapper.writeValueAsString(object)).get();
  }

  private static final class StringObjectMap extends TypeReference<Map<String, Object>> {
  }
}
