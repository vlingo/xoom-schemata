// Copyright © 2012-2020 VLINGO LABS. All rights reserved.
//
// This Source Code Form is subject to the terms of the
// Mozilla Public License, v. 2.0. If a copy of the MPL
// was not distributed with this file, You can obtain
// one at https://mozilla.org/MPL/2.0/.

package io.vlingo.schemata.model;

import java.util.concurrent.atomic.AtomicReference;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import io.vlingo.actors.World;
import io.vlingo.actors.testkit.AccessSafely;
import io.vlingo.lattice.model.sourcing.SourcedTypeRegistry;
import io.vlingo.lattice.model.sourcing.SourcedTypeRegistry.Info;
import io.vlingo.schemata.NoopDispatcher;
import io.vlingo.schemata.model.Id.ContextId;
import io.vlingo.schemata.model.Id.OrganizationId;
import io.vlingo.schemata.model.Id.UnitId;
import io.vlingo.symbio.store.journal.Journal;
import io.vlingo.symbio.store.journal.inmemory.InMemoryJournalActor;

public class ContextEntityTest {
  private AccessSafely access;
  private AtomicReference<ContextState> ctxState;

  private Context context;
  private Journal<String> journal;
  private SourcedTypeRegistry registry;
  private World world;
  private ContextId contextId;

  @Before
  @SuppressWarnings({ "unchecked", "rawtypes" })
  public void setUp() {
    world = World.start("context-test");

    journal = world.actorFor(Journal.class, InMemoryJournalActor.class, new NoopDispatcher());

    registry = new SourcedTypeRegistry(world);
    registry.register(new Info(journal, ContextEntity.class, ContextEntity.class.getSimpleName()));

    contextId = ContextId.uniqueFor(UnitId.uniqueFor(OrganizationId.unique()));
    context = world.actorFor(Context.class, ContextEntity.class, contextId);

    ctxState = new AtomicReference<>();
    access = AccessSafely.afterCompleting(1);
    access.writingWith("state", (ContextState s) -> ctxState.set(s));
    access.readingWith("state", ()-> ctxState.get());
  }

  @After
  public void tearDown() {
    world.terminate();
  }

  @Test
  public void testThatContextIsDefined() {
    context.defineWith("namespace", "description").andThenConsume(s -> access.writeUsing("state", s));

    final ContextState state = access.readFrom("state");

    Assert.assertEquals(contextId.value, state.contextId.value);
    Assert.assertEquals("namespace", state.namespace);
    Assert.assertEquals("description", state.description);
  }

  @Test
  public void testThatContextRenamed() {
    context.moveToNamespace("new namespace").andThenConsume(s -> access.writeUsing("state", s));

    final ContextState state = access.readFrom("state");

    Assert.assertEquals("new namespace", state.namespace);
  }

  @Test
  public void testThatContextIsDescribed() {
    context.describeAs("new description").andThenConsume(s -> access.writeUsing("state", s));

    final ContextState state = access.readFrom("state");

    Assert.assertEquals("new description", state.description);
  }
}
