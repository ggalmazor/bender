package com.buntplanet.bender;

enum HttpMethod {
  GET, POST;

  static HttpMethod iValueOf(final String name) {
    return valueOf(name.toUpperCase());
  }
}
