// Copyright © 2012-2021 VLINGO LABS. All rights reserved.
//
// This Source Code Form is subject to the terms of the
// Mozilla Public License, v. 2.0. If a copy of the MPL
// was not distributed with this file, You can obtain
// one at https://mozilla.org/MPL/2.0/.

package io.vlingo.xoom.schemata.model;

import java.util.Arrays;
import io.vlingo.xoom.actors.testkit.AccessSafely;
import io.vlingo.xoom.lattice.model.sourcing.SourcedTypeRegistry;
import io.vlingo.xoom.symbio.store.journal.Journal;
import io.vlingo.xoom.symbio.store.journal.inmemory.InMemoryJournalActor;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import io.vlingo.xoom.actors.World;
import io.vlingo.xoom.schemata.NoopDispatcher;
import io.vlingo.xoom.schemata.model.Id.ContextId;
import io.vlingo.xoom.schemata.model.Id.OrganizationId;
import io.vlingo.xoom.schemata.model.Id.SchemaId;
import io.vlingo.xoom.schemata.model.Id.UnitId;

import java.util.concurrent.atomic.AtomicReference;

public class SchemaEntityTest {
  private AccessSafely access;
  private AtomicReference<SchemaState> schemaState;

  private Journal<String> journal;
  private Schema schema;
  private SchemaId schemaId;
  private SourcedTypeRegistry registry;
  private World world;

  @Before
  @SuppressWarnings({ "unchecked", "rawtypes" })
  public void setUp() {
    world = World.start("schema-test");

    journal = world.actorFor(Journal.class, InMemoryJournalActor.class, Arrays.asList(new NoopDispatcher()));

    registry = new SourcedTypeRegistry(world);
    registry.register(new SourcedTypeRegistry.Info(journal, SchemaEntity.class, SchemaEntity.class.getSimpleName()));

    schemaId = SchemaId.uniqueFor(ContextId.uniqueFor(UnitId.uniqueFor(OrganizationId.unique())));
    schema = world.actorFor(Schema.class, SchemaEntity.class, schemaId);

    schemaState = new AtomicReference<>();
    access = AccessSafely.afterCompleting(1);
    access.writingWith("state", (SchemaState s) -> schemaState.set(s));
    access.readingWith("state", () -> schemaState.get());
  }

  @After
  public void tearDown() {
    world.terminate();
  }

  @Test
  public void testThatSchemaDefinedIsEquals() {
    schema.defineWith(Category.Event, Scope.Public,"name", "description")
            .andThenConsume(s -> access.writeUsing("state", s));
    final SchemaState state = access.readFrom("state");

    Assert.assertEquals(schemaId.value, state.schemaId.value);
    Assert.assertEquals(Category.Event.name(), state.category.name());
    Assert.assertEquals("name", state.name);
    Assert.assertEquals("description", state.description);
  }

  @Test
  public void testThatSchemaIsDescribed() {
    final SchemaState state = schema.describeAs("new description").await();
    Assert.assertEquals("new description", state.description);
  }

  @Test
  public void testThatSchemaRecategorised() {
    final SchemaState state = schema.categorizeAs(Category.Document).await();
    Assert.assertEquals(Category.Document.name(), state.category.name());
  }

  @Test
  public void testThatSchemaRenamed() {
    final SchemaState state = schema.renameTo("new name").await();
    Assert.assertEquals("new name", state.name);
  }
}
