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
import io.vlingo.lattice.query.StateObjectQueryActor;
import io.vlingo.schemata.model.SchemaState;
import io.vlingo.schemata.resource.data.SchemaData;
import io.vlingo.symbio.store.MapQueryExpression;
import io.vlingo.symbio.store.object.ObjectStore;
import io.vlingo.symbio.store.QueryExpression;

public class SchemaQueriesActor extends StateObjectQueryActor implements SchemaQueries {
  private static final String ById =
          "SELECT * FROM TBL_SCHEMAS " +
          "WHERE organizationId = :organizationId " +
            "AND unitId = :unitId " +
            "AND contextId = :contextId " +
            "AND schemaId = :schemaId";

  private static final String ByName =
          "SELECT * FROM TBL_SCHEMAS " +
          "WHERE organizationId = :organizationId " +
            "AND unitId = :unitId " +
            "AND contextId = :contextId " +
            "AND name = :name";

  private static final String ByNames =
          "SELECT s.* FROM TBL_ORGANIZATIONS AS o " +
            "JOIN TBL_UNITS AS u ON u.organizationId = o.organizationId " +
            "JOIN TBL_CONTEXTS AS c ON c.unitId = u.unitId " +
            "JOIN TBL_SCHEMAS AS s ON s.contextId = c.contextId " +
            "WHERE o.name = :organization AND u.name = :unit AND c.namespace = :context AND s.name = :schema";


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

    return queryOne(ById, parameters);
  }

  @Override
  public Completes<SchemaData> schemaNamed(final String organizationId, final String unitId, final String contextId, final String name) {
    parameters.clear();
    parameters.put("organizationId", organizationId);
    parameters.put("unitId", unitId);
    parameters.put("contextId", contextId);
    parameters.put("name", name);

    return queryOne(ByName, parameters);
  }

  @Override
  public Completes<SchemaData> schemaVersionByNames(String organization, String unit, String context, String schema) {
    parameters.clear();
    parameters.put("organization", organization);
    parameters.put("unit", unit);
    parameters.put("context", context);
    parameters.put("schema", schema);

    return queryOne(ByNames, parameters);
  }

  private Completes<SchemaData> queryOne(final String query, final Map<String,String> parameters) {
    final QueryExpression expression = MapQueryExpression.using(SchemaState.class, query, parameters);

    return queryObject(SchemaState.class, expression, (SchemaState state) -> SchemaData.from(state));
  }
}
