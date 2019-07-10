// Copyright © 2012-2018 Vaughn Vernon. All rights reserved.
//
// This Source Code Form is subject to the terms of the
// Mozilla Public License, v. 2.0. If a copy of the MPL
// was not distributed with this file, You can obtain
// one at https://mozilla.org/MPL/2.0/.

package io.vlingo.schemata.model;

import java.util.List;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import io.vlingo.actors.World;
import io.vlingo.actors.testkit.AccessSafely;
import io.vlingo.lattice.model.DomainEvent;
import io.vlingo.lattice.model.sourcing.SourcedTypeRegistry;
import io.vlingo.schemata.MockJournalDispatcher;
import io.vlingo.schemata.infra.persistence.EntryAdapters;
import io.vlingo.schemata.model.Events.SchemaVersionAssignedVersion;
import io.vlingo.schemata.model.Events.SchemaVersionDefined;
import io.vlingo.schemata.model.Events.SchemaVersionDescribed;
import io.vlingo.schemata.model.Events.SchemaVersionPublished;
import io.vlingo.schemata.model.Events.SchemaVersionRemoved;
import io.vlingo.schemata.model.Events.SchemaVersionSpecified;
import io.vlingo.schemata.model.Id.ContextId;
import io.vlingo.schemata.model.Id.OrganizationId;
import io.vlingo.schemata.model.Id.SchemaId;
import io.vlingo.schemata.model.Id.SchemaVersionId;
import io.vlingo.schemata.model.Id.UnitId;
import io.vlingo.schemata.model.SchemaVersion.Specification;
import io.vlingo.schemata.model.SchemaVersion.Version;
import io.vlingo.symbio.EntryAdapterProvider;
import io.vlingo.symbio.store.journal.Journal;
import io.vlingo.symbio.store.journal.inmemory.InMemoryJournalActor;

public class SchemaVersionEntityTest {
  private AccessSafely access;
  private Journal<String> journal;
  private MockJournalDispatcher listener;
  private SourcedTypeRegistry registry;
  private SchemaVersion schemaVersion;
  private World world;

  @Before
  @SuppressWarnings("unchecked")
  public void setUp() throws Exception {
    world = World.start("schema-version-test");

    listener = new MockJournalDispatcher(EntryAdapterProvider.instance(world));

    journal = world.world().actorFor(Journal.class, InMemoryJournalActor.class, listener);

    registry = new SourcedTypeRegistry(world.world());

    EntryAdapters.register(registry, journal);

    schemaVersion = world.actorFor(
            SchemaVersion.class,
            SchemaVersionEntity.class,
            SchemaVersionId.uniqueFor(SchemaId.uniqueFor(ContextId.uniqueFor(UnitId.uniqueFor(OrganizationId.unique())))));
    access = listener.afterCompleting(1);
    schemaVersion.defineWith("description", new SchemaVersion.Specification("specification"), new SchemaVersion.Version("1.0.0"));
  }

  @After
  public void tearDown() {
    world.terminate();
  }

  @Test
  public void testThatSchemaVersionIsDefined() throws Exception {
    final List<DomainEvent> applied = access.readFrom("entries"); // see setUp()
    final SchemaVersionDefined schemaVersionDefined = (SchemaVersionDefined) applied.get(0);
    Assert.assertEquals("description", schemaVersionDefined.description);
    Assert.assertEquals("specification", schemaVersionDefined.specification);
    Assert.assertEquals("1.0.0", schemaVersionDefined.version);
  }

  @Test
  public void testThatSchemaVersionIsSpecified() throws Exception {
    access.readFrom("entries"); // see setUp
    access = listener.afterCompleting(1);
    schemaVersion.specifyWith(Specification.of("new specification"));
    final List<DomainEvent> applied = access.readFrom("entries");
    final SchemaVersionSpecified schemaVersionSpecified = (SchemaVersionSpecified) applied.get(1);
    Assert.assertEquals("new specification", schemaVersionSpecified.specification);
  }

  @Test
  public void testThatSchemaVersionIsDescribed() throws Exception {
    access.readFrom("entries"); // see setUp
    access = listener.afterCompleting(1);
    schemaVersion.describeAs("new description");
    final List<DomainEvent> applied = access.readFrom("entries");
    final SchemaVersionDescribed schemaVersionDescribed = (SchemaVersionDescribed) applied.get(1);
    Assert.assertEquals("new description", schemaVersionDescribed.description);
  }

  @Test
  public void testThatSchemaVersionPublishes() throws Exception {
    access.readFrom("entries"); // see setUp
    access = listener.afterCompleting(1);
    schemaVersion.publish();
    final List<DomainEvent> applied = access.readFrom("entries");
    final SchemaVersionPublished schemaVersionPublished = (SchemaVersionPublished) applied.get(1);
    Assert.assertNotNull(schemaVersionPublished);
  }

  @Test
  public void testThatSchemaVersionRemoves() throws Exception {
    access.readFrom("entries"); // see setUp
    access = listener.afterCompleting(1);
    schemaVersion.remove();
    final List<DomainEvent> applied = access.readFrom("entries");
    final SchemaVersionRemoved schemaVersionRemoved = (SchemaVersionRemoved) applied.get(1);
    Assert.assertNotNull(schemaVersionRemoved);
  }

  @Test
  public void testThatSchemaVersionAssignedVersion() throws Exception {
    access.readFrom("entries"); // see setUp
    access = listener.afterCompleting(1);
    schemaVersion.assignVersionOf(Version.of("version-1"));
    final List<DomainEvent> applied = access.readFrom("entries");
    final SchemaVersionAssignedVersion schemaVersionAssignedVersion = (SchemaVersionAssignedVersion) applied.get(1);
    Assert.assertEquals("version-1", schemaVersionAssignedVersion.version);
  }
}
