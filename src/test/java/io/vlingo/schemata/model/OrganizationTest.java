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

public class OrganizationTest {
    private TestWorld world;

    @Before
    public void setUp() throws Exception {
        world = TestWorld.start("organization-test");
    }

    @After
    public void tearDown() {
        world.terminate();
    }

    @Test
    public void testApplyOrganizationVlingoSchemata() throws Exception {
        final TestActor<Organization> organizationTestActor =
                world.actorFor(Definition.has(OrganizationEntity.class, Definition.parameters("name", "description")), Organization.class);

        organizationTestActor.actor().renameTo("newName");
        organizationTestActor.actor().describeAs("newDescription");

        //Assertion for organization rename
        Assert.assertEquals(Events.OrganizationRenamed.class, ((ArrayList) organizationTestActor.viewTestState().valueOf("applied")).get(1).getClass());
        Assert.assertEquals(Events.OrganizationRenamed.with(Id.OrganizationId.unique(), "assertionName").name, "assertionName");

        //Assertion for organization description
        Assert.assertEquals(Events.OrganizationDescribed.class, ((ArrayList) organizationTestActor.viewTestState().valueOf("applied")).get(2).getClass());
        Assert.assertEquals(Events.OrganizationDescribed.with(Id.OrganizationId.unique(), "assertionDescription").description, "assertionDescription");

    }


}
