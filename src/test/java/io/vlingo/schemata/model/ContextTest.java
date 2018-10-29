// Copyright © 2012-2018 Vaughn Vernon. All rights reserved.
//
// This Source Code Form is subject to the terms of the
// Mozilla Public License, v. 2.0. If a copy of the MPL
// was not distributed with this file, You can obtain
// one at https://mozilla.org/MPL/2.0/.

package io.vlingo.schemata.model;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import io.vlingo.actors.Definition;
import io.vlingo.actors.World;
import io.vlingo.actors.testkit.TestUntil;
import io.vlingo.schemata.model.Events.ContextDefined;
import io.vlingo.schemata.model.Events.ContextDescribed;
import io.vlingo.schemata.model.Events.ContextRenamed;

public class ContextTest {
  private Context entity;
  private World world;

  @Before
  public void setUp() throws Exception {
    world = World.startWithDefaults("context-test");
    entity = world.actorFor(Definition.has(ContextEntity.class, Definition.parameters(result)), Context.class);
  }

  @Test
  public void testApplyDefinedVlingoSchemata() throws Exception {
    result.until.completes();
    Assert.assertTrue(result.defined);
    Assert.assertEquals(1, result.applied.size());
    Assert.assertEquals(ContextDefined.class, result.applied.get(0).getClass());
    Assert.assertFalse(result.described);
    Assert.assertFalse(result.renamed);
    Assert.assertFalse(result.recategorised);
  }

  @Test
  public void testApplyDescribedVlingoSchemata() throws Exception {
    result.until.completes();
    Assert.assertTrue(result.defined);
    Assert.assertFalse(result.renamed);
    Assert.assertFalse(result.described);
    Assert.assertFalse(result.recategorised);
    Assert.assertEquals(1, result.applied.size());
    Assert.assertEquals(ContextDefined.class, result.applied.get(0).getClass());
    result.until = TestUntil.happenings(1);
    entity.describeAs("desc");
    result.until.completes();
    Assert.assertEquals(2, result.applied.size());
    Assert.assertEquals(ContextDescribed.class, result.applied.get(1).getClass());

  }

  @Test
  public void applyNamespaceChangedVlingoSchemata() throws Exception {
    result.until.completes();
    Assert.assertTrue(result.defined);
    Assert.assertFalse(result.renamed);
    Assert.assertFalse(result.described);
    Assert.assertFalse(result.recategorised);
    Assert.assertEquals(1, result.applied.size());
    Assert.assertEquals(ContextDefined.class, result.applied.get(0).getClass());
    result.until = TestUntil.happenings(1);
    entity.describeAs("description");
    result.until.completes();
    Assert.assertEquals(2, result.applied.size());
    Assert.assertEquals(ContextDescribed.class, result.applied.get(1).getClass());
    result.until = TestUntil.happenings(2);
    entity.changeNamespaceTo("newNamespace");
    result.until.completes();
    Assert.assertEquals(3, result.applied.size());
    Assert.assertEquals(ContextRenamed.class, result.applied.get(2).getClass());
  }

}