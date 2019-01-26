// Copyright © 2012-2018 Vaughn Vernon. All rights reserved.
//
// This Source Code Form is subject to the terms of the
// Mozilla Public License, v. 2.0. If a copy of the MPL
// was not distributed with this file, You can obtain
// one at https://mozilla.org/MPL/2.0/.

package io.vlingo.schemata.model;

import java.util.List;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import io.vlingo.actors.testkit.TestActor;
import io.vlingo.actors.testkit.TestWorld;
import io.vlingo.lattice.model.DomainEvent;
import io.vlingo.lattice.model.sourcing.SourcedTypeRegistry;
import io.vlingo.schemata.MockJournalListener;
import io.vlingo.schemata.infra.persistence.EntryAdapters;
import io.vlingo.schemata.model.Events.UnitDefined;
import io.vlingo.schemata.model.Events.UnitDescribed;
import io.vlingo.schemata.model.Events.UnitRenamed;
import io.vlingo.symbio.store.journal.Journal;
import io.vlingo.symbio.store.journal.inmemory.InMemoryJournalActor;

public class UnitEntityTest {
  private TestWorld world;
  private TestActor<Unit> unit;
  private Journal<String> journal;
  private MockJournalListener listener;
  private SourcedTypeRegistry registry;

  @Before
  @SuppressWarnings("unchecked")
  public void setUp() throws Exception {
    world = TestWorld.start("unit-entity-test");

    listener = new MockJournalListener();

    journal = world.world().actorFor(Journal.class, InMemoryJournalActor.class, listener);

    registry = new SourcedTypeRegistry(world.world());

    EntryAdapters.register(registry, journal);

    unit = world.actorFor(Unit.class, UnitEntity.class, Unit.uniqueId(Organization.uniqueId()));
    unit.context().until.resetHappeningsTo(1);
    unit.actor().defineWith("name", "description");
  }

  @After
  public void tearDown() throws Exception {
    world.terminate();
  }

  @Test
  public void testThatUnitDefined() throws Exception {
    unit.context().until.completes(); // see setUp()
    final List<DomainEvent> applied = unit.viewTestState().valueOf("applied");
    final UnitDefined unitDefined = (UnitDefined) applied.get(0);
    Assert.assertEquals("name", unitDefined.name);
    Assert.assertEquals("description", unitDefined.description);
  }

  @Test
  public void testThatUnitDescribed() throws Exception {
    unit.context().until.completes(); // see setUp()
    unit.context().until.resetHappeningsTo(1);
    unit.actor().describeAs("description");
    unit.context().until.completes();
    final List<DomainEvent> applied = unit.viewTestState().valueOf("applied");
    final UnitDescribed unitDescribed = (UnitDescribed) applied.get(1);
    Assert.assertEquals("description", unitDescribed.description);
  }

  @Test
  public void testThatUnitRenamed() throws Exception {
    unit.context().until.completes(); // see setUp()
    unit.context().until.resetHappeningsTo(1);
    unit.actor().renameTo("newName");
    unit.context().until.completes();
    final List<DomainEvent> applied = unit.viewTestState().valueOf("applied");
    final UnitRenamed unitRenamed = (UnitRenamed) applied.get(1);
    Assert.assertEquals("newName", unitRenamed.name);
  }
}