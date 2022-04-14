// Copyright © 2012-2022 VLINGO LABS. All rights reserved.
//
// This Source Code Form is subject to the terms of the
// Mozilla Public License, v. 2.0. If a copy of the MPL
// was not distributed with this file, You can obtain
// one at https://mozilla.org/MPL/2.0/.

package io.vlingo.xoom.schemata.resource;

import static io.vlingo.xoom.http.Response.Status.Created;
import static io.vlingo.xoom.http.Response.Status.NotFound;
import static io.vlingo.xoom.http.ResponseHeader.Location;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

import io.vlingo.xoom.common.serialization.JsonSerialization;
import io.vlingo.xoom.http.Response;
import io.vlingo.xoom.schemata.model.Category;
import io.vlingo.xoom.schemata.model.Context;
import io.vlingo.xoom.schemata.model.ContextState;
import io.vlingo.xoom.schemata.model.Organization;
import io.vlingo.xoom.schemata.model.OrganizationState;
import io.vlingo.xoom.schemata.model.Scope;
import io.vlingo.xoom.schemata.model.Unit;
import io.vlingo.xoom.schemata.model.UnitState;
import io.vlingo.xoom.schemata.resource.data.SchemaData;

public class SchemaResourceTest extends ResourceTest {
  private static final String OrgId = "O123";
  private static final String UnitId = "U456";
  private static final String ContextId = "C789";
  private static final String SchemaCategory = Category.Event.name();
  private static final String SchemaScope = Scope.Public.name();
  private static final String SchemaName = "Test";
  private static final String SchemaDescription = "Test context.";

  @Test
  public void testThatSchemaIsDefined() {
    final SchemaResource resource = new SchemaResource(stage);
    final Response response = resource.defineWith(OrgId, UnitId, ContextId, SchemaData.just(SchemaCategory, SchemaScope, SchemaName, SchemaDescription)).await();
    assertEquals(Created, response.status);
    assertNotNull(response.headers.headerOf(Location));
    final SchemaData data = JsonSerialization.deserialized(response.entity.content(), SchemaData.class);
    assertEquals(SchemaCategory, data.category);
    assertEquals(SchemaName, data.name);
    assertEquals(SchemaDescription, data.description);
  }

  @Test
  public void testThatNonExistingSchemaReturns404() {
    final SchemaResource resource = new SchemaResource(stage);
    OrganizationState org = Organization.with(stage, Organization.uniqueId(),"o", "d").await();
    UnitState unit = Unit.with(stage, org.organizationId,"u", "d").await();
    ContextState context = Context.with(stage, unit.unitId,"c", "d").await();

    final Response response = resource.querySchema(org.organizationId.value, unit.unitId.value, context.contextId.value, "-1").await();
    assertEquals(NotFound, response.status);
    assertTrue(response.entity.content().contains("Schema not found"));
  }

  @Test
  public void testSchemaCategorizeAs() {
    final SchemaResource resource = new SchemaResource(stage);
    final Response response1 = resource.defineWith(OrgId, UnitId, ContextId, SchemaData.just(SchemaCategory, SchemaScope, SchemaName, SchemaDescription)).await();
    assertEquals(Created, response1.status);
    final SchemaData data1 = JsonSerialization.deserialized(response1.entity.content(), SchemaData.class);
    final Response response2 = resource.categorizeAs(data1.organizationId, data1.unitId, data1.contextId, data1.schemaId, Category.Data.name()).await();
    assertNotEquals(response1.entity.content(), response2.entity.content());
    final SchemaData data2 = JsonSerialization.deserialized(response2.entity.content(), SchemaData.class);
    assertEquals(data1.schemaId, data2.schemaId);
    assertNotEquals(data1.category, data2.category);
    assertEquals(Category.Data.name(), data2.category);
  }

  @Test
  public void testSchemaDescribedAs() {
    final SchemaResource resource = new SchemaResource(stage);
    final Response response1 = resource.defineWith(OrgId, UnitId, ContextId, SchemaData.just(SchemaCategory, SchemaScope, SchemaName, SchemaDescription)).await();
    assertEquals(Created, response1.status);
    final SchemaData data1 = JsonSerialization.deserialized(response1.entity.content(), SchemaData.class);
    final Response response2 = resource.describeAs(data1.organizationId, data1.unitId, data1.contextId, data1.schemaId, SchemaDescription + 1).await();
    assertNotEquals(response1.entity.content(), response2.entity.content());
    final SchemaData data2 = JsonSerialization.deserialized(response2.entity.content(), SchemaData.class);
    assertEquals(data1.schemaId, data2.schemaId);
    assertNotEquals(data1.description, data2.description);
    assertEquals((SchemaDescription + 1), data2.description);
  }

  @Test
  public void testSchemaRedefineWith() {
    final SchemaResource resource = new SchemaResource(stage);
    final Response response1 = resource.defineWith(OrgId, UnitId, ContextId, SchemaData.just(SchemaCategory, SchemaScope, SchemaName, SchemaDescription)).await();
    assertEquals(Created, response1.status);
    final SchemaData data1 = JsonSerialization.deserialized(response1.entity.content(), SchemaData.class);
    final Response response2 = resource.redefineWith(data1.organizationId, data1.unitId, data1.contextId, data1.schemaId, SchemaData.just(Category.Data.name(), Scope.Private.name(), SchemaName + 1, SchemaDescription + 1)).await();
    assertNotEquals(response1.entity.content(), response2.entity.content());
    final SchemaData data2 = JsonSerialization.deserialized(response2.entity.content(), SchemaData.class);
    assertEquals(data1.unitId, data2.unitId);
    assertNotEquals(data1.category, data2.category);
    assertEquals(Category.Data.name(), data2.category);
    assertNotEquals(data1.scope, data2.scope);
    assertEquals(Scope.Private.name(), data2.scope);
    assertNotEquals(data1.name, data2.name);
    assertEquals((SchemaName + 1), data2.name);
    assertNotEquals(data1.description, data2.description);
    assertEquals((SchemaDescription + 1), data2.description);
  }

  @Test
  public void testSchemaRenameTo() {
    final SchemaResource resource = new SchemaResource(stage);
    final Response response1 = resource.defineWith(OrgId, UnitId, ContextId, SchemaData.just(SchemaCategory, SchemaScope, SchemaName, SchemaDescription)).await();
    assertEquals(Created, response1.status);
    final SchemaData data1 = JsonSerialization.deserialized(response1.entity.content(), SchemaData.class);
    final Response response2 = resource.renameTo(data1.organizationId, data1.unitId, data1.contextId, data1.schemaId, SchemaName + 1).await();
    assertNotEquals(response1.entity.content(), response2.entity.content());
    final SchemaData data2 = JsonSerialization.deserialized(response2.entity.content(), SchemaData.class);
    assertEquals(data1.unitId, data2.unitId);
    assertNotEquals(data1.name, data2.name);
    assertEquals((SchemaName + 1), data2.name);
  }
}
