package com.buntplanet.bender;

import net.sourceforge.urin.ParseException;
import net.sourceforge.urin.scheme.http.Http;
import net.sourceforge.urin.scheme.http.HttpQuery;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.StreamSupport;

import static java.util.stream.Collectors.toMap;

public abstract class RouteMatch {
  private final Route route;
  protected final Map<String, String> params = new HashMap<>();

  protected RouteMatch(Route route) {
    this.route = route;
  }

  public abstract boolean matches();

  public Response execute(HttpServletRequest httpServletRequest) {
    return route.getTarget().apply(Request.from(httpServletRequest, params));
  }

  public static Function<RouteMatch, Response> executeWith(HttpServletRequest httpServletRequest) {
    return routeMatch -> routeMatch.execute(httpServletRequest);
  }

  public static class Matching extends RouteMatch {
    private static final Logger logger = LoggerFactory.getLogger(Matching.class);

    protected Matching(Route route) {
      super(route);
    }

    static Matching of(Route route, String inputPath, Map<String, String> pathParams) {
      Matching matchingRoute = new Matching(route);
      StreamHelper.mergeInto(matchingRoute.params, pathParams, parseQueryParams(inputPath));
      return matchingRoute;
    }

    static Map<String, String> parseQueryParams(String uri) {
      try {
        return StreamSupport.stream(Http.parseHttpUrinReference(uri).query().spliterator(), false)
            .collect(toMap(HttpQuery.QueryParameter::name, HttpQuery.QueryParameter::value, (v1, v2) -> v2));
      } catch (ParseException e) {
        logger.error("Can't deserialize incoming path", e);
        throw new RouteNotFoundException("Can't deserialize incoming path");
      } catch (UnsupportedOperationException e) {
        logger.debug("Uri doesn't have query params");
        return new HashMap<>();
      }
    }

    @Override
    public boolean matches() {
      return true;
    }
  }

  public static class NonMatching extends RouteMatch {
    protected NonMatching(Route route) {
      super(route);
    }

    static NonMatching of(Route route) {
      return new NonMatching(route);
    }

    @Override
    public boolean matches() {
      return false;
    }
  }
}