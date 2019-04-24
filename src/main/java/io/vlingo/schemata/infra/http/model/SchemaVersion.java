package io.vlingo.schemata.infra.http.model;

import io.vlingo.schemata.model.SchemaVersionEntity;

public class SchemaVersion {
  public final String id;
  public final String status;


  private SchemaVersion(String id, String status) {
    this.id = id;
    this.status = status;
  }

  public static SchemaVersion from(SchemaVersionEntity ue) {
    // TODO: Adapt from domain model to API model
    throw new UnsupportedOperationException();
  }

  public static SchemaVersion from(String id, String status) {
    return new SchemaVersion(id, status);
  }
}
