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

public class SchemaVersionTest {

    private TestWorld world;

    @Before
    public void setUp() throws Exception {
        world = TestWorld.start("schema-version-test");
    }

    @After
    public void tearDown() {
        world.terminate();
    }

    @Test
    public void testSchemaVersionVlingoSchemata() throws Exception {
        final TestActor<SchemaVersion> schemaVersionTestActor =
                world.actorFor(Definition.has(SchemaVersionEntity.class, Definition.parameters(Id.OrganizationId.unique(), Id.UnitId.unique(),
                        Id.ContextId.unique(), Id.SchemaId.unique(), Id.SchemaVersionId.unique(), "name", "desc", new SchemaVersion.Definition("definition"), SchemaVersion.Status.Undefined, new SchemaVersion.Version("version-1"))), SchemaVersion.class);

        schemaVersionTestActor.actor().definedAs(new SchemaVersion.Definition("newDefinition"));
        schemaVersionTestActor.actor().describeAs("newDescription");
        schemaVersionTestActor.actor().assignStatus(SchemaVersion.Status.Draft);
        schemaVersionTestActor.actor().assignVersion(new SchemaVersion.Version("version-1"));

        //Assertion for schema version definition
        Assert.assertEquals(Events.SchemaVersionDefinition.class, ((ArrayList) schemaVersionTestActor.viewTestState().valueOf("applied")).get(1).getClass());
        Assert.assertEquals(Events.SchemaVersionDefinition.with(Id.OrganizationId.unique(), Id.ContextId.unique(), Id.SchemaId.unique(),
                Id.SchemaVersionId.unique(), Id.UnitId.unique(), new SchemaVersion.Definition("assertionDefinition")).definition, "assertionDefinition");

        //Assertion for schema version description
        Assert.assertEquals(Events.SchemaVersionDescribed.class, ((ArrayList) schemaVersionTestActor.viewTestState().valueOf("applied")).get(2).getClass());
        Assert.assertEquals(Events.SchemaVersionDescribed.with(Id.OrganizationId.unique(), Id.ContextId.unique(), Id.SchemaId.unique(),
                "assertionDescription", Id.SchemaVersionId.unique(), Id.UnitId.unique()).description, "assertionDescription");

        //Assertion for schema version status
        Assert.assertEquals(Events.SchemaVersionStatus.class, ((ArrayList) schemaVersionTestActor.viewTestState().valueOf("applied")).get(3).getClass());
        Assert.assertEquals(Events.SchemaVersionStatus.with(Id.OrganizationId.unique(), Id.ContextId.unique(), Id.SchemaId.unique(),
                Id.SchemaVersionId.unique(), Id.UnitId.unique(), SchemaVersion.Status.Published).status, SchemaVersion.Status.Published.name());

        //Assertion for assign schema version
        Assert.assertEquals(Events.SchemaVersionAssignedVersion.class, ((ArrayList) schemaVersionTestActor.viewTestState().valueOf("applied")).get(4).getClass());
        Assert.assertEquals(Events.SchemaVersionAssignedVersion.with(Id.OrganizationId.unique(), Id.ContextId.unique(), Id.SchemaId.unique(),
                Id.SchemaVersionId.unique(), Id.UnitId.unique(), new SchemaVersion.Version("assertion-version")).version, "assertion-version");
    }

}
