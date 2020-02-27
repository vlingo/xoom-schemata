// Copyright © 2012-2020 VLINGO LABS. All rights reserved.
//
// This Source Code Form is subject to the terms of the
// Mozilla Public License, v. 2.0. If a copy of the MPL
// was not distributed with this file, You can obtain
// one at https://mozilla.org/MPL/2.0/.

package io.vlingo.schemata.resource;

import static io.vlingo.http.Response.Status.Ok;
import static io.vlingo.schemata.model.SchemaVersion.Status.Draft;
import static org.junit.Assert.assertEquals;

import java.util.List;

import org.junit.Ignore;
import org.junit.Test;

import io.vlingo.common.serialization.JsonSerialization;
import io.vlingo.http.Response;
import io.vlingo.schemata.model.Category;
import io.vlingo.schemata.model.Scope;
import io.vlingo.schemata.resource.data.ContextData;
import io.vlingo.schemata.resource.data.OrganizationData;
import io.vlingo.schemata.resource.data.SchemaData;
import io.vlingo.schemata.resource.data.SchemaVersionData;
import io.vlingo.schemata.resource.data.UnitData;

public class ExtendedResourceTest extends ResourceTest {
    private final OrganizationData orgData1 = OrganizationData.just("Org1", "Org1 description");
    private final UnitData unitData10 = UnitData.just("Unit1_0", "Unit1_a description");
    private final UnitData unitData11 = UnitData.just("Unit1_1b", "Unit1_1b description!");
    private final ContextData contextData10 = ContextData.just("io.vlingo.schemata" , "Namespace1 descrption");
    private final ContextData contextData11 = ContextData.just("io.vlingo.lattice" , "namespace 1b descrption");
    private final SchemaData schemaData10 = SchemaData.just(Category.Event.name(), Scope.Public.name(), "SchemaDefined1", "SchemaDefined description");
    private final SchemaData schemaData11 = SchemaData.just(Category.Event.name(), Scope.Public.name(), "SchemaDefined1_1", "SchemaDefined description");
    private final SchemaVersionData schemaVersionData10 = SchemaVersionData.just("event SchemaDefined {}", "SchemaDefined description",
            Draft.name(), "0.0.0", "0.0.2");
    private final SchemaVersionData schemaVersionData10_2 = SchemaVersionData.just("event SchemaDefined {}", "SchemaDefined description",
            Draft.name(), "0.0.2", "0.0.3");
    private final SchemaVersionData schemaVersionData11 = SchemaVersionData.just("event SchemaDefined {}", "SchemaDefined description",
            Draft.name(), "0.0.0", "0.0.7");

    private final OrganizationData orgData2 = OrganizationData.just("org2", "org2 description");
    private final UnitData unitData2 = UnitData.just("Unit_2", "Unit222 description");
    private final ContextData contextData2 = ContextData.just("io.vlingo.http" , "Namespace HTTP!");
    private final SchemaData schemaData2 = SchemaData.just(Category.Event.name(), Scope.Public.name(), "SchemaDefined2_", "Second SchemaDefined");
    private final SchemaVersionData schemaVersionData2 = SchemaVersionData.just("event SchemaDefined2 {}", "SchemaDefined 2 description",
            Draft.name(), "0.0.0", "1.0.11");

    private final OrganizationData orgData3 = OrganizationData.just("_org 3", "_org 3 description");

    /**
     *
     * @return An array with two schemaVersionIds created.
     */
    private String[] createFixture1() {
        final OrganizationResource organizationResource = new OrganizationResource(world);
        final Response organizationResponse = organizationResource.defineWith(orgData1).await();
        final String organizationId1 = extractResourceIdFrom(organizationResponse);

        final UnitResource unitResource = new UnitResource(world);
        final Response unitResponse10 = unitResource.defineWith(organizationId1, unitData10).await();
        final String unitId10 = extractResourceIdFrom(unitResponse10);
        final Response unitResponse11 = unitResource.defineWith(organizationId1, unitData11).await();
        final String unitId11 = extractResourceIdFrom(unitResponse11);

        final ContextResource contextResource = new ContextResource(world);
        final Response contextResponse10 = contextResource.defineWith(organizationId1, unitId10, contextData10).await();
        final String contextId10 = extractResourceIdFrom(contextResponse10);
        final Response contextResponse11 = contextResource.defineWith(organizationId1, unitId11, contextData11).await();
        final String contextId11 = extractResourceIdFrom(contextResponse11);

        final SchemaResource schemaResource = new SchemaResource(world);
        final Response schemaResponse10 = schemaResource.defineWith(organizationId1, unitId10, contextId10, schemaData10).await();
        final String schemaId10 = extractResourceIdFrom(schemaResponse10);
        final Response schemaResponse11 = schemaResource.defineWith(organizationId1, unitId11, contextId11, schemaData11).await();
        final String schemaId11 = extractResourceIdFrom(schemaResponse11);

        final SchemaVersionResource schemaVersionResource = new SchemaVersionResource(world);
        final Response schemaVersionResponse10 = schemaVersionResource.defineWith(organizationId1, unitId10, contextId10, schemaId10, schemaVersionData10).await();
        final String schemaVersionId10 = extractResourceIdFrom(schemaVersionResponse10);
        final Response schemaVersionResponse10_2 = schemaVersionResource.defineWith(organizationId1, unitId10, contextId10, schemaId10, schemaVersionData10_2).await();
        final String schemaVersionId10_2 = extractResourceIdFrom(schemaVersionResponse10_2);
        final Response schemaVersionResponse11 = schemaVersionResource.defineWith(organizationId1, unitId11, contextId11, schemaId11, schemaVersionData11).await();
        final String schemaVersionId11 = extractResourceIdFrom(schemaVersionResponse11);

        return new String[] {schemaVersionId10, schemaVersionId10_2, schemaVersionId11};
    }

    /**
     *
     * @return schemaVersionId for newly created {@link SchemaVersionData}
     */
    private String createFixture2() {
        final OrganizationResource organizationResource = new OrganizationResource(world);
        final Response organizationResponse2 = organizationResource.defineWith(orgData2).await();
        final String organizationId2 = extractResourceIdFrom(organizationResponse2);

        final UnitResource unitResource = new UnitResource(world);
        final Response unitResponse2 = unitResource.defineWith(organizationId2, unitData2).await();
        final String unitId2 = extractResourceIdFrom(unitResponse2);

        final ContextResource contextResource = new ContextResource(world);
        final Response contextResponse2 = contextResource.defineWith(organizationId2, unitId2, contextData2).await();
        final String contextId2 = extractResourceIdFrom(contextResponse2);

        final SchemaResource schemaResource = new SchemaResource(world);
        final Response schemaResponse2 = schemaResource.defineWith(organizationId2, unitId2, contextId2, schemaData2).await();
        final String schemaId2 = extractResourceIdFrom(schemaResponse2);

        final SchemaVersionResource schemaVersionResource = new SchemaVersionResource(world);
        final Response schemaVersionResponse2 = schemaVersionResource.defineWith(organizationId2, unitId2, contextId2, schemaId2, schemaVersionData2).await();
        final String schemaVersionId2 = extractResourceIdFrom(schemaVersionResponse2);

        return schemaVersionId2;
    }

    /**
     * Creates an organization w/o sub resources.
     */
    private void createFixture3() {
        final OrganizationResource organizationResource = new OrganizationResource(world);
        final Response organizationResponse3 = organizationResource.defineWith(orgData3).await();
        @SuppressWarnings("unused")
        final String organizationId3 = extractResourceIdFrom(organizationResponse3);
    }

    @Test
    public void testSchemaVersionSelection() {
        String[] schemaVersion1Ids = createFixture1();
        String schemaVersion2Id = createFixture2();
        createFixture3();

        // Select first schemaVersion
        SchemaVersionResource resource = new SchemaVersionResource(world);
        final Response response10 = resource.searchSchemaVersionByNames(orgData1.name, unitData10.name, contextData10.namespace, schemaData10.name,
                schemaVersionData10.currentVersion).await();
        assertEquals(Ok, response10.status);
        final SchemaVersionData schemaVersionDataResponse10 = JsonSerialization.deserialized(response10.entity.content(), SchemaVersionData.class);
        assertEquals(schemaVersionDataResponse10.status, schemaVersionData10.status);
        assertEquals(schemaVersionDataResponse10.description, schemaVersionData10.description);
        assertEquals(schemaVersionDataResponse10.specification, schemaVersionData10.specification);
        assertEquals(schemaVersionDataResponse10.currentVersion, schemaVersionData10.currentVersion);
        assertEquals(schemaVersionDataResponse10.previousVersion, schemaVersionData10.previousVersion);
        assertEquals(schemaVersionDataResponse10.schemaVersionId, schemaVersion1Ids[0]);

        // Select second schema version
        final Response response10_2 = resource.searchSchemaVersionByNames(orgData1.name, unitData10.name, contextData10.namespace, schemaData10.name,
                schemaVersionData10_2.currentVersion).await();
        assertEquals(Ok, response10_2.status);
        final SchemaVersionData schemaVersionDataResponse10_2 = JsonSerialization.deserialized(response10_2.entity.content(), SchemaVersionData.class);
        assertEquals(schemaVersionDataResponse10_2.status, schemaVersionData10_2.status);
        assertEquals(schemaVersionDataResponse10_2.description, schemaVersionData10_2.description);
        assertEquals(schemaVersionDataResponse10_2.specification, schemaVersionData10_2.specification);
        assertEquals(schemaVersionDataResponse10_2.currentVersion, schemaVersionData10_2.currentVersion);
        assertEquals(schemaVersionDataResponse10_2.previousVersion, schemaVersionData10_2.previousVersion);
        assertEquals(schemaVersionDataResponse10_2.schemaVersionId, schemaVersion1Ids[1]);

        final Response response10all = resource.searchSchemaVersionsByNames(orgData1.name, unitData10.name, contextData10.namespace, schemaData10.name).await();
        assertEquals(Ok, response10all.status);
        final List<?> schemaVersionDataResponse1all = JsonSerialization.deserialized(response10all.entity.content(), List.class);
        assertEquals(2, schemaVersionDataResponse1all.size());

        // Select third schema version
        final Response response11 = resource.searchSchemaVersionByNames(orgData1.name, unitData11.name, contextData11.namespace, schemaData11.name,
                schemaVersionData11.currentVersion).await();
        assertEquals(Ok, response11.status);
        final SchemaVersionData schemaVersionDataResponse11 = JsonSerialization.deserialized(response11.entity.content(), SchemaVersionData.class);
        assertEquals(schemaVersionDataResponse11.status, schemaVersionData11.status);
        assertEquals(schemaVersionDataResponse11.description, schemaVersionData11.description);
        assertEquals(schemaVersionDataResponse11.specification, schemaVersionData11.specification);
        assertEquals(schemaVersionDataResponse11.currentVersion, schemaVersionData11.currentVersion);
        assertEquals(schemaVersionDataResponse11.previousVersion, schemaVersionData11.previousVersion);
        assertEquals(schemaVersionDataResponse11.schemaVersionId, schemaVersion1Ids[2]);

//        // Select no schema version based on mixed values
//        final Response response01 = resource.searchSchemaVersionByNames(orgData1.name, unitData10.name, contextData11.namespace, schemaData10.name,
//                schemaVersionData11.currentVersion).await();
//        assertEquals(NotFound, response01.status);
//
//        // Select no schema version based on mixed values
//        final Response response02 = resource.searchSchemaVersionByNames(orgData1.name, unitData11.name, contextData10.namespace, schemaData11.name,
//                schemaVersionData10.currentVersion).await();
//        assertEquals(NotFound, response02.status);

        final Response response2 = resource.searchSchemaVersionByNames(orgData2.name, unitData2.name, contextData2.namespace, schemaData2.name,
                schemaVersionData2.currentVersion).await();

        assertEquals(Ok, response2.status);
        final SchemaVersionData schemaVersionDataResponse2 = JsonSerialization.deserialized(response2.entity.content(), SchemaVersionData.class);
        assertEquals(schemaVersionDataResponse2.status, schemaVersionData2.status);
        assertEquals(schemaVersionDataResponse2.description, schemaVersionData2.description);
        assertEquals(schemaVersionDataResponse2.specification, schemaVersionData2.specification);
        assertEquals(schemaVersionDataResponse2.currentVersion, schemaVersionData2.currentVersion);
        assertEquals(schemaVersionDataResponse2.previousVersion, schemaVersionData2.previousVersion);
        assertEquals(schemaVersionDataResponse2.schemaVersionId, schemaVersion2Id);
    }
}
