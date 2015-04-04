package com.buntplanet.bender;

import javaslang.monad.Try;
import org.apache.commons.io.IOUtils;

import javax.servlet.ServletInputStream;
import javax.servlet.http.HttpServletRequest;
import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.Map;

public final class Request {
  private final HttpServletRequest rawRequest;
  private final Map<String, String> params;
  private Map<String, Object> payload = null;

  private Request(final HttpServletRequest rawRequest, final Map<String, String> params) {
    this.rawRequest = rawRequest;
    this.params = params;
  }

  static Request of(final HttpServletRequest rawRequest, final Map<String, String> params) {
    return new Request(rawRequest, params);
  }

  public Map<String, Object> payload() {
    if (payload == null)
      payload = Try.<ServletInputStream>of(rawRequest::getInputStream)
          .flatMap(sis -> Try.of(() -> IOUtils.toString(sis, Charset.forName("UTF-8"))))
          .map(JsonHelper::deserialize)
          .orElse(new HashMap<>());
    return payload;
  }

  public Response buildResponse() {
    return new Response();
  }

  public Map<String, String> params() {
    return params;
  }
}
