package com.buntplanet.bender;

enum HttpMethod {
  GET, POST;

  public static HttpMethod iValueOf(String name) {
    return valueOf(name.toUpperCase());
  }
}
