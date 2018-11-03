// Copyright © 2012-2018 Vaughn Vernon. All rights reserved.
//
// This Source Code Form is subject to the terms of the
// Mozilla Public License, v. 2.0. If a copy of the MPL
// was not distributed with this file, You can obtain
// one at https://mozilla.org/MPL/2.0/.

package io.vlingo.schemata.model;

import static org.junit.Assert.assertEquals;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import io.vlingo.actors.Definition;
import io.vlingo.actors.testkit.TestActor;
import io.vlingo.actors.testkit.TestWorld;
import io.vlingo.lattice.model.sourcing.Sourced;
import io.vlingo.schemata.model.Events.SchemaDefined;
import io.vlingo.schemata.model.Events.SchemaDescribed;
import io.vlingo.schemata.model.Events.SchemaRecategorized;
import io.vlingo.schemata.model.Events.SchemaRenamed;
import io.vlingo.schemata.model.Id.ContextId;
import io.vlingo.schemata.model.Id.OrganizationId;
import io.vlingo.schemata.model.Id.SchemaId;
import io.vlingo.schemata.model.Id.UnitId;

public class SchemaEntityTest {
  private TestWorld world;
  private TestActor<Schema> schema;

  @Before
  public void setUp() throws Exception {
    world = TestWorld.start("schema-test");
    schema = world.actorFor(Definition.has(SchemaEntity.class, Definition.parameters(SchemaId.uniqueFor(ContextId.uniqueFor(UnitId.uniqueFor(OrganizationId.unique()))), Category.Events, "name", "description")), Schema.class);
  }

  @After
  public void tearDown() {
    world.terminate();
  }

  @Test
  public void testThatSchemaDefinedIsEquals() throws Exception {
    final SchemaDefined schemaDefined = (SchemaDefined) sourced().appliedEvent(0);
    assertEquals(1, sourced().appliedCount());
    assertEquals(Category.Events.name(), schemaDefined.category);
    assertEquals("name", schemaDefined.name);
    assertEquals("description", schemaDefined.description);
  }

  @Test
  public void testThatSchemaIsDescribed() throws Exception {
    schema.actor().describeAs("new description");
    final SchemaDescribed schemaDescribed = (SchemaDescribed) sourced().appliedEvent(1);
    assertEquals(2, sourced().appliedCount());
    assertEquals("new description", schemaDescribed.description);
  }

  @Test
  public void testThatSchemaRecategorised() throws Exception {
    schema.actor().recategorizedAs(Category.Documents);
    final SchemaRecategorized schemaRecategorized = (SchemaRecategorized) sourced().appliedEvent(1);
    assertEquals(2, sourced().appliedCount());
    assertEquals(Category.Documents.name(), schemaRecategorized.category);
  }

  @Test
  public void testThatSchemaRenamed() throws Exception {
    schema.actor().renameTo("new name");
    final SchemaRenamed schemaRenamed = (SchemaRenamed) sourced().appliedEvent(1);
    assertEquals(2, sourced().appliedCount());
    assertEquals("new name", schemaRenamed.name);
  }

  @SuppressWarnings("unchecked")
  private <T> Sourced<T> sourced() {
    return (Sourced<T>) schema.actorInside();
  }
}
