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
import io.vlingo.schemata.MockJournalListener;
import io.vlingo.schemata.infra.persistence.EntryAdapters;
import io.vlingo.schemata.model.Events.OrganizationDefined;
import io.vlingo.schemata.model.Events.OrganizationDescribed;
import io.vlingo.schemata.model.Events.OrganizationRenamed;
import io.vlingo.schemata.model.Id.OrganizationId;
import io.vlingo.symbio.store.journal.Journal;
import io.vlingo.symbio.store.journal.inmemory.InMemoryJournalActor;

public class OrganizationEntityTest {
  private TestWorld world;
  private TestActor<Organization> organizationTestActor;
  private Journal<String> journal;
  private MockJournalListener listener;
  private SourcedTypeRegistry registry;

  @Before
  @SuppressWarnings("unchecked")
  public void setUp() throws Exception {
    world = TestWorld.start("organization-test");
    
    listener = new MockJournalListener();

    journal = world.world().actorFor(Journal.class, InMemoryJournalActor.class, listener);

    registry = new SourcedTypeRegistry(world.world());

    EntryAdapters.register(registry, journal);

    organizationTestActor = world.actorFor(Organization.class, OrganizationEntity.class, OrganizationId.unique());
    organizationTestActor.context().until.resetHappeningsTo(1);
    organizationTestActor.actor().defineWith("name", "description");
  }

  @After
  public void tearDown() {
    world.terminate();
  }

  @Test
  public void testThatOrganizationDefinedIsEquals() throws Exception {
    organizationTestActor.context().until.completes(); // see setUp()
    final List<DomainEvent> applied = organizationTestActor.viewTestState().valueOf("applied");
    final OrganizationDefined organizationDefined = (OrganizationDefined) applied.get(0);
    Assert.assertEquals("name", organizationDefined.name);
    Assert.assertEquals("description", organizationDefined.description);
  }

  @Test
  public void testThatOrganizationRenamed() throws Exception {
    organizationTestActor.context().until.completes(); // see setUp()
    organizationTestActor.context().until.resetHappeningsTo(1);
    organizationTestActor.actor().renameTo("new name");
    organizationTestActor.context().until.completes();
    final List<DomainEvent> applied = organizationTestActor.viewTestState().valueOf("applied");
    final OrganizationRenamed organizationRenamed = (OrganizationRenamed) applied.get(1);
    Assert.assertEquals("new name", organizationRenamed.name);
  }

  @Test
  public void testThatOrganizationIsDescribed() throws Exception {
    organizationTestActor.context().until.completes(); // see setUp()
    organizationTestActor.context().until.resetHappeningsTo(1);
    organizationTestActor.actor().describeAs("new description");
    organizationTestActor.context().until.completes();
    final List<DomainEvent> applied = organizationTestActor.viewTestState().valueOf("applied");
    final OrganizationDescribed organizationDescribed = (OrganizationDescribed) applied.get(1);
    Assert.assertEquals("new description", organizationDescribed.description);
  }
}
