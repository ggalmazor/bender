package com.buntplanet.bender;


import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import java.util.function.Function;

class Routes {
  private final Set<Route> entries;

  Routes(Set<Route> entries) {
    this.entries = entries;
  }

  static Routes empty() {
    return new Routes(new HashSet<>());
  }

  void add(HttpMethod method, String path, Function<Request, Response> target) {
    add(Route.of(method, path, target));
  }

  private void add(Route entry) {
    if (entries.contains(entry))
      throw new RuntimeException("Route has already been defined");
    entries.add(entry);
  }

  Optional<RouteMatch> findOneMatching(HttpMethod httpMethod, String path) {
    return entries.stream()
        .map(matches(httpMethod, path))
        .filter(RouteMatch::matches)
        .findFirst();
  }

  private Function<Route, RouteMatch> matches(HttpMethod httpMethod, String path) {
    return route -> route.match(httpMethod, path);
  }
}
