// Copyright © 2012-2018 Vaughn Vernon. All rights reserved.
//
// This Source Code Form is subject to the terms of the
// Mozilla Public License, v. 2.0. If a copy of the MPL
// was not distributed with this file, You can obtain
// one at https://mozilla.org/MPL/2.0/.

package io.vlingo.schemata;

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

import static io.vlingo.schemata.Schemata.*;

public class Bootstrap {
   static final int SCHEMATA_PORT_API = 9019;
   static final int SCHEMATA_PORT_UI = 9020;

  private static Bootstrap instance;
  private final Server apiServer;
  private final World apiWorld;

  private final Server uiServer;
  private final World uiWorld;

  public Bootstrap(final String runtimeType) throws Exception {
    apiWorld = World.startWithDefaults("vlingo-schemata-api");
    apiWorld.stageNamed(ApiStageName, Grid.class, new GridAddressFactory(IdentityGeneratorType.RANDOM));

    uiWorld = World.startWithDefaults("vlingo-schemata-ui");
    uiWorld.stageNamed(UiStageName, Grid.class, new GridAddressFactory(IdentityGeneratorType.RANDOM));


    final NoopDispatcher<TextEntry, TextState> dispatcher = new NoopDispatcher<>();

    final SchemataObjectStore schemataObjectStore = SchemataObjectStore.instance(runtimeType);
    final ObjectStore objectStore = schemataObjectStore.objectStoreFor(apiWorld, dispatcher, schemataObjectStore.persistentMappers());
    final ObjectTypeRegistry registry = new ObjectTypeRegistry(apiWorld);
    schemataObjectStore.register(registry, objectStore);

    final Stage stage = apiWorld.stageNamed(ApiStageName);

    Queries.startAll(stage, objectStore);

    final OrganizationResource organizationResource = new OrganizationResource(apiWorld);
    final UnitResource unitResource = new UnitResource(apiWorld);
    final ContextResource contextResource = new ContextResource(apiWorld);
    final SchemaResource schemaResource = new SchemaResource(apiWorld);
    final SchemaVersionResource schemaVersionResource = new SchemaVersionResource(apiWorld);
    final CodeResource codeResource = new CodeResource(apiWorld);
    final UiResource uiResource = new UiResource(apiWorld);

    Resources apiResources = Resources.are(
            organizationResource.routes(),
            unitResource.routes(),
            contextResource.routes(),
            schemaResource.routes(),
            schemaVersionResource.routes(),
            codeResource.routes(),
            uiResource.routes()
    );

    apiServer = Server.startWith(apiWorld.stage(),
      apiResources,
      SCHEMATA_PORT_API,
      Configuration.Sizing.define()
          .withDispatcherPoolSize(2)
          .withMaxBufferPoolSize(100)
          .withMaxMessageSize(4096),
      Configuration.Timing.define());

    uiServer = Server.startWith(uiWorld.stage(),
      apiResources,
      SCHEMATA_PORT_UI,
      Configuration.Sizing.define()
        .withDispatcherPoolSize(2)
        .withMaxBufferPoolSize(20)
        .withMaxMessageSize(1048576),
      Configuration.Timing.define());
    Runtime.getRuntime().addShutdownHook(new Thread(() -> {
      if (instance != null) {
        instance.apiServer.stop();
        instance.uiServer.stop();

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
