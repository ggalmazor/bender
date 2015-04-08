package net.programania.bender;


import javaslang.control.Try;

enum HttpMethod {
  GET, POST, OPTIONS;

  static HttpMethod iValueOf(final String name) {
    return Try.of(() -> valueOf(name.toUpperCase())).get();
  }
}
