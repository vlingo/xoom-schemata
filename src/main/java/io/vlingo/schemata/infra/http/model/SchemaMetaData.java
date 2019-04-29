package io.vlingo.schemata.infra.http.model;

import io.vlingo.schemata.model.SchemaEntity;

import java.util.List;

public class SchemaMetaData {
  public final String id;
  public final String name;
  public final List<Schema> versions;


  private SchemaMetaData(String id, String name, List<Schema> versions) {
    this.id = id;
    this.name = name;
    this.versions = versions;
  }

  public static SchemaMetaData from(SchemaEntity ve) {
    // TODO: Adapt from domain model to API model
    throw new UnsupportedOperationException();
  }

  public static SchemaMetaData from(String id, String name, List<Schema> versions) {
    return new SchemaMetaData(id, name, versions);
  }
}
