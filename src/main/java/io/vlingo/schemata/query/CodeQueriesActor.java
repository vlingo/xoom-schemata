// Copyright © 2012-2018 Vaughn Vernon. All rights reserved.
//
// This Source Code Form is subject to the terms of the
// Mozilla Public License, v. 2.0. If a copy of the MPL
// was not distributed with this file, You can obtain
// one at https://mozilla.org/MPL/2.0/.

package io.vlingo.schemata.query;

import java.util.HashMap;
import java.util.Map;

import io.vlingo.common.Completes;
import io.vlingo.lattice.query.StateObjectQueryActor;
import io.vlingo.schemata.resource.data.AuthorizationData;
import io.vlingo.schemata.resource.data.PathData;
import io.vlingo.schemata.resource.data.SchemaVersionData;
import io.vlingo.symbio.store.object.ObjectStore;

public class CodeQueriesActor extends StateObjectQueryActor implements CodeQueries {
  private Map<String,String> parameters;

  public CodeQueriesActor(final ObjectStore objectStore) {
    super(objectStore);
    this.parameters = new HashMap<>();
  }

  @Override
  public Completes<SchemaVersionData> schemaVersionFor(final AuthorizationData authorization, final PathData path) {

//    final String query =
//            "SELECT SELECT * FROM TBL_SCHEMAVERSIONS " +
//            "";

    return completes().with(SchemaVersionData.from("O1", "U1", "C1", "S1", "V1", "event ItHappened {}", "Initial revision.", "Draft", "0.0.0", "1.0.0"));
  }
}
