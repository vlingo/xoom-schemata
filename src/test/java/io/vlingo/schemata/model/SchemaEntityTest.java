// Copyright © 2012-2018 Vaughn Vernon. All rights reserved.
//
// This Source Code Form is subject to the terms of the
// Mozilla Public License, v. 2.0. If a copy of the MPL
// was not distributed with this file, You can obtain
// one at https://mozilla.org/MPL/2.0/.

package io.vlingo.schemata.model;

import static org.junit.Assert.assertTrue;

import io.vlingo.schemata.NoopDispatcher;
import io.vlingo.symbio.store.object.MapQueryExpression;
import io.vlingo.symbio.store.object.ObjectStore;
import io.vlingo.symbio.store.object.PersistentObjectMapper;
import io.vlingo.symbio.store.object.inmemory.InMemoryObjectStoreActor;
import io.vlingo.actors.World;
import io.vlingo.lattice.model.object.ObjectTypeRegistry;
import io.vlingo.lattice.model.object.ObjectTypeRegistry.Info;
import io.vlingo.schemata.model.Id.ContextId;
import io.vlingo.schemata.model.Id.OrganizationId;
import io.vlingo.schemata.model.Id.SchemaId;
import io.vlingo.schemata.model.Id.UnitId;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class SchemaEntityTest {
  private ObjectTypeRegistry registry;
  private Schema schema;
  private SchemaId schemaId;
  private ObjectStore objectStore;
  private World world;

  @Before
  @SuppressWarnings("unchecked")
  public void setUp() {
    world = World.start("schema-test");

    objectStore = world.actorFor(ObjectStore.class, InMemoryObjectStoreActor.class, new NoopDispatcher());

    registry = new ObjectTypeRegistry(world);

    final Info<Schema> schemaInfo =
            new Info(
                  objectStore,
                  SchemaState.class,
                  "HR-Database",
                  MapQueryExpression.using(Schema.class, "find", MapQueryExpression.map("id", "id")),
                  PersistentObjectMapper.with(Schema.class, new Object(), new Object()));

    registry.register(schemaInfo);

    schemaId = SchemaId.uniqueFor(ContextId.uniqueFor(UnitId.uniqueFor(OrganizationId.unique())));
    schema = world.actorFor(Schema.class, SchemaEntity.class, schemaId);
  }

  @After
  public void tearDown() {
    world.terminate();
  }

  @Test
  public void testThatSchemaDefinedIsEquals() {
    final SchemaState state = schema.defineWith(Category.Event,"name", "description").await();
    assertTrue(state.persistenceId() > 0);
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
    final SchemaState state = schema.recategorizedAs(Category.Document).await();
    Assert.assertEquals(Category.Document.name(), state.category.name());
  }

  @Test
  public void testThatSchemaRenamed() {
    final SchemaState state = schema.renameTo("new name").await();
    Assert.assertEquals("new name", state.name);
  }
}
