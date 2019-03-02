// Copyright © 2012-2018 Vaughn Vernon. All rights reserved.
//
// This Source Code Form is subject to the terms of the
// Mozilla Public License, v. 2.0. If a copy of the MPL
// was not distributed with this file, You can obtain
// one at https://mozilla.org/MPL/2.0/.

package io.vlingo.schemata.model;

import static org.junit.Assert.assertEquals;

import java.util.List;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import io.vlingo.actors.testkit.TestActor;
import io.vlingo.actors.testkit.TestWorld;
import io.vlingo.lattice.model.DomainEvent;
import io.vlingo.lattice.model.sourcing.SourcedTypeRegistry;
import io.vlingo.schemata.NoopJournalListener;
import io.vlingo.schemata.infra.persistence.EntryAdapters;
import io.vlingo.schemata.model.Events.SchemaDefined;
import io.vlingo.schemata.model.Events.SchemaDescribed;
import io.vlingo.schemata.model.Events.SchemaRecategorized;
import io.vlingo.schemata.model.Events.SchemaRenamed;
import io.vlingo.schemata.model.Id.ContextId;
import io.vlingo.schemata.model.Id.OrganizationId;
import io.vlingo.schemata.model.Id.SchemaId;
import io.vlingo.schemata.model.Id.UnitId;
import io.vlingo.symbio.store.journal.Journal;
import io.vlingo.symbio.store.journal.inmemory.InMemoryJournalActor;

public class SchemaEntityTest {
  private TestWorld world;
  private TestActor<Schema> schema;
  private Journal<String> journal;
  private NoopJournalListener listener;
  private SourcedTypeRegistry registry;

  @Before
  @SuppressWarnings("unchecked")
  public void setUp() throws Exception {
    world = TestWorld.start("schema-test");

    listener = new NoopJournalListener();

    journal = world.world().actorFor(Journal.class, InMemoryJournalActor.class, listener);

    registry = new SourcedTypeRegistry(world.world());

    EntryAdapters.register(registry, journal);

    schema = world.actorFor(Schema.class, SchemaEntity.class, SchemaId.uniqueFor(ContextId.uniqueFor(UnitId.uniqueFor(OrganizationId.unique()))));
    schema.context().until.resetHappeningsTo(1);
    schema.actor().defineWith(Category.Event, "name", "description");
  }

  @After
  public void tearDown() {
    world.terminate();
  }

  @Test
  public void testThatSchemaDefinedIsEquals() throws Exception {
    schema.context().until.completes(); // see setUp()
    final List<DomainEvent> applied = schema.viewTestState().valueOf("applied");
    final SchemaDefined schemaDefined = (SchemaDefined) applied.get(0);
    assertEquals(1, applied.size());
    assertEquals(Category.Event.name(), schemaDefined.category);
    assertEquals("name", schemaDefined.name);
    assertEquals("description", schemaDefined.description);
  }

  @Test
  public void testThatSchemaIsDescribed() throws Exception {
    schema.context().until.completes(); // see setUp()
    schema.context().until.resetHappeningsTo(1);
    schema.actor().describeAs("new description");
    schema.context().until.completes();
    final List<DomainEvent> applied = schema.viewTestState().valueOf("applied");
    assertEquals(2, applied.size());
    final SchemaDescribed schemaDescribed = (SchemaDescribed) applied.get(1);
    assertEquals("new description", schemaDescribed.description);
  }

  @Test
  public void testThatSchemaRecategorised() throws Exception {
    schema.context().until.completes(); // see setUp()
    schema.context().until.resetHappeningsTo(1);
    schema.actor().recategorizedAs(Category.Document);
    schema.context().until.completes();
    final List<DomainEvent> applied = schema.viewTestState().valueOf("applied");
    assertEquals(2, applied.size());
    final SchemaRecategorized schemaRecategorized = (SchemaRecategorized) applied.get(1);
    assertEquals(Category.Document.name(), schemaRecategorized.category);
  }

  @Test
  public void testThatSchemaRenamed() throws Exception {
    schema.context().until.completes(); // see setUp()
    schema.context().until.resetHappeningsTo(1);
    schema.actor().renameTo("new name");
    schema.context().until.completes();
    final List<DomainEvent> applied = schema.viewTestState().valueOf("applied");
    assertEquals(2, applied.size());
    final SchemaRenamed schemaRenamed = (SchemaRenamed) applied.get(1);
    assertEquals("new name", schemaRenamed.name);
  }
}
