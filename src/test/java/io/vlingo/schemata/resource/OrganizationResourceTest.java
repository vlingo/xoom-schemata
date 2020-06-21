// Copyright © 2012-2020 VLINGO LABS. All rights reserved.
//
// This Source Code Form is subject to the terms of the
// Mozilla Public License, v. 2.0. If a copy of the MPL
// was not distributed with this file, You can obtain
// one at https://mozilla.org/MPL/2.0/.

package io.vlingo.schemata.resource;

import static io.vlingo.http.Response.Status.Created;
import static io.vlingo.http.Response.Status.NotFound;
import static io.vlingo.http.ResponseHeader.Location;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

import io.vlingo.common.serialization.JsonSerialization;
import io.vlingo.http.Response;
import io.vlingo.schemata.resource.data.OrganizationData;

public class OrganizationResourceTest extends ResourceTest {
  private static final String OrgName = "Test";
  private static final String OrgDescription = "Test org.";

  @Test
  public void testThatOrganizationIsDefined() {
    final OrganizationResource resource = new OrganizationResource(world, organizationQueries);
    final Response response = resource.defineWith(OrganizationData.just(OrgName, OrgDescription)).await();
    assertEquals(Created, response.status);
    assertNotNull(response.headers.headerOf(Location));
    assertTrue(response.entity.content().contains(OrgName));
    assertTrue(response.entity.content().contains(OrgDescription));
  }

  @Test
  public void testThatNonExistingOrganizationReturns404() {
    final OrganizationResource resource = new OrganizationResource(world, organizationQueries);
    String invalidOrganizationId = "-1";
    final Response response = resource.queryOrganization(invalidOrganizationId).await();
    assertEquals(NotFound, response.status);
    assertTrue(response.entity.content().contains("Organization not found"));
  }

  @Test
  public void testOrganizationDescribedAs() {
    final OrganizationResource resource = new OrganizationResource(world, organizationQueries);
    final Response response1 = resource.defineWith(OrganizationData.just(OrgName, OrgDescription)).await();
    assertEquals(Created, response1.status);
    final OrganizationData data1 = JsonSerialization.deserialized(response1.entity.content(), OrganizationData.class);
    final Response response2 = resource.describeAs(data1.organizationId, OrgDescription + 1).await();
    assertNotEquals(response1.entity.content(), response2.entity.content());
    final OrganizationData data2 = JsonSerialization.deserialized(response2.entity.content(), OrganizationData.class);
    assertEquals(data1.organizationId, data2.organizationId);
    assertNotEquals(data1.description, data2.description);
    assertEquals((OrgDescription + 1), data2.description);
  }

  @Test
  public void testOrganizationRedefineWith() {
    final OrganizationResource resource = new OrganizationResource(world, organizationQueries);
    final Response response1 = resource.defineWith(OrganizationData.just(OrgName, OrgDescription)).await();
    assertEquals(Created, response1.status);
    final OrganizationData data1 = JsonSerialization.deserialized(response1.entity.content(), OrganizationData.class);
    final Response response2 = resource.redefineWith(data1.organizationId, OrganizationData.just(OrgName + 1, OrgDescription + 1)).await();
    assertNotEquals(response1.entity.content(), response2.entity.content());
    final OrganizationData data2 = JsonSerialization.deserialized(response2.entity.content(), OrganizationData.class);
    assertEquals(data1.organizationId, data2.organizationId);
    assertNotEquals(data1.name, data2.name);
    assertEquals((OrgName + 1), data2.name);
    assertNotEquals(data1.description, data2.description);
    assertEquals((OrgDescription + 1), data2.description);
  }

  @Test
  public void testOrganizationRenameTo() {
    final OrganizationResource resource = new OrganizationResource(world, organizationQueries);
    final Response response1 = resource.defineWith(OrganizationData.just(OrgName, OrgDescription)).await();
    assertEquals(Created, response1.status);
    final OrganizationData data1 = JsonSerialization.deserialized(response1.entity.content(), OrganizationData.class);
    final Response response2 = resource.renameTo(data1.organizationId, OrgName + 1).await();
    assertNotEquals(response1.entity.content(), response2.entity.content());
    final OrganizationData data2 = JsonSerialization.deserialized(response2.entity.content(), OrganizationData.class);
    assertEquals(data1.organizationId, data2.organizationId);
    assertNotEquals(data1.name, data2.name);
    assertEquals((OrgName + 1), data2.name);
  }
}
