// Copyright © 2012-2018 Vaughn Vernon. All rights reserved.
//
// This Source Code Form is subject to the terms of the
// Mozilla Public License, v. 2.0. If a copy of the MPL
// was not distributed with this file, You can obtain
// one at https://mozilla.org/MPL/2.0/.

package io.vlingo.schemata.model;

import io.vlingo.actors.Stage;
import io.vlingo.actors.World;
import io.vlingo.actors.testkit.TestWorld;
import io.vlingo.lattice.model.object.ObjectTypeRegistry;
import io.vlingo.schemata.NoopDispatcher;
import io.vlingo.schemata.SchemataConfig;
import io.vlingo.schemata.codegen.TypeDefinitionCompilerActor;
import io.vlingo.schemata.codegen.TypeDefinitionMiddleware;
import io.vlingo.schemata.codegen.backend.Backend;
import io.vlingo.schemata.codegen.backend.java.JavaBackend;
import io.vlingo.schemata.codegen.parser.AntlrTypeParser;
import io.vlingo.schemata.codegen.parser.TypeParser;
import io.vlingo.schemata.codegen.processor.Processor;
import io.vlingo.schemata.codegen.processor.types.CacheTypeResolver;
import io.vlingo.schemata.codegen.processor.types.ComputableTypeProcessor;
import io.vlingo.schemata.codegen.processor.types.TypeResolver;
import io.vlingo.schemata.codegen.processor.types.TypeResolverProcessor;
import io.vlingo.schemata.infra.persistence.SchemataObjectStore;
import io.vlingo.schemata.model.Id.*;
import io.vlingo.schemata.model.SchemaVersion.Specification;
import io.vlingo.symbio.store.dispatch.Dispatcher;
import io.vlingo.symbio.store.object.ObjectStore;
import org.junit.Before;
import org.junit.Test;

import java.util.Arrays;

import static junit.framework.TestCase.assertTrue;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.fail;

public class SchemaVersionTest {
  private ObjectTypeRegistry registry;
  private SchemaVersion schemaVersion;
  private SchemaVersionId schemaVersionId;
  private ObjectStore objectStore;
  private TypeDefinitionMiddleware typeDefinitionMiddleware;
  private World world;
  private Stage stage;
  private SchemaVersionState firstVersion;

  @Before
  @SuppressWarnings({"unchecked", "rawtypes"})
  public void setUp() throws Exception {
    World world = TestWorld.startWithDefaults(getClass().getSimpleName()).world();
    TypeParser typeParser = world.actorFor(TypeParser.class, AntlrTypeParser.class);
    Dispatcher dispatcher = new NoopDispatcher();

    final SchemataObjectStore schemataObjectStore = SchemataObjectStore.instance(SchemataConfig.forRuntime("test"));

    ObjectStore objectStore = schemataObjectStore.objectStoreFor(world, dispatcher, schemataObjectStore.persistentMappers());
    final ObjectTypeRegistry registry = new ObjectTypeRegistry(world);
    schemataObjectStore.register(registry, objectStore);

    TypeResolver typeResolver = new CacheTypeResolver();

    typeDefinitionMiddleware = world.actorFor(TypeDefinitionMiddleware.class, TypeDefinitionCompilerActor.class,
        typeParser,
        Arrays.asList(
            world.actorFor(Processor.class, ComputableTypeProcessor.class),
            world.actorFor(Processor.class, TypeResolverProcessor.class, typeResolver)
        ),
        world.actorFor(Backend.class, JavaBackend.class)
    );

    schemaVersionId = SchemaVersionId.uniqueFor(SchemaId.uniqueFor(ContextId.uniqueFor(UnitId.uniqueFor(OrganizationId.unique()))));
    schemaVersion = world.actorFor(SchemaVersion.class, SchemaVersionEntity.class, schemaVersionId);
    firstVersion = defaultTestSchemaVersionState();
  }

  @Test
  public void equalSpecificationsAreCompatible() {
    final SchemaVersionState firstVersion = defaultTestSchemaVersionState();

    final SchemaVersionState secondVersion = firstVersion.withSpecification(
        new Specification(firstVersion.specification.value));

    assertCompatible("Versions with only added attributes must be compatible",
        schemaVersion.isCompatibleWith(typeDefinitionMiddleware, secondVersion.specification).await());
  }

  @Test
  public void schemaVersionWithAddedAttributeIsCompatible() {
    final SchemaVersionState secondVersion = firstVersion.withSpecification(
        new Specification("event Foo { " +
            "string bar\n" +
            "string baz\n" +
            "}"));

    assertCompatible("Versions with only added attributes must be compatible",
        schemaVersion.isCompatibleWith(typeDefinitionMiddleware, secondVersion.specification).await());
  }

  @Test
  public void schemaVersionWithRemovedAttributeIsNotCompatible() {
    final SchemaVersionState secondVersion = firstVersion.withSpecification(
        new Specification("event Foo { " +
            "string baz\n" +
            "}"));

    assertIncompatible("Versions with only removed attributes must not be compatible",
        schemaVersion.isCompatibleWith(typeDefinitionMiddleware, secondVersion.specification).await());
  }

  @Test
  public void schemaVersionWithAddedAndRemovedAttributesAreNotCompatible() {
    final SchemaVersionState secondVersion = firstVersion.withSpecification(
        new Specification("event Foo { " +
            "string baz\n" +
            "}"));

    assertIncompatible("Versions with added and removed attributes must not be compatible",
        schemaVersion.isCompatibleWith(typeDefinitionMiddleware, secondVersion.specification).await());
  }

  @Test
  public void schemaVersionWithTypeChangesAreNotCompatible() {
    final SchemaVersionState secondVersion = firstVersion.withSpecification(
        new Specification("event Foo { " +
            "int bar\n" +
            "}"));

    assertIncompatible("Versions with reordered attributes must not be compatible",
        schemaVersion.isCompatibleWith(typeDefinitionMiddleware, secondVersion.specification).await());
  }

  @Test
  public void schemaVersionWithReorderedAttributesAreNotCompatible() {
    final SchemaVersionState secondVersion = firstVersion.withSpecification(
        new Specification("event Foo { " +
            "string baz\n" +
            "string bar\n" +
            "}"));

    assertIncompatible("Versions with added and removed attributes must not be compatible",
        schemaVersion.isCompatibleWith(typeDefinitionMiddleware, secondVersion.specification).await());
  }

  @Test(expected = IllegalArgumentException.class)
  public void nullSpecificationThrows() {
    final SchemaVersionState secondVersion = firstVersion.withSpecification(null);
    schemaVersion.isCompatibleWith(typeDefinitionMiddleware, secondVersion.specification).await();

    fail("Trying to diff a null specification should have thrown an exception");
  }

  @Test(expected = IllegalArgumentException.class)
  public void emptySpecificationThrows() {
    final SchemaVersionState secondVersion = firstVersion.withSpecification(null);
    schemaVersion.isCompatibleWith(typeDefinitionMiddleware, secondVersion.specification).await();

    fail("Trying to diff an empty specification should have thrown an exception");
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

  private static void assertCompatible(String message, SpecificationDiff diff) {
    assertTrue(message, diff.isCompatible);
  }

  private static void assertIncompatible(String message, SpecificationDiff diff) {
    assertFalse(message, diff.isCompatible);
  }


}
