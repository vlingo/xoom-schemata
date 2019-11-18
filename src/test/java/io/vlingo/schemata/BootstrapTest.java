// Copyright © 2012-2018 Vaughn Vernon. All rights reserved.
//
// This Source Code Form is subject to the terms of the
// Mozilla Public License, v. 2.0. If a copy of the MPL
// was not distributed with this file, You can obtain
// one at https://mozilla.org/MPL/2.0/.

package io.vlingo.schemata;

import org.junit.Test;

import java.io.IOException;
import java.net.URL;

import static junit.framework.TestCase.fail;
import static org.junit.Assert.assertNotNull;

public class BootstrapTest {

  @Test
  public void testThatBootstrapStartsServerCleanly() throws Exception {
    final Bootstrap bootstrap = Bootstrap.instance("dev");
    assertNotNull(bootstrap);

    try {
      new URL("http://127.0.0.1:" + Bootstrap.SCHEMATA_PORT).openConnection().connect();
    } catch (IOException e) {
      fail("Server did not open port " + Bootstrap.SCHEMATA_PORT + ":" + e.getMessage());
    }
  }
}
