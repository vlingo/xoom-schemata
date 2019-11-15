// Copyright © 2012-2018 Vaughn Vernon. All rights reserved.
//
// This Source Code Form is subject to the terms of the
// Mozilla Public License, v. 2.0. If a copy of the MPL
// was not distributed with this file, You can obtain
// one at https://mozilla.org/MPL/2.0/.

package io.vlingo.schemata;

import org.junit.Test;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.URL;
import java.net.URLConnection;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.fail;

public class BootstrapTest {

  @Test
  public void testThatBootstrapStartsServerCleanly() throws Exception {
    final Bootstrap bootstrap = Bootstrap.instance("dev");
    assertNotNull(bootstrap);

    try {
      new URL("http://127.0.0.1:"+Bootstrap.SCHEMATA_PORT_UI).openConnection().connect();
      new URL("http://127.0.0.1:"+Bootstrap.SCHEMATA_PORT_API).openConnection().connect();
    } catch (IOException e) {
      fail("API or UI port not open:" + e.getMessage());
    }
  }
}
