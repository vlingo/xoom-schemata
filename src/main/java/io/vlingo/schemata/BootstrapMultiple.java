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

import static io.vlingo.schemata.Schemata.StageName;

public class BootstrapMultiple {
  private static final int SCHEMATA_PORT = 9019;

  private static BootstrapMultiple instance;
  private final Server server1;
  private final Server server2;
  private final World world1;
  private final World world2;

  /**
   * Demonstrates that running two servers simultaneously leads to hanging requests on both.
   * <p>
   * To reproduce:
   * * Run this class
   * * Try to access http://localhost:9019 or http://localhost:9020
   * <p>
   * Once you remove one of the two servers, requests on the remaining one finish as expected
   */
  public BootstrapMultiple() throws Exception {
    world1 = World.startWithDefaults("test-world-1");
    world2 = World.startWithDefaults("test-world-2");

    server1 = Server.startWith(world1.stageNamed("foo"),
      Resources.are(new TestResource(world1, "foo").routes()),
      9019,
      Configuration.Sizing.define(),
      Configuration.Timing.define());

    server2 = Server.startWith(world2.stageNamed("bar"),
      Resources.are(new TestResource(world2, "bar").routes()),
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
