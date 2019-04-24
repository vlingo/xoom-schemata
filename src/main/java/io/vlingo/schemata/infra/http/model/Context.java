package io.vlingo.schemata.infra.http.model;

import io.vlingo.schemata.model.ContextEntity;
import io.vlingo.schemata.model.UnitEntity;

public class Context {
  public final String id;
  public final String name;
  public final String description;


  private Context(String id, String name, String description) {
    this.id = id;
    this.name = name;
    this.description = description;
  }

  public static Context from(ContextEntity ce) {
    // TODO: Adapt from domain model to API model
    throw new UnsupportedOperationException();
  }

  public static Context from(String id, String name) {
    return from(id, name, "");
  }

  public static Context from(String id, String name, String description) {
    return new Context(id, name, description);
  }
}
