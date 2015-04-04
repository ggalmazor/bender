package com.buntplanet.bender;

import javaslang.monad.Try;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.handler.AbstractHandler;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Optional;
import java.util.function.Function;

public final class Bender implements Runnable {
  private final Routes routes;
  private final int port;

  private Bender(int port, Routes routes) {
    this.port = port;
    this.routes = routes;
  }

  public static Bender at(int port) {
    return new Bender(port, Routes.empty());
  }

  public Bender post(final String path, final Function<Request, Response> handler) {
    routes.add(HttpMethod.POST, path, handler);
    return this;
  }

  public Bender get(final String path, final Function<Request, Response> handler) {
    routes.add(HttpMethod.GET, path, handler);
    return this;
  }

  @Override
  public void run() {
    Server server = new Server(port);

    server.setHandler(new AbstractHandler() {
      @Override
      public void handle(String path, org.eclipse.jetty.server.Request jettyRequest, HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse) throws IOException, ServletException {
        String pathWithQueryString = Optional.ofNullable(jettyRequest.getQueryString())
            .map(qs -> path + "?" + qs).orElse(path);
        HttpMethod httpMethod = HttpMethod.iValueOf(jettyRequest.getMethod());

        routes.findOneMatching(httpMethod, pathWithQueryString)
            .map(RouteMatch.executeWith(httpServletRequest))
            .orElse(new Response().notFound())
            .accept(httpServletResponse);
      }
    });

    Try.run(server::start);
    Try.run(server::join);
  }
}
