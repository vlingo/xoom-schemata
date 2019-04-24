package io.vlingo.schemata.infra.http.model;

import io.vlingo.schemata.model.SchemaVersionEntity;

public class SchemaVersion {
  public final String id;


  private SchemaVersion(String id) {
    this.id = id;
  }

  public static SchemaVersion from(SchemaVersionEntity ue) {
    // TODO: Adapt from domain model to API model
    throw new UnsupportedOperationException();
  }

  public static SchemaVersion from(String id) {
    return new SchemaVersion(id);
  }
}
