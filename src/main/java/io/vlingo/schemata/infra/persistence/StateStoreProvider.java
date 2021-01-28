// Copyright © 2012-2020 VLINGO LABS. All rights reserved.
//
// This Source Code Form is subject to the terms of the
// Mozilla Public License, v. 2.0. If a copy of the MPL
// was not distributed with this file, You can obtain
// one at https://mozilla.org/MPL/2.0/.

package io.vlingo.schemata.infra.persistence;

import java.util.Arrays;

import io.vlingo.actors.World;
import io.vlingo.lattice.model.stateful.StatefulTypeRegistry;
import io.vlingo.lattice.model.stateful.StatefulTypeRegistry.Info;
import io.vlingo.schemata.NoopDispatcher;
import io.vlingo.schemata.SchemataConfig;
import io.vlingo.schemata.query.view.CodeView;
import io.vlingo.schemata.query.view.ContextView;
import io.vlingo.schemata.query.view.ContextsView;
import io.vlingo.schemata.query.view.NamedSchemaView;
import io.vlingo.schemata.query.view.OrganizationView;
import io.vlingo.schemata.query.view.OrganizationsView;
import io.vlingo.schemata.query.view.SchemaVersionView;
import io.vlingo.schemata.query.view.SchemaVersionsView;
import io.vlingo.schemata.query.view.SchemaView;
import io.vlingo.schemata.query.view.SchemasView;
import io.vlingo.schemata.query.view.UnitView;
import io.vlingo.schemata.query.view.UnitsView;
import io.vlingo.symbio.State;
import io.vlingo.symbio.store.DataFormat;
import io.vlingo.symbio.store.common.jdbc.Configuration;
import io.vlingo.symbio.store.common.jdbc.postgres.PostgresConfigurationProvider;
import io.vlingo.symbio.store.state.StateStore;
import io.vlingo.symbio.store.state.inmemory.InMemoryStateStoreActor;
import io.vlingo.symbio.store.state.jdbc.JDBCEntriesInstantWriter;
import io.vlingo.symbio.store.state.jdbc.JDBCEntriesWriter;
import io.vlingo.symbio.store.state.jdbc.JDBCStateStoreActor;
import io.vlingo.symbio.store.state.jdbc.JDBCStorageDelegate;
import io.vlingo.symbio.store.state.jdbc.postgres.PostgresStorageDelegate;

public class StateStoreProvider {
    public final StateStore stateStore;

    @SuppressWarnings({ "rawtypes" })
    public static StateStoreProvider using(World world, final SchemataConfig config) throws Exception {
        final StatefulTypeRegistry registry = new StatefulTypeRegistry(world);

        final StateStore stateStore;

        if (config.isProductionRuntimeType()) {
          final Configuration postgresConfiguration =
                  PostgresConfigurationProvider.configuration(
                          DataFormat.Text,
                          config.databaseUrl,
                          config.databaseName,
                          config.databaseUsername,
                          config.databasePassword,
                          config.databaseOriginator,
                          true);

//          final JDBCDispatcherControlDelegate dispatcherControlDelegate =
//                  new JDBCDispatcherControlDelegate(Configuration.cloneOf(postgresConfiguration), world.defaultLogger());

//          final List<Dispatcher<Dispatchable<Entry<String>, TextState>>> dispatchers =
//                  Collections.singletonList(typed(new NoopDispatcher()));

//          final DispatcherControl dispatcherControl = world.stage().actorFor(DispatcherControl.class,
//                  Definition.has(DispatcherControlActor.class,
//                          new DispatcherControl.DispatcherControlInstantiator(
//                                  dispatchers,
//                                  dispatcherControlDelegate,
//                                  config.confirmationExpirationInterval,
//                                  config.confirmationExpiration)));

          final JDBCStorageDelegate<?> delegate = new PostgresStorageDelegate(postgresConfiguration, world.defaultLogger());

          final JDBCEntriesWriter entriesWriter = new JDBCEntriesInstantWriter(typed(delegate), null, null);

          // TODO: fix this hack. makes store names register before JDBCStateStoreActor
          // is created so that tables can be created by store names. This will be
          // overwritten below when it is run again with the actual store.
          registerStatefulTypes(null, registry);

          stateStore = world.actorFor(StateStore.class, JDBCStateStoreActor.class, typed(delegate), entriesWriter);

        } else {
          stateStore = world.stage().actorFor(StateStore.class,
                  InMemoryStateStoreActor.class,
                  Arrays.asList(new NoopDispatcher()));
        }

        registerStatefulTypes(stateStore, registry);

        return new StateStoreProvider(stateStore);
    }

    private static void registerStatefulTypes(StateStore stateStore, StatefulTypeRegistry registry) {
        registry
                .register(new Info<>(stateStore, OrganizationView.class, OrganizationView.class.getSimpleName()))
                .register(new Info<>(stateStore, OrganizationsView.class, OrganizationsView.class.getSimpleName()))
                .register(new Info<>(stateStore, UnitView.class, UnitView.class.getSimpleName()))
                .register(new Info<>(stateStore, UnitsView.class, UnitsView.class.getSimpleName()))
                .register(new Info<>(stateStore, ContextView.class, ContextView.class.getSimpleName()))
                .register(new Info<>(stateStore, ContextsView.class, ContextsView.class.getSimpleName()))
                .register(new Info<>(stateStore, SchemaView.class, SchemaView.class.getSimpleName()))
                .register(new Info<>(stateStore, SchemasView.class, SchemasView.class.getSimpleName()))
                .register(new Info<>(stateStore, SchemaVersionView.class, SchemaVersionView.class.getSimpleName()))
                .register(new Info<>(stateStore, SchemaVersionsView.class, SchemaVersionsView.class.getSimpleName()))
                .register(new Info<>(stateStore, NamedSchemaView.class, NamedSchemaView.class.getSimpleName()))
                .register(new Info<>(stateStore, CodeView.class, CodeView.class.getSimpleName()));
    }

//    private static void registerStateAdapters(Stage stage) {
//        final StateAdapterProvider stateAdapterProvider = new StateAdapterProvider(stage.world());
//        stateAdapterProvider.registerAdapter(OrganizationView.class, new OrganizationStateAdapter());
//        new EntryAdapterProvider(stage.world()); // future?
//    }

    private StateStoreProvider(StateStore stateStore) {
        this.stateStore = stateStore;
    }

    @SuppressWarnings({"unchecked"})
    private static JDBCStorageDelegate<State.TextState> typed(final JDBCStorageDelegate<?> delegate) {
        return (JDBCStorageDelegate<State.TextState>) delegate;
    }
}
