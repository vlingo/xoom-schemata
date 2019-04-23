package io.vlingo.schemata.infra.http.model;

import io.vlingo.schemata.model.UnitEntity;

public class Unit {
  public final String id;
  public final String name;
  public final String description;


  private Unit(String id, String name, String description) {
    this.id = id;
    this.name = name;
    this.description = description;
  }

  public static Unit from(UnitEntity oe) {
    // TODO: Adapt from domain model to API model
    throw new UnsupportedOperationException();
  }

  public static Unit from(String id, String name) {
    return from(id, name, "");
  }

  public static Unit from(String id, String name, String description) {
    return new Unit(id, name, description);
  }
}
