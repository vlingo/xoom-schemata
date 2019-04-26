package io.vlingo.schemata.infra.http.model;

import io.vlingo.schemata.model.SchemaEntity;

public class Schema {
  public final String id;
  public final String status;


  private Schema(String id, String status) {
    this.id = id;
    this.status = status;
  }

  public static Schema from(SchemaEntity ue) {
    // TODO: Adapt from domain model to API model
    throw new UnsupportedOperationException();
  }

  public static Schema from(String id, String status) {
    return new Schema(id, status);
  }
}
