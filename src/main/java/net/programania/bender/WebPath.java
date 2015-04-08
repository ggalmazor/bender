package net.programania.bender;

import javaslang.control.Try;
import net.sourceforge.urin.ParseException;
import net.sourceforge.urin.Segment;
import net.sourceforge.urin.scheme.http.Http;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.URI;
import java.util.*;
import java.util.function.BiFunction;
import java.util.function.BinaryOperator;
import java.util.function.Function;
import java.util.stream.Collector;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import static java.util.stream.Collectors.toMap;

final class WebPath {
  private static final Logger logger = LoggerFactory.getLogger(WebPath.class);
  private final List<WebPathSegment> parts = new ArrayList<>();

  static WebPath of(URI uri) throws ParseException {
    return Http.parseHttpUrinReference(uri).path().segments().stream()
        .map(Segment::value)
        .map(WebPathSegment::of)
        .collect(WebPath.collector());
  }

  static Collector<WebPathSegment, ?, WebPath> collector() {
    return Collectors.reducing(new WebPath(), (Function<WebPathSegment, WebPath>) (part) -> new WebPath().add(part), (BinaryOperator<WebPath>) WebPath::merge);
  }

  WebPath merge(WebPath p2) {
    this.parts.addAll(p2.parts);
    return this;
  }

  WebPath add(WebPathSegment part) {
    this.parts.add(part);
    return this;
  }

  boolean matches(URI path) {
    return Try.of(() -> matches(WebPath.of(path)))
        .orElseGet(t -> {
          logger.warn("Error parsing incoming path while trying to know if it matches a route", t);
          return false;
        });
  }

  private boolean matches(WebPath other) {
    return this.parts.size() == other.parts.size()
        && IntStream.range(0, this.parts.size()).boxed()
        .map(index -> new WebPathSegmentMatcher(this.parts.get(index), other.parts.get(index)))
        .allMatch(WebPathSegmentMatcher::isMatching);
  }

  Map<String, String> capture(URI path) {
    return Try.of(() -> capture(WebPath.of(path))).get();
  }

  Map<String, String> capture(WebPath other) {
    HashMap<String, String> params = new HashMap<>();

    if (!this.matches(other))
      return params;

    BinaryOperator<Map<String, String>> merger = (m1, m2) -> Stream.of(m1, m2)
        .map(Map::entrySet)
        .flatMap(Collection::stream)
        .collect(toMap(Map.Entry::getKey, Map.Entry::getValue, (v1, v2) -> v2));

    BiFunction<Map<String, String>, WebPathSegmentMatcher, Map<String, String>> accumulator = (accum, t) -> {
      accum.put(t.matcher.getName(), t.candidate.getName());
      return accum;
    };

    return IntStream.range(0, this.parts.size()).boxed()
        .map(index -> new WebPathSegmentMatcher(this.parts.get(index), other.parts.get(index)))
        .filter(WebPathSegmentMatcher::isCapturing)
        .reduce(params, accumulator, merger);
  }

  private final class WebPathSegmentMatcher {
    private final WebPathSegment matcher;
    private final WebPathSegment candidate;

    private WebPathSegmentMatcher(WebPathSegment matcher, WebPathSegment candidate) {
      this.matcher = matcher;
      this.candidate = candidate;
    }

    private boolean isMatching() {
      return matcher.matches(candidate);
    }

    private boolean isCapturing() {
      return matcher.isCapturing();
    }
  }

}