// Copyright © 2012-2018 Vaughn Vernon. All rights reserved.
//
// This Source Code Form is subject to the terms of the
// Mozilla Public License, v. 2.0. If a copy of the MPL
// was not distributed with this file, You can obtain
// one at https://mozilla.org/MPL/2.0/.

package io.vlingo.schemata.model;

import io.vlingo.actors.Definition;
import io.vlingo.actors.testkit.TestActor;
import io.vlingo.actors.testkit.TestWorld;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;

public class ContextEntityTest {
    private TestWorld world;
    private TestActor<Context> contextTestActor;

    @Before
    public void setUp() throws Exception {
        world = TestWorld.start("context-test");
        contextTestActor = world.actorFor(Definition.has(ContextEntity.class, Definition.parameters(Id.OrganizationId.unique(), Id.UnitId.unique(),
                Id.ContextId.unique(), "namespace", "desc")), Context.class);
    }

    @After
    public void tearDown() {
        world.terminate();
    }

    @Test
    public void testThatContextRenamed() throws Exception {
        contextTestActor.actor().changeNamespaceTo("newNamespace");
        //Assertion for change namespace
        final Events.ContextRenamed  contextRenamed = (Events.ContextRenamed)((ArrayList) contextTestActor.viewTestState().valueOf("applied")).get(1);
        Assert.assertEquals("newNamespace", contextRenamed.namespace );
    }

    @Test
    public void testThatContextIsDescribed() throws Exception{
        contextTestActor.actor().describeAs("newDesc");
        //Assertion for description
        final Events.ContextDescribed contextDescribed = (Events.ContextDescribed)((ArrayList) contextTestActor.viewTestState().valueOf("applied")).get(1);
        Assert.assertEquals("newDesc", contextDescribed.description);
    }

}