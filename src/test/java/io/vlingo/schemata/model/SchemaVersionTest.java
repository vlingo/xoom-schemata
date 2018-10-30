// Copyright © 2012-2018 Vaughn Vernon. All rights reserved.
//
// This Source Code Form is subject to the terms of the
// Mozilla Public License, v. 2.0. If a copy of the MPL
// was not distributed with this file, You can obtain
// one at https://mozilla.org/MPL/2.0/.

package io.vlingo.schemata.model;

import io.vlingo.actors.Definition;
import io.vlingo.actors.testkit.TestActor;
import io.vlingo.actors.testkit.TestUntil;
import io.vlingo.actors.testkit.TestWorld;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;

public class SchemaVersionTest {

  private TestWorld world;

  @Before
  public void setUp() throws Exception {
    world = TestWorld.start ( "schema-version-test" );
  }

  @After
  public void tearDown(){
    world.terminate ();
  }

  @Test
  public void testSchemaVersionVlingoSchemata() throws Exception {
    final TestActor<SchemaVersion> schemaVersionTestActor =
            world.actorFor ( Definition.has ( SchemaVersionEntity.class, Definition.parameters ( Id.OrganizationId.unique (), Id.UnitId.unique (),
                    Id.ContextId.unique (), Id.SchemaId.unique (), Id.SchemaVersionId.unique (), "desc", new SchemaVersion.Definition("definition"), SchemaVersion.Status.Undefined, new SchemaVersion.Version("version-1"), "name") ), SchemaVersion.class );

    schemaVersionTestActor.actor ().definedAs ( new SchemaVersion.Definition ( "newDefinition" ) );
    schemaVersionTestActor.actor ().describeAs ( "newDescription" );
    schemaVersionTestActor.actor ().assignStatus ( SchemaVersion.Status.Draft );

    Assert.assertEquals ( 3, TestWorld.Instance.get ().allMessagesFor ( schemaVersionTestActor.address () ).size () );
    Assert.assertEquals ( 4, ((ArrayList)schemaVersionTestActor.viewTestState ().valueOf ( "applied" )).size () );
  }

  @Test
  public void testThatUntilCompletesTimesOut() {
    final TestUntil until = TestUntil.happenings ( 1 );
    Assert.assertFalse ( until.completesWithin ( 100 ) );
  }
}
