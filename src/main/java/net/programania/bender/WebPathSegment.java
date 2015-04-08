package net.programania.bender;

class WebPathSegment {
  private final String name;

  WebPathSegment(String name) {
    this.name = name;
  }

  static WebPathSegment of(String value) {
    if (value.startsWith(":"))
      return new CapturingWebPathSegment(value.substring(1));
    return new WebPathSegment(value);
  }

  boolean isCapturing() {
    return false;
  }

  boolean matches(String value) {
    return this.name.equals(value);
  }

  boolean matches(WebPathSegment other) {
    return this.matches(other.name);
  }

  String getName() {
    return this.name;
  }

  @Override
  public String toString() {
    return name;
  }

  private static final class CapturingWebPathSegment extends WebPathSegment {
    CapturingWebPathSegment(String value) {
      super(value);
    }

    @Override
    boolean isCapturing() {
      return true;
    }

    @Override
    boolean matches(String value) {
      return true;
    }

    @Override
    public String toString() {
      return "(" + super.toString() + ")";
    }
  }
}