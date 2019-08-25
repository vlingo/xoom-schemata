// Copyright © 2012-2018 Vaughn Vernon. All rights reserved.
//
// This Source Code Form is subject to the terms of the
// Mozilla Public License, v. 2.0. If a copy of the MPL
// was not distributed with this file, You can obtain
// one at https://mozilla.org/MPL/2.0/.

package io.vlingo.schemata.resource;

import io.vlingo.symbio.store.DataFormat;
import io.vlingo.symbio.store.common.jdbc.hsqldb.HSQLDBConfigurationProvider;
import io.vlingo.symbio.store.object.jdbc.jdbi.JdbiOnDatabase;

public abstract class ResourceTest {

  protected JdbiOnDatabase jdbi() throws Exception {
    return JdbiOnDatabase.openUsing(
            HSQLDBConfigurationProvider.configuration(
                    DataFormat.Native,
                    "jdbc:hsqldb:mem:",
                    "vlingo_schemata",
                    "SA",
                    "",
                    "MAIN",
                    true));
  }
}
