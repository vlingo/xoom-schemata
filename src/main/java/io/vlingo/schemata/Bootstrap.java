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
import io.vlingo.schemata.resource.*;
import io.vlingo.symbio.BaseEntry.TextEntry;
import io.vlingo.symbio.State.TextState;
import io.vlingo.symbio.store.object.ObjectStore;

public class Bootstrap {
  private static final int SCHEMATA_PORT = 9019;

  private static Bootstrap instance;
  private final Server server;
  private final World world;

  public Bootstrap(final String runtimeType) throws Exception {
    world = World.startWithDefaults("vlingo-schemata");
    world.stageNamed(StageName, Grid.class, new GridAddressFactory(IdentityGeneratorType.RANDOM));

    final NoopDispatcher<TextEntry, TextState> dispatcher = new NoopDispatcher<>();

    final SchemataObjectStore schemataObjectStore = SchemataObjectStore.instance(runtimeType);
    final ObjectStore objectStore = schemataObjectStore.objectStoreFor(world, dispatcher, schemataObjectStore.persistentMappers());
    final ObjectTypeRegistry registry = new ObjectTypeRegistry(world);
    schemataObjectStore.register(registry, objectStore);

    final Stage stage = world.stageNamed(StageName);

    Queries.startAll(stage, objectStore);

    final OrganizationResource organizationResource = new OrganizationResource(world);
    final UnitResource unitResource = new UnitResource(world);
    final ContextResource contextResource = new ContextResource(world);
    final SchemaResource schemaResource = new SchemaResource(world);
    final SchemaVersionResource schemaVersionResource = new SchemaVersionResource(world);
    final CodeResource codeResource = new CodeResource(world);
    final UiResource uiResource = new UiResource();

    Resources allResources = Resources.are(
            organizationResource.routes(),
            unitResource.routes(),
            contextResource.routes(),
            schemaResource.routes(),
            schemaVersionResource.routes(),
            codeResource.routes(),
            uiResource.routes()
    );

    server = Server.startWith(world.stage(),
      allResources,
      SCHEMATA_PORT,
      Configuration.Sizing.define().withDispatcherPoolSize(2).withMaxMessageSize(16777215),
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

  static Bootstrap instance(final String runtimeType) throws Exception {
    if (instance == null) {
      instance = new Bootstrap(runtimeType);
    }
    return instance;
  }

  public static void main(final String[] args) throws Exception {
    System.out.println("=========================");
    System.out.println("service: vlingo-schemata.");
    System.out.println("=========================");
    Bootstrap.instance(args[0]);
  }
}
