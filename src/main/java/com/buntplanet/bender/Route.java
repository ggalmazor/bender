package com.buntplanet.bender;

import javaslang.monad.Try;

import java.net.URI;
import java.util.Objects;
import java.util.function.Function;

final class Route {
  private final HttpMethod httpMethod;
  private final URI path;
  private final WebPath webPath;
  private final Function<Request, Response> target;

  private Route(HttpMethod httpMethod, URI path, WebPath webPath, Function<Request, Response> target) {
    this.httpMethod = httpMethod;
    this.path = path;
    this.webPath = webPath;
    this.target = target;
  }

  static Route of(HttpMethod httpMethod, URI path, Function<Request, Response> target) {
    return Try.of(() -> WebPath.of(path))
        .map(webPath -> new Route(httpMethod, path, webPath, target))
        .get();
  }


  RouteMatch match(HttpMethod httpMethod, URI path) {
    if (this.httpMethod.equals(httpMethod) && webPath.matches(path))
      return RouteMatch.Matching.of(this, path, webPath.capture(path));
    return RouteMatch.NonMatching.of(this);
  }

  RouteMatch match(URI path) {
    if (webPath.matches(path))
      return RouteMatch.Matching.of(this, path);
    return RouteMatch.NonMatching.of(this);
  }

  Function<Request, Response> getTarget() {
    return target;
  }

  public HttpMethod getHttpMethod() {
    return httpMethod;
  }

  @Override
  public boolean equals(final Object obj) {
    if (obj == null)
      return false;
    if (getClass() != obj.getClass())
      return false;

    final Route other = (Route) obj;
    return Objects.equals(this.httpMethod, other.httpMethod)
        && Objects.equals(this.path, other.path)
        && Objects.equals(this.target, other.target);
  }

  @Override
  public int hashCode() {
    return Objects.hash(this.httpMethod, this.path, this.target);
  }
}
