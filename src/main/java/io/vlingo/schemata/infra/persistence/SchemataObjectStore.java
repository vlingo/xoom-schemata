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
                                "INSERT INTO TBL_ORGANIZATIONS(organizationId, name, description) " +
                                        "VALUES (:organizationId.value, :name, :description) ",
                                "UPDATE TBL_ORGANIZATIONS SET name = :name, description = :description " +
                                "WHERE id = :persistenceId",
                                SqlStatement::bindFields,
                                SqlStatement::bindMethods),
                        new OrganizationStateMapper());

        mappersLookup.put(OrganizationState.class, organizationStateMapper);

        StateObjectMapper unitStateMapper =
                StateObjectMapper.with(
                        UnitState.class,
                        JdbiPersistMapper.with(
                                "INSERT INTO TBL_UNITS(unitId, organizationId, name, description) " +
                                        "VALUES (:unitId.value, :unitId.organizationId.value, :name, :description) ",
                                "UPDATE TBL_UNITS SET name = :name, description = :description " +
                                "WHERE id = :persistenceId",
                                SqlStatement::bindFields,
                                SqlStatement::bindMethods),
                        new UnitStateMapper());

        mappersLookup.put(UnitState.class, unitStateMapper);

        StateObjectMapper contextStateMapper =
                StateObjectMapper.with(
                        ContextState.class,
                        JdbiPersistMapper.with(
                                "INSERT INTO TBL_CONTEXTS(contextId, unitId, organizationId, namespace, description) " +
                                        "VALUES (:contextId.value, :contextId.unitId.value, " +
                                        ":contextId.unitId.organizationId.value, :namespace, :description) ",
                                "UPDATE TBL_CONTEXTS SET namespace = :namespace, description = :description " +
                                "WHERE id = :persistenceId",
                                SqlStatement::bindFields,
                                SqlStatement::bindMethods),
                        new ContextStateMapper());

        mappersLookup.put(ContextState.class, contextStateMapper);

        StateObjectMapper schemaStateMapper =
                StateObjectMapper.with(
                        SchemaState.class,
                        JdbiPersistMapper.with(
                                "INSERT INTO TBL_SCHEMAS(schemaId, contextId, unitId, organizationId, category, scope, name, description) " +
                                        "VALUES (:schemaId.value, :schemaId.contextId.value, :schemaId.contextId.unitId.value, " +
                                        ":schemaId.contextId.unitId.organizationId.value, :category, :scope, :name, :description) ",
                                "UPDATE TBL_SCHEMAS SET category = :category, scope = :scope, name = :namespace, description = :description " +
                                "WHERE id = :persistenceId",
                                SqlStatement::bindFields,
                                SqlStatement::bindMethods),
                        new SchemaStateMapper());

        mappersLookup.put(SchemaState.class, schemaStateMapper);

        StateObjectMapper schemaVersionStateMapper =
                StateObjectMapper.with(
                        SchemaVersionState.class,
                        JdbiPersistMapper.with(
                                "INSERT INTO TBL_SCHEMAVERSIONS(schemaVersionId, schemaId, contextId, unitId, organizationId, " +
                                        "description, specification, status, previousVersion, currentVersion) " +
                                        "VALUES (:schemaVersionId.value, :schemaVersionId.schemaId.value, " +
                                        ":schemaVersionId.schemaId.contextId.value, :schemaVersionId.schemaId.contextId.unitId.value, " +
                                        ":schemaVersionId.schemaId.contextId.unitId.organizationId.value, :description, " +
                                        ":specification.value, :status.value, :previousVersion.value, :currentVersion.value)",
                                "UPDATE TBL_SCHEMAVERSIONS SET description = :description, specification = :specification.value, " +
                                        "status = :status.value, previousVersion = :previousVersion.value, currentVersion = :currentVersion.value " +
                                "WHERE id = :persistenceId",
                                SqlStatement::bindFields,
                                SqlStatement::bindMethods),
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
        final String orgQuery = "SELECT * FROM TBL_ORGANIZATIONS " +
                                "WHERE organizationId = :organizationId";

        final Info<Organization> organizationInfo =
                new Info(
                    objectStore,
                    OrganizationState.class,
                    "vlingo_schemata",
                    MapQueryExpression.using(
                            OrganizationState.class,
                            orgQuery,
                            MapQueryExpression.map("organizationId", "")),
                    mappersLookup.get(OrganizationState.class));
        registry.register(organizationInfo);

        final String unitQuery = "SELECT * FROM TBL_UNITS " +
                                 "WHERE organizationId = :organizationId " +
                                 "AND unitId = :unitId";

        final Info<Unit> unitInfo =
                new Info(
                        objectStore,
                        UnitState.class,
                        "vlingo_schemata",
                        MapQueryExpression.using(
                                UnitState.class,
                                unitQuery,
                                MapQueryExpression.map("organizationId", "").and("unitId", "")),
                        mappersLookup.get(UnitState.class));
        registry.register(unitInfo);

        final String contextQuery = "SELECT * FROM TBL_CONTEXTS " +
                                    "WHERE organizationId = :organizationId " +
                                    "AND unitId = :unitId " +
                                    "AND contextId = :contextId";

        final Info<Context> contextInfo =
                new Info(
                        objectStore,
                        ContextState.class,
                        "vlingo_schemata",
                        MapQueryExpression.using(
                                ContextState.class,
                                contextQuery,
                                MapQueryExpression.map("organizationId", "").and("unitId", "").and("contextId", "")),
                        mappersLookup.get(ContextState.class));
        registry.register(contextInfo);

        final String schemaQuery = "SELECT * FROM TBL_SCHEMAS " +
                                   "WHERE organizationId = :organizationId " +
                                   "AND unitId = :unitId " +
                                   "AND contextId = :contextId " +
                                   "AND schemaId = :schemaId";

        final Info<Schema> schemaInfo =
                new Info(
                        objectStore,
                        SchemaState.class,
                        "vlingo_schemata",
                        MapQueryExpression.using(
                                SchemaState.class,
                                schemaQuery,
                                MapQueryExpression.map("organizationId", "").and("unitId", "").and("contextId", "").and("schemaId", "")),
                        mappersLookup.get(SchemaState.class));
        registry.register(schemaInfo);

        final String versionQuery = "SELECT * FROM TBL_SCHEMAVERSIONS " +
                                    "WHERE organizationId = :organizationId " +
                                    "AND unitId = :unitId " +
                                    "AND contextId = :contextId " +
                                    "AND schemaId = :schemaId " +
                                    "AND schemaVersionId = :schemaVersionId";

        final Info<SchemaVersion> schemaVersionInfo =
                new Info(
                        objectStore,
                        SchemaVersionState.class,
                        "vlingo_schemata",
                        MapQueryExpression.using(
                                SchemaVersionState.class,
                                versionQuery,
                                MapQueryExpression.map("organizationId", "").and("unitId", "").and("contextId", "").and("schemaId", "").and("schemaVersionId", "")),
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
        this.createDependencyStateTable();
    }

    private void createOrganizationStateTable() {
        jdbi.handle().execute(
                "CREATE TABLE IF NOT EXISTS TBL_ORGANIZATIONS (" +
                "id BIGINT GENERATED ALWAYS AS IDENTITY(START WITH 1 INCREMENT BY 1) PRIMARY KEY, " +
                "organizationId VARCHAR (50) NOT NULL, " +
                "name VARCHAR(128) NOT NULL, " +
                "description VARCHAR(8000) " +

//                "UNIQUE (name) " +
                ")");

//        jdbi.handle().execute("CREATE UNIQUE INDEX IF NOT EXISTS ORG_ALL_INDEX ON TBL_ORGANIZATIONS (organizationId)");
    }

    private void createUnitStateTable() {
        jdbi.handle().execute(
                "CREATE TABLE IF NOT EXISTS TBL_UNITS (" +
                "id BIGINT GENERATED ALWAYS AS IDENTITY(START WITH 1 INCREMENT BY 1) PRIMARY KEY, " +
                "unitId VARCHAR(50) NOT NULL, " +
                "organizationId VARCHAR (50) NOT NULL, " +
                "name VARCHAR(128) NOT NULL, " +
                "description VARCHAR(8000) " +

//                "UNIQUE (organizationId, name) " +
                ")");

//        jdbi.handle().execute("CREATE UNIQUE INDEX IF NOT EXISTS UNIT_PARENT_INDEX ON TBL_UNITS (organizationId)");
//        jdbi.handle().execute("CREATE UNIQUE INDEX IF NOT EXISTS UNIT_ALL_INDEX ON TBL_UNITS (organizationId, unitId)");
    }

    private void createContextStateTable() {
        jdbi.handle().execute(
                "CREATE TABLE IF NOT EXISTS TBL_CONTEXTS (" +
                "id BIGINT GENERATED ALWAYS AS IDENTITY(START WITH 1 INCREMENT BY 1) PRIMARY KEY, " +
                "contextId VARCHAR(50) NOT NULL, " +
                "unitId VARCHAR(50) NOT NULL, " +
                "organizationId VARCHAR (50) NOT NULL, " +
                "namespace VARCHAR(256) NOT NULL, " +
                "description VARCHAR(8000) " +

//                "UNIQUE (unitId, namespace) " +
                ")");

//        jdbi.handle().execute("CREATE UNIQUE INDEX IF NOT EXISTS CONTEXT_PARENT_INDEX ON TBL_CONTEXTS (organizationId, unitId)");
//        jdbi.handle().execute("CREATE UNIQUE INDEX IF NOT EXISTS CONTEXT_ALL_INDEX ON TBL_CONTEXTS (organizationId, unitId, contextId)");
    }

    private void createSchemaStateTable() {
        jdbi.handle().execute(
                "CREATE TABLE IF NOT EXISTS TBL_SCHEMAS (" +
                "id BIGINT GENERATED ALWAYS AS IDENTITY(START WITH 1 INCREMENT BY 1) PRIMARY KEY, " +
                "schemaId VARCHAR(50) NOT NULL, " +
                "contextId VARCHAR(50) NOT NULL, " +
                "unitId VARCHAR(50) NOT NULL, " +
                "organizationId VARCHAR (50) NOT NULL, " +
                "category VARCHAR(25) NOT NULL, " +
                "scope VARCHAR(25) NOT NULL, " +
                "name VARCHAR(128) NOT NULL, " +
                "description VARCHAR(8000) " +

//                "UNIQUE (contextId, category, name) " +
                ")");

//        jdbi.handle().execute("CREATE UNIQUE INDEX IF NOT EXISTS SCHEMA_PARENT_INDEX ON TBL_SCHEMAS (organizationId, unitId, contextId)");
//        jdbi.handle().execute("CREATE UNIQUE INDEX IF NOT EXISTS SCHEMA_ALL_INDEX ON TBL_SCHEMAS (organizationId, unitId, contextId, schemaId)");
    }

    private void createSchemaVersionStateTable() {
        final int specificationWidth;

        switch (jdbi.databaseType()) {
        case Postgres:
          specificationWidth = 65536;
          break;
        case HSQLDB:
          specificationWidth = 8000;
          break;
        default:
          specificationWidth = 4000;
          break;
        }

        jdbi.handle().execute(
                "CREATE TABLE IF NOT EXISTS TBL_SCHEMAVERSIONS (" +
                "id BIGINT GENERATED ALWAYS AS IDENTITY(START WITH 1 INCREMENT BY 1) PRIMARY KEY, " +
                "schemaVersionId VARCHAR(50) NOT NULL, " +
                "schemaId VARCHAR(50) NOT NULL, " +
                "contextId VARCHAR(50) NOT NULL, " +
                "unitId VARCHAR(50) NOT NULL, " +
                "organizationId VARCHAR (50) NOT NULL, " +
                "specification VARCHAR(" + specificationWidth + ") NOT NULL, " +
                "description VARCHAR(8000), " +
                "status VARCHAR(16) NOT NULL, " +
                "previousVersion VARCHAR(20) NOT NULL, " +
                "currentVersion VARCHAR(20) NOT NULL " +

//                "UNIQUE (schemaId, currentVersion) " +
                ")");

//        jdbi.handle().execute("CREATE UNIQUE INDEX IF NOT EXISTS SCHEMAVERSION_PARENT_INDEX ON TBL_SCHEMAVERSIONS (organizationId, unitId, contextId, schemaId)");
//        jdbi.handle().execute("CREATE UNIQUE INDEX IF NOT EXISTS SCHEMAVERSION_ALL_INDEX ON TBL_SCHEMAVERSIONS (organizationId, unitId, contextId, schemaId, schemaVersionId)");
    }

    private void createDependencyStateTable() {
        jdbi.handle().execute(
                "CREATE TABLE IF NOT EXISTS TBL_DEPENDENCIES (" +
                "id BIGINT GENERATED ALWAYS AS IDENTITY(START WITH 1 INCREMENT BY 1) PRIMARY KEY, " +

                "sourceOrganizationId VARCHAR (50) NOT NULL, " +
                "sourceUnitId VARCHAR(50) NOT NULL, " +
                "sourceContextId VARCHAR(50) NOT NULL, " +
                "sourceSchemaId VARCHAR(50) NOT NULL, " +
                "sourceSchemaVersionId VARCHAR(50) NOT NULL, " +

                "dependentOrganizationId VARCHAR (50) NOT NULL, " +
                "dependentUnitId VARCHAR(50) NOT NULL, " +
                "dependentContextId VARCHAR(50) NOT NULL " +

                ")");
    }
}
