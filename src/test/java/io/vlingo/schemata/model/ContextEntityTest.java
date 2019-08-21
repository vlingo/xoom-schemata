// Copyright © 2012-2018 Vaughn Vernon. All rights reserved.
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
import io.vlingo.schemata.model.Id.ContextId;
import io.vlingo.schemata.model.Id.OrganizationId;
import io.vlingo.schemata.model.Id.UnitId;
import io.vlingo.symbio.store.object.MapQueryExpression;
import io.vlingo.symbio.store.object.ObjectStore;
import io.vlingo.symbio.store.object.StateObjectMapper;
import io.vlingo.symbio.store.object.inmemory.InMemoryObjectStoreActor;

public class ContextEntityTest {
  private Context context;
  private ObjectTypeRegistry registry;
  private ObjectStore objectStore;
  private World world;
  private ContextId contextId;

  @Before
  @SuppressWarnings({ "unchecked", "rawtypes" })
  public void setUp() {
    world = World.start("context-test");

    objectStore = world.actorFor(ObjectStore.class, InMemoryObjectStoreActor.class, new NoopDispatcher());

    registry = new ObjectTypeRegistry(world);

    // NOTE: The InMemoryObjectStoreActor implementation currently
    // does not use PersistentObjectMapper, and thus the no-op decl.
    final Info<Context> contextInfo =
            new Info(
                    objectStore,
                    ContextState.class,
                    "HR-Database",
                    MapQueryExpression.using(Context.class, "find", MapQueryExpression.map("id", "id")),
                    StateObjectMapper.with(Context.class, new Object(), new Object()));

    registry.register(contextInfo);

    contextId = ContextId.uniqueFor(UnitId.uniqueFor(OrganizationId.unique()));
    context = world.actorFor(Context.class, ContextEntity.class, contextId);
  }

  @After
  public void tearDown() {
    world.terminate();
  }

  @Test
  public void testThatContextIsDefined() {
    final ContextState contextState = context.defineWith("namespace", "description").await();
    Assert.assertEquals(ContextState.unidentified(), contextState.persistenceId());
    Assert.assertEquals(contextId.value, contextState.contextId.value);
    Assert.assertEquals("namespace", contextState.namespace);
    Assert.assertEquals("description", contextState.description);
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
