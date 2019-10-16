// Copyright © 2012-2018 Vaughn Vernon. All rights reserved.
//
// This Source Code Form is subject to the terms of the
// Mozilla Public License, v. 2.0. If a copy of the MPL
// was not distributed with this file, You can obtain
// one at https://mozilla.org/MPL/2.0/.

package io.vlingo.schemata.query;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import io.vlingo.actors.CompletesEventually;
import io.vlingo.common.Completes;
import io.vlingo.common.version.SemanticVersion;
import io.vlingo.lattice.query.StateObjectQueryActor;
import io.vlingo.schemata.model.SchemaVersionState;
import io.vlingo.schemata.resource.data.*;
import io.vlingo.symbio.store.object.MapQueryExpression;
import io.vlingo.symbio.store.object.ObjectStore;
import io.vlingo.symbio.store.object.QueryExpression;

public class CodeQueriesActor extends StateObjectQueryActor implements CodeQueries {

  private static final String SchemaVersion =
          "SELECT TBL_SCHEMAVERSIONS.schemaVersionId, " +
                  "TBL_SCHEMAVERSIONS.schemaId, " +
                  "TBL_SCHEMAVERSIONS.contextId, " +
                  "TBL_SCHEMAVERSIONS.unitId, " +
                  "TBL_SCHEMAVERSIONS.organizationId, " +
                  "TBL_SCHEMAVERSIONS.description, " +
                  "TBL_SCHEMAVERSIONS.specification, " +
                  "TBL_SCHEMAVERSIONS.status, " +
                  "TBL_SCHEMAVERSIONS.previousVersion, " +
                  "TBL_SCHEMAVERSIONS.currentVersion " +
              "FROM TBL_SCHEMAVERSIONS " +
              "INNER JOIN TBL_SCHEMAS ON TBL_SCHEMAVERSIONS.schemaId = TBL_SCHEMAS.schemaId " +
                  "AND TBL_SCHEMAVERSIONS.organizationId = TBL_SCHEMAS.organizationId " +
                  "AND TBL_SCHEMAVERSIONS.unitId = TBL_SCHEMAS.unitId " +
                  "AND TBL_SCHEMAVERSIONS.contextId = TBL_SCHEMAS.contextId " +
              "INNER JOIN TBL_CONTEXTS ON TBL_CONTEXTS.contextId = TBL_SCHEMAVERSIONS.contextId " +
                  "AND TBL_SCHEMAVERSIONS.organizationId = TBL_CONTEXTS.organizationId " +
                  "AND TBL_SCHEMAVERSIONS.unitId = TBL_CONTEXTS.unitId " +
              "INNER JOIN TBL_UNITS ON TBL_UNITS.unitId = TBL_SCHEMAVERSIONS.unitId " +
                  "AND TBL_SCHEMAVERSIONS.organizationId = TBL_UNITS.organizationId " +
              "INNER JOIN TBL_ORGANIZATIONS ON TBL_ORGANIZATIONS.organizationId = TBL_SCHEMAVERSIONS.organizationId " +
              "WHERE TBL_SCHEMAVERSIONS.schemaVersionId = :schemaVersionId " +
                  "AND TBL_CONTEXTS.namespace = :namespace " +
                  "AND TBL_UNITS.name = :unitName " +
                  "AND TBL_SCHEMAS.name = :schemaName " +
                  "AND TBL_ORGANIZATIONS.name = :organizationName";
  private final Map<String,String> parameters;

  public CodeQueriesActor(final ObjectStore objectStore) {
    super(objectStore);

    this.parameters = new HashMap<>(5);
  }

  @Override
  public Completes<SchemaVersionData> schemaVersionFor(final AuthorizationData authorization, final PathData path) {
    final CompletesEventually completesEventually = completesEventually();

    parameters.clear();
    parameters.put("schemaVersionId", path.version);
    parameters.put("namespace", path.context);
    parameters.put("unitName", path.unit);
    parameters.put("schemaName", path.schema);
    parameters.put("organizationName", path.organization);

    final QueryExpression query = MapQueryExpression.using(SchemaVersionState.class, SchemaVersion, parameters);

    queryObject(SchemaVersionState.class, query, (SchemaVersionState state) -> SchemaVersionData.from(state))
          .andThenTo(schemaVersion -> validate(authorization, schemaVersion))
          .andThenTo(schemaVersion -> { completesEventually.with(schemaVersion); return Completes.withSuccess(schemaVersion); })
          .otherwise(failure -> SchemaVersionData.none())
          .recoverFrom(exception -> SchemaVersionData.none());

    return completes();
  }

  @Override
  public Completes<SchemaVersionData> schemaVersionFor(final AuthorizationData authorization, final PathData path, final QueryResultsCollector collector) {
    final Completes<SchemaVersionData> data = schemaVersionFor(authorization, path);
    collector.expectSchemaVersion(data);
    return data;
  }

  @Override
  public Completes<SchemaVersionData> schemaVersionFor(final String fullQualifiedTypeName) {
    pathFrom(fullQualifiedTypeName).ifPresent(path -> {
      final CompletesEventually completesEventually = completesEventually();

      final String version = path.versionOrElse(SemanticVersion.greatest().toString());

      parameters.clear();
      parameters.put("schemaVersionId", version);
      parameters.put("namespace", path.context);
      parameters.put("unitName", path.unit);
      parameters.put("schemaName", path.schema);
      parameters.put("organizationName", path.organization);

      final QueryExpression query = MapQueryExpression.using(SchemaVersionState.class, SchemaVersion, parameters);

      queryObject(SchemaVersionState.class, query, (SchemaVersionState state) -> SchemaVersionData.from(state))
              .andThenTo(schemaVersion -> { completesEventually.with(schemaVersion); return Completes.withSuccess(schemaVersion); })
              .otherwise(failure -> SchemaVersionData.none())
              .recoverFrom(exception -> SchemaVersionData.none());
    });

    return completes();
  }

  private Completes<SchemaVersionData> validate(final AuthorizationData authorization, final SchemaVersionData schemaVersionData) {
    return authorization.sameSource(schemaVersionData.organizationId) ?
            Completes.withSuccess(schemaVersionData) :
            Completes.withFailure();
  }

  private Optional<PathData> pathFrom(final String fullQualifiedTypeName) {
    try {
      return Optional.of(PathData.versionOptional(fullQualifiedTypeName));
    } catch (Exception e) {
      return Optional.empty();
    }
  }
}
