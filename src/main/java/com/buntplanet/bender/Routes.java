package com.buntplanet.bender;


import java.net.URI;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.function.Function;

import static java.util.stream.Collectors.toList;

final class Routes {
  private final Set<Route> entries;

  Routes(Set<Route> entries) {
    this.entries = entries;
  }

  static Routes empty() {
    return new Routes(new HashSet<>());
  }

  void add(HttpMethod method, URI path, Function<Request, Response> target) {
    add(Route.of(method, path, target));
  }

  private void add(Route entry) {
    if (entries.contains(entry))
      throw new RuntimeException("Route has already been defined");
    entries.add(entry);
  }

  Optional<RouteMatch> findOneMatching(HttpMethod httpMethod, URI path) {
    return entries.stream()
        .map(matches(httpMethod, path))
        .filter(RouteMatch::matches)
        .findFirst();
  }

  List<RouteMatch> findMatching(URI path) {
    return entries.stream()
        .map(matches(path))
        .filter(RouteMatch::matches)
        .collect(toList());
  }

  private Function<Route, RouteMatch> matches(HttpMethod httpMethod, URI path) {
    return route -> route.match(httpMethod, path);
  }

  private Function<Route, RouteMatch> matches(URI path) {
    return route -> route.match(path);
  }
}
