package com.buntplanet.bender;

import javaslang.monad.Try;
import org.apache.commons.io.IOUtils;

import javax.servlet.ServletInputStream;
import javax.servlet.http.HttpServletRequest;
import java.nio.charset.Charset;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;

public final class Request {
  private final HttpServletRequest rawRequest;
  private final Map<String, String> params;
  private final Map<String, String> headers;
  private Map<String, Object> payload = null;

  Request(final HttpServletRequest rawRequest, final Map<String, String> params, final Map<String, String> headers) {
    this.rawRequest = rawRequest;
    this.params = params;
    this.headers = headers;
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

  public Map<String, String> headers() {
    return headers;
  }

  public static Request of(HttpServletRequest rawRequest, Map<String, String> params) {
    Map<String, String> headers = new HashMap<>();
    Enumeration<String> headerNames = rawRequest.getHeaderNames();
    while (headerNames.hasMoreElements()) {
      String name = headerNames.nextElement();
      headers.put(name, rawRequest.getHeader(name));
    }
    return new Request(rawRequest, params, headers);
  }
}
