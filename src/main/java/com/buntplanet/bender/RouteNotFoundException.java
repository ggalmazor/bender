package com.buntplanet.bender;


import java.util.function.Supplier;

final class RouteNotFoundException extends RuntimeException {

  RouteNotFoundException(HttpMethod httpMethod, String path) {
    super("Route " + httpMethod + " " + path + " not found");
  }

  public RouteNotFoundException(String message) {
    super(message);
  }

  public static Supplier<RouteNotFoundException> supplierOf(HttpMethod httpMethod, String path) {
    return () -> new RouteNotFoundException(httpMethod, path);
  }
}
