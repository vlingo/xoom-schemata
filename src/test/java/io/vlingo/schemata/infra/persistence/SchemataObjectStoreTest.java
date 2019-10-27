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
import io.vlingo.schemata.model.SchemaVersion.Status;
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

        final TestPersistResultInterest persistInterest = new TestPersistResultInterest();
        final AccessSafely access = persistInterest.afterCompleting(1);
        final OrganizationState organizationState = OrganizationState.from(
                1L,
                OrganizationId.existing("A343"),
                orgName, "Organization Vlingo");
        objectStore.persist(organizationState, persistInterest);
        final Outcome<StorageException, Result> outcome = access.readFrom("outcome");
        assertEquals(Result.Success, outcome.andThen(success -> success).get());

        final TestQueryResultInterest queryInterest = new TestQueryResultInterest();

        // Map
        queryInterest.until = TestUntil.happenings(1);
        querySelect(queryInterest, OrganizationState.class, "TBL_ORGANIZATIONS");
        queryInterest.until.completes();
        assertNotNull(queryInterest.singleResult.get());
        final OrganizationState insertedOrganizationState = (OrganizationState) queryInterest.singleResult.get().stateObject;
        assertEquals(organizationState, insertedOrganizationState);

        // List
        queryInterest.until = TestUntil.happenings(1);
        objectStore.queryObject(
                ListQueryExpression.using(
                        OrganizationState.class,
                        "SELECT * FROM TBL_ORGANIZATIONS WHERE id = <listArgValues>",
                        QueryMode.ReadUpdate,
                        Arrays.asList(1L)),
                queryInterest);
        queryInterest.until.completes();
        assertNotNull(queryInterest.singleResult.get());
        assertEquals(organizationState, queryInterest.singleResult.get().stateObject);

        // update
        queryInterest.until = TestUntil.happenings(1);
        final OrganizationState updatedOrganizationState =
                insertedOrganizationState.withDescription("Organization Vlingo V2");

        objectStore.persist(updatedOrganizationState, queryInterest.singleResult.get().updateId, persistInterest);
        querySelect(queryInterest, OrganizationState.class, "TBL_ORGANIZATIONS");

        queryInterest.until.completes();
        assertNotNull(queryInterest.singleResult.get());
        OrganizationState result = ((OrganizationState) queryInterest.singleResult.get().stateObject);
        assertEquals(insertedOrganizationState.persistenceId(), result.persistenceId());
        assertEquals(updatedOrganizationState.description, result.description);
        assertEquals(insertedOrganizationState.name, result.name);
    }

    @Test
    public void testThatObjectStoreInsertsUnitStateAndQuerys() {
        final TestPersistResultInterest persistInterest = new TestPersistResultInterest();
        final AccessSafely access = persistInterest.afterCompleting(1);
        final UnitState unitState = UnitState.from(
                1L,
                UnitId.existing("A343:U44"),
                "Vlingo", "Unit Vlingo");
        objectStore.persist(unitState, persistInterest);
        final Outcome<StorageException, Result> outcome = access.readFrom("outcome");
        assertEquals(Result.Success, outcome.andThen(success -> success).get());

        final TestQueryResultInterest queryInterest = new TestQueryResultInterest();

        queryInterest.until = TestUntil.happenings(1);
        querySelect(queryInterest, UnitState.class, "TBL_UNITS");
        queryInterest.until.completes();
        assertNotNull(queryInterest.singleResult.get());
        final UnitState insertedUnitState = (UnitState) queryInterest.singleResult.get().stateObject;
        assertEquals(unitState, insertedUnitState);

        // update
        queryInterest.until = TestUntil.happenings(1);
        final UnitState updatedUnitState =
                insertedUnitState.defineWith("VlingoV2", "Unit Vlingo V2");

        objectStore.persist(updatedUnitState, insertedUnitState.persistenceId(), persistInterest);
        querySelect(queryInterest, UnitState.class, "TBL_UNITS");

        queryInterest.until.completes();
        assertNotNull(queryInterest.singleResult.get());

        UnitState result = (UnitState) queryInterest.singleResult.get().stateObject;
        assertEquals(insertedUnitState.persistenceId(), result.persistenceId());
        assertEquals(updatedUnitState.description, result.description);
        assertEquals(updatedUnitState.name, result.name);
    }

    @Test
    public void testThatObjectStoreInsertsContextStateAndQuerys() {
        final TestPersistResultInterest persistInterest = new TestPersistResultInterest();
        final AccessSafely access = persistInterest.afterCompleting(1);
        final ContextState contextState = ContextState.from(
                1L,
                ContextId.existing("A343:U44:C13"),
                "io.vlingo", "Context Vlingo");
        objectStore.persist(contextState, persistInterest);
        final Outcome<StorageException, Result> outcome = access.readFrom("outcome");
        assertEquals(Result.Success, outcome.andThen(success -> success).get());

        final TestQueryResultInterest queryInterest = new TestQueryResultInterest();

        queryInterest.until = TestUntil.happenings(1);
        querySelect(queryInterest, ContextState.class, "TBL_CONTEXTS");
        queryInterest.until.completes();
        assertNotNull(queryInterest.singleResult.get());
        final ContextState insertedContextState = (ContextState) queryInterest.singleResult.get().stateObject;
        assertEquals(contextState, insertedContextState);

        // update
        queryInterest.until = TestUntil.happenings(1);
        final ContextState updatedContextState =
                insertedContextState.defineWith("io.vlingoV2", "Context Vlingo V2");

        objectStore.persist(updatedContextState, contextState.persistenceId(), persistInterest);
        querySelect(queryInterest, ContextState.class, "TBL_CONTEXTS");

        queryInterest.until.completes();
        assertNotNull(queryInterest.singleResult.get());

        // assert updates
        final ContextState updated = (ContextState) queryInterest.singleResult.get().stateObject;
        assertEquals(updatedContextState.contextId.value, updated.contextId.value);
        assertEquals(updatedContextState.namespace, updated.namespace);
        assertEquals(updatedContextState.description, updated.description);
        assertNotEquals(insertedContextState.namespace, updated.namespace);
        assertNotEquals(insertedContextState.description, updated.description);
    }

    @Test
    public void testThatObjectStoreInsertsSchemaStateAndQuerys() {
        final TestPersistResultInterest persistInterest = new TestPersistResultInterest();
        final AccessSafely access = persistInterest.afterCompleting(1);
        final SchemaState schemaState = SchemaState.from(
                1L,
                SchemaId.undefined(),
                Category.Event, Scope.Public, "Vlingo", "Schema Vlingo");
        objectStore.persist(schemaState, persistInterest);
        final Outcome<StorageException, Result> outcome = access.readFrom("outcome");
        assertEquals(Result.Success, outcome.andThen(success -> success).get());

        final SchemaState persisted = access.readFrom("persistentObject");

        final TestQueryResultInterest queryInterest = new TestQueryResultInterest();

        queryInterest.until = TestUntil.happenings(1);
        querySelect(queryInterest, SchemaState.class, "TBL_SCHEMAS", persisted.persistenceId());
        queryInterest.until.completes();

        assertNotNull(queryInterest.singleResult.get());
        final SchemaState insertedSchemaState = (SchemaState) queryInterest.singleResult.get().stateObject;
        assertEquals(persisted.category, insertedSchemaState.category);
        assertEquals(persisted.description, insertedSchemaState.description);
        assertEquals(persisted.name, insertedSchemaState.name);
        assertEquals(persisted.scope, insertedSchemaState.scope);
        assertEquals(persisted.persistenceId(), insertedSchemaState.persistenceId());

        // update
        queryInterest.until = TestUntil.happenings(1);
        final SchemaState updatedSchemaState =
                insertedSchemaState.defineWith(Category.Document, Scope.Public, "VlingoV2", "Schema Vlingo V2");

        objectStore.persist(updatedSchemaState, insertedSchemaState.persistenceId(), persistInterest);
        querySelect(queryInterest, SchemaState.class, "TBL_SCHEMAS", insertedSchemaState.persistenceId());

        queryInterest.until.completes();
        assertNotNull(queryInterest.singleResult.get());
        SchemaState result = (SchemaState) queryInterest.singleResult.get().stateObject;
        assertEquals(insertedSchemaState.persistenceId(), result.persistenceId());
        assertEquals(updatedSchemaState.name, result.name);
        assertEquals(updatedSchemaState.description, result.description);
        assertEquals(updatedSchemaState.schemaId, result.schemaId);
        assertEquals(updatedSchemaState.category, result.category);
        assertEquals(updatedSchemaState.scope, result.scope);
    }

    @Test
    public void testThatObjectStoreInsertsSchemaVersionStateAndQuerys() {
        final TestPersistResultInterest persistInterest = new TestPersistResultInterest();
        final AccessSafely access = persistInterest.afterCompleting(1);
        final SchemaVersionState schemaVersionState = SchemaVersionState.from(
                1L,
                SchemaVersionId.existing("A343:U44:C13:S78:SV778"),
                Specification.of("Spec"),
                "Schema Version Vlingo",
                Status.Draft,
                Version.of("0.0.0"),
                Version.of("1.0.0"));
        objectStore.persist(schemaVersionState, persistInterest);
        final Outcome<StorageException, Result> outcome = access.readFrom("outcome");
        assertEquals(Result.Success, outcome.andThen(success -> success).get());

        final TestQueryResultInterest queryInterest = new TestQueryResultInterest();

        queryInterest.until = TestUntil.happenings(1);
        querySelect(queryInterest, SchemaVersionState.class, "TBL_SCHEMAVERSIONS");
        queryInterest.until.completes();
        assertNotNull(queryInterest.singleResult.get());
        final SchemaVersionState insertedSchemaVersionState = (SchemaVersionState) queryInterest.singleResult.get().stateObject;
        assertEquals(schemaVersionState, insertedSchemaVersionState);

        // update
        queryInterest.until = TestUntil.happenings(1);
        final SchemaVersionState updatedSchemaVersionState =
                insertedSchemaVersionState.defineWith("Schema Version Vlingo V2", Specification.of("SpecV2"), Version.of("1.0.0"), Version.of("2.0.0"));

        objectStore.persist(updatedSchemaVersionState, insertedSchemaVersionState.persistenceId(), persistInterest);
        querySelect(queryInterest, SchemaVersionState.class, "TBL_SCHEMAVERSIONS");

        queryInterest.until.completes();
        assertNotNull(queryInterest.singleResult.get());
        SchemaVersionState result = (SchemaVersionState) queryInterest.singleResult.get().stateObject;
        assertEquals(insertedSchemaVersionState.persistenceId(), result.persistenceId());
        assertEquals(updatedSchemaVersionState.description, result.description);
        assertEquals(updatedSchemaVersionState.specification.value, result.specification.value);
        assertEquals(updatedSchemaVersionState.currentVersion.value, result.currentVersion.value);
        assertEquals(updatedSchemaVersionState.previousVersion.value, result.previousVersion.value);
    }

    @Test
    public void testThatObjectStoreCanUpdateOrganization() {
        final OrganizationState state = OrganizationState
                .from(OrganizationId.unique())
                .defineWith("name", "description");

        final TestPersistResultInterest persistInterest = new TestPersistResultInterest();
        final AccessSafely access = persistInterest.afterCompleting(1);
        objectStore.persist(state, persistInterest);

        final TestQueryResultInterest queryInterest = new TestQueryResultInterest();
        queryInterest.until = TestUntil.happenings(1);
        querySelect(queryInterest, OrganizationState.class, "TBL_ORGANIZATIONS", "organizationId", state.organizationId.value);
        queryInterest.until.completes();
        assertNotNull(queryInterest.singleResult.get());

        final OrganizationState insertedState = (OrganizationState) queryInterest.singleResult.get().stateObject;
        assertEquals(state.name, insertedState.name);
        assertEquals(state.description, insertedState.description);

        OrganizationState updatedState = insertedState.defineWith("updated name", "updated description");
        objectStore.persist(updatedState, insertedState.persistenceId(), persistInterest);

        queryInterest.until = TestUntil.happenings(1);
        querySelect(queryInterest, OrganizationState.class, "TBL_ORGANIZATIONS", insertedState.persistenceId());
        queryInterest.until.completes();
        assertNotNull(queryInterest.singleResult.get());

        OrganizationState result = ((OrganizationState) queryInterest.singleResult.get().stateObject);
        assertEquals(insertedState.persistenceId(), result.persistenceId());
        assertEquals(updatedState.description, result.description);
        assertEquals(updatedState.name, result.name);
    }

    @Test
    public void testThatObjectStoreCanUpdateUnit() {
        final UnitState state = UnitState
                .from(UnitId.uniqueFor(OrganizationId.unique()))
                .defineWith("name", "description");

        final TestPersistResultInterest persistInterest = new TestPersistResultInterest();
        final AccessSafely access = persistInterest.afterCompleting(1);
        objectStore.persist(state, persistInterest);

        final TestQueryResultInterest queryInterest = new TestQueryResultInterest();
        queryInterest.until = TestUntil.happenings(1);
        querySelect(queryInterest, UnitState.class, "TBL_UNITS", "unitId", state.unitId.value);
        queryInterest.until.completes();
        assertNotNull(queryInterest.singleResult.get());

        final UnitState insertedState = (UnitState) queryInterest.singleResult.get().stateObject;
        assertEquals(state.name, insertedState.name);
        assertEquals(state.description, insertedState.description);

        UnitState updatedState = insertedState.defineWith("updated name", "updated description");
        objectStore.persist(updatedState, insertedState.persistenceId(), persistInterest);

        queryInterest.until = TestUntil.happenings(1);
        querySelect(queryInterest, UnitState.class, "TBL_UNITS", insertedState.persistenceId());
        queryInterest.until.completes();
        assertNotNull(queryInterest.singleResult.get());

        UnitState result = ((UnitState) queryInterest.singleResult.get().stateObject);
        assertEquals(insertedState.persistenceId(), result.persistenceId());
        assertEquals(updatedState.description, result.description);
        assertEquals(updatedState.name, result.name);
    }

    @Test
    public void testThatObjectStoreCanUpdateContext() {
        final ContextState state = ContextState
                .from(ContextId.uniqueFor(UnitId.uniqueFor(OrganizationId.unique())))
                .defineWith("namespace", "description");

        final TestPersistResultInterest persistInterest = new TestPersistResultInterest();
        final AccessSafely access = persistInterest.afterCompleting(1);
        objectStore.persist(state, persistInterest);

        final TestQueryResultInterest queryInterest = new TestQueryResultInterest();
        queryInterest.until = TestUntil.happenings(1);
        querySelect(queryInterest, ContextState.class, "TBL_CONTEXTS", "contextId", state.contextId.value);
        queryInterest.until.completes();
        assertNotNull(queryInterest.singleResult.get());

        final ContextState insertedState = (ContextState) queryInterest.singleResult.get().stateObject;
        assertEquals(state.namespace, insertedState.namespace);
        assertEquals(state.description, insertedState.description);

        ContextState updatedState = insertedState.defineWith("updated namespace", "updated description");
        objectStore.persist(updatedState, insertedState.persistenceId(), persistInterest);

        queryInterest.until = TestUntil.happenings(1);
        querySelect(queryInterest, ContextState.class, "TBL_CONTEXTS", insertedState.persistenceId());
        queryInterest.until.completes();
        assertNotNull(queryInterest.singleResult.get());

        ContextState result = ((ContextState) queryInterest.singleResult.get().stateObject);
        assertEquals(insertedState.persistenceId(), result.persistenceId());
        assertEquals(updatedState.description, result.description);
        assertEquals(updatedState.namespace, result.namespace);
    }

    @Test
    public void testThatObjectStoreCanUpdateSchema() {
        final SchemaState state = SchemaState.from(
                SchemaId.uniqueFor(ContextId.uniqueFor(UnitId.uniqueFor(OrganizationId.unique()))))
                .defineWith(
                        Category.Unknown,
                        Scope.Private,
                        "name",
                        "description");

        final TestPersistResultInterest persistInterest = new TestPersistResultInterest();
        final AccessSafely access = persistInterest.afterCompleting(1);
        objectStore.persist(state, persistInterest);

        final TestQueryResultInterest queryInterest = new TestQueryResultInterest();
        queryInterest.until = TestUntil.happenings(1);
        querySelect(queryInterest, SchemaState.class, "TBL_SCHEMAS", "schemaId", state.schemaId.value);
        queryInterest.until.completes();
        assertNotNull(queryInterest.singleResult.get());

        final SchemaState insertedState = (SchemaState) queryInterest.singleResult.get().stateObject;
        assertEquals(state.schemaId.value, insertedState.schemaId.value);
        assertEquals(state.category, insertedState.category);
        assertEquals(state.scope, insertedState.scope);
        assertEquals(state.name, insertedState.name);
        assertEquals(state.description, insertedState.description);

        SchemaState updatedState = insertedState
                .withScope(Scope.Public)
                .withCategory(Category.Data)
                .withDescription("updated description")
                .withName("updated name");

        objectStore.persist(updatedState, insertedState.persistenceId(), persistInterest);

        queryInterest.until = TestUntil.happenings(1);
        querySelect(queryInterest, SchemaState.class, "TBL_SCHEMAS", insertedState.persistenceId());
        queryInterest.until.completes();
        assertNotNull(queryInterest.singleResult.get());

        SchemaState result = ((SchemaState) queryInterest.singleResult.get().stateObject);
        assertEquals(insertedState.persistenceId(), result.persistenceId());
        assertEquals(updatedState.schemaId.value, result.schemaId.value);
        assertEquals(updatedState.category, result.category);
        assertEquals(updatedState.scope, result.scope);
        assertEquals(updatedState.name, result.name);
        assertEquals(updatedState.description, result.description);
    }

    @Test
    public void testThatObjectStoreCanUpdateSchemaVersion() {
        final SchemaVersionState state = SchemaVersionState.from(
                SchemaVersionId.uniqueFor(SchemaId.uniqueFor(ContextId.uniqueFor(UnitId.uniqueFor(OrganizationId.unique())))))
                .defineWith(
                        "description",
                        Specification.of("spec"),
                        Version.of("0.0.0"),
                        Version.of("0.0.1"));

        final TestPersistResultInterest persistInterest = new TestPersistResultInterest();
        final AccessSafely access = persistInterest.afterCompleting(1);
        objectStore.persist(state, persistInterest);

        final TestQueryResultInterest queryInterest = new TestQueryResultInterest();
        queryInterest.until = TestUntil.happenings(1);
        querySelect(queryInterest, SchemaVersionState.class, "TBL_SCHEMAVERSIONS", "schemaVersionId", state.schemaVersionId.value);
        queryInterest.until.completes();
        assertNotNull(queryInterest.singleResult.get());

        final SchemaVersionState insertedState = (SchemaVersionState) queryInterest.singleResult.get().stateObject;
        assertEquals(state.schemaVersionId.value, insertedState.schemaVersionId.value);
        assertEquals(state.status, insertedState.status);
        assertEquals(state.previousVersion.value, insertedState.previousVersion.value);
        assertEquals(state.currentVersion.value, insertedState.currentVersion.value);
        assertEquals(state.description, insertedState.description);
        assertEquals(state.specification.value, insertedState.specification.value);

        SchemaVersionState updatedState = insertedState
                .withDescription("updated description")
                .withSpecification(Specification.of("updated spec"))
                .asPublished();
        objectStore.persist(updatedState, insertedState.persistenceId(), persistInterest);

        queryInterest.until = TestUntil.happenings(1);
        querySelect(queryInterest, SchemaVersionState.class, "TBL_SCHEMAVERSIONS", insertedState.persistenceId());
        queryInterest.until.completes();
        assertNotNull(queryInterest.singleResult.get());

        SchemaVersionState result = ((SchemaVersionState) queryInterest.singleResult.get().stateObject);
        assertEquals(insertedState.persistenceId(), result.persistenceId());
        assertEquals(updatedState.schemaVersionId.value, result.schemaVersionId.value);
        assertEquals(updatedState.previousVersion.value, result.previousVersion.value);
        assertEquals(updatedState.currentVersion.value, result.currentVersion.value);
        assertEquals(updatedState.description, result.description);
        assertEquals(updatedState.specification.value, result.specification.value);
        assertEquals(updatedState.status, result.status);
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

    private void querySelect(final TestQueryResultInterest queryInterest, Class<?> type, final String name) {
        querySelect(queryInterest, type, name, 1L);
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
