// Copyright © 2012-2018 Vaughn Vernon. All rights reserved.
//
// This Source Code Form is subject to the terms of the
// Mozilla Public License, v. 2.0. If a copy of the MPL
// was not distributed with this file, You can obtain
// one at https://mozilla.org/MPL/2.0/.

package io.vlingo.schemata;

import io.vlingo.actors.World;
import io.vlingo.http.resource.Configuration;
import io.vlingo.http.resource.Resources;
import io.vlingo.http.resource.Server;
import io.vlingo.lattice.model.sourcing.SourcedTypeRegistry;
import io.vlingo.schemata.infra.persistence.EntryAdapters;
import io.vlingo.schemata.resource.MockApiResource;
import io.vlingo.schemata.resource.SchemaResource;
import io.vlingo.schemata.resource.UiResource;
import io.vlingo.symbio.store.journal.Journal;
import io.vlingo.symbio.store.journal.inmemory.InMemoryJournalActor;

public class Bootstrap {
  private static final int SCHEMATA_PORT = 9019;

  private static Bootstrap instance;
  private final Server server;
  private final World world;

  public Bootstrap() {
    world = World.startWithDefaults("vlingo-schemata");

    NoopDispatcher dispatcher = new NoopDispatcher();
    Journal<String> journal = Journal.using(world.stage(), InMemoryJournalActor.class, dispatcher);

    SourcedTypeRegistry registry = new SourcedTypeRegistry(world);
    EntryAdapters.register(registry, journal);

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

  static Bootstrap instance() {
    if (instance == null) {
      instance = new Bootstrap();
    }
    return instance;
  }

  public static void main(final String[] args) {
    System.out.println("=======================");
    System.out.println("service: vlingo-schemata.");
    System.out.println("=======================");
    Bootstrap.instance();
  }
}
