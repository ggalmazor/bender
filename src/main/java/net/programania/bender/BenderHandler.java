package net.programania.bender;

import javaslang.control.*;
import org.eclipse.jetty.server.handler.AbstractHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.net.URI;
import java.util.Optional;

import static java.util.stream.Collectors.joining;

class BenderHandler extends AbstractHandler {
  private final static Logger LOGGER = LoggerFactory.getLogger(BenderHandler.class);
  private final Routes routes;

  public BenderHandler(Routes routes) {
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
    return Optional.of(routes.findMatching(uri).stream()
        .map(RouteMatch::getRoute)
        .map(Route::getHttpMethod)
        .map(Enum::name)
        .collect(joining(",")))
        .filter(String::isEmpty)
        .map(Response::cors)
        .orElse(new Response().notFound());
  }

  private Response process(HttpServletRequest httpServletRequest) {
    HttpMethod httpMethod = HttpMethod.iValueOf(httpServletRequest.getMethod());
    String path = httpServletRequest.getPathInfo();
    String pathWithQueryString = Optional.ofNullable(httpServletRequest.getQueryString())
        .map(qs -> path + "?" + qs)
        .orElse(path);

    URI uri = Try.of(() -> new URI(pathWithQueryString)).get();

    return routes.findFirstMatching(httpMethod, uri)
        .map(RouteMatch.executeWith(httpServletRequest))
        .orElse(new Response().notFound());
  }

}
