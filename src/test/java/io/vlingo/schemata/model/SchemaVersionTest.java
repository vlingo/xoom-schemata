// Copyright © 2012-2018 Vaughn Vernon. All rights reserved.
//
// This Source Code Form is subject to the terms of the
// Mozilla Public License, v. 2.0. If a copy of the MPL
// was not distributed with this file, You can obtain
// one at https://mozilla.org/MPL/2.0/.

package io.vlingo.schemata.model;

import io.vlingo.actors.World;
import io.vlingo.lattice.model.object.ObjectTypeRegistry;
import io.vlingo.lattice.model.object.ObjectTypeRegistry.Info;
import io.vlingo.schemata.NoopDispatcher;
import io.vlingo.schemata.model.Id.*;
import io.vlingo.schemata.model.SchemaVersion.Specification;
import io.vlingo.symbio.store.object.MapQueryExpression;
import io.vlingo.symbio.store.object.ObjectStore;
import io.vlingo.symbio.store.object.StateObjectMapper;
import io.vlingo.symbio.store.object.inmemory.InMemoryObjectStoreActor;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static junit.framework.TestCase.assertTrue;
import static org.junit.Assert.assertFalse;

public class SchemaVersionTest {
  private ObjectTypeRegistry registry;
  private SchemaVersion schemaVersion;
  private SchemaVersionId schemaVersionId;
  private ObjectStore objectStore;
  private World world;

  @Before
  @SuppressWarnings({"unchecked", "rawtypes"})
  public void setUp() {
    world = World.start("schema-version-test");

    objectStore = world.actorFor(ObjectStore.class, InMemoryObjectStoreActor.class, new NoopDispatcher());

    registry = new ObjectTypeRegistry(world);

    final Info<SchemaVersion> schemaVersionInfo =
        new Info(
            objectStore,
            SchemaVersionState.class,
            "schema-version-test-store",
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
  public void schemaVersionWithAddedAttributeIsCompatible() {
    final SchemaVersionState firstVersion = defaultTestSchemaVersionState();

    final SchemaVersionState secondVersion = firstVersion.withSpecification(
        new Specification("event Foo { " +
            "string bar\n" +
            "string baz\n" +
            "}"));

    assertTrue("Versions with only added attributes must be compatible", schemaVersion.isCompatibleWith(secondVersion.specification).await());
  }

  @Test
  public void schemaVersionWithRemovedAttributeIsNotCompatible() {
    final SchemaVersionState firstVersion = defaultTestSchemaVersionState();

    final SchemaVersionState secondVersion = firstVersion.withSpecification(
        new Specification("event Foo { " +
            "string baz\n" +
            "}"));

    assertFalse("Versions with only removed attributes must not be compatible", schemaVersion.isCompatibleWith(secondVersion.specification).await());
  }

  @Test
  public void schemaVersionWithAddedAndRemovedAttributesAreNotCompatible() {
    final SchemaVersionState firstVersion = defaultTestSchemaVersionState();

    final SchemaVersionState secondVersion = firstVersion.withSpecification(
        new Specification("event Foo { " +
            "string baz\n" +
            "}"));

    assertFalse("Versions with added and removed attributes must not be compatible", schemaVersion.isCompatibleWith(secondVersion.specification).await());
  }

  @Test
  public void schemaVersionWithTypeChangesAreNotCompatible() {
    final SchemaVersionState firstVersion = defaultTestSchemaVersionState();

    final SchemaVersionState secondVersion = firstVersion.withSpecification(
        new Specification("event Foo { " +
            "int bar\n" +
            "}"));

    assertFalse("Versions with reordered attributes must not be compatible", schemaVersion.isCompatibleWith(secondVersion.specification).await());
  }

  @Test
  public void schemaVersionWithNameClashesAreNotCompatible() {
    final SchemaVersionState firstVersion = defaultTestSchemaVersionState();

    final SchemaVersionState secondVersion = firstVersion.withSpecification(
        new Specification("event Foo { " +
            "string bar\n" +
            "int bar\n" +
            "}"));

    assertFalse("Versions with name clashing attributes must not be compatible", schemaVersion.isCompatibleWith(secondVersion.specification).await());
  }

  @Test
  public void schemaVersionWithReorderedAttributesAreNotCompatible() {
    final SchemaVersionState firstVersion = defaultTestSchemaVersionState();

    final SchemaVersionState secondVersion = firstVersion.withSpecification(
        new Specification("event Foo { " +
            "string baz\n" +
            "string bar\n" +
            "}"));

    assertFalse("Versions with added and removed attributes must not be compatible", schemaVersion.isCompatibleWith(secondVersion.specification).await());
  }

  private SchemaVersionState defaultTestSchemaVersionState() {
    return schemaVersion.defineWith(
          new Specification("event Foo { " +
              "string bar\n" +
              "}"),
          "description",
          new SchemaVersion.Version("0.0.0"),
          new SchemaVersion.Version("1.0.0"))
          .await();
  }


}
