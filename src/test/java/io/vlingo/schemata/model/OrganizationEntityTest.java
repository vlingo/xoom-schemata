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

public class OrganizationEntityTest {
    private TestWorld world;
    private TestActor<Organization> organizationTestActor;

    @Before
    public void setUp() throws Exception {
        world = TestWorld.start("organization-test");
        organizationTestActor = world.actorFor(Definition.has(OrganizationEntity.class, Definition.parameters("name", "description")), Organization.class);
    }

    @After
    public void tearDown() {
        world.terminate();
    }


    @Test
    public void testThatOrganizationRenamed() throws Exception{
        organizationTestActor.actor().renameTo("newName");
        //Assertion for organization rename
        final Events.OrganizationRenamed organizationRenamed = (Events.OrganizationRenamed) ((ArrayList) organizationTestActor.viewTestState().valueOf("applied")).get(1);
        Assert.assertEquals("newName", organizationRenamed.name);
    }

    @Test
    public void testThatOrganizationIsDescribed() throws Exception{
        organizationTestActor.actor().describeAs("newDescription");
        //Assertion for organization description
        final Events.OrganizationDescribed organizationDescribed = (Events.OrganizationDescribed)((ArrayList) organizationTestActor.viewTestState().valueOf("applied")).get(1);
        Assert.assertEquals("newDescription", organizationDescribed.description);
    }

    @Test
    public void testThatOrganizationEquals() throws Exception{
        final Events.OrganizationDefined organizationDefined = (Events.OrganizationDefined) ((ArrayList) organizationTestActor.viewTestState().valueOf("applied")).get(0);
        final Events.OrganizationDefined newOrganizationDefined = new Events.OrganizationDefined(Id.OrganizationId.existing(organizationDefined.organizationId), organizationDefined.name, organizationDefined.description);
        Assert.assertEquals(newOrganizationDefined, organizationDefined);
    }


}
