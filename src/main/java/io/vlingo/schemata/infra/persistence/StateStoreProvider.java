// Copyright © 2012-2020 VLINGO LABS. All rights reserved.
//
// This Source Code Form is subject to the terms of the
// Mozilla Public License, v. 2.0. If a copy of the MPL
// was not distributed with this file, You can obtain
// one at https://mozilla.org/MPL/2.0/.

package io.vlingo.schemata.infra.persistence;

import io.vlingo.actors.ActorInstantiator;
import io.vlingo.actors.World;
import io.vlingo.lattice.model.stateful.StatefulTypeRegistry;
import io.vlingo.lattice.model.stateful.StatefulTypeRegistry.Info;
import io.vlingo.schemata.NoopDispatcher;
import io.vlingo.schemata.SchemataConfig;
import io.vlingo.schemata.query.view.*;
import io.vlingo.symbio.State;
import io.vlingo.symbio.store.DataFormat;
import io.vlingo.symbio.store.common.jdbc.Configuration;
import io.vlingo.symbio.store.common.jdbc.postgres.PostgresConfigurationProvider;
import io.vlingo.symbio.store.state.StateStore;
import io.vlingo.symbio.store.state.StateStore.InitializationPrimer;
import io.vlingo.symbio.store.state.inmemory.InMemoryStateStoreActor;
import io.vlingo.symbio.store.state.jdbc.JDBCEntriesInstantWriter;
import io.vlingo.symbio.store.state.jdbc.JDBCStateStoreActor;
import io.vlingo.symbio.store.state.jdbc.JDBCStateStoreActor.JDBCStateStoreInstantiator;
import io.vlingo.symbio.store.state.jdbc.JDBCStorageDelegate;
import io.vlingo.symbio.store.state.jdbc.postgres.PostgresStorageDelegate;

import java.util.Arrays;

public class StateStoreProvider {

  private static final int MAXIMUM_RETRIES = 5;

  public final StateStore stateStore;

  @SuppressWarnings({"rawtypes", "unchecked"})
  public static StateStoreProvider using(final World world, final SchemataConfig config) throws Exception {
    final StateStore stateStore =
            config.isProductionRuntimeType() || config.isEnvironmentRuntimeType() ?
                    resolveProductionDatabase(world, config) : resolveDeveloperDatabase(world);

    return new StateStoreProvider(world, stateStore);
  }

  private static StateStore resolveProductionDatabase(final World world, final SchemataConfig config) throws Exception {
    final Configuration databaseConfiguration = buildDatabaseConfiguration(world, config);

    final JDBCStorageDelegate<?> delegate =
            new PostgresStorageDelegate(databaseConfiguration, world.defaultLogger());

    final JDBCEntriesInstantWriter entriesWriter =
            new JDBCEntriesInstantWriter(typed(delegate), null, null);

    final ActorInstantiator instantiator =
            new JDBCStateStoreInstantiator(typed(delegate), entriesWriter, new StateStoreInitializationPrimer(world));

    return world.actorFor(StateStore.class, JDBCStateStoreActor.class, instantiator);
  }

  private static Configuration buildDatabaseConfiguration(final World world, final SchemataConfig config) throws Exception {
    Exception connectionException = null;
    for (int retryCount = 1; retryCount <= MAXIMUM_RETRIES; retryCount++) {
      try {
        world.defaultLogger().info("[Attempt {}] Connecting to database...", retryCount);

        return PostgresConfigurationProvider.configuration(
                DataFormat.Text,
                config.databaseUrl,
                config.databaseName,
                config.databaseUsername,
                config.databasePassword,
                config.databaseOriginator,
                true);

      } catch (final Exception exception) {
        world.defaultLogger().error(exception.getMessage());
        connectionException = exception;
        Thread.sleep(5000);
      }
    }
    throw connectionException;
  }

  private static StateStore resolveDeveloperDatabase(final World world) {
    return world.stage().actorFor(StateStore.class, InMemoryStateStoreActor.class, Arrays.asList(new NoopDispatcher()));
  }

  private StateStoreProvider(final World world, final StateStore stateStore) {
    this.stateStore = stateStore;
    new StateStoreInitializationPrimer(world).prime(stateStore);
  }

  @SuppressWarnings({"unchecked"})
  private static JDBCStorageDelegate<State.TextState> typed(final JDBCStorageDelegate<?> delegate) {
    return (JDBCStorageDelegate<State.TextState>) delegate;
  }

  public static class StateStoreInitializationPrimer implements InitializationPrimer {
    private final World world;

    StateStoreInitializationPrimer(final World world) {
      this.world = world;
    }

    @Override
    public void prime(final StateStore stateStore) {
      registerStatefulTypes(stateStore);
    }

    private void registerStatefulTypes(final StateStore stateStore) {
      world.defaultLogger().info("=============== PRE-INITIALIZATION ===============");
      final StatefulTypeRegistry registry = new StatefulTypeRegistry(world);

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
  }
}
