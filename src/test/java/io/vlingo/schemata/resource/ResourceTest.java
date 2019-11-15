// Copyright © 2012-2018 Vaughn Vernon. All rights reserved.
//
// This Source Code Form is subject to the terms of the
// Mozilla Public License, v. 2.0. If a copy of the MPL
// was not distributed with this file, You can obtain
// one at https://mozilla.org/MPL/2.0/.

package io.vlingo.schemata.resource;

import org.junit.Before;

import io.vlingo.actors.Stage;
import io.vlingo.actors.World;
import io.vlingo.common.identity.IdentityGeneratorType;
import io.vlingo.lattice.grid.Grid;
import io.vlingo.lattice.grid.GridAddressFactory;
import io.vlingo.lattice.model.object.ObjectTypeRegistry;
import io.vlingo.schemata.NoopDispatcher;
import io.vlingo.schemata.Schemata;
import io.vlingo.schemata.infra.persistence.SchemataObjectStore;
import io.vlingo.schemata.query.CodeQueries;
import io.vlingo.schemata.query.ContextQueries;
import io.vlingo.schemata.query.OrganizationQueries;
import io.vlingo.schemata.query.Queries;
import io.vlingo.schemata.query.SchemaQueries;
import io.vlingo.schemata.query.SchemaVersionQueries;
import io.vlingo.schemata.query.UnitQueries;
import io.vlingo.symbio.store.DataFormat;
import io.vlingo.symbio.store.common.jdbc.hsqldb.HSQLDBConfigurationProvider;
import io.vlingo.symbio.store.object.ObjectStore;
import io.vlingo.symbio.store.object.jdbc.jdbi.JdbiOnDatabase;

public abstract class ResourceTest {
  protected ObjectStore objectStore;
  protected ObjectTypeRegistry registry;
  protected Stage stage;
  protected World world;

  protected OrganizationQueries organizationQueries;
  protected UnitQueries unitQueries;
  protected ContextQueries contextQueries;
  protected SchemaQueries schemaQueries;
  protected SchemaVersionQueries schemaVersionQueries;
  protected CodeQueries codeQueries;

  @Before
  @SuppressWarnings({ "unchecked", "rawtypes" })
  public void setUp() throws Exception {
    world = World.startWithDefaults("test-command-router");
    world.stageNamed(Schemata.ApiStageName, Grid.class, new GridAddressFactory(IdentityGeneratorType.RANDOM));
    stage = world.stageNamed(Schemata.ApiStageName);

    final SchemataObjectStore schemataObjectStore = SchemataObjectStore.instance("dev");
    registry = new ObjectTypeRegistry(world);
    objectStore = schemataObjectStore.objectStoreFor(world, new NoopDispatcher(), schemataObjectStore.persistentMappers());
    schemataObjectStore.register(registry, objectStore);

    Queries.startAll(stage, objectStore);

    organizationQueries = Queries.forOrganizations();
    unitQueries = Queries.forUnits();
    contextQueries = Queries.forContexts();
    schemaQueries = Queries.forSchemas();
    schemaVersionQueries = Queries.forSchemaVersions();
    codeQueries = Queries.forCode();
  }

  protected JdbiOnDatabase jdbi() throws Exception {
    return JdbiOnDatabase.openUsing(
            HSQLDBConfigurationProvider.configuration(
                    DataFormat.Native,
                    "jdbc:hsqldb:mem:",
                    "vlingo_schemata",
                    "SA",
                    "",
                    "MAIN",
                    true));
  }
}
