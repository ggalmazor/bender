package com.buntplanet.bender;

import javaslang.monad.Try;
import net.sourceforge.urin.scheme.http.Http;
import net.sourceforge.urin.scheme.http.HttpQuery;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.http.HttpServletRequest;
import java.net.URI;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.StreamSupport;

import static java.util.stream.Collectors.toMap;

abstract class RouteMatch {
  private final Route route;
  protected final Map<String, String> params = new HashMap<>();

  protected RouteMatch(Route route) {
    this.route = route;
  }

  protected abstract boolean matches();

  public Route getRoute() {
    return route;
  }

  private Response execute(HttpServletRequest httpServletRequest) {
    return route.getTarget().apply(Request.of(httpServletRequest, params));
  }

  static Function<RouteMatch, Response> executeWith(HttpServletRequest httpServletRequest) {
    return routeMatch -> routeMatch.execute(httpServletRequest);
  }

  final static class Matching extends RouteMatch {
    private static final Logger logger = LoggerFactory.getLogger(Matching.class);

    protected Matching(Route route) {
      super(route);
    }

    static Matching of(Route route, URI inputPath, Map<String, String> pathParams) {
      Matching matchingRoute = new Matching(route);
      StreamHelper.mergeInto(matchingRoute.params, pathParams, parseQueryParams(inputPath));
      return matchingRoute;
    }

    static Matching of(Route route, URI inputPath) {
      Matching matchingRoute = new Matching(route);
      StreamHelper.mergeInto(matchingRoute.params, new HashMap<>(), parseQueryParams(inputPath));
      return matchingRoute;
    }

    static Map<String, String> parseQueryParams(URI uri) {
      return Try.of(() -> Http.parseHttpUrinReference(uri).query().spliterator())
          .map(spliterator -> StreamSupport.stream(spliterator, false))
          .map(stream -> stream.collect(toMap(HttpQuery.QueryParameter::name, HttpQuery.QueryParameter::value, (v1, v2) -> v2)))
          .toOption()
          .orElse(new HashMap<>());
    }

    @Override
    protected boolean matches() {
      return true;
    }
  }

  final static class NonMatching extends RouteMatch {
    protected NonMatching(Route route) {
      super(route);
    }

    static NonMatching of(Route route) {
      return new NonMatching(route);
    }

    @Override
    protected boolean matches() {
      return false;
    }
  }
}
