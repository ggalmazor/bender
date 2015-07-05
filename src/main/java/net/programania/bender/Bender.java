package net.programania.bender;

import javaslang.control.Try;
import org.eclipse.jetty.server.Server;

import java.net.URI;
import java.util.function.Function;

public final class Bender implements Runnable {
  private final Routes<Request, Response> routes;
  private final int port;

  private Bender(int port, Routes<Request, Response> routes) {
    this.port = port;
    this.routes = routes;
  }

  public static Bender at(int port) {
    return new Bender(port, Routes.empty());
  }

  public Bender post(final String path, final Function<Request, Response> handler) {
    return Try.of(() -> new URI(path))
        .map(uri -> post(uri, handler)).get();
  }

  public Bender post(final URI path, final Function<Request, Response> handler) {
    routes.add(HttpMethod.POST, path, handler);
    return this;
  }

  public Bender get(final String path, final Function<Request, Response> handler) {
    return Try.of(() -> new URI(path))
        .map(uri -> get(uri, handler)).get();
  }

  public Bender get(final URI path, final Function<Request, Response> handler) {
    routes.add(HttpMethod.GET, path, handler);
    return this;
  }

  @Override
  public void run() {
    Server server = new Server(port);
    server.setHandler(new BenderHandler(routes));
    Try.run(server::start);
    Try.run(server::join);
  }

}
