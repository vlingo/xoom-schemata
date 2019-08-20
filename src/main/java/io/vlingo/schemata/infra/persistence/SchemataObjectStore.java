// Copyright © 2012-2018 Vaughn Vernon. All rights reserved.
//
// This Source Code Form is subject to the terms of the
// Mozilla Public License, v. 2.0. If a copy of the MPL
// was not distributed with this file, You can obtain
// one at https://mozilla.org/MPL/2.0/.

package io.vlingo.schemata.infra.persistence;

import io.vlingo.actors.World;
import io.vlingo.lattice.model.object.ObjectTypeRegistry;
import io.vlingo.lattice.model.object.ObjectTypeRegistry.Info;
import io.vlingo.schemata.infra.persistence.mappers.ContextStateMapper;
import io.vlingo.schemata.infra.persistence.mappers.OrganizationStateMapper;
import io.vlingo.schemata.infra.persistence.mappers.UnitStateMapper;
import io.vlingo.schemata.model.*;
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
import org.jdbi.v3.core.statement.SqlStatement;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

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
                                "INSERT INTO ORGANIZATION(organizationId, name, description) VALUES (:organizationId.value, :name, :description)",
                                "UPDATE ORGANIZATION SET name = :name, description = :description WHERE id = :id",
                                SqlStatement::bindFields),
                        new OrganizationStateMapper());

        mappersLookup.put(OrganizationState.class, organizationStateMapper);

        StateObjectMapper unitStateMapper =
                StateObjectMapper.with(
                        UnitState.class,
                        JdbiPersistMapper.with(
                                "INSERT INTO UNIT(unitId, organizationId, name, description) VALUES (:unitId.value, :unitId.organizationId.value, :name, :description)",
                                "UPDATE UNIT SET name = :name, description = :description WHERE id = :id",
                                SqlStatement::bindFields),
                        new UnitStateMapper());

        mappersLookup.put(UnitState.class, unitStateMapper);

        StateObjectMapper contextStateMapper =
                StateObjectMapper.with(
                        ContextState.class,
                        JdbiPersistMapper.with(
                                "INSERT INTO CONTEXT(contextId, unitId, organizationId, namespace, description) " +
                                        "VALUES (:contextId.value, :contextId.unitId.value, :contextId.unitId.organizationId.value, :namespace, :description)",
                                "UPDATE CONTEXT SET namespace = :namespace, description = :description WHERE id = :id",
                                SqlStatement::bindFields),
                        new ContextStateMapper());

        mappersLookup.put(ContextState.class, contextStateMapper);

        return mappersLookup.values();
        // TODO: add more mappers
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
                        MapQueryExpression.using(Unit.class, "find", MapQueryExpression.map("id", "id")),
                        mappersLookup.get(ContextState.class));
        registry.register(contextInfo);
    }

    private void initializeDatabase(final Configuration configuration) throws Exception {
        jdbi = JdbiOnHSQLDB.openUsing(configuration);
        jdbi.createCommonTables();

        this.createOrganizationStateTable();
        this.createUnitStateTable();
        this.createContextStateTable();
        // TODO: add more tables
    }

    private void createOrganizationStateTable() {
        jdbi.handle().execute("CREATE TABLE IF NOT EXISTS ORGANIZATION " +
                "(id BIGINT GENERATED ALWAYS AS IDENTITY(START WITH 1 INCREMENT BY 1) PRIMARY KEY, " +
                "organizationId VARCHAR (50), name VARCHAR(100), description VARCHAR(1024))");
    }

    private void createUnitStateTable() {
        jdbi.handle().execute("CREATE TABLE IF NOT EXISTS UNIT " +
                "(id BIGINT GENERATED ALWAYS AS IDENTITY(START WITH 1 INCREMENT BY 1) PRIMARY KEY, " +
                "unitId VARCHAR(50), organizationId VARCHAR (50), name VARCHAR(100), description VARCHAR(1024))");
    }

    private void createContextStateTable() {
        jdbi.handle().execute("CREATE TABLE IF NOT EXISTS CONTEXT " +
                "(id BIGINT GENERATED ALWAYS AS IDENTITY(START WITH 1 INCREMENT BY 1) PRIMARY KEY, " +
                "contextId VARCHAR(50), unitId VARCHAR(50), organizationId VARCHAR (50), namespace VARCHAR(100), description VARCHAR(1024))");
    }
}
