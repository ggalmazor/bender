package com.buntplanet.bender;

import javaslang.monad.Try;

enum HttpMethod {
  GET, POST, OPTIONS;

  static HttpMethod iValueOf(final String name) {
    return Try.of(() -> valueOf(name.toUpperCase())).get();
  }
}
