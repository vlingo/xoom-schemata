// Copyright © 2012-2018 Vaughn Vernon. All rights reserved.
//
// This Source Code Form is subject to the terms of the
// Mozilla Public License, v. 2.0. If a copy of the MPL
// was not distributed with this file, You can obtain
// one at https://mozilla.org/MPL/2.0/.

package io.vlingo.schemata.query;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import io.vlingo.common.Completes;
import io.vlingo.lattice.query.StateObjectQueryActor;
import io.vlingo.schemata.model.ContextState;
import io.vlingo.schemata.resource.data.ContextData;
import io.vlingo.symbio.store.object.MapQueryExpression;
import io.vlingo.symbio.store.object.ObjectStore;
import io.vlingo.symbio.store.object.QueryExpression;

public class ContextQueriesActor extends StateObjectQueryActor implements ContextQueries {
  private final Map<String,String> parameters;

  public ContextQueriesActor(final ObjectStore objectStore) {
    super(objectStore);
    this.parameters = new HashMap<>(3);
  }

  @Override
  public Completes<List<ContextData>> contexts(final String organizationId, final String unitId) {
    parameters.clear();
    parameters.put("organizationId", organizationId);
    parameters.put("unitId", unitId);

    final QueryExpression query =
            MapQueryExpression.using(
                    ContextState.class,
                    "SELECT * FROM TBL_CONTEXTS " +
                    "WHERE organizationId = :organizationId " +
                      "AND unitId = :unitId",
                    parameters);

    return queryAll(ContextState.class, query, (List<ContextState> states) -> ContextData.from(states));
  }

  @Override
  public Completes<ContextData> context(final String organizationId, final String unitId, final String contextId) {
    parameters.clear();
    parameters.put("organizationId", organizationId);
    parameters.put("unitId", unitId);
    parameters.put("contextId", contextId);

    final QueryExpression query =
            MapQueryExpression.using(
                    ContextState.class,
                    "SELECT * FROM TBL_CONTEXTS " +
                    "WHERE organizationId = :organizationId " +
                      "AND unitId = :unitId " +
                      "AND contextId = :contextId",
                    parameters);

    return queryObject(ContextState.class, query, (ContextState state) -> ContextData.from(state));
  }
}
