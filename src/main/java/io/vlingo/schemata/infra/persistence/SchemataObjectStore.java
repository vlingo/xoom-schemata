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
import io.vlingo.schemata.infra.persistence.mappers.OrganizationStateMapper;
import io.vlingo.schemata.model.Organization;
import io.vlingo.schemata.model.OrganizationState;
import io.vlingo.symbio.BaseEntry.TextEntry;
import io.vlingo.symbio.State.TextState;
import io.vlingo.symbio.store.DataFormat;
import io.vlingo.symbio.store.common.jdbc.hsqldb.HSQLDBConfigurationProvider;
import io.vlingo.symbio.store.dispatch.Dispatchable;
import io.vlingo.symbio.store.dispatch.Dispatcher;
import io.vlingo.symbio.store.object.MapQueryExpression;
import io.vlingo.symbio.store.object.ObjectStore;
import io.vlingo.symbio.store.object.PersistentObjectMapper;
import io.vlingo.symbio.store.object.jdbc.jdbi.JdbiOnDatabase;
import io.vlingo.symbio.store.object.jdbc.jdbi.JdbiOnHSQLDB;
import io.vlingo.symbio.store.object.jdbc.jdbi.JdbiPersistMapper;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class SchemataObjectStore {

    private final JdbiOnDatabase jdbi;
    private final Map<Class<?>, PersistentObjectMapper> mappersLookup;

    public SchemataObjectStore(final String username, final String password) throws Exception {
        jdbi = initializeDatabase(username, password);
        mappersLookup = new HashMap<>(5);
    }

    public Collection<PersistentObjectMapper> persistentMappers()  {
        PersistentObjectMapper organizationStateMapper =
                PersistentObjectMapper.with(
                        OrganizationState.class,
                        JdbiPersistMapper.with(
                                "INSERT INTO ORGANIZATION(id, name, description) VALUES (:id, :name, :description)",
                                "UPDATE ORGANIZATION SET name = :name, description = :description WHERE id = :id",
                                (update,object) -> update.bindFields(object)),
                        new OrganizationStateMapper());

        mappersLookup.put(OrganizationState.class, organizationStateMapper);

        return Collections.singletonList(organizationStateMapper);
        // TODO: add more mappers
    }

    public ObjectStore objectStoreFor(
            World world,
            Dispatcher<Dispatchable<TextEntry, TextState>> dispatcher,
            Collection<PersistentObjectMapper> mappers) {
        return jdbi.objectStore(world, dispatcher, mappers);
    }

    @SuppressWarnings({ "unchecked", "rawtypes" })
    public void register(ObjectTypeRegistry registry, ObjectStore objectStore) {
        final Info<Organization> organizationInfo =
                new Info(
                    objectStore,
                    OrganizationState.class,
                        "vlingo-schemata",
                    MapQueryExpression.using(Organization.class, "find", MapQueryExpression.map("id", "id")),
                    mappersLookup.get(OrganizationState.class));

        registry.register(organizationInfo);
    }

    private JdbiOnDatabase initializeDatabase(final String username, final String password) throws Exception {
        JdbiOnDatabase jdbi = JdbiOnHSQLDB.openUsing(HSQLDBConfigurationProvider.configuration(
                DataFormat.Native,
                "jdbc:hsqldb:mem:",
                "vlingo-schemata",
                username,
                password,
                "MAIN",
                true));
        jdbi.createCommonTables();

        this.createOrganizationStateTable();
        // TODO: add more tables

        return jdbi;
    }

    private void createOrganizationStateTable() {
        jdbi.handle().execute("CREATE TABLE IF NOT EXISTS ORGANIZATION " +
                "(id BIGINT GENERATED ALWAYS AS IDENTITY(START WITH 1 INCREMENT BY 1) PRIMARY KEY, " +
                "name VARCHAR(100), description VARCHAR(1024)");
    }
}
