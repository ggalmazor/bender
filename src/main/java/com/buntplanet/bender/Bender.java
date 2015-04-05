package com.buntplanet.bender;

import javaslang.monad.Try;
import org.eclipse.jetty.server.Server;

import java.net.URI;
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
    URI uri = Try.of(() -> new URI(path)).orElseThrow(t -> new RuntimeException("Wrong URI syntax", t));
    return post(uri, handler);
  }

  public Bender post(final URI path, final Function<Request, Response> handler) {
    routes.add(HttpMethod.POST, path, handler);
    return this;
  }

  public Bender get(final String path, final Function<Request, Response> handler) {
    URI uri = Try.of(() -> new URI(path)).orElseThrow(t -> new RuntimeException("Wrong URI syntax", t));
    return get(uri, handler);
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
