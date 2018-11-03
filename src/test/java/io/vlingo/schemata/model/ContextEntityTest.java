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

import io.vlingo.actors.Definition;
import io.vlingo.actors.testkit.TestActor;
import io.vlingo.actors.testkit.TestWorld;
import io.vlingo.lattice.model.sourcing.Sourced;
import io.vlingo.schemata.model.Events.ContextDefined;
import io.vlingo.schemata.model.Events.ContextDescribed;
import io.vlingo.schemata.model.Events.ContextRenamed;
import io.vlingo.schemata.model.Id.ContextId;
import io.vlingo.schemata.model.Id.OrganizationId;
import io.vlingo.schemata.model.Id.UnitId;

public class ContextEntityTest {
  private TestWorld world;
  private TestActor<Context> contextTestActor;

  @Before
  public void setUp() throws Exception {
    world = TestWorld.start("context-test");
    contextTestActor = world.actorFor(Definition.has(ContextEntity.class, Definition.parameters(ContextId.uniqueFor(UnitId.uniqueFor(OrganizationId.unique())), "namespace", "description")), Context.class);
  }

  @After
  public void tearDown() {
    world.terminate();
  }

  @Test
  public void testThatContextIsDefined() throws Exception {
    contextTestActor.actor();
    final ContextDefined contextDefined = (ContextDefined) sourced().appliedEvent(0);
    Assert.assertEquals("namespace", contextDefined.namespace);
    Assert.assertEquals("description", contextDefined.description);
  }

  @Test
  public void testThatContextRenamed() throws Exception {
    contextTestActor.actor().changeNamespaceTo("new namespace");
    final ContextRenamed contextRenamed = (ContextRenamed) sourced().appliedEvent(1);
    Assert.assertEquals("new namespace", contextRenamed.namespace);
  }

  @Test
  public void testThatContextIsDescribed() throws Exception {
    contextTestActor.actor().describeAs("new description");
    final ContextDescribed contextDescribed = (ContextDescribed) sourced().appliedEvent(1);
    Assert.assertEquals("new description", contextDescribed.description);
  }

  @SuppressWarnings("unchecked")
  private <T> Sourced<T> sourced() {
    return (Sourced<T>) contextTestActor.actorInside();
  }
}