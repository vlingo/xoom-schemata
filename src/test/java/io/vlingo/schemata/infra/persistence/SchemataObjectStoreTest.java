// Copyright © 2012-2018 Vaughn Vernon. All rights reserved.
//
// This Source Code Form is subject to the terms of the
// Mozilla Public License, v. 2.0. If a copy of the MPL
// was not distributed with this file, You can obtain
// one at https://mozilla.org/MPL/2.0/.

package io.vlingo.schemata.infra.persistence;

import io.vlingo.actors.World;
import io.vlingo.actors.testkit.AccessSafely;
import io.vlingo.actors.testkit.TestUntil;
import io.vlingo.common.Outcome;
import io.vlingo.lattice.model.object.ObjectTypeRegistry;
import io.vlingo.schemata.NoopDispatcher;
import io.vlingo.schemata.model.*;
import io.vlingo.schemata.model.Id.*;
import io.vlingo.schemata.model.SchemaVersion.Specification;
import io.vlingo.schemata.model.SchemaVersion.Version;
import io.vlingo.symbio.store.Result;
import io.vlingo.symbio.store.StorageException;
import io.vlingo.symbio.store.object.ListQueryExpression;
import io.vlingo.symbio.store.object.MapQueryExpression;
import io.vlingo.symbio.store.object.ObjectStore;
import io.vlingo.symbio.store.object.ObjectStoreReader.QueryMode;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.Arrays;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.Assert.*;

@SuppressWarnings({"rawtypes", "unchecked"})
public class SchemataObjectStoreTest {

    private static final AtomicInteger nameSequence = new AtomicInteger(0);

    private NoopDispatcher dispatcher;
    private ObjectStore objectStore;
    private World world;

    @Test
    public void testThatObjectStoreConnects() {
        assertNotNull(objectStore);
    }

    @Test
    public void testThatObjectStoreInsertsOrganizationStateAndQuerys() {
        final String orgName = "Vlingo-" + nameSequence.incrementAndGet();
        final OrganizationId orgId = OrganizationId.unique();
        final TestPersistResultInterest persistInterest = new TestPersistResultInterest();
        final AccessSafely access = persistInterest.afterCompleting(1);
        final OrganizationState organizationState = OrganizationState
                .from(orgId)
                .defineWith(orgName, "Organization Vlingo");

        objectStore.persist(organizationState, persistInterest);
        final Outcome<StorageException, Result> outcome = access.readFrom("outcome");
        assertEquals(Result.Success, outcome.andThen(success -> success).get());

        final TestQueryResultInterest queryInterest = new TestQueryResultInterest();

        // Map
        queryInterest.until = TestUntil.happenings(1);
        querySelect(queryInterest, OrganizationState.class, "TBL_ORGANIZATIONS", "organizationId", orgId.value);
        queryInterest.until.completes();
        assertNotNull(queryInterest.single());
        final OrganizationState insertedOrganizationState = (OrganizationState) queryInterest.single().stateObject;
        assertEquals(organizationState, insertedOrganizationState);

        // List
        queryInterest.until = TestUntil.happenings(1);
        objectStore.queryObject(
                ListQueryExpression.using(
                        OrganizationState.class,
                        "SELECT * FROM TBL_ORGANIZATIONS WHERE organizationId = <listArgValues>",
                        QueryMode.ReadUpdate,
                        Arrays.asList(orgId.value)),
                queryInterest);
        queryInterest.until.completes();
        assertNotNull(queryInterest.single());
        assertEquals(organizationState, queryInterest.single().stateObject);

        // update
        queryInterest.until = TestUntil.happenings(1);
        final OrganizationState updatedOrganizationState =
                insertedOrganizationState.withDescription("Organization Vlingo V2");

        objectStore.persist(updatedOrganizationState, persistInterest);
        querySelect(queryInterest, OrganizationState.class, "TBL_ORGANIZATIONS", "organizationId", orgId.value);


        queryInterest.until.completes();
        assertNotNull(queryInterest.single());
        assertEquals(updatedOrganizationState, queryInterest.single().stateObject);

        OrganizationState result = ((OrganizationState) queryInterest.single().stateObject);
        assertEquals(insertedOrganizationState.persistenceId(), result.persistenceId());
        assertEquals(updatedOrganizationState.description, result.description);
        assertEquals(insertedOrganizationState.name, result.name);
    }

    @Test
    public void testThatObjectStoreInsertsUnitStateAndQuerys() {
        final UnitId unitId = UnitId.uniqueFor(OrganizationId.unique());
        final TestPersistResultInterest persistInterest = new TestPersistResultInterest();
        final AccessSafely access = persistInterest.afterCompleting(1);
        final UnitState unitState = UnitState
                .from(unitId)
                .defineWith("Vlingo", "Unit Vlingo");

        objectStore.persist(unitState, persistInterest);
        final Outcome<StorageException, Result> outcome = access.readFrom("outcome");
        assertEquals(Result.Success, outcome.andThen(success -> success).get());

        final TestQueryResultInterest queryInterest = new TestQueryResultInterest();

        queryInterest.until = TestUntil.happenings(1);
        querySelect(queryInterest, UnitState.class, "TBL_UNITS", "unitId", unitId.value);
        queryInterest.until.completes();
        assertNotNull(queryInterest.single());
        final UnitState insertedUnitState = (UnitState) queryInterest.single().stateObject;
        assertEquals(unitState, insertedUnitState);

        // update
        queryInterest.until = TestUntil.happenings(1);
        final UnitState updatedUnitState =
                insertedUnitState.defineWith("VlingoV2", "Unit Vlingo V2");

        objectStore.persist(updatedUnitState, persistInterest);
        querySelect(queryInterest, UnitState.class, "TBL_UNITS", "unitId", unitId.value);

        queryInterest.until.completes();
        assertNotNull(queryInterest.single());
        assertEquals(updatedUnitState, queryInterest.single().stateObject);

        UnitState result = (UnitState) queryInterest.single().stateObject;
        assertEquals(insertedUnitState.persistenceId(), result.persistenceId());
        assertEquals(updatedUnitState.description, result.description);
        assertEquals(updatedUnitState.name, result.name);
    }

    @Test
    public void testThatObjectStoreInsertsContextStateAndQuerys() {
        final ContextId contextId = ContextId.uniqueFor(UnitId.uniqueFor(OrganizationId.unique()));
        final TestPersistResultInterest persistInterest = new TestPersistResultInterest();
        final AccessSafely access = persistInterest.afterCompleting(1);
        final ContextState contextState = ContextState
                .from(contextId)
                .defineWith("io.vlingo", "Context Vlingo");

        objectStore.persist(contextState, persistInterest);
        final Outcome<StorageException, Result> outcome = access.readFrom("outcome");
        assertEquals(Result.Success, outcome.andThen(success -> success).get());

        final TestQueryResultInterest queryInterest = new TestQueryResultInterest();

        queryInterest.until = TestUntil.happenings(1);
        querySelect(queryInterest, ContextState.class, "TBL_CONTEXTS", "contextId", contextId.value);
        queryInterest.until.completes();
        assertNotNull(queryInterest.single());
        final ContextState insertedContextState = (ContextState) queryInterest.single().stateObject;
        assertEquals(contextState, insertedContextState);

        // update
        queryInterest.until = TestUntil.happenings(1);
        final ContextState updatedContextState =
                insertedContextState.defineWith("io.vlingoV2", "Context Vlingo V2");

        objectStore.persist(updatedContextState, persistInterest);
        querySelect(queryInterest, ContextState.class, "TBL_CONTEXTS", "contextId", contextId.value);

        queryInterest.until.completes();
        assertNotNull(queryInterest.single());
        assertEquals(updatedContextState, queryInterest.single().stateObject);

        // assert updates
        final ContextState updated = (ContextState) queryInterest.single().stateObject;
        assertEquals(updatedContextState.contextId.value, updated.contextId.value);
        assertEquals(updatedContextState.namespace, updated.namespace);
        assertEquals(updatedContextState.description, updated.description);
        assertNotEquals(insertedContextState.namespace, updated.namespace);
        assertNotEquals(insertedContextState.description, updated.description);
    }

    @Test
    public void testThatObjectStoreInsertsSchemaStateAndQuerys() {
        final SchemaId schemaId =SchemaId.uniqueFor(ContextId.uniqueFor(UnitId.uniqueFor(OrganizationId.unique())));
        final TestPersistResultInterest persistInterest = new TestPersistResultInterest();
        final AccessSafely access = persistInterest.afterCompleting(1);
        final SchemaState schemaState = SchemaState.from(schemaId)
                .defineWith(Category.Event, Scope.Public, "Vlingo", "Schema Vlingo");

        objectStore.persist(schemaState, persistInterest);
        final Outcome<StorageException, Result> outcome = access.readFrom("outcome");
        assertEquals(Result.Success, outcome.andThen(success -> success).get());

        final SchemaState persisted = access.readFrom("persistentObject");

        final TestQueryResultInterest queryInterest = new TestQueryResultInterest();

        queryInterest.until = TestUntil.happenings(1);
        querySelect(queryInterest, SchemaState.class, "TBL_SCHEMAS", "schemaId", schemaId.value);
        queryInterest.until.completes();
        assertNotNull(queryInterest.single());
        final SchemaState insertedSchemaState = (SchemaState) queryInterest.single().stateObject;
        assertEquals(persisted.category, insertedSchemaState.category);
        assertEquals(persisted.description, insertedSchemaState.description);
        assertEquals(persisted.name, insertedSchemaState.name);
        assertEquals(persisted.scope, insertedSchemaState.scope);
        assertEquals(persisted.persistenceId(), insertedSchemaState.persistenceId());

        // update
        queryInterest.until = TestUntil.happenings(1);
        final SchemaState updatedSchemaState =
                insertedSchemaState.defineWith(Category.Document, Scope.Public, "VlingoV2", "Schema Vlingo V2");

        objectStore.persist(updatedSchemaState, persistInterest);
        querySelect(queryInterest, SchemaState.class, "TBL_SCHEMAS", "schemaId", schemaId.value);

        queryInterest.until.completes();
        assertNotNull(queryInterest.single());
        assertEquals(updatedSchemaState, queryInterest.single().stateObject);
        SchemaState result = (SchemaState) queryInterest.single().stateObject;
        assertEquals(insertedSchemaState.persistenceId(), result.persistenceId());
        assertEquals(updatedSchemaState.name, result.name);
        assertEquals(updatedSchemaState.description, result.description);
        assertEquals(updatedSchemaState.schemaId, result.schemaId);
        assertEquals(updatedSchemaState.category, result.category);
        assertEquals(updatedSchemaState.scope, result.scope);
    }

    @Test
    public void testThatObjectStoreInsertsSchemaVersionStateAndQuerys() {
        final SchemaVersionId schemaVersionId = SchemaVersionId.uniqueFor(SchemaId.uniqueFor(ContextId.uniqueFor(UnitId.uniqueFor(OrganizationId.unique()))));
        final TestPersistResultInterest persistInterest = new TestPersistResultInterest();
        final AccessSafely access = persistInterest.afterCompleting(1);
        final SchemaVersionState schemaVersionState = SchemaVersionState.from(schemaVersionId)
                .defineWith(
                        "Schema Version Vlingo",
                        Specification.of("Spec"),
                        Version.of("0.0.0"),
                        Version.of("1.0.0"));

        objectStore.persist(schemaVersionState, persistInterest);
        final Outcome<StorageException, Result> outcome = access.readFrom("outcome");
        assertEquals(Result.Success, outcome.andThen(success -> success).get());

        final TestQueryResultInterest queryInterest = new TestQueryResultInterest();

        queryInterest.until = TestUntil.happenings(1);
        querySelect(queryInterest, SchemaVersionState.class, "TBL_SCHEMAVERSIONS", "schemaVersionId", schemaVersionId.value);
        queryInterest.until.completes();
        assertNotNull(queryInterest.single());
        final SchemaVersionState insertedSchemaVersionState = (SchemaVersionState) queryInterest.single().stateObject;
        assertEquals(schemaVersionState, insertedSchemaVersionState);

        // update
        queryInterest.until = TestUntil.happenings(1);
        final SchemaVersionState updatedSchemaVersionState =
                insertedSchemaVersionState.defineWith("Schema Version Vlingo V2", Specification.of("SpecV2"), Version.of("1.0.0"), Version.of("2.0.0"));

        objectStore.persist(updatedSchemaVersionState, persistInterest);
        querySelect(queryInterest, SchemaVersionState.class, "TBL_SCHEMAVERSIONS", "schemaVersionId", schemaVersionId.value);

        queryInterest.until.completes();
        assertNotNull(queryInterest.single());
        SchemaVersionState result = (SchemaVersionState) queryInterest.single().stateObject;
        assertEquals(insertedSchemaVersionState.persistenceId(), result.persistenceId());
        assertEquals(updatedSchemaVersionState.description, result.description);
        assertEquals(updatedSchemaVersionState.specification.value, result.specification.value);
        assertEquals(updatedSchemaVersionState.currentVersion.value, result.currentVersion.value);
        assertEquals(updatedSchemaVersionState.previousVersion.value, result.previousVersion.value);
    }

    @Before
    public void setUp() throws Exception {
        world = World.startWithDefaults("test-store");

        dispatcher = new NoopDispatcher();

        final SchemataObjectStore schemataObjectStore = SchemataObjectStore.instance("dev");
        objectStore = schemataObjectStore.objectStoreFor(world, dispatcher, schemataObjectStore.persistentMappers());
        final ObjectTypeRegistry registry = new ObjectTypeRegistry(world);
        schemataObjectStore.register(registry, objectStore);
    }

    @After
    public void tearDown() {
        objectStore.close();
        world.terminate();
    }

    private void querySelect(final TestQueryResultInterest queryInterest, Class<?> type, final String table, String idColumn, String idValue) {
        objectStore.queryObject(
                MapQueryExpression.using(
                        type,
                        "SELECT * FROM " + table + " WHERE " + idColumn + " = :idValue",
                        MapQueryExpression.map("idValue", idValue)),
                queryInterest);
        ;
    }

    private void querySelect(final TestQueryResultInterest queryInterest, Class<?> type, final String name, final long id) {
        objectStore.queryObject(
                MapQueryExpression.using(
                        type,
                        "SELECT * FROM " + name + " WHERE id = :id",
                        MapQueryExpression.map("id", id)),
                queryInterest);
    }
}
