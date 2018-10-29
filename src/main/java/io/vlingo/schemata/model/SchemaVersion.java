package io.vlingo.schemata.model;

public interface SchemaVersion {
  void describeAs(final String description);
  void definedAs(final String defintion);
  void assignStatus(final Status status);

  public static class Definition {
    public final String value;

    public Definition(final String value) {
      this.value = value;
    }
  }

  public static enum Status {
    Draft, Published, Removed, Undefined;
  }

  public static class Version {
    public final String value;

    public Version(final String value) {
      this.value = value;
    }
  }
}
