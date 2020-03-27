// Copyright © 2012-2020 VLINGO LABS. All rights reserved.
//
// This Source Code Form is subject to the terms of the
// Mozilla Public License, v. 2.0. If a copy of the MPL
// was not distributed with this file, You can obtain
// one at https://mozilla.org/MPL/2.0/.

package io.vlingo.schemata.model;

import java.util.concurrent.atomic.AtomicReference;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import io.vlingo.actors.World;
import io.vlingo.actors.testkit.AccessSafely;
import io.vlingo.lattice.model.sourcing.SourcedTypeRegistry;
import io.vlingo.lattice.model.sourcing.SourcedTypeRegistry.Info;
import io.vlingo.schemata.NoopDispatcher;
import io.vlingo.schemata.model.Id.OrganizationId;
import io.vlingo.symbio.store.journal.Journal;
import io.vlingo.symbio.store.journal.inmemory.InMemoryJournalActor;

public class OrganizationEntityTest {
  private AccessSafely access;
  private AtomicReference<OrganizationState> orgState;

  private Journal<String> journal;
  private Organization organization;
  private OrganizationId organizationId;
  private SourcedTypeRegistry registry;
  private World world;

  @Before
  @SuppressWarnings({ "unchecked", "rawtypes" })
  public void setUp() {
    world = World.start("organization-test");

    journal = world.actorFor(Journal.class, InMemoryJournalActor.class, new NoopDispatcher());

    registry = new SourcedTypeRegistry(world);
    registry.register(new Info(journal, OrganizationEntity.class, OrganizationEntity.class.getSimpleName()));

    organizationId = OrganizationId.unique();
    organization = world.actorFor(Organization.class, OrganizationEntity.class, organizationId);

    orgState = new AtomicReference<>();
    access = AccessSafely.afterCompleting(1);
    access.writingWith("state", (OrganizationState s) -> orgState.set(s));
    access.readingWith("state", ()-> orgState.get());
  }

  @After
  public void tearDown() {
    world.terminate();
  }

  @Test
  public void testThatOrganizationDefinedIsEquals() {
    organization.defineWith("name", "description").andThenConsume(s -> access.writeUsing("state", s));

    final OrganizationState state = access.readFrom("state");

    Assert.assertEquals(organizationId.value, state.organizationId.value);
    Assert.assertEquals("name", state.name);
    Assert.assertEquals("description", state.description);
  }

  @Test
  public void testThatOrganizationRenamed() {
    organization.renameTo("new name").andThenConsume(s -> access.writeUsing("state", s));

    final OrganizationState state = access.readFrom("state");

    Assert.assertEquals("new name", state.name);
  }

  @Test
  public void testThatOrganizationIsDescribed() {
    organization.describeAs("new description").andThenConsume(s -> access.writeUsing("state", s));

    final OrganizationState state = access.readFrom("state");

    Assert.assertEquals("new description", state.description);
  }
}
