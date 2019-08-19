// Copyright © 2012-2018 Vaughn Vernon. All rights reserved.
//
// This Source Code Form is subject to the terms of the
// Mozilla Public License, v. 2.0. If a copy of the MPL
// was not distributed with this file, You can obtain
// one at https://mozilla.org/MPL/2.0/.

package io.vlingo.schemata;

import io.vlingo.actors.World;
import io.vlingo.common.identity.IdentityGeneratorType;
import io.vlingo.http.resource.Configuration;
import io.vlingo.http.resource.Resources;
import io.vlingo.http.resource.Server;
import io.vlingo.lattice.grid.Grid;
import io.vlingo.lattice.grid.GridAddressFactory;
import io.vlingo.lattice.model.sourcing.SourcedTypeRegistry;
import io.vlingo.schemata.infra.persistence.EntryAdapters;
import io.vlingo.lattice.model.object.ObjectTypeRegistry;
import io.vlingo.schemata.infra.persistence.SchemataObjectStore;
import io.vlingo.schemata.resource.MockApiResource;
import io.vlingo.schemata.resource.SchemaResource;
import io.vlingo.schemata.resource.UiResource;
import io.vlingo.symbio.store.object.ObjectStore;

public class Bootstrap {
  private static final int SCHEMATA_PORT = 9019;

  private static Bootstrap instance;
  private final Server server;
  private final World world;

  public Bootstrap() throws Exception {
    world = World.startWithDefaults("vlingo-schemata");
    world.stageNamed("vlingo-schemata-grid", Grid.class, new GridAddressFactory(IdentityGeneratorType.RANDOM));

    final NoopDispatcher dispatcher = new NoopDispatcher();

    final SchemataObjectStore schemataObjectStore = new SchemataObjectStore("SA", "");
    final ObjectStore objectStore = schemataObjectStore.objectStoreFor(world, dispatcher, schemataObjectStore.persistentMappers());
    final ObjectTypeRegistry registry = new ObjectTypeRegistry(world);
    schemataObjectStore.register(registry, objectStore);

    Resources allResources = Resources.are(
      SchemaResource.asResource(),
      UiResource.asResource(),
      MockApiResource.asResource()
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
        System.out.println("=======================");
        System.out.println("Stopping vlingo-schemata.");
        System.out.println("=======================");
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
    System.out.println("=======================");
    System.out.println("service: vlingo-schemata.");
    System.out.println("=======================");
    Bootstrap.instance();
  }
}
