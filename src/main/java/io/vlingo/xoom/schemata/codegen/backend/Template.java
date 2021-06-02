package io.vlingo.xoom.schemata.codegen.backend;

public enum Template {
  SCHEMA_TYPE("SchemaType");

  public final String filename;

  private Template(final String filename) {
    this.filename = filename;
  }
}
