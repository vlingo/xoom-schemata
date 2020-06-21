// Copyright © 2012-2020 VLINGO LABS. All rights reserved.
//
// This Source Code Form is subject to the terms of the
// Mozilla Public License, v. 2.0. If a copy of the MPL
// was not distributed with this file, You can obtain
// one at https://mozilla.org/MPL/2.0/.

package io.vlingo.schemata.model;

import io.vlingo.actors.World;
import io.vlingo.actors.testkit.AccessSafely;
import io.vlingo.lattice.model.sourcing.SourcedTypeRegistry;
import io.vlingo.lattice.model.sourcing.SourcedTypeRegistry.Info;
import io.vlingo.schemata.NoopDispatcher;
import io.vlingo.schemata.model.Id.OrganizationId;
import io.vlingo.schemata.model.Id.UnitId;
import io.vlingo.symbio.store.journal.Journal;
import io.vlingo.symbio.store.journal.inmemory.InMemoryJournalActor;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.concurrent.atomic.AtomicReference;

public class UnitEntityTest {
  private AccessSafely access;
  private AtomicReference<UnitState> unitState;

  private Journal<String> journal;
  private Unit unit;
  private UnitId unitId;
  private SourcedTypeRegistry registry;
  private World world;

  @Before
  @SuppressWarnings({ "unchecked", "rawtypes" })
  public void setUp() {
    world = World.start("unit-entity-test");

    journal = world.actorFor(Journal.class, InMemoryJournalActor.class, new NoopDispatcher());

    registry = new SourcedTypeRegistry(world);
    registry.register(new Info(journal, UnitEntity.class, UnitEntity.class.getSimpleName()));

    unitId = UnitId.uniqueFor(OrganizationId.unique());
    unit = world.actorFor(Unit.class, UnitEntity.class, unitId);

    unitState = new AtomicReference<>();
    access = AccessSafely.afterCompleting(1);
    access.writingWith("state", (UnitState s) -> unitState.set(s));
    access.readingWith("state", () -> unitState.get());
  }

  @After
  public void tearDown() {
    world.terminate();
  }

  @Test
  public void testThatUnitDefined() {
    unit.defineWith("name", "description")
            .andThenConsume(s -> access.writeUsing("state", s));
    final UnitState state = access.readFrom("state");

    Assert.assertEquals(unitId.value, state.unitId.value);
    Assert.assertEquals("name", state.name);
    Assert.assertEquals("description", state.description);
  }

  @Test
  public void testThatUnitDescribed() {
    unit.describeAs("new description").andThenConsume(s -> access.writeUsing("state", s));

    final UnitState state = access.readFrom("state");

    Assert.assertEquals("new description", state.description);
  }

  @Test
  public void testThatUnitRenamed() {
    unit.renameTo("new name").andThenConsume(s -> access.writeUsing("state", s));

    final UnitState state = access.readFrom("state");

    Assert.assertEquals("new name", state.name);
  }
}
