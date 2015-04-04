package com.buntplanet.bender;

public class WebPathSegment {
  private final String name;

  public WebPathSegment(String name) {
    this.name = name;
  }

  public static WebPathSegment of(String value) {
    if (value.startsWith(":"))
      return new CapturingWebPathSegment(value.substring(1));
    return new WebPathSegment(value);
  }

  public boolean isCapturing() {
    return false;
  }

  public boolean matches(String value) {
    return this.name.equals(value);
  }

  public boolean matches(WebPathSegment other) {
    return this.matches(other.name);
  }

  public String getName() {
    return this.name;
  }

  public String toString() {
    return name;
  }

  private static class CapturingWebPathSegment extends WebPathSegment {
    public CapturingWebPathSegment(String value) {
      super(value);
    }

    @Override
    public boolean isCapturing() {
      return true;
    }

    @Override
    public boolean matches(String value) {
      return true;
    }

    @Override
    public String toString() {
      return "(" + super.toString() + ")";
    }
  }
}