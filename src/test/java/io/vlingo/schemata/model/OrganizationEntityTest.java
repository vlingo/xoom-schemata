// Copyright © 2012-2018 Vaughn Vernon. All rights reserved.
//
// This Source Code Form is subject to the terms of the
// Mozilla Public License, v. 2.0. If a copy of the MPL
// was not distributed with this file, You can obtain
// one at https://mozilla.org/MPL/2.0/.

package io.vlingo.schemata.model;

import io.vlingo.lattice.model.object.ObjectTypeRegistry;
import io.vlingo.lattice.model.object.ObjectTypeRegistry.Info;
import io.vlingo.schemata.NoopDispatcher;
import io.vlingo.symbio.store.object.MapQueryExpression;
import io.vlingo.symbio.store.object.ObjectStore;
import io.vlingo.symbio.store.object.PersistentObjectMapper;
import io.vlingo.symbio.store.object.inmemory.InMemoryObjectStoreActor;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import io.vlingo.actors.World;
import io.vlingo.schemata.model.Id.OrganizationId;

import static org.junit.Assert.assertTrue;

public class OrganizationEntityTest {
  private ObjectTypeRegistry registry;
  private Organization organization;
  private ObjectStore objectStore;
  private World world;

  @Before
  @SuppressWarnings("unchecked")
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
                    PersistentObjectMapper.with(Organization.class, new Object(), new Object()));

    registry.register(organizationInfo);

    organization = world.actorFor(Organization.class, OrganizationEntity.class);
  }

  @After
  public void tearDown() {
    world.terminate();
  }

  @Test
  public void testThatOrganizationDefinedIsEquals() throws Exception {
    final OrganizationId organizationId = OrganizationId.unique();
    final OrganizationState state = organization.defineWith(organizationId,"name", "description").await();
    assertTrue(state.persistenceId() > 0);
    Assert.assertEquals(organizationId.value, state.organizationId.value);
    Assert.assertEquals("name", state.name);
    Assert.assertEquals("description", state.description);
  }

  @Test
  public void testThatOrganizationRenamed() throws Exception {
    final OrganizationState state = organization.renameTo("new name").await();
    Assert.assertEquals("new name", state.name);
  }

  @Test
  public void testThatOrganizationIsDescribed() throws Exception {
    final OrganizationState state = organization.describeAs("new description").await();
    Assert.assertEquals("new description", state.description);
  }
}
