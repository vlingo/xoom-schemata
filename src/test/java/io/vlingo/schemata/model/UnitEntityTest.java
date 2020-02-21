// Copyright © 2012-2020 VLINGO LABS. All rights reserved.
//
// This Source Code Form is subject to the terms of the
// Mozilla Public License, v. 2.0. If a copy of the MPL
// was not distributed with this file, You can obtain
// one at https://mozilla.org/MPL/2.0/.

package io.vlingo.schemata.model;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import io.vlingo.actors.World;
import io.vlingo.lattice.model.object.ObjectTypeRegistry;
import io.vlingo.lattice.model.object.ObjectTypeRegistry.Info;
import io.vlingo.schemata.NoopDispatcher;
import io.vlingo.schemata.model.Id.OrganizationId;
import io.vlingo.schemata.model.Id.UnitId;
import io.vlingo.symbio.store.MapQueryExpression;
import io.vlingo.symbio.store.object.ObjectStore;
import io.vlingo.symbio.store.object.StateObjectMapper;
import io.vlingo.symbio.store.object.inmemory.InMemoryObjectStoreActor;

public class UnitEntityTest {
  private ObjectTypeRegistry registry;
  private Unit unit;
  private UnitId unitId;
  private ObjectStore objectStore;
  private World world;

  @Before
  @SuppressWarnings({ "unchecked", "rawtypes" })
  public void setUp() {
    world = World.start("unit-entity-test");

    objectStore = world.actorFor(ObjectStore.class, InMemoryObjectStoreActor.class, new NoopDispatcher());

    registry = new ObjectTypeRegistry(world);

    final Info<Unit> unitInfo =
            new Info(
                  objectStore,
                  UnitState.class,
                  "HR-Database",
                  MapQueryExpression.using(Unit.class, "find", MapQueryExpression.map("id", "id")),
                  StateObjectMapper.with(Unit.class, new Object(), new Object()));

    registry.register(unitInfo);
    unitId = UnitId.uniqueFor(OrganizationId.unique());
    unit = world.actorFor(Unit.class, UnitEntity.class, unitId);
  }

  @After
  public void tearDown() {
    world.terminate();
  }

  @Test
  public void testThatUnitDefined() {
    final UnitState state = unit.defineWith("name", "description").await();
    Assert.assertNotEquals(UnitState.unidentified(), state.persistenceId());
    Assert.assertEquals("name", state.name);
    Assert.assertEquals("description", state.description);
  }

  @Test
  public void testThatUnitDescribed() {
    final UnitState state = unit.describeAs("new description").await();
    Assert.assertEquals("new description", state.description);
  }

  @Test
  public void testThatUnitRenamed() {
    final UnitState state = unit.renameTo("new name").await();
    Assert.assertEquals("new name", state.name);
  }
}
