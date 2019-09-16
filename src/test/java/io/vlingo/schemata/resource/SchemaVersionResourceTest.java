// Copyright © 2012-2018 Vaughn Vernon. All rights reserved.
//
// This Source Code Form is subject to the terms of the
// Mozilla Public License, v. 2.0. If a copy of the MPL
// was not distributed with this file, You can obtain
// one at https://mozilla.org/MPL/2.0/.

package io.vlingo.schemata.resource;

import static io.vlingo.http.Response.Status.Created;
import static io.vlingo.http.ResponseHeader.Location;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNotNull;

import org.junit.Test;

import io.vlingo.common.serialization.JsonSerialization;
import io.vlingo.http.Response;
import io.vlingo.schemata.model.SchemaVersion.Status;
import io.vlingo.schemata.resource.data.SchemaVersionData;

public class SchemaVersionResourceTest extends ResourceTest {
  private static final String OrgId = "O123";
  private static final String UnitId = "U456";
  private static final String ContextId = "C789";
  private static final String SchemaId = "S135";
  private static final String SchemaVersionDescription = "Test context.";
  private static final String SchemaVersionSpecification = "{ spec = \"spec\" ";
  private static final String SchemaVersionStatus = "Draft";
  private static final String SchemaVersionVersion000 = "0.0.0";
  private static final String SchemaVersionVersion100 = "1.0.0";
  private static final String SchemaVersionVersion101 = "1.0.1";

  @Test
  public void testThatSchemaVersionIsDefined() {
    final SchemaVersionResource resource = new SchemaVersionResource(world);
    final SchemaVersionData defineData = SchemaVersionData.just(SchemaVersionSpecification, SchemaVersionDescription, "", SchemaVersionVersion000, SchemaVersionVersion100);
    final Response response1 = resource.defineWith(OrgId, UnitId, ContextId, SchemaId, defineData).await();
    assertEquals(Created, response1.status);
    assertNotNull(response1.headers.headerOf(Location));
    final SchemaVersionData data = JsonSerialization.deserialized(response1.entity.content(), SchemaVersionData.class);
    assertEquals(SchemaVersionStatus, data.status);
    assertEquals(SchemaVersionSpecification, data.specification);
    assertEquals(SchemaVersionDescription, data.description);
    assertEquals(SchemaVersionVersion100, data.currentVersion);
  }

  @Test
  public void testThatSchemaVersionMinorUpgradeIsDefined() {
    final SchemaVersionResource resource = new SchemaVersionResource(world);
    final SchemaVersionData defineData = SchemaVersionData.just(SchemaVersionSpecification, SchemaVersionDescription, "", SchemaVersionVersion100, SchemaVersionVersion101);
    final Response response1 = resource.defineWith(OrgId, UnitId, ContextId, SchemaId, defineData).await();
    assertEquals(Created, response1.status);
    assertNotNull(response1.headers.headerOf(Location));
    final SchemaVersionData data = JsonSerialization.deserialized(response1.entity.content(), SchemaVersionData.class);
    assertEquals(SchemaVersionStatus, data.status);
    assertEquals(SchemaVersionSpecification, data.specification);
    assertEquals(SchemaVersionDescription, data.description);
    assertEquals(SchemaVersionVersion101, data.currentVersion);
  }

  @Test
  public void testSchemaVersionDescribedAs() {
    final SchemaVersionResource resource = new SchemaVersionResource(world);
    final SchemaVersionData defineData = SchemaVersionData.just(SchemaVersionSpecification, SchemaVersionDescription, "", SchemaVersionVersion000, SchemaVersionVersion100);
    final Response response1 = resource.defineWith(OrgId, UnitId, ContextId, SchemaId, defineData).await();
    assertEquals(Created, response1.status);
    final SchemaVersionData data1 = JsonSerialization.deserialized(response1.entity.content(), SchemaVersionData.class);
    final Response response2 = resource.describeAs(data1.organizationId, data1.unitId, data1.contextId, data1.schemaId, data1.schemaVersionId, SchemaVersionDescription + 1).await();
    assertNotEquals(response1.entity.content(), response2.entity.content());
    final SchemaVersionData data2 = JsonSerialization.deserialized(response2.entity.content(), SchemaVersionData.class);
    assertEquals(data1.schemaVersionId, data2.schemaVersionId);
    assertNotEquals(data1.description, data2.description);
    assertEquals((SchemaVersionDescription + 1), data2.description);
  }

  @Test
  public void testThatSchemaVersionIsPublished() {
    final SchemaVersionResource resource = new SchemaVersionResource(world);
    final SchemaVersionData defineData = SchemaVersionData.just(SchemaVersionSpecification, SchemaVersionDescription, "", SchemaVersionVersion000, SchemaVersionVersion100);
    final Response response1 = resource.defineWith(OrgId, UnitId, ContextId, SchemaId, defineData).await();
    assertEquals(Created, response1.status);
    final SchemaVersionData data1 = JsonSerialization.deserialized(response1.entity.content(), SchemaVersionData.class);
    final Response response2 = resource.statusOf(data1.organizationId, data1.unitId, data1.contextId, data1.schemaId, data1.schemaVersionId, Status.Published.name()).await();
    assertNotEquals(response1.entity.content(), response2.entity.content());
    final SchemaVersionData data2 = JsonSerialization.deserialized(response2.entity.content(), SchemaVersionData.class);
    assertEquals(data1.schemaVersionId, data2.schemaVersionId);
    assertNotEquals(data1.status, data2.status);
    assertEquals(Status.Published.name(), data2.status);
  }

  @Test
  public void testThatSchemaVersionIsDeprecated() {
    final SchemaVersionResource resource = new SchemaVersionResource(world);
    final SchemaVersionData defineData = SchemaVersionData.just(SchemaVersionSpecification, SchemaVersionDescription, "", SchemaVersionVersion000, SchemaVersionVersion100);
    final Response response1 = resource.defineWith(OrgId, UnitId, ContextId, SchemaId, defineData).await();
    assertEquals(Created, response1.status);
    final SchemaVersionData data1 = JsonSerialization.deserialized(response1.entity.content(), SchemaVersionData.class);
    resource.statusOf(data1.organizationId, data1.unitId, data1.contextId, data1.schemaId, data1.schemaVersionId, Status.Published.name()).await();
    final Response response2 = resource.statusOf(data1.organizationId, data1.unitId, data1.contextId, data1.schemaId, data1.schemaVersionId, Status.Deprecated.name()).await();
    assertNotEquals(response1.entity.content(), response2.entity.content());
    final SchemaVersionData data2 = JsonSerialization.deserialized(response2.entity.content(), SchemaVersionData.class);
    assertEquals(data1.schemaVersionId, data2.schemaVersionId);
    assertNotEquals(data1.status, data2.status);
    assertEquals(Status.Deprecated.name(), data2.status);
  }

  @Test
  public void testThatSchemaVersionIsNotDeprecated() {
    final SchemaVersionResource resource = new SchemaVersionResource(world);
    final SchemaVersionData defineData = SchemaVersionData.just(SchemaVersionSpecification, SchemaVersionDescription, "", SchemaVersionVersion000, SchemaVersionVersion100);
    final Response response1 = resource.defineWith(OrgId, UnitId, ContextId, SchemaId, defineData).await();
    assertEquals(Created, response1.status);
    final SchemaVersionData data1 = JsonSerialization.deserialized(response1.entity.content(), SchemaVersionData.class);
    final Response response2 = resource.statusOf(data1.organizationId, data1.unitId, data1.contextId, data1.schemaId, data1.schemaVersionId, Status.Deprecated.name()).await();
    assertEquals(response1.entity.content(), response2.entity.content());
    final SchemaVersionData data2 = JsonSerialization.deserialized(response2.entity.content(), SchemaVersionData.class);
    assertEquals(data1.schemaVersionId, data2.schemaVersionId);
    assertEquals(data1.status, data2.status);
    assertNotEquals(Status.Deprecated.name(), data2.status);
  }

  @Test
  public void testThatSchemaVersionIsRemoved() {
    final SchemaVersionResource resource = new SchemaVersionResource(world);
    final SchemaVersionData defineData = SchemaVersionData.just(SchemaVersionSpecification, SchemaVersionDescription, "", SchemaVersionVersion000, SchemaVersionVersion100);
    final Response response1 = resource.defineWith(OrgId, UnitId, ContextId, SchemaId, defineData).await();
    assertEquals(Created, response1.status);
    final SchemaVersionData data1 = JsonSerialization.deserialized(response1.entity.content(), SchemaVersionData.class);
    resource.statusOf(data1.organizationId, data1.unitId, data1.contextId, data1.schemaId, data1.schemaVersionId, Status.Published.name()).await();
    resource.statusOf(data1.organizationId, data1.unitId, data1.contextId, data1.schemaId, data1.schemaVersionId, Status.Deprecated.name()).await();
    final Response response2 = resource.statusOf(data1.organizationId, data1.unitId, data1.contextId, data1.schemaId, data1.schemaVersionId, Status.Removed.name()).await();
    final SchemaVersionData data2 = JsonSerialization.deserialized(response2.entity.content(), SchemaVersionData.class);
    assertEquals(data1.schemaVersionId, data2.schemaVersionId);
    assertNotEquals(data1.status, data2.status);
    assertEquals(Status.Removed.name(), data2.status);
  }

  @Test
  public void testThatSchemaVersionIsNotRemoved() {
    final SchemaVersionResource resource = new SchemaVersionResource(world);
    final SchemaVersionData defineData = SchemaVersionData.just(SchemaVersionSpecification, SchemaVersionDescription, "", SchemaVersionVersion000, SchemaVersionVersion100);
    final Response response1 = resource.defineWith(OrgId, UnitId, ContextId, SchemaId, defineData).await();
    assertEquals(Created, response1.status);
    final SchemaVersionData data1 = JsonSerialization.deserialized(response1.entity.content(), SchemaVersionData.class);
    resource.statusOf(data1.organizationId, data1.unitId, data1.contextId, data1.schemaId, data1.schemaVersionId, Status.Published.name()).await();
    final Response response2 = resource.statusOf(data1.organizationId, data1.unitId, data1.contextId, data1.schemaId, data1.schemaVersionId, Status.Removed.name()).await();
    final SchemaVersionData data2 = JsonSerialization.deserialized(response2.entity.content(), SchemaVersionData.class);
    assertEquals(data1.schemaVersionId, data2.schemaVersionId);
    assertNotEquals(Status.Removed.name(), data2.status);
  }
}
