package net.programania.bender;

import javaslang.control.Match;
import javaslang.control.Try;
import org.eclipse.jetty.server.handler.AbstractHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.net.URI;
import java.util.Optional;
import java.util.function.Function;

import static java.util.stream.Collectors.joining;

class BenderHandler extends AbstractHandler {
  private final static Logger LOGGER = LoggerFactory.getLogger(BenderHandler.class);
  private final Routes<Request, Response> routes;

  BenderHandler(Routes<Request, Response> routes) {
    this.routes = routes;
  }

  @Override
  public void handle(String path, org.eclipse.jetty.server.Request jettyRequest, HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse) throws IOException, ServletException {
    Response response = Try
        .of(() -> {
          HttpMethod httpMethod = HttpMethod.iValueOf(httpServletRequest.getMethod());
          return HttpMethod.OPTIONS.equals(httpMethod) ? processOptions(httpServletRequest) : process(httpServletRequest);
        })
        .orElseGet(t -> {
          LOGGER.warn("Error processing request", t);
          return Match
              .caze((IllegalArgumentException iae) -> new Response().badRequest(t))
              .orElse(new Response().internalServerError(t))
              .apply(t);
        });
    response.accept(httpServletResponse);
    jettyRequest.setHandled(true);
  }

  private Response processOptions(HttpServletRequest httpServletRequest) {
    URI uri = Try.of(() -> new URI(httpServletRequest.getPathInfo())).get();
    String methods = routes.findMatching(uri).stream()
        .map(RouteMatch::getRoute)
        .map(Route::getHttpMethod)
        .map(Enum::name)
        .collect(joining(","));
    if (methods.isEmpty())
      return new Response().notFound();
    return Response.cors(methods);
  }

  private Response process(HttpServletRequest httpServletRequest) {
    HttpMethod httpMethod = HttpMethod.iValueOf(httpServletRequest.getMethod());
    String path = httpServletRequest.getPathInfo();
    String pathWithQueryString = Optional.ofNullable(httpServletRequest.getQueryString())
        .map(qs -> path + "?" + qs)
        .orElse(path);

    URI uri = Try.of(() -> new URI(pathWithQueryString)).get();

    return routes.findFirstMatching(httpMethod, uri)
        .map(routeMatch -> {
          Request request = Request.of(httpServletRequest, routeMatch.getParams());
          Function<Request, Response> target = routeMatch.getRoute().getTarget();
          return target.apply(request);
        })
        .orElse(new Response().notFound());
  }

}
