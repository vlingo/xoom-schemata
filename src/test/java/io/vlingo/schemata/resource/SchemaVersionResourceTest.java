// Copyright © 2012-2020 VLINGO LABS. All rights reserved.
//
// This Source Code Form is subject to the terms of the
// Mozilla Public License, v. 2.0. If a copy of the MPL
// was not distributed with this file, You can obtain
// one at https://mozilla.org/MPL/2.0/.

package io.vlingo.schemata.resource;

import io.vlingo.common.serialization.JsonSerialization;
import io.vlingo.http.Response;
import io.vlingo.schemata.Schemata;
import io.vlingo.schemata.model.*;
import io.vlingo.schemata.model.SchemaVersion.Status;
import io.vlingo.schemata.resource.data.SchemaVersionData;
import org.junit.Ignore;
import org.junit.Test;

import static io.vlingo.http.Response.Status.*;
import static io.vlingo.http.ResponseHeader.Location;
import static org.junit.Assert.*;

public class SchemaVersionResourceTest extends ResourceTest {
    private static final String OrgId = "O123";
    private static final String UnitId = "U456";
    private static final String ContextId = "C789";
    private static final String SchemaId = "S135";
    private static final String SchemaVersionDescription = "Test context.";
    private static final String SchemaVersionSpecification = "event Spec { type t }";
    private static final String SchemaVersionStatus = "Draft";
    private static final String SchemaVersionVersion000 = "0.0.0";
    private static final String SchemaVersionVersion100 = "1.0.0";
    private static final String SchemaVersionVersion101 = "1.0.1";
    private static final String SchemaVersionVersion300 = "3.0.0";

    @Test
    public void testThatSchemaVersionIsDefined() {
        final SchemaVersionResource resource = new SchemaVersionResource(world);
        final SchemaVersionData defineData = SchemaVersionData.just(SchemaVersionSpecification, SchemaVersionDescription, "", SchemaVersionVersion000, SchemaVersionVersion100);
        final Response response1 = resource.defineWith(OrgId, UnitId, ContextId, SchemaId, defineData).await();
        assertEquals(Created, response1.status);
        assertNotNull(response1.headers.headerOf(Location));
        final SchemaVersionData data = JsonSerialization.deserialized(response1.entity.content(), SchemaVersionData.class);
        assertEquals(SchemaVersionStatus, data.status);
        assertEquals(SchemaVersionSpecification, data.specification);
        assertEquals(SchemaVersionDescription, data.description);
        assertEquals(SchemaVersionVersion100, data.currentVersion);
    }

    @Test
    public void testThatNonExistingSchemaVersionReturns404() {
        final SchemaVersionResource resource = new SchemaVersionResource(world);
        OrganizationState org = Organization.with(world.stageNamed(Schemata.StageName), Organization.uniqueId(),"o", "d").await();
        UnitState unit = Unit.with(world.stageNamed(Schemata.StageName), org.organizationId,"u", "d").await();
        ContextState context = Context.with(world.stageNamed(Schemata.StageName), unit.unitId,"c", "d").await();
        SchemaState schema = Schema.with(world.stageNamed(Schemata.StageName), context.contextId,Category.Event, Scope.Public, "s", "d").await();

        final Response response = resource.querySchemaVersionByIds(org.organizationId.value, unit.unitId.value, context.contextId.value, schema.schemaId.value, "-1").await();
        assertEquals(NotFound, response.status);
        assertTrue(response.entity.content().contains("Schema Version not found"));
    }

    @Test
    @Ignore("Skipped due to hanging issues when not run alone; to be re-enabled before merging. See https://github.com/vlingo/vlingo-schemata/issues/130")
    public void testThatSchemaVersionMinorUpgradeIsDefined() {
        final SchemaVersionResource resource = new SchemaVersionResource(world);
        final SchemaVersionData previousData = SchemaVersionData.just(SchemaVersionSpecification, SchemaVersionDescription, "", SchemaVersionVersion000, SchemaVersionVersion100);
        resource.defineWith(OrgId, UnitId, ContextId, SchemaId, previousData).await();

        final SchemaVersionData defineData = SchemaVersionData.just(SchemaVersionSpecification, SchemaVersionDescription, "", SchemaVersionVersion100, SchemaVersionVersion101);
        final Response response1 = resource.defineWith(OrgId, UnitId, ContextId, SchemaId, defineData).await();
        assertEquals(Created, response1.status);
        assertNotNull(response1.headers.headerOf(Location));
        final SchemaVersionData data = JsonSerialization.deserialized(response1.entity.content(), SchemaVersionData.class);
        assertEquals(SchemaVersionStatus, data.status);
        assertEquals(SchemaVersionSpecification, data.specification);
        assertEquals(SchemaVersionDescription, data.description);
        assertEquals(SchemaVersionVersion101, data.currentVersion);
    }

    @Test
    public void testSchemaVersionDescribedAs() {
        final SchemaVersionResource resource = new SchemaVersionResource(world);
        final SchemaVersionData defineData = SchemaVersionData.just(SchemaVersionSpecification, SchemaVersionDescription, "", SchemaVersionVersion000, SchemaVersionVersion100);
        final Response response1 = resource.defineWith(OrgId, UnitId, ContextId, SchemaId, defineData).await();
        assertEquals(Created, response1.status);
        final SchemaVersionData data1 = JsonSerialization.deserialized(response1.entity.content(), SchemaVersionData.class);
        final Response response2 = resource.describeAs(data1.organizationId, data1.unitId, data1.contextId, data1.schemaId, data1.schemaVersionId, SchemaVersionDescription + 1).await();
        assertNotEquals(response1.entity.content(), response2.entity.content());
        final SchemaVersionData data2 = JsonSerialization.deserialized(response2.entity.content(), SchemaVersionData.class);
        assertEquals(data1.schemaVersionId, data2.schemaVersionId);
        assertNotEquals(data1.description, data2.description);
        assertEquals((SchemaVersionDescription + 1), data2.description);
    }

    @Test
    public void testThatSchemaVersionIsPublished() {
        final SchemaVersionResource resource = new SchemaVersionResource(world);
        final SchemaVersionData defineData = SchemaVersionData.just(SchemaVersionSpecification, SchemaVersionDescription, "", SchemaVersionVersion000, SchemaVersionVersion100);
        final Response response1 = resource.defineWith(OrgId, UnitId, ContextId, SchemaId, defineData).await();
        assertEquals(Created, response1.status);
        final SchemaVersionData data1 = JsonSerialization.deserialized(response1.entity.content(), SchemaVersionData.class);
        final Response response2 = resource.statusOf(data1.organizationId, data1.unitId, data1.contextId, data1.schemaId, data1.schemaVersionId, Status.Published.name()).await();
        assertNotEquals(response1.entity.content(), response2.entity.content());
        final SchemaVersionData data2 = JsonSerialization.deserialized(response2.entity.content(), SchemaVersionData.class);
        assertEquals(data1.schemaVersionId, data2.schemaVersionId);
        assertNotEquals(data1.status, data2.status);
        assertEquals(Status.Published.name(), data2.status);
    }

    @Test
    public void testThatSchemaVersionIsDeprecated() {
        final SchemaVersionResource resource = new SchemaVersionResource(world);
        final SchemaVersionData defineData = SchemaVersionData.just(SchemaVersionSpecification, SchemaVersionDescription, "", SchemaVersionVersion000, SchemaVersionVersion100);
        final Response response1 = resource.defineWith(OrgId, UnitId, ContextId, SchemaId, defineData).await();
        assertEquals(Created, response1.status);
        final SchemaVersionData data1 = JsonSerialization.deserialized(response1.entity.content(), SchemaVersionData.class);
        resource.statusOf(data1.organizationId, data1.unitId, data1.contextId, data1.schemaId, data1.schemaVersionId, Status.Published.name()).await();
        final Response response2 = resource.statusOf(data1.organizationId, data1.unitId, data1.contextId, data1.schemaId, data1.schemaVersionId, Status.Deprecated.name()).await();
        assertNotEquals(response1.entity.content(), response2.entity.content());
        final SchemaVersionData data2 = JsonSerialization.deserialized(response2.entity.content(), SchemaVersionData.class);
        assertEquals(data1.schemaVersionId, data2.schemaVersionId);
        assertNotEquals(data1.status, data2.status);
        assertEquals(Status.Deprecated.name(), data2.status);
    }

    @Test
    public void testThatSchemaVersionIsNotDeprecated() {
        Id.OrganizationId orgId = Id.OrganizationId.unique();
        Id.UnitId unitId = Id.UnitId.uniqueFor(orgId);
        Id.ContextId contextId = Id.ContextId.uniqueFor(unitId);
        Id.SchemaId schemaId = Id.SchemaId.uniqueFor(contextId);

        final SchemaVersionResource resource = new SchemaVersionResource(world);
        final SchemaVersionData defineData = SchemaVersionData.just(SchemaVersionSpecification, SchemaVersionDescription, "", SchemaVersionVersion000, SchemaVersionVersion100);
        final Response response1 = resource.defineWith(
                orgId.value, unitId.value, contextId.value, schemaId.value, defineData)
                .await();
        assertEquals(Created, response1.status);
        final SchemaVersionData data1 = JsonSerialization.deserialized(response1.entity.content(), SchemaVersionData.class);

        final Response response2 = resource.statusOf(data1.organizationId, data1.unitId, data1.contextId, data1.schemaId, data1.schemaVersionId, Status.Deprecated.name()).await();
        assertEquals(Response.Status.Conflict, response2.status);
        assertTrue(response2.entity.content().contains("Status 'Deprecated' cannot be reached from current status"));

        Response responseAfterTriedUpdate =
                resource.querySchemaVersionByIds(orgId.value, unitId.value, contextId.value, schemaId.value, data1.schemaVersionId)
                .await();

        final SchemaVersionData data2 =
                JsonSerialization.deserialized(responseAfterTriedUpdate.entity.content(), SchemaVersionData.class);
        assertEquals(data1.schemaVersionId, data2.schemaVersionId);
        assertEquals(data1.status, data2.status);
        assertNotEquals(Status.Deprecated.name(), data2.status);
    }

    @Test
    public void testThatSchemaVersionIsRemoved() {
        final SchemaVersionResource resource = new SchemaVersionResource(world);
        final SchemaVersionData defineData = SchemaVersionData.just(SchemaVersionSpecification, SchemaVersionDescription, "", SchemaVersionVersion000, SchemaVersionVersion100);
        final Response response1 = resource.defineWith(OrgId, UnitId, ContextId, SchemaId, defineData).await();
        assertEquals(Created, response1.status);
        final SchemaVersionData data1 = JsonSerialization.deserialized(response1.entity.content(), SchemaVersionData.class);
        resource.statusOf(data1.organizationId, data1.unitId, data1.contextId, data1.schemaId, data1.schemaVersionId, Status.Published.name()).await();
        resource.statusOf(data1.organizationId, data1.unitId, data1.contextId, data1.schemaId, data1.schemaVersionId, Status.Deprecated.name()).await();
        final Response response2 = resource.statusOf(data1.organizationId, data1.unitId, data1.contextId, data1.schemaId, data1.schemaVersionId, Status.Removed.name()).await();
        final SchemaVersionData data2 = JsonSerialization.deserialized(response2.entity.content(), SchemaVersionData.class);
        assertEquals(data1.schemaVersionId, data2.schemaVersionId);
        assertNotEquals(data1.status, data2.status);
        assertEquals(Status.Removed.name(), data2.status);
    }

    @Test
    public void testThatSchemaVersionIsNotRemoved() {
        Id.OrganizationId orgId = Id.OrganizationId.unique();
        Id.UnitId unitId = Id.UnitId.uniqueFor(orgId);
        Id.ContextId contextId = Id.ContextId.uniqueFor(unitId);
        Id.SchemaId schemaId = Id.SchemaId.uniqueFor(contextId);

        final SchemaVersionResource resource = new SchemaVersionResource(world);
        final SchemaVersionData defineData = SchemaVersionData.just(SchemaVersionSpecification, SchemaVersionDescription, "", SchemaVersionVersion000, SchemaVersionVersion100);
        final Response response1 =
                resource.defineWith(orgId.value, unitId.value, contextId.value, schemaId.value, defineData)
                        .await();
        assertEquals(Created, response1.status);
        final SchemaVersionData data1 = JsonSerialization.deserialized(response1.entity.content(), SchemaVersionData.class);
        resource.statusOf(data1.organizationId, data1.unitId, data1.contextId, data1.schemaId, data1.schemaVersionId, Status.Published.name()).await();
        final Response response2 = resource.statusOf(data1.organizationId, data1.unitId, data1.contextId, data1.schemaId, data1.schemaVersionId, Status.Removed.name()).await();
        assertEquals(Response.Status.Conflict, response2.status);

        Response responseAfterTriedUpdate =
                resource.querySchemaVersionByIds(orgId.value, unitId.value, contextId.value, schemaId.value, data1.schemaVersionId)
                        .await();

        final SchemaVersionData data2 =
                JsonSerialization.deserialized(responseAfterTriedUpdate.entity.content(), SchemaVersionData.class);
        assertEquals(data1.schemaVersionId, data2.schemaVersionId);
        assertNotEquals(Status.Removed.name(), data2.status);
    }

    @Test
    public void testFailDefineWithNullSpecification() {
        final SchemaVersionResource resource = new SchemaVersionResource(world);
        final SchemaVersionData defineData = SchemaVersionData.just(null, SchemaVersionDescription, "", SchemaVersionVersion000, SchemaVersionVersion100);
        final Response response1 = resource.defineWith(OrgId, UnitId, ContextId, SchemaId, defineData).await();
        assertEquals(BadRequest, response1.status);
        assertTrue(response1.entity.content().toLowerCase().contains("missing"));
        assertNull(response1.headers.headerOf(Location));
    }

    @Test
    public void testFailDefineWithEmptySpecification() {
        final SchemaVersionResource resource = new SchemaVersionResource(world);
        final SchemaVersionData defineData = SchemaVersionData.just("", SchemaVersionDescription, "", SchemaVersionVersion000, SchemaVersionVersion100);
        final Response response1 = resource.defineWith(OrgId, UnitId, ContextId, SchemaId, defineData).await();
        assertEquals(BadRequest, response1.status);
        assertTrue(response1.entity.content().toLowerCase().contains("missing"));
        assertNull(response1.headers.headerOf(Location));
    }

    @Test
    public void testFailDefineWithZeroVersions() {
        final SchemaVersionResource resource = new SchemaVersionResource(world);
        final SchemaVersionData defineData = SchemaVersionData.just(SchemaVersionSpecification, SchemaVersionDescription, "", SchemaVersionVersion000, SchemaVersionVersion000);
        final Response response1 = resource.defineWith(OrgId, UnitId, ContextId, SchemaId, defineData).await();
        assertEquals(BadRequest, response1.status);
        assertTrue(response1.entity.content().toLowerCase().contains("conflicting"));
        assertNull(response1.headers.headerOf(Location));
    }

    @Test
    public void testFailDefineWithHighLowVersions() {
        final SchemaVersionResource resource = new SchemaVersionResource(world);
        final SchemaVersionData defineData = SchemaVersionData.just(SchemaVersionSpecification, SchemaVersionDescription, "", SchemaVersionVersion100, SchemaVersionVersion000);
        final Response response1 = resource.defineWith(OrgId, UnitId, ContextId, SchemaId, defineData).await();
        assertEquals(BadRequest, response1.status);
        assertTrue(response1.entity.content().toLowerCase().contains("conflicting"));
        assertNull(response1.headers.headerOf(Location));
    }

    @Test
    public void testFailDefineWithGappedVersions() {
        final SchemaVersionResource resource = new SchemaVersionResource(world);
        final SchemaVersionData defineData = SchemaVersionData.just(SchemaVersionSpecification, SchemaVersionDescription, "", SchemaVersionVersion100, SchemaVersionVersion300);
        final Response response1 = resource.defineWith(OrgId, UnitId, ContextId, SchemaId, defineData).await();
        assertEquals(BadRequest, response1.status);
        assertTrue(response1.entity.content().toLowerCase().contains("conflicting"));
        assertNull(response1.headers.headerOf(Location));
    }
}
