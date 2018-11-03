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

import io.vlingo.actors.Definition;
import io.vlingo.actors.testkit.TestActor;
import io.vlingo.actors.testkit.TestWorld;
import io.vlingo.lattice.model.sourcing.Sourced;
import io.vlingo.schemata.model.Events.OrganizationDefined;
import io.vlingo.schemata.model.Events.OrganizationDescribed;
import io.vlingo.schemata.model.Events.OrganizationRenamed;
import io.vlingo.schemata.model.Id.OrganizationId;

public class OrganizationEntityTest {
  private TestWorld world;
  private TestActor<Organization> organizationTestActor;

  @Before
  public void setUp() throws Exception {
    world = TestWorld.start("organization-test");
    organizationTestActor = world.actorFor(Definition.has(OrganizationEntity.class, Definition.parameters(OrganizationId.unique(), "name", "description")), Organization.class);
  }

  @After
  public void tearDown() {
    world.terminate();
  }

  @Test
  public void testThatOrganizationDefinedIsEquals() throws Exception {
    final OrganizationDefined organizationDefined = (OrganizationDefined) sourced().appliedEvent(0);
    Assert.assertEquals("name", organizationDefined.name);
    Assert.assertEquals("description", organizationDefined.description);
  }

  @Test
  public void testThatOrganizationRenamed() throws Exception {
    organizationTestActor.actor().renameTo("new name");
    final OrganizationRenamed organizationRenamed = (OrganizationRenamed) sourced().appliedEvent(1);
    Assert.assertEquals("new name", organizationRenamed.name);
  }

  @Test
  public void testThatOrganizationIsDescribed() throws Exception {
    organizationTestActor.actor().describeAs("new description");
    final OrganizationDescribed organizationDescribed = (OrganizationDescribed) sourced().appliedEvent(1);
    Assert.assertEquals("new description", organizationDescribed.description);
  }

  @SuppressWarnings("unchecked")
  private <T> Sourced<T> sourced() {
    return (Sourced<T>) organizationTestActor.actorInside();
  }
}
