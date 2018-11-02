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

public class SchemaVersionEntityTest {

    private TestWorld world;
    private TestActor<SchemaVersion> schemaVersionTestActor;

    @Before
    public void setUp() throws Exception {
        world = TestWorld.start("schema-version-test");
        schemaVersionTestActor = world.actorFor(Definition.has(SchemaVersionEntity.class, Definition.parameters(Id.OrganizationId.unique(), Id.UnitId.unique(),
                Id.ContextId.unique(), Id.SchemaId.unique(), Id.SchemaVersionId.unique(), "name", "desc", new SchemaVersion.Definition("definition"), SchemaVersion.Status.Undefined, new SchemaVersion.Version("version-1"))), SchemaVersion.class);
    }

    @After
    public void tearDown() {
        world.terminate();
    }

    @Test
    public void testThatSchemaVersionDefined() throws Exception {
        schemaVersionTestActor.actor().definedAs(new SchemaVersion.Definition("newDefinition"));
        //Assertion for schema version definition
        final Events.SchemaVersionDefinition schemaVersionDefinition = (Events.SchemaVersionDefinition) ((ArrayList) schemaVersionTestActor.viewTestState().valueOf("applied")).get(1);
        Assert.assertEquals("newDefinition", schemaVersionDefinition.definition);



    }

    @Test
    public void testThatSchemaVersionIsDescribed() throws Exception{
        schemaVersionTestActor.actor().describeAs("newDescription");
        //Assertion for schema version description
        final Events.SchemaVersionDescribed schemaVersionDescribed = (Events.SchemaVersionDescribed) ((ArrayList) schemaVersionTestActor.viewTestState().valueOf("applied")).get(1);
        Assert.assertEquals("newDescription", schemaVersionDescribed.description);

    }

    @Test
    public void testThatSchemaVersionAssignedStatus() throws Exception{
        schemaVersionTestActor.actor().assignStatus(SchemaVersion.Status.Draft);
        //Assertion for schema version status
        final Events.SchemaVersionStatus schemaVersionStatus = (Events.SchemaVersionStatus) ((ArrayList) schemaVersionTestActor.viewTestState().valueOf("applied")).get(1);
        Assert.assertEquals(SchemaVersion.Status.Draft.name(), schemaVersionStatus.status);
    }

    @Test
    public void testThatSchemaVersionAssignedVersion() throws Exception{
        schemaVersionTestActor.actor().assignVersion(new SchemaVersion.Version("version-1"));
        //Assertion for assign schema version
        final Events.SchemaVersionAssignedVersion schemaVersionAssignedVersion = (Events.SchemaVersionAssignedVersion) ((ArrayList) schemaVersionTestActor.viewTestState().valueOf("applied")).get(1);
        Assert.assertEquals("version-1", schemaVersionAssignedVersion.version);
    }

    @Test
    public void testThatSchemaEquals() throws Exception {
        final Events.SchemaVersionDefined schemaVersionDefined = (Events.SchemaVersionDefined) ((ArrayList) schemaVersionTestActor.viewTestState().valueOf("applied")).get(0);
        final Events.SchemaVersionDefined newSchemaVersionDefined = new Events.SchemaVersionDefined(Id.OrganizationId.existing(schemaVersionDefined.organizationId),
                Id.ContextId.existing(schemaVersionDefined.contextId), Id.SchemaId.existing(schemaVersionDefined.schemaId), Category.valueOf(schemaVersionDefined.category),
                schemaVersionDefined.name, schemaVersionDefined.description, Id.SchemaVersionId.existing(schemaVersionDefined.schemaVersionId), SchemaVersion.Status.valueOf(schemaVersionDefined.status),
                new SchemaVersion.Definition(schemaVersionDefined.definition), Id.UnitId.existing(schemaVersionDefined.unitId), new SchemaVersion.Version(schemaVersionDefined.version));
        Assert.assertEquals(newSchemaVersionDefined, schemaVersionDefined);
    }

}
