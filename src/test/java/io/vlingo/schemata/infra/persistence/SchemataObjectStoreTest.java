// Copyright © 2012-2018 Vaughn Vernon. All rights reserved.
//
// This Source Code Form is subject to the terms of the
// Mozilla Public License, v. 2.0. If a copy of the MPL
// was not distributed with this file, You can obtain
// one at https://mozilla.org/MPL/2.0/.

package io.vlingo.schemata.infra.persistence;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNotNull;

import java.util.Arrays;
import java.util.concurrent.atomic.AtomicInteger;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import io.vlingo.actors.World;
import io.vlingo.actors.testkit.AccessSafely;
import io.vlingo.actors.testkit.TestUntil;
import io.vlingo.common.Outcome;
import io.vlingo.lattice.model.object.ObjectTypeRegistry;
import io.vlingo.schemata.NoopDispatcher;
import io.vlingo.schemata.model.Category;
import io.vlingo.schemata.model.ContextState;
import io.vlingo.schemata.model.Id.ContextId;
import io.vlingo.schemata.model.Id.OrganizationId;
import io.vlingo.schemata.model.Id.SchemaId;
import io.vlingo.schemata.model.Id.SchemaVersionId;
import io.vlingo.schemata.model.Id.UnitId;
import io.vlingo.schemata.model.OrganizationState;
import io.vlingo.schemata.model.SchemaState;
import io.vlingo.schemata.model.SchemaVersion.Specification;
import io.vlingo.schemata.model.SchemaVersion.Status;
import io.vlingo.schemata.model.SchemaVersion.Version;
import io.vlingo.schemata.model.SchemaVersionState;
import io.vlingo.schemata.model.Scope;
import io.vlingo.schemata.model.UnitState;
import io.vlingo.symbio.store.Result;
import io.vlingo.symbio.store.StorageException;
import io.vlingo.symbio.store.object.ListQueryExpression;
import io.vlingo.symbio.store.object.MapQueryExpression;
import io.vlingo.symbio.store.object.ObjectStore;
import io.vlingo.symbio.store.object.ObjectStoreReader.QueryMode;

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
        assertEquals(updatedOrganizationState, queryInterest.singleResult.get().stateObject);
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
        querySelect(queryInterest, UnitState.class,"TBL_UNITS");
        queryInterest.until.completes();
        assertNotNull(queryInterest.singleResult.get());
        final UnitState insertedUnitState = (UnitState) queryInterest.singleResult.get().stateObject;
        assertEquals(unitState, insertedUnitState);

        // update
        queryInterest.until = TestUntil.happenings(1);
        final UnitState updatedUnitState =
                insertedUnitState.defineWith("VlingoV2", "Unit Vlingo V2");

        objectStore.persist(updatedUnitState, persistInterest);
        querySelect(queryInterest, UnitState.class, "TBL_UNITS");

        queryInterest.until.completes();
        assertNotNull(queryInterest.singleResult.get());
        assertNotEquals(updatedUnitState, queryInterest.singleResult.get().stateObject);
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
        querySelect(queryInterest, ContextState.class,"TBL_CONTEXTS");
        queryInterest.until.completes();
        assertNotNull(queryInterest.singleResult.get());
        final ContextState insertedContextState = (ContextState) queryInterest.singleResult.get().stateObject;
        assertEquals(contextState, insertedContextState);

        // update
        queryInterest.until = TestUntil.happenings(1);
        final ContextState updatedContextState =
                insertedContextState.defineWith("io.vlingoV2", "Context Vlingo V2");

        objectStore.persist(updatedContextState, persistInterest);
        querySelect(queryInterest, ContextState.class, "TBL_CONTEXTS");

        queryInterest.until.completes();
        assertNotNull(queryInterest.singleResult.get());

        // assert updates
        assertEquals(updatedContextState.contextId.value, ((ContextState) queryInterest.singleResult.get().stateObject).contextId.value);
        assertEquals(updatedContextState.namespace, ((ContextState) queryInterest.singleResult.get().stateObject).namespace);
        assertEquals(updatedContextState.description, ((ContextState) queryInterest.singleResult.get().stateObject).description);
        assertNotEquals(updatedContextState, queryInterest.singleResult.get().stateObject);
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
        querySelect(queryInterest, SchemaState.class,"TBL_SCHEMAS", persisted.persistenceId());
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

        objectStore.persist(updatedSchemaState, persistInterest);
        querySelect(queryInterest, SchemaState.class, "TBL_SCHEMAS");

        queryInterest.until.completes();
        assertNotNull(queryInterest.singleResult.get());
        assertNotEquals(updatedSchemaState, queryInterest.singleResult.get().stateObject);
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
        querySelect(queryInterest, SchemaVersionState.class,"TBL_SCHEMAVERSIONS");
        queryInterest.until.completes();
        assertNotNull(queryInterest.singleResult.get());
        final SchemaVersionState insertedSchemaVersionState = (SchemaVersionState) queryInterest.singleResult.get().stateObject;
        assertEquals(schemaVersionState, insertedSchemaVersionState);

        // update
        queryInterest.until = TestUntil.happenings(1);
        final SchemaVersionState updatedSchemaVersionState =
                insertedSchemaVersionState.defineWith("Schema Version Vlingo V2", Specification.of("SpecV2"), Version.of("1.0.0"), Version.of("2.0.0"));

        objectStore.persist(updatedSchemaVersionState, persistInterest);
        querySelect(queryInterest, SchemaVersionState.class, "TBL_SCHEMAVERSIONS");

        queryInterest.until.completes();
        assertNotNull(queryInterest.singleResult.get());
        assertNotEquals(updatedSchemaVersionState, queryInterest.singleResult.get().stateObject);
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

    private void querySelect(final TestQueryResultInterest queryInterest, Class<?> type, final String name, final long id) {
        objectStore.queryObject(
                MapQueryExpression.using(
                        type,
                        "SELECT * FROM " + name + " WHERE id = :id",
                        MapQueryExpression.map("id", id)),
                queryInterest);
    }
}
