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
import io.vlingo.schemata.model.Events.ContextDefined;
import io.vlingo.schemata.model.Events.ContextDescribed;
import io.vlingo.schemata.model.Events.ContextRenamed;
import io.vlingo.schemata.model.Id.ContextId;
import io.vlingo.schemata.model.Id.OrganizationId;
import io.vlingo.schemata.model.Id.UnitId;
import io.vlingo.symbio.EntryAdapterProvider;
import io.vlingo.symbio.store.journal.Journal;
import io.vlingo.symbio.store.journal.inmemory.InMemoryJournalActor;

public class ContextEntityTest {
  private AccessSafely access;
  private Context context;
  private Journal<String> journal;
  private MockJournalListener listener;
  private SourcedTypeRegistry registry;
  private World world;

  @Before
  @SuppressWarnings({ "unchecked" })
  public void setUp() throws Exception {
    world = World.start("context-test");

    listener = new MockJournalListener(EntryAdapterProvider.instance(world));

    journal = world.world().actorFor(Journal.class, InMemoryJournalActor.class, listener);

    registry = new SourcedTypeRegistry(world.world());

    EntryAdapters.register(registry, journal);

    context = world.actorFor(Context.class, ContextEntity.class, ContextId.uniqueFor(UnitId.uniqueFor(OrganizationId.unique())));
    access = listener.afterCompleting(1);
    context.defineWith("namespace", "description");
  }

  @After
  public void tearDown() {
    world.terminate();
  }

  @Test
  public void testThatContextIsDefined() throws Exception {
    final List<DomainEvent> applied = access.readFrom("entries"); // see setUp()
    final ContextDefined contextDefined = (ContextDefined) applied.get(0);
    Assert.assertEquals("namespace", contextDefined.name);
    Assert.assertEquals("description", contextDefined.description);
  }

  @Test
  public void testThatContextRenamed() throws Exception {
    access.readFrom("entries"); // see setUp
    access = listener.afterCompleting(1);
    context.changeNamespaceTo("new namespace");
    final List<DomainEvent> applied = access.readFrom("entries");
    final ContextRenamed contextRenamed = (ContextRenamed) applied.get(1);
    Assert.assertEquals("new namespace", contextRenamed.namespace);
  }

  @Test
  public void testThatContextIsDescribed() throws Exception {
    access.readFrom("entries"); // see setUp()
    access = listener.afterCompleting(1);
    context.describeAs("new description");
    final List<DomainEvent> applied = access.readFrom("entries");
    final ContextDescribed contextDescribed = (ContextDescribed) applied.get(1);
    Assert.assertEquals("new description", contextDescribed.description);
  }
}
