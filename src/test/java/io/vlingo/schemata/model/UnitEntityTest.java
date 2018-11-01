package io.vlingo.schemata.model;

import io.vlingo.actors.Definition;
import io.vlingo.actors.testkit.TestActor;
import io.vlingo.actors.testkit.TestWorld;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;

public class UnitEntityTest {

    private TestWorld world;
    private TestActor<Organization> unitTestActor;

    @Before
    public void setUp() throws Exception {
        world = TestWorld.start("unit-entity-test");
        unitTestActor = world.actorFor(Definition.has(UnitEntity.class, Definition.parameters(Organization.uniqueId(), Unit.uniqueId(), "name", "description")), Organization.class);
    }

    @After
    public void tearDown() throws Exception {
        world.terminate();
    }

    @Test
    public void testThatUnitDescribed() throws Exception {
        unitTestActor.actor().describeAs("description");
        final Events.UnitDescribed unitDescribed = (Events.UnitDescribed)((ArrayList)unitTestActor.viewTestState().valueOf("applied")).get(1);
        Assert.assertEquals("description", unitDescribed.description);
    }

    @Test
    public void testThatUnitReNamed() throws Exception {
        unitTestActor.actor().renameTo("newName");
        final Events.UnitRenamed unitRenamed = (Events.UnitRenamed)((ArrayList)unitTestActor.viewTestState().valueOf("applied")).get(1);
        Assert.assertEquals("newName", unitRenamed.name);
    }

}