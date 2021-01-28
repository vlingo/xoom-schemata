// Copyright © 2012-2020 VLINGO LABS. All rights reserved.
//
// This Source Code Form is subject to the terms of the
// Mozilla Public License, v. 2.0. If a copy of the MPL
// was not distributed with this file, You can obtain
// one at https://mozilla.org/MPL/2.0/.

package io.vlingo.schemata.resource;

import org.junit.Before;

import io.vlingo.actors.Grid;
import io.vlingo.actors.GridAddressFactory;
import io.vlingo.actors.Stage;
import io.vlingo.actors.World;
import io.vlingo.common.identity.IdentityGeneratorType;
import io.vlingo.http.Response;
import io.vlingo.http.ResponseHeader;
import io.vlingo.lattice.model.sourcing.SourcedTypeRegistry;
import io.vlingo.schemata.Schemata;
import io.vlingo.schemata.SchemataConfig;
import io.vlingo.schemata.infra.persistence.ProjectionDispatcherProvider;
import io.vlingo.schemata.infra.persistence.StateStoreProvider;
import io.vlingo.schemata.infra.persistence.StorageProvider;
import io.vlingo.schemata.query.CodeQueries;
import io.vlingo.schemata.query.ContextQueries;
import io.vlingo.schemata.query.OrganizationQueries;
import io.vlingo.schemata.query.SchemaQueries;
import io.vlingo.schemata.query.SchemaVersionQueries;
import io.vlingo.schemata.query.UnitQueries;
import io.vlingo.symbio.store.journal.Journal;

public abstract class ResourceTest {
  protected Journal<String> journal;
  protected SourcedTypeRegistry registry;
  protected Stage stage;
  protected Grid grid;
  protected World world;

  protected OrganizationQueries organizationQueries;
  protected UnitQueries unitQueries;
  protected ContextQueries contextQueries;
  protected SchemaQueries schemaQueries;
  protected SchemaVersionQueries schemaVersionQueries;
  protected CodeQueries codeQueries;

  @Before
  public void setUp() throws Exception {
    world = World.startWithDefaults("test-command-router");
    // TODO: Start an actual Grid here using Grid.start(...). Needs a test grid configuration first
    world.stageNamed(Schemata.StageName, Stage.class, new GridAddressFactory(IdentityGeneratorType.RANDOM));
    stage = world.stageNamed(Schemata.StageName);

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

  protected String extractResourceIdFrom(final Response response) {
    final String[] parts = response.headerValueOr(ResponseHeader.Location, null).split("/");
    return parts[parts.length - 1];
  }
}
