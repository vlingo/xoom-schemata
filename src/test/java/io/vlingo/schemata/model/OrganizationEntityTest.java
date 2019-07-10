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
import io.vlingo.schemata.model.Events.OrganizationDefined;
import io.vlingo.schemata.model.Events.OrganizationDescribed;
import io.vlingo.schemata.model.Events.OrganizationRenamed;
import io.vlingo.schemata.model.Id.OrganizationId;
import io.vlingo.symbio.EntryAdapterProvider;
import io.vlingo.symbio.store.journal.Journal;
import io.vlingo.symbio.store.journal.inmemory.InMemoryJournalActor;

public class OrganizationEntityTest {
  private AccessSafely access;
  private Journal<String> journal;
  private MockJournalDispatcher listener;
  private Organization organization;
  private SourcedTypeRegistry registry;
  private World world;

  @Before
  @SuppressWarnings("unchecked")
  public void setUp() throws Exception {
    world = World.start("organization-test");

    listener = new MockJournalDispatcher(EntryAdapterProvider.instance(world));

    journal = world.world().actorFor(Journal.class, InMemoryJournalActor.class, listener);

    registry = new SourcedTypeRegistry(world.world());

    EntryAdapters.register(registry, journal);

    organization = world.actorFor(Organization.class, OrganizationEntity.class, OrganizationId.unique());
    access = listener.afterCompleting(1);
    organization.defineWith("name", "description");
  }

  @After
  public void tearDown() {
    world.terminate();
  }

  @Test
  public void testThatOrganizationDefinedIsEquals() throws Exception {
    final List<DomainEvent> applied = access.readFrom("entries"); // see setUp()
    final OrganizationDefined organizationDefined = (OrganizationDefined) applied.get(0);
    Assert.assertEquals("name", organizationDefined.name);
    Assert.assertEquals("description", organizationDefined.description);
  }

  @Test
  public void testThatOrganizationRenamed() throws Exception {
    access.readFrom("entries"); // see setUp()
    access = listener.afterCompleting(1);
    organization.renameTo("new name");
    final List<DomainEvent> applied = access.readFrom("entries");;
    final OrganizationRenamed organizationRenamed = (OrganizationRenamed) applied.get(1);
    Assert.assertEquals("new name", organizationRenamed.name);
  }

  @Test
  public void testThatOrganizationIsDescribed() throws Exception {
    access.readFrom("entries"); // see setUp()
    access = listener.afterCompleting(1);
    organization.describeAs("new description");
    final List<DomainEvent> applied = access.readFrom("entries");;
    final OrganizationDescribed organizationDescribed = (OrganizationDescribed) applied.get(1);
    Assert.assertEquals("new description", organizationDescribed.description);
  }
}
