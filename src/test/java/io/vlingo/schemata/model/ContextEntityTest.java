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
import io.vlingo.schemata.model.Events.ContextDefined;
import io.vlingo.schemata.model.Events.ContextDescribed;
import io.vlingo.schemata.model.Events.ContextRenamed;
import io.vlingo.schemata.model.Id.ContextId;
import io.vlingo.schemata.model.Id.OrganizationId;
import io.vlingo.schemata.model.Id.UnitId;
import io.vlingo.symbio.store.journal.Journal;
import io.vlingo.symbio.store.journal.inmemory.InMemoryJournalActor;

public class ContextEntityTest {
  private TestWorld world;
  private TestActor<Context> contextTestActor;
  private Journal<String> journal;
  private MockJournalListener listener;
  private SourcedTypeRegistry registry;

  @Before
  @SuppressWarnings({ "unchecked" })
  public void setUp() throws Exception {
    world = TestWorld.start("context-test");

    listener = new MockJournalListener();

    journal = world.world().actorFor(Journal.class, InMemoryJournalActor.class, listener);

    registry = new SourcedTypeRegistry(world.world());

    EntryAdapters.register(registry, journal);

    contextTestActor = world.actorFor(Context.class, ContextEntity.class, ContextId.uniqueFor(UnitId.uniqueFor(OrganizationId.unique())));
    contextTestActor.context().until.resetHappeningsTo(1);
    contextTestActor.actor().defineWith("namespace", "description");
  }

  @After
  public void tearDown() {
    world.terminate();
  }

  @Test
  public void testThatContextIsDefined() throws Exception {
    contextTestActor.context().until.completes(); // see setUp()
    final List<DomainEvent> applied = contextTestActor.viewTestState().valueOf("applied");
    final ContextDefined contextDefined = (ContextDefined) applied.get(0);
    Assert.assertEquals("namespace", contextDefined.name);
    Assert.assertEquals("description", contextDefined.description);
  }

  @Test
  public void testThatContextRenamed() throws Exception {
    contextTestActor.context().until.completes(); // see setUp()
    contextTestActor.context().until.resetHappeningsTo(1);
    contextTestActor.actor().changeNamespaceTo("new namespace");
    contextTestActor.context().until.completes();
    final List<DomainEvent> applied = contextTestActor.viewTestState().valueOf("applied");
    final ContextRenamed contextRenamed = (ContextRenamed) applied.get(1);
    Assert.assertEquals("new namespace", contextRenamed.namespace);
  }

  @Test
  public void testThatContextIsDescribed() throws Exception {
    contextTestActor.context().until.completes(); // see setUp()
    contextTestActor.context().until.resetHappeningsTo(1);
    contextTestActor.actor().describeAs("new description");
    contextTestActor.context().until.completes();
    final List<DomainEvent> applied = contextTestActor.viewTestState().valueOf("applied");
    final ContextDescribed contextDescribed = (ContextDescribed) applied.get(1);
    Assert.assertEquals("new description", contextDescribed.description);
  }
}
