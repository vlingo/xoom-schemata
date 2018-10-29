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
import io.vlingo.schemata.model.Events.SchemaDefined;
import io.vlingo.schemata.model.Events.SchemaDescribed;
import io.vlingo.schemata.model.Events.SchemaRecategorized;
import io.vlingo.schemata.model.Events.SchemaRenamed;

public class SchemaTest {
  private Schema entity;
  private World world;

  @Before
  public void setUp() throws Exception {
    world = World.startWithDefaults("schema-test");
    entity = world.actorFor(Definition.has(SchemaEntity.class, Definition.parameters(result)), Schema.class);
  }

  @Test
  public void testApplyDefinedVlingoSchemata() throws Exception {
    result.until.completes();
    Assert.assertTrue(result.defined);
    Assert.assertEquals(1, result.applied.size());
    Assert.assertEquals(SchemaDefined.class, result.applied.get(0).getClass());
    Assert.assertFalse(result.recategorised);
    Assert.assertFalse(result.renamed);
    Assert.assertFalse(result.described);
  }

  @Test
  public void testApplyDescribedVlingoSchemata() throws Exception {
    result.until.completes();
    Assert.assertTrue(result.defined);
    Assert.assertFalse(result.recategorised);
    Assert.assertFalse(result.renamed);
    Assert.assertFalse(result.described);
    Assert.assertEquals(1, result.applied.size());
    Assert.assertEquals(SchemaDefined.class, result.applied.get(0).getClass());
    result.until = TestUntil.happenings(1);
    entity.describeAs("newDesc");
    result.until.completes();
    Assert.assertEquals(2, result.applied.size());
    Assert.assertEquals(SchemaDescribed.class, result.applied.get(1).getClass());

  }

  @Test
  public void testApplyRecategorizedVlingoSchemata() throws Exception {
    result.until.completes();
    Assert.assertTrue(result.defined);
    Assert.assertFalse(result.recategorised);
    Assert.assertFalse(result.renamed);
    Assert.assertFalse(result.described);
    Assert.assertEquals(1, result.applied.size());
    Assert.assertEquals(SchemaDefined.class, result.applied.get(0).getClass());
    result.until = TestUntil.happenings(1);
    entity.describeAs("newDesc");
    result.until.completes();
    Assert.assertEquals(2, result.applied.size());
    Assert.assertEquals(SchemaDescribed.class, result.applied.get(1).getClass());
    result.until = TestUntil.happenings(2);
    entity.recategorizedAs(EventsCategory.Category.Commands);
    result.until.completes();
    Assert.assertEquals(3, result.applied.size());
    Assert.assertEquals(SchemaRecategorized.class, result.applied.get(2).getClass());
  }

  @Test
  public void testApplyRenamedVlingoSchemata() throws Exception {
    result.until.completes();
    Assert.assertTrue(result.defined);
    Assert.assertFalse(result.recategorised);
    Assert.assertFalse(result.renamed);
    Assert.assertFalse(result.described);
    Assert.assertEquals(1, result.applied.size());
    Assert.assertEquals(SchemaDefined.class, result.applied.get(0).getClass());
    result.until = TestUntil.happenings(1);
    entity.describeAs("newDesc");
    result.until.completes();
    Assert.assertEquals(2, result.applied.size());
    Assert.assertEquals(SchemaDescribed.class, result.applied.get(1).getClass());
    result.until = TestUntil.happenings(2);
    entity.recategorizedAs(EventsCategory.Category.Commands);
    result.until.completes();
    Assert.assertEquals(3, result.applied.size());
    Assert.assertEquals(SchemaRecategorized.class, result.applied.get(2).getClass());
    result.until = TestUntil.happenings(3);
    entity.renameTo("newName");
    result.until.completes();
    Assert.assertEquals(4, result.applied.size());
    Assert.assertEquals(SchemaRenamed.class, result.applied.get(3).getClass());
  }
}
