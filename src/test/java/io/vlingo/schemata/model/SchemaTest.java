// Copyright © 2012-2018 Vaughn Vernon. All rights reserved.
//
// This Source Code Form is subject to the terms of the
// Mozilla Public License, v. 2.0. If a copy of the MPL
// was not distributed with this file, You can obtain
// one at https://mozilla.org/MPL/2.0/.

package io.vlingo.schemata.model;

import io.vlingo.actors.Definition;
import io.vlingo.actors.World;
import io.vlingo.actors.testkit.TestActor;
import io.vlingo.actors.testkit.TestUntil;
import io.vlingo.actors.testkit.TestWorld;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;

public class SchemaTest {
   private TestWorld world;

    @Before
    public void setUp() throws Exception {
        world = TestWorld.start ( "schema-test" );
    }

    @After
    public void tearDown(){
        world.terminate ();
    }

    @Test
    public void testApplySchemaVlingoSchemata() throws Exception {

        final TestActor<Schema> schemaTestActor =
                world.actorFor ( Definition.has ( SchemaEntity.class, Definition.parameters ( Id.OrganizationId.unique (), Id.UnitId.unique (),
                        Id.ContextId.unique (), Id.SchemaId.unique (), Category.None, "name", "desc") ), Schema.class );

        schemaTestActor.actor ().describeAs ( "newDescription" );
        schemaTestActor.actor ().recategorizedAs ( Category.Commands );
        schemaTestActor.actor ().renameTo ( "newName" );

        Assert.assertEquals ( 3, TestWorld.Instance.get ().allMessagesFor ( schemaTestActor.address () ).size () );
        Assert.assertEquals ( 4, ((ArrayList)schemaTestActor.viewTestState ().valueOf ( "applied" )).size () );
    }

    @Test
    public void testThatUntilCompletesTimesOut() {
        final TestUntil until = TestUntil.happenings ( 1 );
        Assert.assertFalse ( until.completesWithin ( 100 ) );
    }

}
