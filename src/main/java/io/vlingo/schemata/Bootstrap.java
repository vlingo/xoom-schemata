// Copyright © 2012-2020 VLINGO LABS. All rights reserved.
//
// This Source Code Form is subject to the terms of the
// Mozilla Public License, v. 2.0. If a copy of the MPL
// was not distributed with this file, You can obtain
// one at https://mozilla.org/MPL/2.0/.

package io.vlingo.schemata;

import io.vlingo.actors.GridAddressFactory;
import io.vlingo.actors.Stage;
import io.vlingo.actors.World;
import io.vlingo.common.identity.IdentityGeneratorType;
import io.vlingo.http.resource.Configuration;
import io.vlingo.http.resource.Resources;
import io.vlingo.http.resource.Server;
import io.vlingo.schemata.infra.persistence.ProjectionDispatcherProvider;
import io.vlingo.schemata.infra.persistence.StateStoreProvider;
import io.vlingo.schemata.infra.persistence.StorageProvider;
import io.vlingo.schemata.resource.*;

import java.io.IOException;
import java.net.ServerSocket;

import static io.vlingo.schemata.Schemata.StageName;

public class Bootstrap {

  private static Bootstrap instance;
  private final int port;
  private final Server server;
  private final World world;

  public Bootstrap(final String runtimeType) throws Exception {
    SchemataConfig config = SchemataConfig.forRuntime(runtimeType);

    world = World.startWithDefaults("vlingo-schemata");
    // TODO: Start an actual Grid here using Grid.start(...). Needs a complete grid configuration first
    world.stageNamed(StageName, Stage.class, new GridAddressFactory(IdentityGeneratorType.RANDOM));

    final StateStoreProvider stateStoreProvider = StateStoreProvider.using(world);
    final ProjectionDispatcherProvider projectionDispatcherProvider =
            ProjectionDispatcherProvider.using(world.stage(), stateStoreProvider.stateStore);

    StorageProvider storageProvider = StorageProvider.with(world, config, stateStoreProvider.stateStore, projectionDispatcherProvider.storeDispatcher);

    final OrganizationResource organizationResource = new OrganizationResource(world, storageProvider.organizationQueries);
    final UnitResource unitResource = new UnitResource(world, storageProvider.unitQueries);
    final ContextResource contextResource = new ContextResource(world, storageProvider.contextQueries);
    final SchemaResource schemaResource = new SchemaResource(world, storageProvider.schemaQueries);
    final SchemaVersionResource schemaVersionResource = new SchemaVersionResource(world);
    final CodeResource codeResource = new CodeResource(world);
    final UiResource uiResource = new UiResource(world);

    Resources allResources = Resources.are(
            organizationResource.routes(),
            unitResource.routes(),
            contextResource.routes(),
            schemaResource.routes(),
            schemaVersionResource.routes(),
            codeResource.routes(),
            uiResource.routes()
    );

    port = config.randomPort ? nextFreePort(9019, 9100) : config.serverPort;

    server = Server.startWith(world.stage(),
      allResources,
      port,
      Configuration.Sizing.define()
          .withDispatcherPoolSize(2)
          .withMaxBufferPoolSize(100)
          .withMaxMessageSize(4096),
      Configuration.Timing.defineWith(7, 3,100));
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

  void stopServer() throws Exception {
    if (instance == null) {
      throw new IllegalStateException("Schemata server not running");
    }
    instance.server.stop();
  }


  public static void main(final String[] args) throws Exception {
    System.out.println("=========================");
    System.out.println("service: vlingo-schemata.");
    System.out.println("=========================");
    Bootstrap.instance(args[0]);
  }

  public int __internal__only_test_port() {
    return port;
  }

  public World __internal__only_test_world() {
    return world;
  }

  private int nextFreePort(int from, int to) throws IOException {
    int port = from;
    while (port < to) {
      if (isPortFree(port)) {
        return port;
      } else {
        port++;
      }
    }
    throw new IOException("No open port in range " + from + " to " + to);
  }

  private boolean isPortFree(int port) {
    try {
      new ServerSocket(port).close();
      return true;
    } catch (IOException e) {
      return false;
    }
  }
}
