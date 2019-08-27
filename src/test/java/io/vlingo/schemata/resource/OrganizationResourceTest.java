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
import static org.junit.Assert.assertTrue;

import org.junit.Before;
import org.junit.Test;

import io.vlingo.actors.World;
import io.vlingo.common.identity.IdentityGeneratorType;
import io.vlingo.common.serialization.JsonSerialization;
import io.vlingo.http.Response;
import io.vlingo.lattice.grid.Grid;
import io.vlingo.lattice.grid.GridAddressFactory;
import io.vlingo.lattice.model.object.ObjectTypeRegistry;
import io.vlingo.lattice.model.object.ObjectTypeRegistry.Info;
import io.vlingo.schemata.NoopDispatcher;
import io.vlingo.schemata.Schemata;
import io.vlingo.schemata.model.Organization;
import io.vlingo.schemata.model.OrganizationState;
import io.vlingo.schemata.model.Unit;
import io.vlingo.schemata.query.OrganizationQueries;
import io.vlingo.schemata.query.Queries;
import io.vlingo.schemata.resource.data.OrganizationData;
import io.vlingo.symbio.store.object.MapQueryExpression;
import io.vlingo.symbio.store.object.ObjectStore;
import io.vlingo.symbio.store.object.StateObjectMapper;
import io.vlingo.symbio.store.object.inmemory.InMemoryObjectStoreActor;

public class OrganizationResourceTest extends ResourceTest {
  private static final String OrgName = "Test";
  private static final String OrgDescription = "Test org.";

  private ObjectStore objectStore;
  private OrganizationQueries queries;
  private ObjectTypeRegistry registry;
  private World world;

  @Test
  public void testThatOrganizationIsDefined() {
    final OrganizationResource resource = new OrganizationResource(world, queries);
    final Response response = resource.defineWith(OrganizationData.just(OrgName, OrgDescription)).await();
    assertEquals(Created, response.status);
    assertNotNull(response.headers.headerOf(Location));
    assertTrue(response.entity.content().contains(OrgName));
    assertTrue(response.entity.content().contains(OrgDescription));
  }

  @Test
  public void testOrganizationDescribedAs() {
    final OrganizationResource resource = new OrganizationResource(world, queries);
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
  public void testOrganizationRenameTo() {
    final OrganizationResource resource = new OrganizationResource(world, queries);
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

  @Before
  @SuppressWarnings({ "unchecked", "rawtypes" })
  public void setUp() throws Exception {
    world = World.startWithDefaults("test-command-router");
    world.stageNamed(Schemata.StageName, Grid.class, new GridAddressFactory(IdentityGeneratorType.RANDOM));

    objectStore = world.actorFor(ObjectStore.class, InMemoryObjectStoreActor.class, new NoopDispatcher());

    queries = Queries.forOrganizations(world.stageNamed(Schemata.StageName), objectStore);

    registry = new ObjectTypeRegistry(world);

    final Info<Unit> unitInfo =
            new Info(
                  objectStore,
                  OrganizationState.class,
                  "Organization",
                  MapQueryExpression.using(Organization.class, "find", MapQueryExpression.map("id", "id")),
                  StateObjectMapper.with(Organization.class, new Object(), new Object()));

    registry.register(unitInfo);
 }
}
