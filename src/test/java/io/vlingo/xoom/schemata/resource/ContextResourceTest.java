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
import io.vlingo.xoom.schemata.model.Organization;
import io.vlingo.xoom.schemata.model.OrganizationState;
import io.vlingo.xoom.schemata.model.Unit;
import io.vlingo.xoom.schemata.model.UnitState;
import io.vlingo.xoom.schemata.resource.data.ContextData;

public class ContextResourceTest extends ResourceTest {
  private static final String OrgId = "O123";
  private static final String UnitId = "U456";
  private static final String ContextNamespace = "Test";
  private static final String ContextDescription = "Test context.";

  @Test
  public void testThatContextIsDefined() {
    final ContextResource resource = new ContextResource(stage);
    final Response response = resource.defineWith(OrgId, UnitId, ContextData.just(ContextNamespace, ContextDescription)).await();
    assertEquals(Created, response.status);
    assertNotNull(response.headers.headerOf(Location));
    final ContextData data = JsonSerialization.deserialized(response.entity.content(), ContextData.class);
    assertEquals(ContextNamespace, data.namespace);
    assertEquals(ContextDescription, data.description);
  }

  @Test
  public void testThatNonExistingContextReturns404() {
    final ContextResource resource = new ContextResource(stage);
    OrganizationState org = Organization.with(stage, Organization.uniqueId(),"o", "d").await();
    UnitState unit = Unit.with(stage, org.organizationId,"u", "d").await();
    final Response response = resource.queryContext(org.organizationId.value, unit.unitId.value, "-1").await();
    assertEquals(NotFound, response.status);
    assertTrue(response.entity.content().contains("Context not found"));
  }

  @Test
  public void testContextDescribedAs() {
    final ContextResource resource = new ContextResource(stage);
    final Response response1 = resource.defineWith(OrgId, UnitId, ContextData.just(ContextNamespace, ContextDescription)).await();
    assertEquals(Created, response1.status);
    final ContextData data1 = JsonSerialization.deserialized(response1.entity.content(), ContextData.class);
    final Response response2 = resource.describeAs(data1.organizationId, data1.unitId, data1.contextId, ContextDescription + 1).await();
    assertNotEquals(response1.entity.content(), response2.entity.content());
    final ContextData data2 = JsonSerialization.deserialized(response2.entity.content(), ContextData.class);
    assertEquals(data1.contextId, data2.contextId);
    assertNotEquals(data1.description, data2.description);
    assertEquals((ContextDescription + 1), data2.description);
  }

  @Test
  public void testContextMovedToNamespace() {
    final ContextResource resource = new ContextResource(stage);
    final Response response1 = resource.defineWith(OrgId, UnitId, ContextData.just(ContextNamespace, ContextDescription)).await();
    assertEquals(Created, response1.status);
    final ContextData data1 = JsonSerialization.deserialized(response1.entity.content(), ContextData.class);
    final Response response2 = resource.moveToNamespace(data1.organizationId, data1.unitId, data1.contextId, ContextNamespace + 1).await();
    assertNotEquals(response1.entity.content(), response2.entity.content());
    final ContextData data2 = JsonSerialization.deserialized(response2.entity.content(), ContextData.class);
    assertEquals(data1.unitId, data2.unitId);
    assertNotEquals(data1.namespace, data2.namespace);
    assertEquals((ContextNamespace + 1), data2.namespace);
  }

  @Test
  public void testContextRedefined() {
    final ContextResource resource = new ContextResource(stage);
    final Response response1 = resource.defineWith(OrgId, UnitId, ContextData.just(ContextNamespace, ContextDescription)).await();
    assertEquals(Created, response1.status);
    final ContextData data1 = JsonSerialization.deserialized(response1.entity.content(), ContextData.class);
    final Response response2 = resource.redefineWith(data1.organizationId, data1.unitId, data1.contextId, ContextData.just(ContextNamespace + 1, ContextDescription + 1)).await();
    assertNotEquals(response1.entity.content(), response2.entity.content());
    final ContextData data2 = JsonSerialization.deserialized(response2.entity.content(), ContextData.class);
    assertEquals(data1.unitId, data2.unitId);
    assertNotEquals(data1.namespace, data2.namespace);
    assertEquals((ContextNamespace + 1), data2.namespace);
    assertNotEquals(data1.description, data2.description);
    assertEquals((ContextDescription + 1), data2.description);
  }
}
