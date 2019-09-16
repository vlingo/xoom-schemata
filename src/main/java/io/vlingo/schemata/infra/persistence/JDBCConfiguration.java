// Copyright © 2012-2018 Vaughn Vernon. All rights reserved.
//
// This Source Code Form is subject to the terms of the
// Mozilla Public License, v. 2.0. If a copy of the MPL
// was not distributed with this file, You can obtain
// one at https://mozilla.org/MPL/2.0/.

package io.vlingo.schemata.infra.persistence;

import io.vlingo.symbio.store.DataFormat;
import io.vlingo.symbio.store.common.jdbc.Configuration;
import io.vlingo.symbio.store.common.jdbc.hsqldb.HSQLDBConfigurationProvider;

public class JDBCConfiguration {
  public static Configuration jdbcConfiguration() throws Exception {
    return HSQLDBConfigurationProvider.configuration(DataFormat.Native,
            "jdbc:hsqldb:mem:",
            "vlingo_schemata",
            "SA",
            "",
            "MAIN",
            true);
  }
}
