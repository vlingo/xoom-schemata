// Copyright © 2012-2020 VLINGO LABS. All rights reserved.
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
import io.vlingo.common.Failure;
import io.vlingo.common.Outcome;
import io.vlingo.common.Success;
import io.vlingo.lattice.query.StateObjectQueryActor;
import io.vlingo.schemata.errors.EntityNotFoundException;
import io.vlingo.schemata.model.ContextState;
import io.vlingo.schemata.resource.data.ContextData;
import io.vlingo.symbio.store.MapQueryExpression;
import io.vlingo.symbio.store.object.ObjectStore;
import io.vlingo.symbio.store.QueryExpression;

public class ContextQueriesActor extends StateObjectQueryActor implements ContextQueries {
  private static final String AllQuery =
          "SELECT * FROM TBL_CONTEXTS " +
                  "WHERE organizationId = :organizationId " +
                    "AND unitId = :unitId";

  private static final String ById =
          "SELECT * FROM TBL_CONTEXTS " +
          "WHERE organizationId = :organizationId " +
            "AND unitId = :unitId " +
            "AND contextId = :contextId";

  private static final String ByNamespace =
          "SELECT * FROM TBL_CONTEXTS " +
          "WHERE organizationId = :organizationId " +
            "AND unitId = :unitId " +
            "AND namespace = :namespace";

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

    final QueryExpression query = MapQueryExpression.using(ContextState.class, AllQuery, parameters);

    return queryAll(ContextState.class, query, (List<ContextState> states) -> ContextData.from(states));
  }

  @Override
  public Completes<Outcome<EntityNotFoundException,ContextData>> context(final String organizationId, final String unitId, final String contextId) {
    parameters.clear();
    parameters.put("organizationId", organizationId);
    parameters.put("unitId", unitId);
    parameters.put("contextId", contextId);

    return queryOne(ById, parameters);
  }

  @Override
  public Completes<Outcome<EntityNotFoundException,ContextData>> context(final String organizationId, final String unitId, final String contextId, final QueryResultsCollector collector) {
    final Completes<Outcome<EntityNotFoundException,ContextData>> data = context(organizationId, unitId, contextId);
    collector.expectContext(data.andThen(Outcome::getOrNull));
    return data;
  }

  @Override
  public Completes<Outcome<EntityNotFoundException,ContextData>> contextOfNamespace(final String organizationId, final String unitId, final String namespace) {
    parameters.clear();
    parameters.put("organizationId", organizationId);
    parameters.put("unitId", unitId);
    parameters.put("namespace", namespace);

    return queryOne(ByNamespace, parameters);
  }

  @Override
  public Completes<Outcome<EntityNotFoundException,ContextData>> contextOfNamespace(final String organizationId, final String unitId, final String namespace, final QueryResultsCollector collector) {
    final Completes<Outcome<EntityNotFoundException,ContextData>> data = contextOfNamespace(organizationId, unitId, namespace);
    collector.expectContext(data.andThen(Outcome::getOrNull));
    return data;
  }

  private Completes<Outcome<EntityNotFoundException,ContextData>> queryOne(final String query, final Map<String,String> parameters) {
    final QueryExpression expression = MapQueryExpression.using(ContextState.class, query, parameters);

    return queryObject(ContextState.class, expression,
            (ContextState state) -> state == null
                    ? Failure.of(new EntityNotFoundException("Context", parameters))
                    : Success.of(ContextData.from(state)));
  }
}
