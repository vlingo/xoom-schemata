package io.vlingo.schemata.infra.http.model;

import io.vlingo.schemata.model.UnitEntity;

import java.util.List;

public class Unit {
  public final String id;
  public final String name;
  public final String description;
  public final List<Context> contexts;


  private Unit(String id, String name, String description, List<Context> contexts) {
    this.id = id;
    this.name = name;
    this.description = description;
    this.contexts = contexts;
  }

  public static Unit from(UnitEntity ue) {
    // TODO: Adapt from domain model to API model
    throw new UnsupportedOperationException();
  }

  public static Unit from(String id, String name, List<Context> contexts) {
    return from(id, name, "", contexts);
  }

  public static Unit from(String id, String name, String description, List<Context> contexts) {
    return new Unit(id, name, description, contexts);
  }
}
