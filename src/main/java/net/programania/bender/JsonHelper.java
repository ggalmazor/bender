package net.programania.bender;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import javaslang.control.Try;

import java.util.Map;

final class JsonHelper {
  private static final ObjectMapper mapper = new ObjectMapper();

  static <T> Map<String, T> deserialize(final String json) {
    return Try.<Map<String, T>>of(() -> mapper.readValue(json, new TypedValueMap<T>())).get();
  }

  static String serialize(final Object object) {
    return Try.of(() -> mapper.writeValueAsString(object)).get();
  }

  private static final class TypedValueMap<T> extends TypeReference<Map<String, T>> {
  }
}
