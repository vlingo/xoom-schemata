package io.vlingo.xoom.schemata.infra.persistence;

import io.vlingo.xoom.schemata.model.Category;
import io.vlingo.xoom.schemata.model.Scope;

import static io.vlingo.xoom.schemata.model.SchemaVersion.Specification;
import static io.vlingo.xoom.schemata.model.SchemaVersion.Version;

class Fixtures {
  public static final String OrgName = "VLINGO, LLC";
  public static final String OrgDescription = "We are the VLINGO XOOM Platform company.";
  public static final String UnitName = "Development Team";
  public static final String UnitDescription = "We are VLINGO XOOM Platform development.";
  public static final String ContextNamespace = "io.vlingo.xoom.schemata";
  public static final String ContextDescription = "We are the VLINGO XOOM Scehmata team.";
  public static final String SchemaName = "SchemaDefined";
  public static final String SchemaDescription = "Captures that a schema's definition occurred.";
  public static final Category SchemaCategory = Category.Event;
  public static final Scope SchemaScope = Scope.Public;
  public static final String SchemaVersionDescription = "Test context.";
  public static final Specification SchemaVersionSpecification = Specification.of("event SchemaDefined {}");
  public static final Version SchemaVersionVersion000 = Version.of("0.0.0");
  public static final Version SchemaVersionVersion100 = Version.of("1.0.0");
  public static final Version SchemaVersionVersion101 = Version.of("1.0.1");
}
