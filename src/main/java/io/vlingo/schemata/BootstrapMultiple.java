// Copyright © 2012-2018 Vaughn Vernon. All rights reserved.
//
// This Source Code Form is subject to the terms of the
// Mozilla Public License, v. 2.0. If a copy of the MPL
// was not distributed with this file, You can obtain
// one at https://mozilla.org/MPL/2.0/.

package io.vlingo.schemata;

import io.vlingo.actors.Stage;
import io.vlingo.actors.World;
import io.vlingo.common.Completes;
import io.vlingo.common.identity.IdentityGeneratorType;
import io.vlingo.http.Body;
import io.vlingo.http.Response;
import io.vlingo.http.resource.*;
import io.vlingo.lattice.grid.Grid;
import io.vlingo.lattice.grid.GridAddressFactory;
import io.vlingo.lattice.model.object.ObjectTypeRegistry;
import io.vlingo.schemata.infra.persistence.SchemataObjectStore;
import io.vlingo.schemata.query.Queries;
import io.vlingo.schemata.resource.*;
import io.vlingo.symbio.BaseEntry.TextEntry;
import io.vlingo.symbio.State.TextState;
import io.vlingo.symbio.store.object.ObjectStore;

import static io.vlingo.http.Response.Status.Ok;
import static io.vlingo.schemata.Schemata.StageName;

public class BootstrapMultiple {
  private static final int SCHEMATA_PORT = 9019;

  private static BootstrapMultiple instance;
  private final Server server1;
  private final Server server2;
  private final World world;

  /**
   * Demonstrates that running two servers simultaneously leads to hanging requests on both.
   *
   * To reproduce:
   * * Run this class
   * * Try to access http://localhost:9019 or http://localhost:9020
   *
   * Once you remove one of the two servers, requests on the remaining one finish as expected
   */
  public BootstrapMultiple() throws Exception {
    world = World.startWithDefaults("test-world");
    world.stageNamed(StageName, Grid.class, new GridAddressFactory(IdentityGeneratorType.RANDOM));

     server1 = Server.startWith(world.stage(),
       Resources.are(new TestResource(world,"foo").routes()),
      9019,
      Configuration.Sizing.define(),
      Configuration.Timing.define());

     server2 = Server.startWith(world.stage(),
       Resources.are(new TestResource(world,"bar").routes()),
      9020,
      Configuration.Sizing.define(),
      Configuration.Timing.define());

    Runtime.getRuntime().addShutdownHook(new Thread(() -> {
      if (instance != null) {
        instance.server1.stop();
        instance.server2.stop();

        System.out.println("stopping");
      }
    }));
  }

  static BootstrapMultiple instance() throws Exception {
    if (instance == null) {
      instance = new BootstrapMultiple();
    }
    return instance;
  }

  public static void main(final String[] args) throws Exception {
    System.out.println("starting");
    BootstrapMultiple.instance();
  }
}
