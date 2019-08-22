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
import io.vlingo.schemata.model.Context;
import io.vlingo.schemata.model.ContextState;
import io.vlingo.schemata.resource.data.ContextData;
import io.vlingo.symbio.store.object.MapQueryExpression;
import io.vlingo.symbio.store.object.ObjectStore;
import io.vlingo.symbio.store.object.StateObjectMapper;
import io.vlingo.symbio.store.object.inmemory.InMemoryObjectStoreActor;

public class ContextResourceTest {
  private static final String OrgId = "O123";
  private static final String UnitId = "U456";
  private static final String ContextNamespace = "Test";
  private static final String ContextDescription = "Test context.";

  private ObjectStore objectStore;
  private ObjectTypeRegistry registry;
  private World world;

  @Test
  public void testThatContextIsDefined() {
    final ContextResource resource = new ContextResource(world);
    final Response response = resource.defineWith(OrgId, UnitId, ContextData.just(ContextNamespace, ContextDescription)).await();
    assertEquals(Created, response.status);
    assertNotNull(response.headers.headerOf(Location));
    final ContextData data = JsonSerialization.deserialized(response.entity.content(), ContextData.class);
    assertEquals(ContextNamespace, data.namespace);
    assertEquals(ContextDescription, data.description);
  }

  @Test
  public void testContextDescribedAs() {
    final ContextResource resource = new ContextResource(world);
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
  public void testContextRenameTo() {
    final ContextResource resource = new ContextResource(world);
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

  @Before
  @SuppressWarnings({ "unchecked", "rawtypes" })
  public void setUp() {
    world = World.startWithDefaults("test-command-router");
    world.stageNamed("vlingo-schemata-grid", Grid.class, new GridAddressFactory(IdentityGeneratorType.RANDOM));

    objectStore = world.actorFor(ObjectStore.class, InMemoryObjectStoreActor.class, new NoopDispatcher());

    registry = new ObjectTypeRegistry(world);

    final Info<Context> unitInfo =
            new Info(
                  objectStore,
                  ContextState.class,
                  "Context",
                  MapQueryExpression.using(Context.class, "find", MapQueryExpression.map("id", "id")),
                  StateObjectMapper.with(Context.class, new Object(), new Object()));

    registry.register(unitInfo);
  }
}
