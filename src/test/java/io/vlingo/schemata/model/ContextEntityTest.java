// Copyright © 2012-2018 Vaughn Vernon. All rights reserved.
//
// This Source Code Form is subject to the terms of the
// Mozilla Public License, v. 2.0. If a copy of the MPL
// was not distributed with this file, You can obtain
// one at https://mozilla.org/MPL/2.0/.

package io.vlingo.schemata.model;

import io.vlingo.actors.World;
import io.vlingo.lattice.model.object.ObjectTypeRegistry;
import io.vlingo.lattice.model.object.ObjectTypeRegistry.Info;
import io.vlingo.schemata.NoopDispatcher;
import io.vlingo.schemata.model.Id.ContextId;
import io.vlingo.schemata.model.Id.UnitId;
import io.vlingo.schemata.model.Id.OrganizationId;
import io.vlingo.symbio.store.object.MapQueryExpression;
import io.vlingo.symbio.store.object.ObjectStore;
import io.vlingo.symbio.store.object.PersistentObjectMapper;
import io.vlingo.symbio.store.object.inmemory.InMemoryObjectStoreActor;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertTrue;

public class ContextEntityTest {
  private Context context;
  private ObjectTypeRegistry registry;
  private ObjectStore objectStore;
  private World world;

  @Before
  @SuppressWarnings({ "unchecked" })
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
                    PersistentObjectMapper.with(Context.class, new Object(), new Object()));

    registry.register(contextInfo);

    // Line below is commented because once the proxy is created a call to any of its methods blocks on .await()
    // the lambda in the proxy is never invoked.
    // context = world.actorFor(Context.class, ContextEntity.class, ContextId.uniqueFor(UnitId.uniqueFor(OrganizationId.unique())));
    context = world.actorFor(Context.class, ContextEntity.class);
  }

  @After
  public void tearDown() {
    world.terminate();
  }

  @Test
  public void testThatContextIsDefined() {
    final ContextId contextId = ContextId.uniqueFor(UnitId.uniqueFor(OrganizationId.unique()));
    final ContextState contextState = context.defineWith(contextId,"namespace", "description").await();
    assertTrue(contextState.persistenceId() > 0);
    Assert.assertEquals(contextId.value, contextState.contextId.value);
    Assert.assertEquals("namespace", contextState.namespace);
    Assert.assertEquals("description", contextState.description);
  }

  @Test
  public void testThatContextRenamed() throws Exception {
    final ContextState contextState = context.changeNamespaceTo("new namespace").await();
    Assert.assertEquals("new namespace", contextState.namespace);
  }

  @Test
  public void testThatContextIsDescribed() throws Exception {
    final ContextState contextState = context.describeAs("new description").await();
    Assert.assertEquals("new description", contextState.description);
  }
}
