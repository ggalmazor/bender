package com.buntplanet.bender;

import javaslang.monad.Try;

import javax.servlet.http.HttpServletResponse;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.function.Consumer;

public final class Response implements Consumer<HttpServletResponse> {
  private final Map<String, String> headers = new HashMap<>();
  private Optional<String> content = Optional.empty();
  private Status status = Status.PENDING;

  Response() {

  }

  public Map<String, String> headers() {
    return headers;
  }

  public Response content(Object content) {
    return maybeContent(Optional.ofNullable(content));
  }

  public Response maybeContent(Optional<Object> content) {
    this.content = content.map(JsonHelper::serialize);
    if (!this.content.isPresent())
      this.noContent();
    return this;
  }

  public Response ok() {
    this.status = Status.OK;
    return this;
  }

  public Response noContent() {
    this.status = Status.NO_CONTENT;
    return this;
  }

  public Response unauthorized() {
    this.status = Status.UNAUTHORIZED;
    return this;
  }

  public Response notFound() {
    this.status = Status.NOT_FOUND;
    return this;
  }

  public Response badRequest(Throwable t) {
    this.status = Status.BAD_REQUEST;
    this.content(t);
    return this;
  }

  public Response internalServerError(Throwable t) {
    this.status = Status.INTERNAL_SERVER_ERROR;
    this.content(t);
    return this;
  }

  @Override
  public void accept(HttpServletResponse raw) {
    raw.addHeader("Content-Type", "application/json; charset=UTF-8");
    raw.setStatus(status.code);
    content.ifPresent(c -> Try.<PrintWriter>of(raw::getWriter)
            .flatMap(writer -> {
              writer.write(c);
              writer.flush();
              return Try.run(writer::close);
            })
            .orElseThrow(t -> new RuntimeException("Error writing content to HttpServletResponse", t))
    );
  }

  private enum Status {
    PENDING(0),
    OK(200), NO_CONTENT(204),
    BAD_REQUEST(400), UNAUTHORIZED(401), NOT_FOUND(404),
    INTERNAL_SERVER_ERROR(500);

    public final int code;

    Status(int code) {
      this.code = code;
    }

  }
}
