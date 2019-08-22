// Copyright © 2012-2018 Vaughn Vernon. All rights reserved.
//
// This Source Code Form is subject to the terms of the
// Mozilla Public License, v. 2.0. If a copy of the MPL
// was not distributed with this file, You can obtain
// one at https://mozilla.org/MPL/2.0/.

package io.vlingo.schemata.infra.persistence;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import org.jdbi.v3.core.statement.SqlStatement;

import io.vlingo.actors.World;
import io.vlingo.lattice.model.object.ObjectTypeRegistry;
import io.vlingo.lattice.model.object.ObjectTypeRegistry.Info;
import io.vlingo.schemata.infra.persistence.mappers.ContextStateMapper;
import io.vlingo.schemata.infra.persistence.mappers.OrganizationStateMapper;
import io.vlingo.schemata.infra.persistence.mappers.SchemaStateMapper;
import io.vlingo.schemata.infra.persistence.mappers.SchemaVersionStateMapper;
import io.vlingo.schemata.infra.persistence.mappers.UnitStateMapper;
import io.vlingo.schemata.model.Context;
import io.vlingo.schemata.model.ContextState;
import io.vlingo.schemata.model.Organization;
import io.vlingo.schemata.model.OrganizationState;
import io.vlingo.schemata.model.Schema;
import io.vlingo.schemata.model.SchemaState;
import io.vlingo.schemata.model.SchemaVersion;
import io.vlingo.schemata.model.SchemaVersionState;
import io.vlingo.schemata.model.Unit;
import io.vlingo.schemata.model.UnitState;
import io.vlingo.symbio.BaseEntry.TextEntry;
import io.vlingo.symbio.State.TextState;
import io.vlingo.symbio.store.common.jdbc.Configuration;
import io.vlingo.symbio.store.dispatch.Dispatchable;
import io.vlingo.symbio.store.dispatch.Dispatcher;
import io.vlingo.symbio.store.object.MapQueryExpression;
import io.vlingo.symbio.store.object.ObjectStore;
import io.vlingo.symbio.store.object.StateObjectMapper;
import io.vlingo.symbio.store.object.jdbc.jdbi.JdbiOnDatabase;
import io.vlingo.symbio.store.object.jdbc.jdbi.JdbiOnHSQLDB;
import io.vlingo.symbio.store.object.jdbc.jdbi.JdbiPersistMapper;

public class SchemataObjectStore {

    private JdbiOnDatabase jdbi;
    private final Map<Class<?>, StateObjectMapper> mappersLookup;

    public SchemataObjectStore(final Configuration configuration) throws Exception {
        initializeDatabase(configuration);
        mappersLookup = new HashMap<>(5);
    }

    public Collection<StateObjectMapper> persistentMappers()  {
        StateObjectMapper organizationStateMapper =
                StateObjectMapper.with(
                        OrganizationState.class,
                        JdbiPersistMapper.with(
                                "INSERT INTO ORGANIZATION(organizationId, name, description) " +
                                        "VALUES (:organizationId.value, :name, :description)",
                                "UPDATE ORGANIZATION SET name = :name, description = :description WHERE id = :id",
                                SqlStatement::bindFields),
                        new OrganizationStateMapper());

        mappersLookup.put(OrganizationState.class, organizationStateMapper);

        StateObjectMapper unitStateMapper =
                StateObjectMapper.with(
                        UnitState.class,
                        JdbiPersistMapper.with(
                                "INSERT INTO UNIT(unitId, organizationId, name, description) " +
                                        "VALUES (:unitId.value, :unitId.organizationId.value, :name, :description)",
                                "UPDATE UNIT SET name = :name, description = :description WHERE id = :id",
                                SqlStatement::bindFields),
                        new UnitStateMapper());

        mappersLookup.put(UnitState.class, unitStateMapper);

        StateObjectMapper contextStateMapper =
                StateObjectMapper.with(
                        ContextState.class,
                        JdbiPersistMapper.with(
                                "INSERT INTO CONTEXT(contextId, unitId, organizationId, namespace, description) " +
                                        "VALUES (:contextId.value, :contextId.unitId.value, " +
                                        ":contextId.unitId.organizationId.value, :namespace, :description)",
                                "UPDATE CONTEXT SET namespace = :namespace, description = :description WHERE id = :id",
                                SqlStatement::bindFields),
                        new ContextStateMapper());

        mappersLookup.put(ContextState.class, contextStateMapper);

        StateObjectMapper schemaStateMapper =
                StateObjectMapper.with(
                        SchemaState.class,
                        JdbiPersistMapper.with(
                                "INSERT INTO SCHEMA(schemaId, contextId, unitId, organizationId, category, name, description) " +
                                        "VALUES (:schemaId.value, :schemaId.contextId.value, :schemaId.contextId.unitId.value, " +
                                        ":schemaId.contextId.unitId.organizationId.value, :category, :name, :description)",
                                "UPDATE SCHEMA SET category = :category, name = :namespace, description = :description WHERE id = :id",
                                SqlStatement::bindFields),
                        new SchemaStateMapper());

        mappersLookup.put(SchemaState.class, schemaStateMapper);

        StateObjectMapper schemaVersionStateMapper =
                StateObjectMapper.with(
                        SchemaVersionState.class,
                        JdbiPersistMapper.with(
                                "INSERT INTO SCHEMAVERSION(schemaVersionId, schemaId, contextId, unitId, organizationId, " +
                                        "description, specification, status, previousVersion, currentVersion) " +
                                        "VALUES (:schemaVersionId.value, :schemaVersionId.schemaId.value, " +
                                        ":schemaVersionId.schemaId.contextId.value, :schemaVersionId.schemaId.contextId.unitId.value, " +
                                        ":schemaVersionId.schemaId.contextId.unitId.organizationId.value, :description, " +
                                        ":specification.value, :status.value, :previousVersion.value, :currentVersion.value)",
                                "UPDATE SCHEMAVERSION SET description = :description, specification = :specification.value, " +
                                        "status = :status.value, previousVersion = :previousVersion.value, currentVersion = :currentVersion.value " +
                                "WHERE id = :id",
                                SqlStatement::bindFields),
                        new SchemaVersionStateMapper());

        mappersLookup.put(SchemaVersionState.class, schemaVersionStateMapper);

        return mappersLookup.values();
    }

    public ObjectStore objectStoreFor(
            final World world,
            final Dispatcher<Dispatchable<TextEntry, TextState>> dispatcher,
            final Collection<StateObjectMapper> mappers) {
        return jdbi.objectStore(world, dispatcher, mappers);
    }

    @SuppressWarnings({ "unchecked", "rawtypes" })
    public void register(final ObjectTypeRegistry registry, final ObjectStore objectStore) {
        final Info<Organization> organizationInfo =
                new Info(
                    objectStore,
                    OrganizationState.class,
                        "vlingo_schemata",
                    MapQueryExpression.using(Organization.class, "find", MapQueryExpression.map("id", "id")),
                    mappersLookup.get(OrganizationState.class));
        registry.register(organizationInfo);

        final Info<Unit> unitInfo =
                new Info(
                        objectStore,
                        UnitState.class,
                        "vlingo_schemata",
                        MapQueryExpression.using(Unit.class, "find", MapQueryExpression.map("id", "id")),
                        mappersLookup.get(UnitState.class));
        registry.register(unitInfo);

        final Info<Context> contextInfo =
                new Info(
                        objectStore,
                        ContextState.class,
                        "vlingo_schemata",
                        MapQueryExpression.using(Context.class, "find", MapQueryExpression.map("id", "id")),
                        mappersLookup.get(ContextState.class));
        registry.register(contextInfo);

        final Info<Schema> schemaInfo =
                new Info(
                        objectStore,
                        SchemaState.class,
                        "vlingo_schemata",
                        MapQueryExpression.using(Schema.class, "find", MapQueryExpression.map("id", "id")),
                        mappersLookup.get(SchemaState.class));
        registry.register(schemaInfo);

        final Info<SchemaVersion> schemaVersionInfo =
                new Info(
                        objectStore,
                        SchemaVersionState.class,
                        "vlingo_schemata",
                        MapQueryExpression.using(SchemaVersion.class, "find", MapQueryExpression.map("id", "id")),
                        mappersLookup.get(SchemaVersionState.class));
        registry.register(schemaVersionInfo);
    }

    private void initializeDatabase(final Configuration configuration) throws Exception {
        jdbi = JdbiOnHSQLDB.openUsing(configuration);
        jdbi.createCommonTables();

        this.createOrganizationStateTable();
        this.createUnitStateTable();
        this.createContextStateTable();
        this.createSchemaStateTable();
        this.createSchemaVersionStateTable();
    }

    private void createOrganizationStateTable() {
        jdbi.handle().execute("CREATE TABLE IF NOT EXISTS ORGANIZATION " +
                "(id BIGINT GENERATED ALWAYS AS IDENTITY(START WITH 1 INCREMENT BY 1) PRIMARY KEY, " +
                "organizationId VARCHAR (50), name VARCHAR(128), description VARCHAR(8000))");
    }

    private void createUnitStateTable() {
        jdbi.handle().execute("CREATE TABLE IF NOT EXISTS UNIT " +
                "(id BIGINT GENERATED ALWAYS AS IDENTITY(START WITH 1 INCREMENT BY 1) PRIMARY KEY, " +
                "unitId VARCHAR(50), organizationId VARCHAR (50), name VARCHAR(128), description VARCHAR(8000))");
    }

    private void createContextStateTable() {
        jdbi.handle().execute("CREATE TABLE IF NOT EXISTS CONTEXT " +
                "(id BIGINT GENERATED ALWAYS AS IDENTITY(START WITH 1 INCREMENT BY 1) PRIMARY KEY, " +
                "contextId VARCHAR(50), unitId VARCHAR(50), organizationId VARCHAR (50), namespace VARCHAR(256), description VARCHAR(8000))");
    }

    private void createSchemaStateTable() {
        jdbi.handle().execute("CREATE TABLE IF NOT EXISTS SCHEMA " +
                "(id BIGINT GENERATED ALWAYS AS IDENTITY(START WITH 1 INCREMENT BY 1) PRIMARY KEY, " +
                "schemaId VARCHAR(50), contextId VARCHAR(50), unitId VARCHAR(50), organizationId VARCHAR (50), " +
                "category VARCHAR(25), name VARCHAR(128), description VARCHAR(8000))");
    }

    private void createSchemaVersionStateTable() {
        jdbi.handle().execute("CREATE TABLE IF NOT EXISTS SCHEMAVERSION " +
                "(id BIGINT GENERATED ALWAYS AS IDENTITY(START WITH 1 INCREMENT BY 1) PRIMARY KEY, " +
                "schemaVersionId VARCHAR(50), schemaId VARCHAR(50), contextId VARCHAR(50), unitId VARCHAR(50), organizationId VARCHAR (50), " +
                "specification VARCHAR(8000), description VARCHAR(8000), status VARCHAR(16), previousVersion VARCHAR(16), currentVersion VARCHAR(16))");
    }
}
