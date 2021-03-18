// Copyright © 2012-2020 VLINGO LABS. All rights reserved.
//
// This Source Code Form is subject to the terms of the
// Mozilla Public License, v. 2.0. If a copy of the MPL
// was not distributed with this file, You can obtain
// one at https://mozilla.org/MPL/2.0/.

package io.vlingo.schemata.resource;

import io.vlingo.common.serialization.JsonSerialization;
import io.vlingo.http.Response;
import io.vlingo.schemata.model.Organization;
import io.vlingo.schemata.model.OrganizationState;
import io.vlingo.schemata.resource.data.UnitData;
import org.junit.Test;

import static io.vlingo.http.Response.Status.Created;
import static io.vlingo.http.Response.Status.NotFound;
import static io.vlingo.http.ResponseHeader.Location;
import static org.junit.Assert.*;

public class UnitResourceTest extends ResourceTest {
  private static final String OrgId = "O123";
  private static final String UnitName = "Test";
  private static final String UnitDescription = "Test unit.";

  @Test
  public void testThatUnitIsDefined() {
    final UnitResource resource = new UnitResource(stage);
    final Response response = resource.defineWith(OrgId, UnitData.just(UnitName, UnitDescription)).await();
    assertEquals(Created, response.status);
    assertNotNull(response.headers.headerOf(Location));
    assertTrue(response.entity.content().contains(UnitName));
    assertTrue(response.entity.content().contains(UnitDescription));
  }

  @Test
  public void testThatNonExistingUnitReturns404() {
    final UnitResource resource = new UnitResource(stage);
    OrganizationState org = Organization.with(stage, Organization.uniqueId(),"o", "d").await();
    final Response response = resource.queryUnit(org.organizationId.value,"-1").await();
    assertEquals(NotFound, response.status);
    assertTrue(response.entity.content().contains("Unit not found"));
  }

  @Test
  public void testUnitDescribedAs() {
    final UnitResource resource = new UnitResource(stage);
    final Response response1 = resource.defineWith(OrgId, UnitData.just(UnitName, UnitDescription)).await();
    assertEquals(Created, response1.status);
    final UnitData data1 = JsonSerialization.deserialized(response1.entity.content(), UnitData.class);
    final Response response2 = resource.describeAs(data1.organizationId, data1.unitId, UnitDescription + 1).await();
    assertNotEquals(response1.entity.content(), response2.entity.content());
    final UnitData data2 = JsonSerialization.deserialized(response2.entity.content(), UnitData.class);
    assertEquals(data1.unitId, data2.unitId);
    assertNotEquals(data1.description, data2.description);
    assertEquals((UnitDescription + 1), data2.description);
  }

  @Test
  public void testUnitRedefineWith() {
    final UnitResource resource = new UnitResource(stage);
    final Response response1 = resource.defineWith(OrgId, UnitData.just(UnitName, UnitDescription)).await();
    assertEquals(Created, response1.status);
    final UnitData data1 = JsonSerialization.deserialized(response1.entity.content(), UnitData.class);
    final Response response2 = resource.redefineWith(data1.organizationId, data1.unitId, UnitData.just(UnitName + 1, UnitDescription + 1)).await();
    assertNotEquals(response1.entity.content(), response2.entity.content());
    final UnitData data2 = JsonSerialization.deserialized(response2.entity.content(), UnitData.class);
    assertEquals(data1.unitId, data2.unitId);
    assertNotEquals(data1.name, data2.name);
    assertEquals((UnitName + 1), data2.name);
    assertEquals((UnitDescription + 1), data2.description);
  }

  @Test
  public void testUnitRenameTo() {
    final UnitResource resource = new UnitResource(stage);
    final Response response1 = resource.defineWith(OrgId, UnitData.just(UnitName, UnitDescription)).await();
    assertEquals(Created, response1.status);
    final UnitData data1 = JsonSerialization.deserialized(response1.entity.content(), UnitData.class);
    final Response response2 = resource.renameTo(data1.organizationId, data1.unitId, UnitName + 1).await();
    assertNotEquals(response1.entity.content(), response2.entity.content());
    final UnitData data2 = JsonSerialization.deserialized(response2.entity.content(), UnitData.class);
    assertEquals(data1.unitId, data2.unitId);
    assertNotEquals(data1.name, data2.name);
    assertEquals((UnitName + 1), data2.name);
  }
}
