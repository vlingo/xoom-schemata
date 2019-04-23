package io.vlingo.schemata.infra.http.model;

import io.vlingo.schemata.model.OrganizationEntity;

import java.util.List;

public class Organization {
  public final String id;
  public final String name;
  public final String description;
  public final List<Unit> units;


  private Organization(String id, String name, String description, List<Unit> units) {
    this.id = id;
    this.name = name;
    this.description = description;
    this.units = units;
  }

  public static Organization from(OrganizationEntity oe) {
    // TODO: Adapt from domain model to API model
    throw new UnsupportedOperationException();
  }

  public static Organization from(String id, String name, String description, List<Unit> units) {
    return new Organization(id, name, description, units);
  }
}
