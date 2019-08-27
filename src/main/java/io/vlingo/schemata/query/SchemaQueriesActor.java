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
import io.vlingo.schemata.model.SchemaState;
import io.vlingo.schemata.resource.data.SchemaData;
import io.vlingo.symbio.store.object.MapQueryExpression;
import io.vlingo.symbio.store.object.ObjectStore;
import io.vlingo.symbio.store.object.QueryExpression;

public class SchemaQueriesActor extends StateObjectQueryActor implements SchemaQueries {
  private final Map<String,String> parameters;

  public SchemaQueriesActor(final ObjectStore objectStore) {
    super(objectStore);
    this.parameters = new HashMap<>(4);
  }

  @Override
  public Completes<List<SchemaData>> schemas(final String organizationId, final String unitId, final String contextId) {
    parameters.clear();
    parameters.put("organizationId", organizationId);
    parameters.put("unitId", unitId);
    parameters.put("contextId", contextId);

    final QueryExpression query =
            MapQueryExpression.using(
                    SchemaState.class,
                    "SELECT * FROM TBL_SCHEMAS " +
                    "WHERE organizationId = :organizationId " +
                      "AND unitId = :unitId " +
                      "AND contextId = :contextId",
                    parameters);

    return queryAll(SchemaState.class, query, (List<SchemaState> states) -> SchemaData.from(states));
  }

  @Override
  public Completes<SchemaData> schema(final String organizationId, final String unitId, final String contextId, final String schemaId) {
    parameters.clear();
    parameters.put("organizationId", organizationId);
    parameters.put("unitId", unitId);
    parameters.put("contextId", contextId);
    parameters.put("schemaId", schemaId);

    final QueryExpression query =
            MapQueryExpression.using(
                    SchemaState.class,
                    "SELECT * FROM TBL_SCHEMAS " +
                    "WHERE organizationId = :organizationId " +
                      "AND unitId = :unitId " +
                      "AND contextId = :contextId " +
                      "AND schemaId = :schemaId",
                    parameters);

    return queryObject(SchemaState.class, query, (SchemaState state) -> SchemaData.from(state));
  }
}
