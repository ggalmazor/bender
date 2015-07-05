package net.programania.bender;

import java.net.URI;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.function.Function;

import static java.util.stream.Collectors.toList;

public final class Routes<REQ, RES> {
  private final Set<Route<REQ, RES>> entries;

  Routes(Set<Route<REQ, RES>> entries) {
    this.entries = entries;
  }

  public static <I_REQ, I_RES> Routes<I_REQ, I_RES> empty() {
    return new Routes<>(new HashSet<>());
  }

  public void add(HttpMethod method, URI path, Function<REQ, RES> target) {
    add(Route.of(method, path, target));
  }

  private void add(Route<REQ, RES> entry) {
    if (entries.contains(entry))
      throw new RuntimeException("Route has already been defined");
    entries.add(entry);
  }

  public Optional<RouteMatch<REQ, RES>> findFirstMatching(HttpMethod httpMethod, URI path) {
    return entries.stream()
        .map(matches(httpMethod, path))
        .filter(RouteMatch::matches)
        .findFirst();
  }

  public List<RouteMatch<REQ, RES>> findMatching(URI path) {
    return entries.stream()
        .map(matches(path))
        .filter(RouteMatch::matches)
        .collect(toList());
  }

  private Function<Route<REQ, RES>, RouteMatch<REQ, RES>> matches(HttpMethod httpMethod, URI path) {
    return route -> route.match(httpMethod, path);
  }

  private Function<Route<REQ, RES>, RouteMatch<REQ, RES>> matches(URI path) {
    return route -> route.match(path);
  }
}
