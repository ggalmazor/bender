package net.programania.bender;

import javaslang.control.Try;

import javax.servlet.http.HttpServletResponse;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.function.Consumer;

public final class Response implements Consumer<HttpServletResponse> {
  private final Map<String, String> headers = new HashMap<>();
  private Optional<String> content = Optional.empty();
  private Status status = Status.OK;

  Response() {
    // Prevent Response creation from the outside of this package
  }

  public Response header(String key, String value) {
    headers.put(key, value);
    return this;
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

  public Response badRequest() {
    this.status = Status.BAD_REQUEST;
    return this;
  }

  public Response badRequest(Throwable t) {
    this.status = Status.BAD_REQUEST;
    this.content(t);
    return this;
  }

  public Response internalServerError() {
    this.status = Status.INTERNAL_SERVER_ERROR;
    return this;
  }

  public Response internalServerError(Throwable t) {
    this.status = Status.INTERNAL_SERVER_ERROR;
    this.content(t);
    return this;
  }

  @Override
  public void accept(HttpServletResponse raw) {
    raw.setStatus(status.code);
    headers.keySet().forEach(name -> raw.addHeader(name, headers.get(name)));
    content.ifPresent(c -> Try.run(() -> {
      PrintWriter writer = raw.getWriter();
      writer.write(c);
      writer.flush();
      writer.close();
    }));
  }

  public static Response cors(String httpMethods) {
    Response response = new Response();
    response.header("Access-Control-Allow-Origin", "*");
    response.header("Access-Control-Allow-Methods", httpMethods + ",OPTIONS");
    response.header("Access-Control-Allow-Headers", "Content-Type,Accept,Origin,X-Auth-Token");
    return response;
  }

  private enum Status {
    OK(200), NO_CONTENT(204),
    BAD_REQUEST(400), UNAUTHORIZED(401), NOT_FOUND(404),
    INTERNAL_SERVER_ERROR(500);

    public final int code;

    Status(int code) {
      this.code = code;
    }

  }
}
