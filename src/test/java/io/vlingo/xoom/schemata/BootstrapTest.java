// Copyright © 2012-2022 VLINGO LABS. All rights reserved.
//
// This Source Code Form is subject to the terms of the
// Mozilla Public License, v. 2.0. If a copy of the MPL
// was not distributed with this file, You can obtain
// one at https://mozilla.org/MPL/2.0/.

package io.vlingo.xoom.schemata;

import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.net.URL;

import org.junit.After;
import org.junit.Test;

public class BootstrapTest {

  @Test
  public void testThatBootstrapStartsServerCleanly() throws Exception {
    XoomInitializer.main(new String[]{ "dev" });

    final int serverPort = XoomInitializer.instance().serverPort();

    final String serverURL = "http://localhost:" + serverPort;

    System.out.println("BootstrapTest: PORT: " + serverPort + " ATTEMPT TO REOPEN: " + serverURL);

    boolean clientOpenedPort;

    try {
      new URL(serverURL).openConnection().connect();

      clientOpenedPort = true;

    } catch (IOException e) {
      clientOpenedPort = false;
    }

    assertTrue("Client should be able to open server port: "  + serverPort, clientOpenedPort);
  }

  @After
  public void tearDown() throws Exception {
    io.vlingo.xoom.schemata.XoomInitializer.instance().stopServer();
  }
}
