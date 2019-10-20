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
import io.vlingo.schemata.model.Id.OrganizationId;
import io.vlingo.symbio.store.object.MapQueryExpression;
import io.vlingo.symbio.store.object.ObjectStore;
import io.vlingo.symbio.store.object.StateObjectMapper;
import io.vlingo.symbio.store.object.inmemory.InMemoryObjectStoreActor;

public class OrganizationEntityTest {
  private ObjectTypeRegistry registry;
  private Organization organization;
  private OrganizationId organizationId;
  private ObjectStore objectStore;
  private World world;

  @Before
  @SuppressWarnings({ "unchecked", "rawtypes" })
  public void setUp() {
    world = World.start("organization-test");

    objectStore = world.actorFor(ObjectStore.class, InMemoryObjectStoreActor.class, new NoopDispatcher());

    registry = new ObjectTypeRegistry(world);

    final Info<Organization> organizationInfo =
            new Info(
                    objectStore,
                    OrganizationState.class,
                    "HR-Database",
                    MapQueryExpression.using(Organization.class, "find", MapQueryExpression.map("id", "id")),
                    StateObjectMapper.with(Organization.class, new Object(), new Object()));

    registry.register(organizationInfo);
    organizationId = OrganizationId.unique();
    organization = world.actorFor(Organization.class, OrganizationEntity.class, organizationId);
  }

  @After
  public void tearDown() {
    world.terminate();
  }

  @Test
  public void testThatOrganizationDefinedIsEquals() {
    final OrganizationState state = organization.defineWith("name", "description").await();
    Assert.assertNotEquals(OrganizationState.unidentified(), state.persistenceId());
    Assert.assertEquals(organizationId.value, state.organizationId.value);
    Assert.assertEquals("name", state.name);
    Assert.assertEquals("description", state.description);
  }

  @Test
  public void testThatOrganizationRenamed() {
    final OrganizationState state = organization.renameTo("new name").await();
    Assert.assertEquals("new name", state.name);
  }

  @Test
  public void testThatOrganizationIsDescribed() {
    final OrganizationState state = organization.describeAs("new description").await();
    Assert.assertEquals("new description", state.description);
  }
}
