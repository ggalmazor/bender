package net.programania.bender;

import javaslang.control.Try;
import net.sourceforge.urin.scheme.http.Http;
import net.sourceforge.urin.scheme.http.HttpQuery;

import java.net.URI;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

import static java.util.stream.Collectors.toMap;

public abstract class RouteMatch<REQ, RES> {
  private final Route<REQ, RES> route;
  protected final Map<String, String> params = new HashMap<>();

  protected RouteMatch(Route<REQ, RES> route) {
    this.route = route;
  }

  protected abstract boolean matches();

  public Route<REQ, RES> getRoute() {
    return route;
  }

  public Map<String, String> getParams() {
    return params;
  }

  public final static class Matching<M_REQ, M_RES> extends RouteMatch<M_REQ, M_RES> {

    protected Matching(Route<M_REQ, M_RES> route) {
      super(route);
    }

    static <M_I_REQ, M_I_RES> Matching of(Route<M_I_REQ, M_I_RES> route, URI inputPath, Map<String, String> pathParams) {
      Matching<M_I_REQ, M_I_RES> matchingRoute = new Matching<>(route);
      StreamHelper.mergeInto(matchingRoute.params, Stream.of(pathParams, parseQueryParams(inputPath)));
      return matchingRoute;
    }

    static <M_I_REQ, M_I_RES> Matching of(Route<M_I_REQ, M_I_RES> route, URI inputPath) {
      Matching<M_I_REQ, M_I_RES> matchingRoute = new Matching<>(route);
      StreamHelper.mergeInto(matchingRoute.params, Stream.of(new HashMap<>(), parseQueryParams(inputPath)));
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

  public final static class NonMatching<NM_REQ, NM_RES> extends RouteMatch<NM_REQ, NM_RES> {
    protected NonMatching(Route<NM_REQ, NM_RES> route) {
      super(route);
    }

    static <NM_I_REQ, NM_I_RES> NonMatching of(Route<NM_I_REQ, NM_I_RES> route) {
      return new NonMatching<>(route);
    }

    @Override
    protected boolean matches() {
      return false;
    }
  }
}
