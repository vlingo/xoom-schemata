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
import io.vlingo.common.Completes;
import io.vlingo.common.Outcome;
import io.vlingo.lattice.model.sourcing.SourcedTypeRegistry;
import io.vlingo.lattice.model.sourcing.SourcedTypeRegistry.Info;
import io.vlingo.schemata.NoopDispatcher;
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
import io.vlingo.schemata.errors.SchemataBusinessException;
import io.vlingo.schemata.model.Id.*;
import io.vlingo.schemata.model.SchemaVersion.Specification;
import io.vlingo.schemata.resource.data.SchemaVersionData;
import io.vlingo.symbio.store.journal.Journal;
import io.vlingo.symbio.store.journal.inmemory.InMemoryJournalActor;
import io.vlingo.symbio.store.object.ObjectStore;
import org.junit.Before;
import org.junit.Test;

import java.util.Arrays;
import java.util.List;

import static io.vlingo.schemata.LambdaMatcher.matches;
import static java.util.stream.Collectors.toList;
import static junit.framework.TestCase.assertTrue;
import static org.hamcrest.Matchers.*;
import static org.junit.Assert.*;

public class SchemaVersionTest {
  protected Journal<String> journal;
  protected SourcedTypeRegistry registry;
  private SchemaVersion simpleSchemaVersion;
  private SchemaVersionId simpleSchemaVersionId;
  private SchemaVersionState simpleVersion;
  private SchemaVersion basicTypesSchemaVersion;
  private SchemaVersionId basicTypesSchemaVersionId;
  private SchemaVersionState basicTypesVersion;
  private TypeDefinitionMiddleware typeDefinitionMiddleware;
  private World world;
  private Stage stage;

  @Before
  @SuppressWarnings({"unchecked", "rawtypes"})
  public void setUp() throws Exception {
    World world = TestWorld.startWithDefaults(getClass().getSimpleName()).world();
    TypeParser typeParser = new AntlrTypeParser();

    journal = world.actorFor(Journal.class, InMemoryJournalActor.class, new NoopDispatcher());

    registry = new SourcedTypeRegistry(world);
    registry.register(new Info(journal, SchemaVersionEntity.class, SchemaVersionEntity.class.getSimpleName()));

    TypeResolver typeResolver = new CacheTypeResolver();

    typeDefinitionMiddleware = new TypeDefinitionCompilerActor(
            typeParser,
            Arrays.asList(
                    world.actorFor(Processor.class, ComputableTypeProcessor.class),
                    world.actorFor(Processor.class, TypeResolverProcessor.class, typeResolver)),
            new JavaBackend());

    simpleSchemaVersionId = SchemaVersionId.uniqueFor(SchemaId.uniqueFor(ContextId.uniqueFor(UnitId.uniqueFor(OrganizationId.unique()))));
    simpleSchemaVersion = world.actorFor(SchemaVersion.class, SchemaVersionEntity.class, simpleSchemaVersionId);
    simpleVersion = simpleSchemaVersionState();

    basicTypesSchemaVersionId = SchemaVersionId.uniqueFor(SchemaId.uniqueFor(ContextId.uniqueFor(UnitId.uniqueFor(OrganizationId.unique()))));
    basicTypesSchemaVersion = world.actorFor(SchemaVersion.class, SchemaVersionEntity.class, basicTypesSchemaVersionId);
    basicTypesVersion = completeBasicTypesSchemaVersionState();
  }

  @Test
  public void equalSpecificationsAreCompatible() {
    final SchemaVersionState firstVersion = simpleSchemaVersionState();

    final SchemaVersionData secondVersion = SchemaVersionData.from(firstVersion.withSpecification(
        new Specification(firstVersion.specification.value)));

    assertCompatible("Versions with only added attributes must be compatible",
            unwrap(simpleSchemaVersion.diff(typeDefinitionMiddleware, secondVersion)));
  }

  @Test
  public void schemaVersionWithAddedAttributeIsCompatible() {
    final SchemaVersionData secondVersion = SchemaVersionData.from(simpleVersion.withSpecification(
        new Specification("event Foo { " +
            "string bar\n" +
            "string baz\n" +
            "}")));

    assertCompatible("Versions with only added attributes must be compatible",
            unwrap(simpleSchemaVersion.diff(typeDefinitionMiddleware, secondVersion)));
  }

  @Test
  public void schemaVersionWithRemovedAttributeIsNotCompatible() {
    final SchemaVersionData secondVersion = SchemaVersionData.from(simpleVersion.withSpecification(
        new Specification("event Foo { " +
            "string baz\n" +
            "}")));

    assertIncompatible("Versions with only removed attributes must not be compatible",
            unwrap(simpleSchemaVersion.diff(typeDefinitionMiddleware, secondVersion)));
  }

  @Test
  public void schemaVersionWithAddedAndRemovedAttributesAreNotCompatible() {
    final SchemaVersionData secondVersion = SchemaVersionData.from(simpleVersion.withSpecification(
        new Specification("event Foo { " +
            "string baz\n" +
            "}")));

    assertIncompatible("Versions with added and removed attributes must not be compatible",
            unwrap(simpleSchemaVersion.diff(typeDefinitionMiddleware, secondVersion)));
  }

  @Test
  public void schemaVersionWithTypeChangesAreNotCompatible() {
    final SchemaVersionData secondVersion = SchemaVersionData.from(simpleVersion.withSpecification(
        new Specification("event Foo { " +
            "int bar\n" +
            "}")));

    assertIncompatible("Versions with type changes must not be compatible",
            unwrap(simpleSchemaVersion.diff(typeDefinitionMiddleware, secondVersion)));
  }

  @Test
  public void schemaVersionWithReorderedAttributesAreNotCompatible() {
    final SchemaVersionData secondVersion = SchemaVersionData.from(basicTypesVersion.withSpecification(
        new Specification("event Foo { " +
          "string stringAttribute\n"+
          "short shortAttribute\n"+
          "long longAttribute\n"+
          "byte byteAttribute\n"+
          "int intAttribute\n"+
          "double doubleAttribute\n"+
          "char charAttribute\n"+
          "float floatAttribute\n"+
          "boolean booleanAttribute\n"+
          "version eventVersion\n"+
          "timestamp occurredOn\n"+
          "type eventType\n"+
            "}")));

    assertIncompatible("Versions with reordered attributes must not be compatible",
            unwrap(basicTypesSchemaVersion.diff(typeDefinitionMiddleware, secondVersion)));
  }

  @Test public void identicalSpecificationsHaveNoDiff() {
    SchemaVersionData dst = SchemaVersionData.just("event Foo { string bar }",null,null,null, null);

    SpecificationDiff diff = unwrap(simpleSchemaVersion.diff(typeDefinitionMiddleware,dst));
    assertThat(diff.changes().size(),is(0));
  }

  @Test public void removalIsTrackedInDiff() {
    SchemaVersionData dst = SchemaVersionData.just("event Foo { }",null,null,null, null);

    SpecificationDiff diff = unwrap(simpleSchemaVersion.diff(typeDefinitionMiddleware, dst));
    assertThat(diff.changes().size(),is(1));
    assertThat(diff.changes().get(0).subject, is("bar"));
    assertThat(diff.changes().get(0).type, is(Change.Type.REMOVE_FIELD));
  }

  @Test public void multipleRemovalsAreTrackedInDiff() {
    SchemaVersionData dst = SchemaVersionData.just("event Foo { \n" +
      "type eventType\n"+
      "version eventVersion\n"+
      "byte byteAttribute\n"+
      "double doubleAttribute\n"+
      "int intAttribute\n"+
      "short shortAttribute\n"+
      "}",null,null,null, null);

    SpecificationDiff diff = unwrap(basicTypesSchemaVersion.diff(typeDefinitionMiddleware, dst));
    assertThat(diff.changes().size(),is(11));
    assertThat(diff.changes().stream().filter(c -> c.type == Change.Type.REMOVE_FIELD).count(), is(6L));
    assertThat(diff.changes().stream().filter(c -> c.type == Change.Type.MOVE_FIELD).count(), is(5L));
  }

  @Test public void additionIsTrackedInDiff() {
    SchemaVersionData dst = SchemaVersionData.just("event Foo { string bar\nstring baz\nstring qux }",null,null,null, null);

    SpecificationDiff diff = unwrap(simpleSchemaVersion.diff(typeDefinitionMiddleware,dst));
    assertThat(diff.changes().size(),is(2));
    assertThat(diff.changes(), everyItem(matches(c -> c.type == Change.Type.ADD_FIELD, "All changes must be additions")));
    assertThat(diff.changes(), hasItems(
      Change.additionOfField("baz"),
      Change.additionOfField("qux")
    ));
  }

  @Test public void renamingIsTrackedInDiff() {
    SchemaVersionData dst = SchemaVersionData.just("event Foo { string baz }",null,null,null, null);

    SpecificationDiff diff = unwrap(simpleSchemaVersion.diff(typeDefinitionMiddleware,dst));
    assertThat(diff.changes().size(),is(2));
    assertThat(diff.changes().stream().filter(c -> c.type == Change.Type.ADD_FIELD).count(), is(1L));
    assertThat(diff.changes().stream().filter(c -> c.type == Change.Type.REMOVE_FIELD).count(), is(1L));
  }

  @Test public void typeChangeIsTrackedInDiff() {
    SchemaVersionData dst = SchemaVersionData.just("event Bar { string bar }",null,null,null, null);

    SpecificationDiff diff = unwrap(simpleSchemaVersion.diff(typeDefinitionMiddleware,dst));
    assertThat(diff.changes().size(),is(1));
    assertThat(diff.changes().get(0).subject, is("Foo"));
    assertThat(diff.changes().get(0).type, is(Change.Type.CHANGE_TYPE));
    assertThat(diff.changes().get(0).oldValue, is("Foo"));
    assertThat(diff.changes().get(0).newValue, is("Bar"));
  }

  @Test public void fieldTypeChangeIsTrackedInDiff() {
    SchemaVersionData dst = SchemaVersionData.just("event Foo { int bar }",null,null,null, null);

    SpecificationDiff diff = unwrap(simpleSchemaVersion.diff(typeDefinitionMiddleware,dst));
    assertThat(diff.changes().size(),is(1));
    assertThat(diff.changes().get(0).subject, is("bar"));
    assertThat(diff.changes().get(0).type, is(Change.Type.CHANGE_FIELD_TYPE));
  }

  @Test public void mixedChangesArtTrackedInDiff() {
    SchemaVersionData dst = SchemaVersionData.just("event Bar { " +
      "timestamp at\n"+
      "string newStringAttribute\n" +
      "version eventVersion\n"+
      "int charAttribute\n"+
      "int intAttribute\n"+
      "long longAttribute\n"+
      "short shortAttribute\n"+
      "string stringAttribute\n"+
      "}",null,null,null, null);

    SpecificationDiff diff = unwrap(basicTypesSchemaVersion.diff(typeDefinitionMiddleware, dst));
    assertThat(diff.changes().size(),is(14));

    List<Change> typeChanges = diff.changes().stream().filter(c -> c.type == Change.Type.CHANGE_TYPE).collect(toList());
    List<Change> additions = diff.changes().stream().filter(c -> c.type == Change.Type.ADD_FIELD).collect(toList());
    List<Change> removals = diff.changes().stream().filter(c -> c.type == Change.Type.REMOVE_FIELD).collect(toList());
    List<Change> changes = diff.changes().stream().filter(c -> c.type == Change.Type.CHANGE_FIELD_TYPE).collect(toList());
    List<Change> moves = diff.changes().stream().filter(c -> c.type == Change.Type.MOVE_FIELD).collect(toList());

    assertEquals(typeChanges.size(), 1);
    assertThat(typeChanges, hasItem(Change.ofType("Foo", "Bar")));


    assertEquals(removals.size(), 6);
    assertThat(removals, hasItems(
      Change.removalOfField("eventType"),
      Change.removalOfField("occurredOn"),
      Change.removalOfField("booleanAttribute"),
      Change.removalOfField("byteAttribute"),
      Change.removalOfField("doubleAttribute"),
      Change.removalOfField("floatAttribute")));


    assertEquals(changes.size(), 1);
    assertThat(changes, hasItems(
      Change.ofFieldType("charAttribute", "char","int")));

    assertEquals(additions.size(), 2);
    assertThat(additions, hasItems(
      Change.additionOfField("newStringAttribute"),
      Change.additionOfField("at")));

    assertEquals(moves.size(), 4);
    assertThat(moves, hasItems(
      Change.moveOf("intAttribute"),
      Change.moveOf("longAttribute"),
      Change.moveOf("shortAttribute"),
      Change.moveOf("stringAttribute")));
  }


  private SchemaVersionState simpleSchemaVersionState() {
    return simpleSchemaVersion.defineWith(
            new Specification("event Foo { " +
                    "string bar\n" +
                    "}"),
            "description",
            new SchemaVersion.Version("0.0.0"),
            new SchemaVersion.Version("1.0.0"))
            .await();
  }

  private SchemaVersionState completeBasicTypesSchemaVersionState() {
    return basicTypesSchemaVersion.defineWith(
            new Specification("event Foo { " +
                    "type eventType\n" +
                    "timestamp occurredOn\n" +
                    "version eventVersion\n" +
                    "boolean booleanAttribute\n" +
                    "byte byteAttribute\n" +
                    "char charAttribute\n" +
                    "double doubleAttribute\n" +
                    "float floatAttribute\n" +
                    "int intAttribute\n" +
                    "long longAttribute\n" +
                    "short shortAttribute\n" +
                    "string stringAttribute\n" +
                    "}"),
            "description",
            new SchemaVersion.Version("0.0.0"),
            new SchemaVersion.Version("1.0.0"))
            .await();
  }

  private static void assertCompatible(String message, SpecificationDiff diff) {
    assertTrue(message, diff.isCompatible());
  }

  private static void assertIncompatible(String message, SpecificationDiff diff) {
    assertFalse(message, diff.isCompatible());
  }

  private SpecificationDiff unwrap(Completes<Outcome<SchemataBusinessException, SpecificationDiff>> outcome) throws SchemataBusinessException {
    return ((Outcome<SchemataBusinessException, SpecificationDiff>)outcome.await()).get();
  }
}
