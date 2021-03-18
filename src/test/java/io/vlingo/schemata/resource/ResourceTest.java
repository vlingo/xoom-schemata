// Copyright © 2012-2020 VLINGO LABS. All rights reserved.
//
// This Source Code Form is subject to the terms of the
// Mozilla Public License, v. 2.0. If a copy of the MPL
// was not distributed with this file, You can obtain
// one at https://mozilla.org/MPL/2.0/.

package io.vlingo.schemata.resource;

import io.vlingo.actors.Grid;
import io.vlingo.actors.World;
import io.vlingo.cluster.model.Properties;
import io.vlingo.http.Response;
import io.vlingo.http.ResponseHeader;
import io.vlingo.lattice.model.sourcing.SourcedTypeRegistry;
import io.vlingo.schemata.Schemata;
import io.vlingo.schemata.SchemataConfig;
import io.vlingo.schemata.infra.persistence.ProjectionDispatcherProvider;
import io.vlingo.schemata.infra.persistence.StateStoreProvider;
import io.vlingo.schemata.infra.persistence.StorageProvider;
import io.vlingo.schemata.query.*;
import io.vlingo.symbio.store.journal.Journal;
import io.vlingo.xoom.Boot;
import org.junit.After;
import org.junit.Before;

import java.io.IOException;

public abstract class ResourceTest {
  protected Journal<String> journal;
  protected SourcedTypeRegistry registry;
  protected Grid stage;
  protected World world;

  protected OrganizationQueries organizationQueries;
  protected UnitQueries unitQueries;
  protected ContextQueries contextQueries;
  protected SchemaQueries schemaQueries;
  protected SchemaVersionQueries schemaVersionQueries;
  protected CodeQueries codeQueries;

  @Before
  public void setUp() throws Exception {
    // TODO: Start an actual Grid here using Grid.start(...). Needs a test grid configuration first
    stage = Boot.start("test-command-router", Schemata.NodeName, clusterProperties());
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

  public Properties clusterProperties() {
    try {
      final java.util.Properties properties = new java.util.Properties();
      properties.load(this.getClass().getResourceAsStream("/vlingo-cluster.properties"));
      return Properties.openForTest(properties);
    } catch (IOException e) {
      System.out.println("Unable to load cluster properties for Schemata.");
      return null;
    }
  }
}
