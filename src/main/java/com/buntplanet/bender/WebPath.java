package com.buntplanet.bender;

import javaslang.monad.Try;
import net.sourceforge.urin.ParseException;
import net.sourceforge.urin.Segment;
import net.sourceforge.urin.scheme.http.Http;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.function.BiFunction;
import java.util.function.BinaryOperator;
import java.util.function.Function;
import java.util.stream.Collector;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import static java.util.stream.Collectors.toMap;

public class WebPath {
  private static final Logger logger = LoggerFactory.getLogger(WebPath.class);
  private final List<WebPathSegment> parts = new ArrayList<>();

  public static WebPath of(String uri) throws ParseException {
    return Http.parseHttpUrinReference(uri).path().segments().stream()
        .map(Segment::value)
        .map(WebPathSegment::of)
        .collect(WebPath.collector());
  }

  public static Collector<WebPathSegment, ?, WebPath> collector() {
    return Collectors.reducing(new WebPath(), (Function<WebPathSegment, WebPath>) (part) -> new WebPath().add(part), (BinaryOperator<WebPath>) WebPath::merge);
  }

  public WebPath merge(WebPath p2) {
    this.parts.addAll(p2.parts);
    return this;
  }

  public WebPath add(WebPathSegment part) {
    this.parts.add(part);
    return this;
  }

  public boolean matches(String path) {
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

  public Map<String, String> capture(String path) {
    try {
      return capture(WebPath.of(path));
    } catch (ParseException e) {
      logger.warn("Error parsing incoming path while trying to capture path parts", e);
      throw new RuntimeException("Error parsing incoming path while trying to capture path parts", e);
    } catch (UnsupportedOperationException e) {
      logger.warn("Error parsing incoming path while trying to capture", e);
      throw new RuntimeException("Error parsing incoming path while trying to capture path parts", e);
    }
  }

  public Map<String, String> capture(WebPath other) {
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

  private class WebPathSegmentMatcher {
    public final WebPathSegment matcher;
    public final WebPathSegment candidate;

    public WebPathSegmentMatcher(WebPathSegment matcher, WebPathSegment candidate) {
      this.matcher = matcher;
      this.candidate = candidate;
    }

    public boolean isMatching() {
      return matcher.matches(candidate);
    }

    public boolean isCapturing() {
      return matcher.isCapturing();
    }
  }

}