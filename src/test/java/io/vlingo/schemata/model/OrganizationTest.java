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
import io.vlingo.schemata.model.Events.OrganizationDefined;
import io.vlingo.schemata.model.Events.OrganizationDescribed;
import io.vlingo.schemata.model.Events.OrganizationRenamed;

public class OrganizationTest {
  private Organization entity;
  private World world;

  @Before
  public void setUp() throws Exception {
    world = World.startWithDefaults("organization-test");
    entity = world.actorFor(Definition.has(OrganizationEntity.class, Definition.parameters(result)), Organization.class);
  }

  @Test
  public void testApplyDefinedVlingoSchemata() throws Exception {
    result.until.completes();
    Assert.assertTrue(result.defined);
    Assert.assertEquals(1, result.applied.size());
    Assert.assertEquals(OrganizationDefined.class, result.applied.get(0).getClass());
    Assert.assertFalse(result.described);
    Assert.assertFalse(result.renamed);
    Assert.assertFalse(result.recategorised);
  }

  @Test
  public void testApplyDescribedVlingoSchemata() throws Exception {
    result.until.completes();
    Assert.assertTrue(result.defined);
    Assert.assertFalse(result.described);
    Assert.assertFalse(result.renamed);
    Assert.assertFalse(result.recategorised);
    Assert.assertEquals(1, result.applied.size());
    Assert.assertEquals(OrganizationDefined.class, result.applied.get(0).getClass());
    result.until = TestUntil.happenings(1);
    entity.describeAs("newDesc");
    result.until.completes();
    Assert.assertEquals(2, result.applied.size());
    Assert.assertEquals(OrganizationDescribed.class, result.applied.get(1).getClass());

  }

  @Test
  public void testApplyRenamedVlingoSchemata() throws Exception {
    result.until.completes();
    Assert.assertTrue(result.defined);
    Assert.assertFalse(result.described);
    Assert.assertFalse(result.renamed);
    Assert.assertFalse(result.recategorised);
    Assert.assertEquals(1, result.applied.size());
    Assert.assertEquals(OrganizationDefined.class, result.applied.get(0).getClass());
    result.until = TestUntil.happenings(1);
    entity.describeAs("newDesc");
    result.until.completes();
    Assert.assertEquals(2, result.applied.size());
    Assert.assertEquals(OrganizationDescribed.class, result.applied.get(1).getClass());
    result.until = TestUntil.happenings(2);
    entity.renameTo("newName");
    result.until.completes();
    Assert.assertEquals(3, result.applied.size());
    Assert.assertEquals(OrganizationRenamed.class, result.applied.get(2).getClass());
  }
}
