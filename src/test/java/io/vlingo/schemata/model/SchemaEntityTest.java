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

public class SchemaEntityTest {
    private TestWorld world;
    private TestActor<Schema> schemaTestActor;

    @Before
    public void setUp() throws Exception {
        world = TestWorld.start("schema-test");
        schemaTestActor = world.actorFor(Definition.has(SchemaEntity.class, Definition.parameters(Id.OrganizationId.unique(), Id.UnitId.unique(),
                Id.ContextId.unique(), Id.SchemaId.unique(), Category.None, "name", "desc")), Schema.class);
    }

    @After
    public void tearDown() {
        world.terminate();
    }

    @Test
    public void testThatSchemaDescribed() throws Exception {
        schemaTestActor.actor().describeAs("newDescription");
        //Assertion for schema description
        final Events.SchemaDescribed schemaDescribed = (Events.SchemaDescribed) ((ArrayList) schemaTestActor.viewTestState().valueOf("applied")).get(1);
        Assert.assertEquals("newDescription", schemaDescribed.description);
    }

    @Test
    public void testThatSchemaReCategorised() throws Exception{
        schemaTestActor.actor().recategorizedAs(Category.Commands);
        //Assertion for schema reCategorized
        final Events.SchemaRecategorized schemaRecategorized = (Events.SchemaRecategorized) ((ArrayList) schemaTestActor.viewTestState().valueOf("applied")).get(1);
        Assert.assertEquals(Category.Commands.name(), schemaRecategorized.category);
    }

    @Test
    public void testThatSchemaReNamed() throws Exception{
        schemaTestActor.actor().renameTo("newName");
        //Assertion for schema rename
        final Events.SchemaRenamed schemaRenamed = (Events.SchemaRenamed) ((ArrayList) schemaTestActor.viewTestState().valueOf("applied")).get(1);
        Assert.assertEquals("newName", schemaRenamed.name);
    }

}
