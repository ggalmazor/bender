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

  public Response content(Object content) {
    this.content = Optional.ofNullable(JsonHelper.serialize(content));
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

  public Response notFound() {
    this.status = Status.NOT_FOUND;
    return this;
  }

  @Override
  public void accept(HttpServletResponse raw) {
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
    NOT_FOUND(404);

    public final int code;

    Status(int code) {
      this.code = code;
    }
  }
}
