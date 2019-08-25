// Copyright © 2012-2018 Vaughn Vernon. All rights reserved.
//
// This Source Code Form is subject to the terms of the
// Mozilla Public License, v. 2.0. If a copy of the MPL
// was not distributed with this file, You can obtain
// one at https://mozilla.org/MPL/2.0/.

package io.vlingo.schemata;

import static io.vlingo.schemata.Schemata.StageName;

import io.vlingo.actors.Stage;
import io.vlingo.actors.World;
import io.vlingo.common.identity.IdentityGeneratorType;
import io.vlingo.http.resource.Configuration;
import io.vlingo.http.resource.Resources;
import io.vlingo.http.resource.Server;
import io.vlingo.lattice.grid.Grid;
import io.vlingo.lattice.grid.GridAddressFactory;
import io.vlingo.lattice.model.object.ObjectTypeRegistry;
import io.vlingo.schemata.infra.persistence.SchemataObjectStore;
import io.vlingo.schemata.query.Queries;
import io.vlingo.schemata.resource.ContextResource;
import io.vlingo.schemata.resource.OrganizationResource;
import io.vlingo.schemata.resource.SchemaResource;
import io.vlingo.schemata.resource.SchemaVersionResource;
import io.vlingo.schemata.resource.UnitResource;
import io.vlingo.symbio.BaseEntry.TextEntry;
import io.vlingo.symbio.State.TextState;
import io.vlingo.symbio.store.DataFormat;
import io.vlingo.symbio.store.common.jdbc.hsqldb.HSQLDBConfigurationProvider;
import io.vlingo.symbio.store.object.ObjectStore;
import io.vlingo.symbio.store.object.jdbc.jdbi.JdbiOnDatabase;

public class Bootstrap {
  private static final int SCHEMATA_PORT = 9019;

  private static Bootstrap instance;
  private final Server server;
  private final World world;

  public Bootstrap() throws Exception {
    world = World.startWithDefaults("vlingo-schemata");
    world.stageNamed("vlingo-schemata-grid", Grid.class, new GridAddressFactory(IdentityGeneratorType.RANDOM));

    final NoopDispatcher<TextEntry, TextState> dispatcher = new NoopDispatcher<>();

    final io.vlingo.symbio.store.common.jdbc.Configuration configuration = jdbcConfiguration();
    final SchemataObjectStore schemataObjectStore = new SchemataObjectStore(configuration);
    final ObjectStore objectStore = schemataObjectStore.objectStoreFor(world, dispatcher, schemataObjectStore.persistentMappers());
    final ObjectTypeRegistry registry = new ObjectTypeRegistry(world);
    schemataObjectStore.register(registry, objectStore);

    final Stage stage = world.stageNamed(StageName);

    final OrganizationResource organizationResource =
            new OrganizationResource(world, Queries.forOrganizations(stage, jdbiFrom(configuration)));

    final UnitResource unitResource =
            new UnitResource(world, Queries.forUnits(stage, jdbiFrom(configuration)));

    final ContextResource contextResource =
            new ContextResource(world, Queries.forContexts(stage, jdbiFrom(configuration)));

    final SchemaResource schemaResource =
            new SchemaResource(world, Queries.forSchemas(stage, jdbiFrom(configuration)));

    final SchemaVersionResource schemaVersionResource =
            new SchemaVersionResource(world, Queries.forSchemaVersions(stage, jdbiFrom(configuration)));

    Resources allResources = Resources.are(
            organizationResource.routes(),
            unitResource.routes(),
            contextResource.routes(),
            schemaResource.routes(),
            schemaVersionResource.routes()

//      SchemaResource.asResource(),
//      UiResource.asResource(),
//      MockApiResource.asResource()
    );

    server = Server.startWith(world.stage(),
      allResources,
      SCHEMATA_PORT,
      Configuration.Sizing.define().withMaxMessageSize(16777215),
      Configuration.Timing.define());
    Runtime.getRuntime().addShutdownHook(new Thread(() -> {
      if (instance != null) {
        instance.server.stop();

        System.out.println("\n");
        System.out.println("=========================");
        System.out.println("Stopping vlingo-schemata.");
        System.out.println("=========================");
      }
    }));
  }

  static Bootstrap instance() throws Exception {
    if (instance == null) {
      instance = new Bootstrap();
    }
    return instance;
  }

  public static void main(final String[] args) throws Exception {
    System.out.println("=========================");
    System.out.println("service: vlingo-schemata.");
    System.out.println("=========================");
    Bootstrap.instance();
  }

  private JdbiOnDatabase jdbiFrom(
          final io.vlingo.symbio.store.common.jdbc.Configuration configuration) {
    return JdbiOnDatabase.openUsing(io.vlingo.symbio.store.common.jdbc.Configuration.cloneOf(configuration));
  }

  private io.vlingo.symbio.store.common.jdbc.Configuration jdbcConfiguration() throws Exception {
    return HSQLDBConfigurationProvider.configuration(
            DataFormat.Native,
            "jdbc:hsqldb:mem:",
            "vlingo_schemata",
            "SA",
            "",
            "MAIN",
            true);
  }
}
