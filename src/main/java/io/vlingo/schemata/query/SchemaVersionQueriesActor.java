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
import io.vlingo.schemata.model.SchemaVersionState;
import io.vlingo.schemata.resource.data.SchemaVersionData;
import io.vlingo.symbio.store.object.MapQueryExpression;
import io.vlingo.symbio.store.object.ObjectStore;
import io.vlingo.symbio.store.object.QueryExpression;

public class SchemaVersionQueriesActor extends StateObjectQueryActor implements SchemaVersionQueries {
  private static final String ById =
          "SELECT * FROM TBL_SCHEMAVERSIONS " +
          "WHERE organizationId = :organizationId " +
            "AND unitId = :unitId " +
            "AND contextId = :contextId " +
            "AND schemaId = :schemaId " +
            "AND schemaVersionId = :schemaVersionId";

  private static final String ByCurrentVersion =
          "SELECT * FROM TBL_SCHEMAVERSIONS " +
          "WHERE organizationId = :organizationId " +
            "AND unitId = :unitId " +
            "AND contextId = :contextId " +
            "AND schemaId = :schemaId " +
            "AND currentVersion = :currentVersion";

  private final Map<String,String> parameters;

  public SchemaVersionQueriesActor(final ObjectStore objectStore) {
    super(objectStore);
    this.parameters = new HashMap<>(5);
  }

  @Override
  public Completes<List<SchemaVersionData>> schemaVersions(final String organizationId, final String unitId, final String contextId, final String schemaId) {
    parameters.clear();
    parameters.put("organizationId", organizationId);
    parameters.put("unitId", unitId);
    parameters.put("contextId", contextId);
    parameters.put("schemaId", schemaId);

    final QueryExpression query =
            MapQueryExpression.using(
                    SchemaVersionState.class,
                    "SELECT * FROM TBL_SCHEMAVERSIONS " +
                    "WHERE organizationId = :organizationId " +
                      "AND unitId = :unitId " +
                      "AND contextId = :contextId " +
                      "AND schemaId = :schemaId",
                    parameters);

    return queryAll(SchemaVersionState.class, query, (List<SchemaVersionState> states) -> SchemaVersionData.from(states));
  }

  @Override
  public Completes<SchemaVersionData> schemaVersion(final String organizationId, final String unitId, final String contextId, final String schemaId, final String schemaVersionId) {
    parameters.clear();
    parameters.put("organizationId", organizationId);
    parameters.put("unitId", unitId);
    parameters.put("contextId", contextId);
    parameters.put("schemaId", schemaId);
    parameters.put("schemaVersionId", schemaVersionId);

    return queryOne(ById, parameters);
  }

  @Override
  public Completes<SchemaVersionData> schemaVersionOfVersion(final String organizationId, final String unitId, final String contextId, final String schemaId, final String version) {
    parameters.clear();
    parameters.put("organizationId", organizationId);
    parameters.put("unitId", unitId);
    parameters.put("contextId", contextId);
    parameters.put("schemaId", schemaId);
    parameters.put("currentVersion", version);

    return queryOne(ByCurrentVersion, parameters);
  }

  private Completes<SchemaVersionData> queryOne(final String query, final Map<String,String> parameters) {
    final QueryExpression expression = MapQueryExpression.using(SchemaVersionState.class, query, parameters);

    return queryObject(SchemaVersionState.class, expression, (SchemaVersionState state) -> SchemaVersionData.from(state));
  }
}
