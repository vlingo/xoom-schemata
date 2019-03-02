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

import io.vlingo.actors.testkit.TestActor;
import io.vlingo.actors.testkit.TestWorld;
import io.vlingo.lattice.model.DomainEvent;
import io.vlingo.lattice.model.sourcing.SourcedTypeRegistry;
import io.vlingo.schemata.NoopJournalListener;
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
import io.vlingo.symbio.store.journal.Journal;
import io.vlingo.symbio.store.journal.inmemory.InMemoryJournalActor;

public class SchemaVersionEntityTest {
  private TestWorld world;
  private TestActor<SchemaVersion> schemaVersion;
  private Journal<String> journal;
  private NoopJournalListener listener;
  private SourcedTypeRegistry registry;

  @Before
  @SuppressWarnings("unchecked")
  public void setUp() throws Exception {
    world = TestWorld.start("schema-version-test");

    listener = new NoopJournalListener();

    journal = world.world().actorFor(Journal.class, InMemoryJournalActor.class, listener);

    registry = new SourcedTypeRegistry(world.world());

    EntryAdapters.register(registry, journal);

    schemaVersion = world.actorFor(
            SchemaVersion.class,
            SchemaVersionEntity.class,
            SchemaVersionId.uniqueFor(SchemaId.uniqueFor(ContextId.uniqueFor(UnitId.uniqueFor(OrganizationId.unique())))));
    schemaVersion.context().until.resetHappeningsTo(1);
    schemaVersion.actor().defineWith("description", new SchemaVersion.Specification("specification"), new SchemaVersion.Version("1.0.0"));
  }

  @After
  public void tearDown() {
    world.terminate();
  }

  @Test
  public void testThatSchemaVersionIsDefined() throws Exception {
    schemaVersion.context().until.completes(); // see setUp()
    final List<DomainEvent> applied = schemaVersion.viewTestState().valueOf("applied");
    final SchemaVersionDefined schemaVersionDefined = (SchemaVersionDefined) applied.get(0);
    Assert.assertEquals("description", schemaVersionDefined.description);
    Assert.assertEquals("specification", schemaVersionDefined.specification);
    Assert.assertEquals("1.0.0", schemaVersionDefined.version);
  }

  @Test
  public void testThatSchemaVersionIsSpecified() throws Exception {
    schemaVersion.context().until.completes(); // see setUp()
    schemaVersion.context().until.resetHappeningsTo(1);
    schemaVersion.actor().specifyWith(new SchemaVersion.Specification("new specification"));
    schemaVersion.context().until.completes();
    final List<DomainEvent> applied = schemaVersion.viewTestState().valueOf("applied");
    final SchemaVersionSpecified schemaVersionSpecified = (SchemaVersionSpecified) applied.get(1);
    Assert.assertEquals("new specification", schemaVersionSpecified.specification);
  }

  @Test
  public void testThatSchemaVersionIsDescribed() throws Exception {
    schemaVersion.context().until.completes(); // see setUp()
    schemaVersion.context().until.resetHappeningsTo(1);
    schemaVersion.actor().describeAs("new description");
    schemaVersion.context().until.completes();
    final List<DomainEvent> applied = schemaVersion.viewTestState().valueOf("applied");
    final SchemaVersionDescribed schemaVersionDescribed = (SchemaVersionDescribed) applied.get(1);
    Assert.assertEquals("new description", schemaVersionDescribed.description);
  }

  @Test
  public void testThatSchemaVersionPublishes() throws Exception {
    schemaVersion.context().until.completes(); // see setUp()
    schemaVersion.context().until.resetHappeningsTo(1);
    schemaVersion.actor().publish();
    schemaVersion.context().until.completes();
    final List<DomainEvent> applied = schemaVersion.viewTestState().valueOf("applied");
    final SchemaVersionPublished schemaVersionPublished = (SchemaVersionPublished) applied.get(1);
    Assert.assertNotNull(schemaVersionPublished);
  }

  @Test
  public void testThatSchemaVersionRemoves() throws Exception {
    schemaVersion.context().until.completes(); // see setUp()
    schemaVersion.context().until.resetHappeningsTo(1);
    schemaVersion.actor().remove();
    schemaVersion.context().until.completes();
    final List<DomainEvent> applied = schemaVersion.viewTestState().valueOf("applied");
    final SchemaVersionRemoved schemaVersionRemoved = (SchemaVersionRemoved) applied.get(1);
    Assert.assertNotNull(schemaVersionRemoved);
  }

  @Test
  public void testThatSchemaVersionAssignedVersion() throws Exception {
    schemaVersion.context().until.completes(); // see setUp()
    schemaVersion.context().until.resetHappeningsTo(1);
    schemaVersion.actor().assignVersionOf(new SchemaVersion.Version("version-1"));
    schemaVersion.context().until.completes();
    final List<DomainEvent> applied = schemaVersion.viewTestState().valueOf("applied");
    final SchemaVersionAssignedVersion schemaVersionAssignedVersion = (SchemaVersionAssignedVersion) applied.get(1);
    Assert.assertEquals("version-1", schemaVersionAssignedVersion.version);
  }
}
