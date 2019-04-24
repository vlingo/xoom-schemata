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

import io.vlingo.actors.World;
import io.vlingo.actors.testkit.AccessSafely;
import io.vlingo.lattice.model.DomainEvent;
import io.vlingo.lattice.model.sourcing.SourcedTypeRegistry;
import io.vlingo.schemata.MockJournalListener;
import io.vlingo.schemata.infra.persistence.EntryAdapters;
import io.vlingo.schemata.model.Events.UnitDefined;
import io.vlingo.schemata.model.Events.UnitDescribed;
import io.vlingo.schemata.model.Events.UnitRenamed;
import io.vlingo.symbio.EntryAdapterProvider;
import io.vlingo.symbio.store.journal.Journal;
import io.vlingo.symbio.store.journal.inmemory.InMemoryJournalActor;

public class UnitEntityTest {
  private AccessSafely access;
  private Journal<String> journal;
  private MockJournalListener listener;
  private SourcedTypeRegistry registry;
  private Unit unit;
  private World world;

  @Before
  @SuppressWarnings("unchecked")
  public void setUp() throws Exception {
    world = World.start("unit-entity-test");

    listener = new MockJournalListener(EntryAdapterProvider.instance(world));

    journal = world.world().actorFor(Journal.class, InMemoryJournalActor.class, listener);

    registry = new SourcedTypeRegistry(world.world());

    EntryAdapters.register(registry, journal);

    unit = world.actorFor(Unit.class, UnitEntity.class, Unit.uniqueId(Organization.uniqueId()));
    access = listener.afterCompleting(1);
    unit.defineWith("name", "description");
  }

  @After
  public void tearDown() throws Exception {
    world.terminate();
  }

  @Test
  public void testThatUnitDefined() throws Exception {
    final List<DomainEvent> applied = access.readFrom("entries"); // see setUp()
    final UnitDefined unitDefined = (UnitDefined) applied.get(0);
    Assert.assertEquals("name", unitDefined.name);
    Assert.assertEquals("description", unitDefined.description);
  }

  @Test
  public void testThatUnitDescribed() throws Exception {
    access.readFrom("entries"); // see setUp
    access = listener.afterCompleting(1);
    unit.describeAs("description");
    final List<DomainEvent> applied = access.readFrom("entries");
    final UnitDescribed unitDescribed = (UnitDescribed) applied.get(1);
    Assert.assertEquals("description", unitDescribed.description);
  }

  @Test
  public void testThatUnitRenamed() throws Exception {
    access.readFrom("entries"); // see setUp
    access = listener.afterCompleting(1);
    unit.renameTo("newName");
    final List<DomainEvent> applied = access.readFrom("entries");
    final UnitRenamed unitRenamed = (UnitRenamed) applied.get(1);
    Assert.assertEquals("newName", unitRenamed.name);
  }
}