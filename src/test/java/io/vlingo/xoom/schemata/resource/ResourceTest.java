// Copyright © 2012-2023 VLINGO LABS. All rights reserved.
//
// This Source Code Form is subject to the terms of the
// Mozilla Public License, v. 2.0. If a copy of the MPL
// was not distributed with this file, You can obtain
// one at https://mozilla.org/MPL/2.0/.

package io.vlingo.xoom.schemata.resource;

import io.vlingo.xoom.cluster.StaticClusterConfiguration;
import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;

import io.vlingo.xoom.actors.Configuration;
import io.vlingo.xoom.actors.World;
import io.vlingo.xoom.actors.plugin.mailbox.concurrentqueue.ConcurrentQueueMailboxPlugin.ConcurrentQueueMailboxPluginConfiguration;
import io.vlingo.xoom.http.Response;
import io.vlingo.xoom.http.ResponseHeader;
import io.vlingo.xoom.lattice.grid.Grid;
import io.vlingo.xoom.schemata.SchemataConfig;
import io.vlingo.xoom.schemata.infra.persistence.ProjectionDispatcherProvider;
import io.vlingo.xoom.schemata.infra.persistence.StateStoreProvider;
import io.vlingo.xoom.schemata.infra.persistence.StorageProvider;
import io.vlingo.xoom.schemata.query.CodeQueries;
import io.vlingo.xoom.schemata.query.ContextQueries;
import io.vlingo.xoom.schemata.query.OrganizationQueries;
import io.vlingo.xoom.schemata.query.SchemaQueries;
import io.vlingo.xoom.schemata.query.SchemaVersionQueries;
import io.vlingo.xoom.schemata.query.UnitQueries;

public abstract class ResourceTest {
  protected Grid stage;
  protected World world;

  protected OrganizationQueries organizationQueries;
  protected UnitQueries unitQueries;
  protected ContextQueries contextQueries;
  protected SchemaQueries schemaQueries;
  protected SchemaVersionQueries schemaVersionQueries;
  protected CodeQueries codeQueries;

  @BeforeClass
  public static void clear() {
    StorageProvider.clear();
  }

  @Before
  public void setUp() throws Exception {
    final Configuration actorsConfig =
            Configuration.define().with(
                    ConcurrentQueueMailboxPluginConfiguration
                      .define()
                      .defaultMailbox()
                      .numberOfDispatchersFactor(0)
                      .numberOfDispatchers(12)
                      .dispatcherThrottlingCount(10));

    StaticClusterConfiguration staticConfiguration = StaticClusterConfiguration.oneNode();
    stage = Grid.start("test-command-router",
            actorsConfig,
            staticConfiguration.properties,
            staticConfiguration.propertiesOf(0));
    world = stage.world();

    final SchemataConfig config = SchemataConfig.forRuntime(SchemataConfig.RUNTIME_TYPE_DEV);

    final StateStoreProvider stateStoreProvider = StateStoreProvider.using(world, config);
    final ProjectionDispatcherProvider projectionDispatcherProvider =
            ProjectionDispatcherProvider.using(world.stage(), stateStoreProvider.stateStore);

    StorageProvider storageProvider = StorageProvider.newInstance(world, stateStoreProvider.stateStore, projectionDispatcherProvider.storeDispatcher, config);

    organizationQueries = storageProvider.organizationQueries;
    unitQueries = storageProvider.unitQueries;
    contextQueries = storageProvider.contextQueries;
    schemaQueries = storageProvider.schemaQueries;
    schemaVersionQueries = storageProvider.schemaVersionQueries;
    codeQueries = storageProvider.codeQueries;
  }

  @After
  public void tearDown() {
    world.terminate();
  }

  protected String extractResourceIdFrom(final Response response) {
    final String[] parts = response.headerValueOr(ResponseHeader.Location, null).split("/");
    return parts[parts.length - 1];
  }
}
