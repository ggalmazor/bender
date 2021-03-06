package net.programania.bender;

import java.util.Collection;
import java.util.Map;
import java.util.stream.Stream;

import static java.util.stream.Collectors.toMap;

final class StreamHelper {

  static <T1, T2> void mergeInto(Map<T1, T2> output, Stream<Map<T1, T2>> maps) {
    maps
        .map(Map::entrySet)
        .flatMap(Collection::stream)
        .collect(toMap(Map.Entry::getKey, Map.Entry::getValue, (v1, v2) -> v2, () -> output));
  }
}
