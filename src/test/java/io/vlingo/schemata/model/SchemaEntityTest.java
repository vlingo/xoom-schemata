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

import io.vlingo.actors.World;
import io.vlingo.actors.testkit.AccessSafely;
import io.vlingo.lattice.model.DomainEvent;
import io.vlingo.lattice.model.sourcing.SourcedTypeRegistry;
import io.vlingo.schemata.MockJournalDispatcher;
import io.vlingo.schemata.infra.persistence.EntryAdapters;
import io.vlingo.schemata.model.Events.SchemaDefined;
import io.vlingo.schemata.model.Events.SchemaDescribed;
import io.vlingo.schemata.model.Events.SchemaRecategorized;
import io.vlingo.schemata.model.Events.SchemaRenamed;
import io.vlingo.schemata.model.Id.ContextId;
import io.vlingo.schemata.model.Id.OrganizationId;
import io.vlingo.schemata.model.Id.SchemaId;
import io.vlingo.schemata.model.Id.UnitId;
import io.vlingo.symbio.EntryAdapterProvider;
import io.vlingo.symbio.store.journal.Journal;
import io.vlingo.symbio.store.journal.inmemory.InMemoryJournalActor;

public class SchemaEntityTest {
  private AccessSafely access;
  private Journal<String> journal;
  private MockJournalDispatcher listener;
  private SourcedTypeRegistry registry;
  private Schema schema;
  private World world;

  @Before
  @SuppressWarnings("unchecked")
  public void setUp() throws Exception {
    world = World.start("schema-test");

    listener = new MockJournalDispatcher(EntryAdapterProvider.instance(world));

    journal = world.world().actorFor(Journal.class, InMemoryJournalActor.class, listener);

    registry = new SourcedTypeRegistry(world.world());

    EntryAdapters.register(registry, journal);

    schema = world.actorFor(Schema.class, SchemaEntity.class, SchemaId.uniqueFor(ContextId.uniqueFor(UnitId.uniqueFor(OrganizationId.unique()))));
    access = listener.afterCompleting(1);
    schema.defineWith(Category.Event, "name", "description");
  }

  @After
  public void tearDown() {
    world.terminate();
  }

  @Test
  public void testThatSchemaDefinedIsEquals() throws Exception {
    final List<DomainEvent> applied = access.readFrom("entries"); // see setUp()
    final SchemaDefined schemaDefined = (SchemaDefined) applied.get(0);
    assertEquals(1, applied.size());
    assertEquals(Category.Event.name(), schemaDefined.category);
    assertEquals("name", schemaDefined.name);
    assertEquals("description", schemaDefined.description);
  }

  @Test
  public void testThatSchemaIsDescribed() throws Exception {
    access.readFrom("entries"); // see setUp()
    access = listener.afterCompleting(1);
    schema.describeAs("new description");
    final List<DomainEvent> applied = access.readFrom("entries");
    assertEquals(2, applied.size());
    final SchemaDescribed schemaDescribed = (SchemaDescribed) applied.get(1);
    assertEquals("new description", schemaDescribed.description);
  }

  @Test
  public void testThatSchemaRecategorised() throws Exception {
    access.readFrom("entries"); // see setUp()
    access = listener.afterCompleting(1);
    schema.recategorizedAs(Category.Document);
    final List<DomainEvent> applied = access.readFrom("entries");
    assertEquals(2, applied.size());
    final SchemaRecategorized schemaRecategorized = (SchemaRecategorized) applied.get(1);
    assertEquals(Category.Document.name(), schemaRecategorized.category);
  }

  @Test
  public void testThatSchemaRenamed() throws Exception {
    access.readFrom("entries"); // see setUp()
    access = listener.afterCompleting(1);
    schema.renameTo("new name");
    final List<DomainEvent> applied = access.readFrom("entries");
    assertEquals(2, applied.size());
    final SchemaRenamed schemaRenamed = (SchemaRenamed) applied.get(1);
    assertEquals("new name", schemaRenamed.name);
  }
}
