// Copyright © 2012-2020 VLINGO LABS. All rights reserved.
//
// This Source Code Form is subject to the terms of the
// Mozilla Public License, v. 2.0. If a copy of the MPL
// was not distributed with this file, You can obtain
// one at https://mozilla.org/MPL/2.0/.

package io.vlingo.xoom.schemata.model;

import java.util.Arrays;
import io.vlingo.xoom.actors.World;
import io.vlingo.xoom.actors.testkit.AccessSafely;
import io.vlingo.xoom.lattice.model.sourcing.SourcedTypeRegistry;
import io.vlingo.xoom.schemata.NoopDispatcher;
import io.vlingo.xoom.schemata.model.Id.ContextId;
import io.vlingo.xoom.schemata.model.Id.OrganizationId;
import io.vlingo.xoom.schemata.model.Id.UnitId;
import io.vlingo.xoom.symbio.store.journal.Journal;
import io.vlingo.xoom.symbio.store.journal.inmemory.InMemoryJournalActor;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.concurrent.atomic.AtomicReference;

public class ContextEntityTest {
  private AccessSafely access;
  private AtomicReference<ContextState> contextState;

  private Journal<String> journal;
  private Context context;
  private ContextId contextId;
  private SourcedTypeRegistry registry;
  private World world;

  @Before
  @SuppressWarnings({ "unchecked", "rawtypes" })
  public void setUp() {
    world = World.start("context-test");

    journal = world.actorFor(Journal.class, InMemoryJournalActor.class, Arrays.asList(new NoopDispatcher()));

    registry = new SourcedTypeRegistry(world);
    registry.register(new SourcedTypeRegistry.Info(journal, ContextEntity.class, ContextEntity.class.getSimpleName()));

    contextId = ContextId.uniqueFor(UnitId.uniqueFor(OrganizationId.unique()));
    context = world.actorFor(Context.class, ContextEntity.class, contextId);

    contextState = new AtomicReference<>();
    access = AccessSafely.afterCompleting(1);
    access.writingWith("state", (ContextState s) -> contextState.set(s));
    access.readingWith("state", () -> contextState.get());
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
    final ContextState contextState = context.moveToNamespace("new namespace").await();
    Assert.assertEquals("new namespace", contextState.namespace);
  }

  @Test
  public void testThatContextIsDescribed() {
    final ContextState contextState = context.describeAs("new description").await();
    Assert.assertEquals("new description", contextState.description);
  }
}
