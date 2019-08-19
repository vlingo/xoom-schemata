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

import io.vlingo.actors.World;
import io.vlingo.lattice.model.object.ObjectTypeRegistry;
import io.vlingo.lattice.model.object.ObjectTypeRegistry.Info;
import io.vlingo.schemata.NoopDispatcher;
import io.vlingo.schemata.model.Id.ContextId;
import io.vlingo.schemata.model.Id.OrganizationId;
import io.vlingo.schemata.model.Id.SchemaId;
import io.vlingo.schemata.model.Id.SchemaVersionId;
import io.vlingo.schemata.model.Id.UnitId;
import io.vlingo.schemata.model.SchemaVersion.Specification;
import io.vlingo.schemata.model.SchemaVersion.Version;
import io.vlingo.symbio.store.object.MapQueryExpression;
import io.vlingo.symbio.store.object.ObjectStore;
import io.vlingo.symbio.store.object.StateObjectMapper;
import io.vlingo.symbio.store.object.inmemory.InMemoryObjectStoreActor;

public class SchemaVersionEntityTest {
  private ObjectTypeRegistry registry;
  private SchemaVersion schemaVersion;
  private SchemaVersionId schemaVersionId;
  private ObjectStore objectStore;
  private World world;

  @Before
  @SuppressWarnings({ "unchecked", "rawtypes" })
  public void setUp() {
    world = World.start("schema-version-test");

    objectStore = world.actorFor(ObjectStore.class, InMemoryObjectStoreActor.class, new NoopDispatcher());

    registry = new ObjectTypeRegistry(world);

    final Info<SchemaVersion> schemaVersionInfo =
            new Info(
                    objectStore,
                    SchemaVersionState.class,
                    "HR-Database",
                    MapQueryExpression.using(SchemaVersion.class, "find", MapQueryExpression.map("id", "id")),
                    StateObjectMapper.with(SchemaVersion.class, new Object(), new Object()));

    registry.register(schemaVersionInfo);

    schemaVersionId = SchemaVersionId.uniqueFor(SchemaId.uniqueFor(ContextId.uniqueFor(UnitId.uniqueFor(OrganizationId.unique()))));
    schemaVersion = world.actorFor(SchemaVersion.class, SchemaVersionEntity.class, schemaVersionId);
  }

  @After
  public void tearDown() {
    world.terminate();
  }

  @Test
  public void testThatSchemaVersionIsDefined() {
    final SchemaVersionState state = schemaVersion.defineWith("description", new SchemaVersion.Specification("specification"), new SchemaVersion.Version("1.0.0")).await();
    Assert.assertEquals(SchemaVersionState.unidentified(), state.persistenceId());
    Assert.assertEquals(schemaVersionId.value, state.schemaVersionId.value);
    Assert.assertEquals("description", state.description);
    Assert.assertEquals("specification", state.specification.value);
    Assert.assertEquals(SchemaVersion.Status.Draft.name(), state.status.name());
    Assert.assertEquals("1.0.0", state.versionState.value);
  }

  @Test
  public void testThatSchemaVersionIsSpecified() {
    final SchemaVersionState state = schemaVersion.specifyWith(Specification.of("new specification")).await();
    Assert.assertEquals("new specification", state.specification.value);
  }

  @Test
  public void testThatSchemaVersionIsDescribed() {
    final SchemaVersionState state = schemaVersion.describeAs("new description").await();
    Assert.assertEquals("new description", state.description);
  }

  @Test
  public void testThatSchemaVersionPublishes() {
    final SchemaVersionState state = schemaVersion.publish().await();
    Assert.assertEquals(SchemaVersion.Status.Published, state.status);
  }

  @Test
  public void testThatSchemaVersionRemoves() {
    final SchemaVersionState state = schemaVersion.remove().await();
    Assert.assertEquals(SchemaVersion.Status.Removed, state.status);
  }

  @Test
  public void testThatSchemaVersionAssignedVersion() {
    final SchemaVersionState state = schemaVersion.assignVersionOf(Version.of("version-1")).await();
    Assert.assertEquals("version-1", state.versionState.value);
  }
}
