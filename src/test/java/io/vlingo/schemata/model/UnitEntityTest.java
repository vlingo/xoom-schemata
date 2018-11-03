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
import io.vlingo.schemata.model.Events.UnitDescribed;
import io.vlingo.schemata.model.Events.UnitRenamed;

public class UnitEntityTest {

  private TestWorld world;
  private TestActor<Unit> unit;

  @Before
  public void setUp() throws Exception {
    world = TestWorld.start("unit-entity-test");
    unit = world.actorFor(Definition.has(UnitEntity.class,
            Definition.parameters(Unit.uniqueId(Organization.uniqueId()), "name", "description")), Unit.class);
  }

  @After
  public void tearDown() throws Exception {
    world.terminate();
  }

  @Test
  public void testThatUnitDescribed() throws Exception {
    unit.actor().describeAs("description");
    final UnitDescribed unitDescribed = (UnitDescribed) sourced().appliedEvent(1);
    Assert.assertEquals("description", unitDescribed.description);
  }

  @Test
  public void testThatUnitRenamed() throws Exception {
    unit.actor().renameTo("newName");
    final UnitRenamed unitRenamed = (UnitRenamed) sourced().appliedEvent(1);
    Assert.assertEquals("newName", unitRenamed.name);
  }

  @SuppressWarnings("unchecked")
  private <T> Sourced<T> sourced() {
    return (Sourced<T>) unit.actorInside();
  }
}