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
import java.util.Properties;

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
import io.vlingo.symbio.store.DataFormat;
import io.vlingo.symbio.store.common.jdbc.Configuration;
import io.vlingo.symbio.store.common.jdbc.DatabaseType;
import io.vlingo.symbio.store.dispatch.Dispatchable;
import io.vlingo.symbio.store.dispatch.Dispatcher;
import io.vlingo.symbio.store.object.MapQueryExpression;
import io.vlingo.symbio.store.object.ObjectStore;
import io.vlingo.symbio.store.object.StateObjectMapper;
import io.vlingo.symbio.store.object.jdbc.jdbi.JdbiOnDatabase;
import io.vlingo.symbio.store.object.jdbc.jdbi.JdbiOnHSQLDB;
import io.vlingo.symbio.store.object.jdbc.jdbi.JdbiPersistMapper;

public abstract class SchemataObjectStore {
    private final Map<Class<?>, StateObjectMapper> mappersLookup;

    protected JdbiOnDatabase jdbi;

    public static SchemataObjectStore instance(final String runtimeType) throws Exception {

      final Properties properties = new java.util.Properties();
      final String propertiesFile = "/vlingo-schemata-" + runtimeType + ".properties";
      properties.load(Properties.class.getResourceAsStream(propertiesFile));

      final io.vlingo.symbio.store.common.jdbc.Configuration configuration = jdbcConfiguration(properties);

      final String classname = properties.getProperty("database.type");
      final Class<?> type = Class.forName(classname);
      final SchemataObjectStore schemataObjectStore = (SchemataObjectStore) type.newInstance();
      schemataObjectStore.initializeDatabase(configuration);
      return schemataObjectStore;
    }

    private static Configuration jdbcConfiguration(final Properties properties) throws Exception {
      final DatabaseType databaseType = DatabaseType.databaseType(properties.getProperty("database.url"));

      return new Configuration(
              databaseType,
              Configuration.interestOf(databaseType),
              properties.getProperty("database.driver"),
              DataFormat.Native,
              properties.getProperty("database.url"),
              properties.getProperty("database.name"),
              properties.getProperty("database.username"),
              properties.getProperty("database.password"),
              false,          // useSSL
              properties.getProperty("database.originator"),
              true);
    }

    public SchemataObjectStore() throws Exception {
        mappersLookup = new HashMap<>(5);
    }

    public void initializeDatabase(final Configuration configuration) throws Exception {
        jdbi = JdbiOnHSQLDB.openUsing(configuration);
        jdbi.createCommonTables();

        this.createOrganizationStateTable();
        this.createUnitStateTable();
        this.createContextStateTable();
        this.createSchemaStateTable();
        this.createSchemaVersionStateTable();
        this.createDependencyStateTable();
    }

    public Collection<StateObjectMapper> persistentMappers()  {
        StateObjectMapper organizationStateMapper =
                StateObjectMapper.with(
                        OrganizationState.class,
                        JdbiPersistMapper.with(
                                "INSERT INTO TBL_ORGANIZATIONS(dataVersion, organizationId, name, description) " +
                                        "VALUES (:version, :organizationId.value, :name, :description) ",
                                "UPDATE TBL_ORGANIZATIONS SET dataVersion = :version, name = :name, description = :description " +
                                "WHERE id = :persistenceId",
                                SqlStatement::bindFields,
                                SqlStatement::bindMethods),
                        new OrganizationStateMapper());

        mappersLookup.put(OrganizationState.class, organizationStateMapper);

        StateObjectMapper unitStateMapper =
                StateObjectMapper.with(
                        UnitState.class,
                        JdbiPersistMapper.with(
                                "INSERT INTO TBL_UNITS(dataVersion, unitId, organizationId, name, description) " +
                                        "VALUES (:version, :unitId.value, :unitId.organizationId.value, :name, :description) ",
                                "UPDATE TBL_UNITS SET dataVersion = :version, name = :name, description = :description " +
                                "WHERE id = :persistenceId",
                                SqlStatement::bindFields,
                                SqlStatement::bindMethods),
                        new UnitStateMapper());

        mappersLookup.put(UnitState.class, unitStateMapper);

        StateObjectMapper contextStateMapper =
                StateObjectMapper.with(
                        ContextState.class,
                        JdbiPersistMapper.with(
                                "INSERT INTO TBL_CONTEXTS(dataVersion, contextId, unitId, organizationId, namespace, description) " +
                                        "VALUES (:version, :contextId.value, :contextId.unitId.value, " +
                                        ":contextId.unitId.organizationId.value, :namespace, :description) ",
                                "UPDATE TBL_CONTEXTS SET dataVersion = :version, namespace = :namespace, description = :description " +
                                "WHERE id = :persistenceId",
                                SqlStatement::bindFields,
                                SqlStatement::bindMethods),
                        new ContextStateMapper());

        mappersLookup.put(ContextState.class, contextStateMapper);

        StateObjectMapper schemaStateMapper =
                StateObjectMapper.with(
                        SchemaState.class,
                        JdbiPersistMapper.with(
                                "INSERT INTO TBL_SCHEMAS(dataVersion, schemaId, contextId, unitId, organizationId, category, scope, name, description) " +
                                        "VALUES (:version, :schemaId.value, :schemaId.contextId.value, :schemaId.contextId.unitId.value, " +
                                        ":schemaId.contextId.unitId.organizationId.value, :category, :scope, :name, :description) ",
                                "UPDATE TBL_SCHEMAS SET dataVersion = :version, category = :category, scope = :scope, name = :namespace, description = :description " +
                                "WHERE id = :persistenceId",
                                SqlStatement::bindFields,
                                SqlStatement::bindMethods),
                        new SchemaStateMapper());

        mappersLookup.put(SchemaState.class, schemaStateMapper);

        StateObjectMapper schemaVersionStateMapper =
                StateObjectMapper.with(
                        SchemaVersionState.class,
                        JdbiPersistMapper.with(
                                "INSERT INTO TBL_SCHEMAVERSIONS(dataVersion, schemaVersionId, schemaId, contextId, unitId, organizationId, " +
                                        "description, specification, status, previousVersion, currentVersion) " +
                                        "VALUES (:version, :schemaVersionId.value, :schemaVersionId.schemaId.value, " +
                                        ":schemaVersionId.schemaId.contextId.value, :schemaVersionId.schemaId.contextId.unitId.value, " +
                                        ":schemaVersionId.schemaId.contextId.unitId.organizationId.value, :description, " +
                                        ":specification.value, :status.value, :previousVersion.value, :currentVersion.value)",
                                "UPDATE TBL_SCHEMAVERSIONS SET dataVersion = :version, description = :description, specification = :specification.value, " +
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

    protected abstract void createOrganizationStateTable();
    protected abstract void createUnitStateTable();
    protected abstract void createContextStateTable();
    protected abstract void createSchemaStateTable();
    protected abstract void createSchemaVersionStateTable();
    protected abstract void createDependencyStateTable();
}
