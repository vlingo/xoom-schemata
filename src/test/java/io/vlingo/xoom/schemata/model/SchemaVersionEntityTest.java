// Copyright © 2012-2020 VLINGO LABS. All rights reserved.
//
// This Source Code Form is subject to the terms of the
// Mozilla Public License, v. 2.0. If a copy of the MPL
// was not distributed with this file, You can obtain
// one at https://mozilla.org/MPL/2.0/.

package io.vlingo.xoom.schemata.model;

import io.vlingo.xoom.actors.World;
import io.vlingo.xoom.actors.testkit.AccessSafely;
import io.vlingo.xoom.lattice.model.sourcing.SourcedTypeRegistry;
import io.vlingo.xoom.schemata.NoopDispatcher;
import io.vlingo.xoom.schemata.model.Id.*;
import io.vlingo.xoom.schemata.model.SchemaVersion.Specification;
import io.vlingo.xoom.symbio.store.journal.Journal;
import io.vlingo.xoom.symbio.store.journal.inmemory.InMemoryJournalActor;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.Arrays;
import java.util.concurrent.atomic.AtomicReference;

public class SchemaVersionEntityTest {
  private AccessSafely access;
  private AtomicReference<SchemaVersionState> schemaVersionState;

  private Journal<String> journal;
  private SchemaVersion schemaVersion;
  private SchemaVersionId schemaVersionId;
  private SourcedTypeRegistry registry;
  private World world;

  @Before
  @SuppressWarnings({ "unchecked", "rawtypes" })
  public void setUp() {
    world = World.start("schema-version-test");

    journal = world.actorFor(Journal.class, InMemoryJournalActor.class, Arrays.asList(new NoopDispatcher()));

    registry = new SourcedTypeRegistry(world);
    registry.register(new SourcedTypeRegistry.Info(journal, SchemaVersionEntity.class, SchemaVersionEntity.class.getSimpleName()));

    schemaVersionId = SchemaVersionId.uniqueFor(SchemaId.uniqueFor(ContextId.uniqueFor(UnitId.uniqueFor(OrganizationId.unique()))));
    schemaVersion = world.actorFor(SchemaVersion.class, SchemaVersionEntity.class, schemaVersionId);

    schemaVersionState = new AtomicReference<>();
    access = AccessSafely.afterCompleting(1);
    access.writingWith("state", (SchemaVersionState s) -> schemaVersionState.set(s));
    access.readingWith("state", () -> schemaVersionState.get());
  }

  @After
  public void tearDown() {
    world.terminate();
  }

  @Test
  public void testThatSchemaVersionIsDefined() {
    schemaVersion.defineWith(new SchemaVersion.Specification("specification"), "description", new SchemaVersion.Version("0.0.0"), new SchemaVersion.Version("1.0.0"))
            .andThenConsume(s -> access.writeUsing("state", s));
    final SchemaVersionState state = access.readFrom("state");

    Assert.assertEquals(schemaVersionId.value, state.schemaVersionId.value);
    Assert.assertEquals("description", state.description);
    Assert.assertEquals("specification", state.specification.value);
    Assert.assertEquals(SchemaVersion.Status.Draft.name(), state.status.name());
    Assert.assertEquals("1.0.0", state.currentVersion.value);
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
    schemaVersion.publish();
    schemaVersion.deprecate();
    final SchemaVersionState state = schemaVersion.remove().await();
    Assert.assertEquals(SchemaVersion.Status.Removed, state.status);
  }
}
