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
import io.vlingo.schemata.model.Unit;
import io.vlingo.schemata.model.UnitState;
import io.vlingo.schemata.query.Queries;
import io.vlingo.schemata.query.UnitQueries;
import io.vlingo.schemata.resource.data.UnitData;
import io.vlingo.symbio.store.object.MapQueryExpression;
import io.vlingo.symbio.store.object.ObjectStore;
import io.vlingo.symbio.store.object.StateObjectMapper;
import io.vlingo.symbio.store.object.inmemory.InMemoryObjectStoreActor;

public class UnitResourceTest extends ResourceTest {
  private static final String OrgId = "O123";
  private static final String UnitName = "Test";
  private static final String UnitDescription = "Test unit.";

  private ObjectStore objectStore;
  private UnitQueries queries;
  private ObjectTypeRegistry registry;
  private World world;

  @Test
  public void testThatUnitIsDefined() {
    final UnitResource resource = new UnitResource(world, queries);
    final Response response = resource.defineWith(OrgId, UnitData.just(UnitName, UnitDescription)).await();
    assertEquals(Created, response.status);
    assertNotNull(response.headers.headerOf(Location));
    assertTrue(response.entity.content().contains(UnitName));
    assertTrue(response.entity.content().contains(UnitDescription));
  }

  @Test
  public void testUnitDescribedAs() {
    final UnitResource resource = new UnitResource(world, queries);
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
  public void testUnitRenameTo() {
    final UnitResource resource = new UnitResource(world, queries);
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

  @Before
  @SuppressWarnings({ "unchecked", "rawtypes" })
  public void setUp() throws Exception {
    world = World.startWithDefaults("test-command-router");
    world.stageNamed("vlingo-schemata-grid", Grid.class, new GridAddressFactory(IdentityGeneratorType.RANDOM));

    objectStore = world.actorFor(ObjectStore.class, InMemoryObjectStoreActor.class, new NoopDispatcher());

    queries = Queries.forUnits(world.stageNamed(Schemata.StageName), objectStore);

    registry = new ObjectTypeRegistry(world);

    final Info<Unit> unitInfo =
            new Info(
                  objectStore,
                  UnitState.class,
                  "Unit",
                  MapQueryExpression.using(Unit.class, "find", MapQueryExpression.map("id", "id")),
                  StateObjectMapper.with(Unit.class, new Object(), new Object()));

    registry.register(unitInfo);
  }
}
