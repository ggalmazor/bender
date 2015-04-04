package com.buntplanet.bender;

import javaslang.monad.Try;
import org.apache.commons.io.IOUtils;

import javax.servlet.http.HttpServletRequest;
import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.Map;

public class Request {
  public final HttpServletRequest raw;
  public final Map<String, String> params;
  private Map<String, Object> payload = null;

  private Request(HttpServletRequest raw, Map<String, String> params) {
    this.raw = raw;
    this.params = params;
  }


  public static Request from(HttpServletRequest raw, Map<String, String> params) {
    return new Request(raw, params);
  }

  public Map<String, Object> payload() {
    if (payload == null)
      payload = Try.of(() -> raw.getInputStream())
          .flatMap(sis -> Try.of(() -> IOUtils.toString(sis, Charset.forName("UTF-8"))))
          .map(JsonHelper::deserialize)
          .orElse(new HashMap<>());
    return payload;
  }

  public Response buildResponse() {
    return new Response();
  }
}
