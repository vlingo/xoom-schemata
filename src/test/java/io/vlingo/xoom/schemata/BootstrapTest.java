// Copyright © 2012-2020 VLINGO LABS. All rights reserved.
//
// This Source Code Form is subject to the terms of the
// Mozilla Public License, v. 2.0. If a copy of the MPL
// was not distributed with this file, You can obtain
// one at https://mozilla.org/MPL/2.0/.

package io.vlingo.xoom.schemata;

import org.junit.After;
import org.junit.Test;

import java.io.IOException;
import java.net.URL;

import static org.junit.Assert.fail;

public class BootstrapTest {

  @Test
  public void testThatBootstrapStartsServerCleanly() throws Exception {
    XoomInitializer.main(new String[]{"test"});

    try {
      new URL("http://127.0.0.1:19090").openConnection().connect();
    } catch (IOException e) {
      fail("Server did not open port 19090:" + e.getMessage());
    }
  }

  @After
  public void tearDown() throws Exception {
    io.vlingo.xoom.schemata.XoomInitializer.instance().stopServer();
  }
}
