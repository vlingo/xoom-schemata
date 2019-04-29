package io.vlingo.schemata.infra.http.model;

import io.vlingo.schemata.model.SchemaVersionEntity;

public class SchemaVersion {
  public final String description;
  public final String specification;
  public final String status;
  public final String version;


  private SchemaVersion(String description, String specification, String status, String version) {
    this.description = description;
    this.specification = specification;
    this.status = status;
    this.version = version;
  }

  public static SchemaVersion from(SchemaVersionEntity ue) {
    // TODO: Adapt from domain model to API model
    throw new UnsupportedOperationException();
  }

  public static SchemaVersion from(String description, String specification, String status, String version) {
    return new SchemaVersion(description, specification, status, version);
  }
}
