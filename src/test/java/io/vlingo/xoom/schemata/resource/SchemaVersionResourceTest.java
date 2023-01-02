// Copyright © 2012-2023 VLINGO LABS. All rights reserved.
//
// This Source Code Form is subject to the terms of the
// Mozilla Public License, v. 2.0. If a copy of the MPL
// was not distributed with this file, You can obtain
// one at https://mozilla.org/MPL/2.0/.

package io.vlingo.xoom.schemata.resource;

import static io.vlingo.xoom.http.Response.Status.BadRequest;
import static io.vlingo.xoom.http.Response.Status.Created;
import static io.vlingo.xoom.http.Response.Status.NotFound;
import static io.vlingo.xoom.http.ResponseHeader.Location;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import org.junit.Ignore;
import org.junit.Test;

import io.vlingo.xoom.common.serialization.JsonSerialization;
import io.vlingo.xoom.http.Response;
import io.vlingo.xoom.schemata.model.Category;
import io.vlingo.xoom.schemata.model.Context;
import io.vlingo.xoom.schemata.model.ContextState;
import io.vlingo.xoom.schemata.model.Id;
import io.vlingo.xoom.schemata.model.Organization;
import io.vlingo.xoom.schemata.model.OrganizationState;
import io.vlingo.xoom.schemata.model.Schema;
import io.vlingo.xoom.schemata.model.SchemaState;
import io.vlingo.xoom.schemata.model.SchemaVersion.Status;
import io.vlingo.xoom.schemata.model.Scope;
import io.vlingo.xoom.schemata.model.Unit;
import io.vlingo.xoom.schemata.model.UnitState;
import io.vlingo.xoom.schemata.resource.data.SchemaVersionData;

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
        final SchemaVersionResource resource = new SchemaVersionResource(stage);
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
        final SchemaVersionResource resource = new SchemaVersionResource(stage);
        OrganizationState org = Organization.with(stage, Organization.uniqueId(),"o", "d").await();
        UnitState unit = Unit.with(stage, org.organizationId,"u", "d").await();
        ContextState context = Context.with(stage, unit.unitId,"c", "d").await();
        SchemaState schema = Schema.with(stage, context.contextId, Category.Event, Scope.Public, "s", "d").await();

        final Response response = resource.querySchemaVersionByIds(org.organizationId.value, unit.unitId.value, context.contextId.value, schema.schemaId.value, "-1").await();
        assertEquals(NotFound, response.status);
        assertTrue(response.entity.content().contains("SchemaVersion not found"));
    }

    @Test
    @Ignore
    public void testThatSchemaVersionMinorUpgradeIsDefined() {
        final SchemaVersionResource resource = new SchemaVersionResource(stage);
        final SchemaVersionData previousData = SchemaVersionData.just(SchemaVersionSpecification, SchemaVersionDescription, "", SchemaVersionVersion000, SchemaVersionVersion100);
        resource.defineWith(OrgId, UnitId, ContextId, SchemaId, previousData).await();

        final SchemaVersionData defineData = SchemaVersionData.just(SchemaVersionSpecification, SchemaVersionDescription, "", SchemaVersionVersion100, SchemaVersionVersion101);
        final Response response1 = resource.defineWith(OrgId, UnitId, ContextId, SchemaId, defineData).await();
        System.out.println("SchemaVersionResourceTest#testThatSchemaVersionMinorUpgradeIsDefined: " + response1);
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
        final SchemaVersionResource resource = new SchemaVersionResource(stage);
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
        final SchemaVersionResource resource = new SchemaVersionResource(stage);
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
        final SchemaVersionResource resource = new SchemaVersionResource(stage);
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

        final SchemaVersionResource resource = new SchemaVersionResource(stage);
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
        final SchemaVersionResource resource = new SchemaVersionResource(stage);
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

        final SchemaVersionResource resource = new SchemaVersionResource(stage);
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
        final SchemaVersionResource resource = new SchemaVersionResource(stage);
        final SchemaVersionData defineData = SchemaVersionData.just(null, SchemaVersionDescription, "", SchemaVersionVersion000, SchemaVersionVersion100);
        final Response response1 = resource.defineWith(OrgId, UnitId, ContextId, SchemaId, defineData).await();
        assertEquals(BadRequest, response1.status);
        assertTrue(response1.entity.content().toLowerCase().contains("missing"));
        assertNull(response1.headers.headerOf(Location));
    }

    @Test
    public void testFailDefineWithEmptySpecification() {
        final SchemaVersionResource resource = new SchemaVersionResource(stage);
        final SchemaVersionData defineData = SchemaVersionData.just("", SchemaVersionDescription, "", SchemaVersionVersion000, SchemaVersionVersion100);
        final Response response1 = resource.defineWith(OrgId, UnitId, ContextId, SchemaId, defineData).await();
        assertEquals(BadRequest, response1.status);
        assertTrue(response1.entity.content().toLowerCase().contains("missing"));
        assertNull(response1.headers.headerOf(Location));
    }

    @Test
    public void testFailDefineWithZeroVersions() {
        final SchemaVersionResource resource = new SchemaVersionResource(stage);
        final SchemaVersionData defineData = SchemaVersionData.just(SchemaVersionSpecification, SchemaVersionDescription, "", SchemaVersionVersion000, SchemaVersionVersion000);
        final Response response1 = resource.defineWith(OrgId, UnitId, ContextId, SchemaId, defineData).await();
        assertEquals(BadRequest, response1.status);
        assertTrue(response1.entity.content().toLowerCase().contains("conflicting"));
        assertNull(response1.headers.headerOf(Location));
    }

    @Test
    public void testFailDefineWithHighLowVersions() {
        final SchemaVersionResource resource = new SchemaVersionResource(stage);
        final SchemaVersionData defineData = SchemaVersionData.just(SchemaVersionSpecification, SchemaVersionDescription, "", SchemaVersionVersion100, SchemaVersionVersion000);
        final Response response1 = resource.defineWith(OrgId, UnitId, ContextId, SchemaId, defineData).await();
        assertEquals(BadRequest, response1.status);
        assertTrue(response1.entity.content().toLowerCase().contains("conflicting"));
        assertNull(response1.headers.headerOf(Location));
    }

    @Test
    public void testFailDefineWithGappedVersions() {
        final SchemaVersionResource resource = new SchemaVersionResource(stage);
        final SchemaVersionData defineData = SchemaVersionData.just(SchemaVersionSpecification, SchemaVersionDescription, "", SchemaVersionVersion100, SchemaVersionVersion300);
        final Response response1 = resource.defineWith(OrgId, UnitId, ContextId, SchemaId, defineData).await();
        assertEquals(BadRequest, response1.status);
        assertTrue(response1.entity.content().toLowerCase().contains("conflicting"));
        assertNull(response1.headers.headerOf(Location));
    }
}
