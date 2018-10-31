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

public class ContextTest {
    private TestWorld world;

    @Before
    public void setUp() throws Exception {
        world = TestWorld.start("context-test");
    }

    @After
    public void tearDown() {
        world.terminate();
    }

    @Test
    public void testApplyContextVlingoSchemata() throws Exception {
        final TestActor<Context> contextTestActor =
                world.actorFor(Definition.has(ContextEntity.class, Definition.parameters(Id.OrganizationId.unique(), Id.UnitId.unique(),
                        Id.ContextId.unique(), "namespace", "desc")), Context.class);
        contextTestActor.actor().changeNamespaceTo("newNamespace");
        contextTestActor.actor().describeAs("newDesc");

        //Assertion for change namespace
        Assert.assertEquals(Events.ContextRenamed.class, ((ArrayList) contextTestActor.viewTestState().valueOf("applied")).get(1).getClass());
        Assert.assertEquals(Events.ContextRenamed.with(Id.OrganizationId.unique(), Id.UnitId.unique(),
                Id.ContextId.unique(), "assertionName").namespace, "assertionName");

        //Assertion for description
        Assert.assertEquals(Events.ContextDescribed.class, ((ArrayList) contextTestActor.viewTestState().valueOf("applied")).get(2).getClass());
        Assert.assertEquals(Events.ContextDescribed.with(Id.OrganizationId.unique(), Id.UnitId.unique(),
                Id.ContextId.unique(), "assertionDescription").description, "assertionDescription");

    }

}