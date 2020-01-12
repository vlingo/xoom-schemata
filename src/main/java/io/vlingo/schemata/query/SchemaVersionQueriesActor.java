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

import io.vlingo.actors.CompletesEventually;
import io.vlingo.common.Completes;
import io.vlingo.common.Tuple2;
import io.vlingo.common.version.SemanticVersion;
import io.vlingo.lattice.query.StateObjectQueryActor;
import io.vlingo.schemata.Schemata;
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

  /**
   * The combination (organization, unit, context, schema, currentVersion) is unique
   * => the cardinal product across below tables produce exactly one row.
   */
  private static final String ByNames =
          "SELECT sv.* FROM TBL_ORGANIZATIONS AS o " +
          "JOIN TBL_UNITS AS u ON u.organizationId = o.organizationId " +
          "JOIN TBL_CONTEXTS AS c ON c.unitId = u.unitId " +
          "JOIN TBL_SCHEMAS AS s ON s.contextId = c.contextId " +
          "JOIN TBL_SCHEMAVERSIONS AS sv ON sv.schemaId = s.schemaId " +
          "WHERE o.name = :organization AND u.name = :unit AND c.namespace = :context AND s.name = :schema AND sv.currentVersion = :currentVersion";

  /**
   * Selects all SchemaVersions for specified (organization, unit, context, schema) parameters.
   * The combination (organization, unit, context, schema) is unique.
   */
  private static final String ByNamesWoVersion =
          "SELECT sv.* FROM TBL_ORGANIZATIONS AS o " +
                  "JOIN TBL_UNITS AS u ON u.organizationId = o.organizationId " +
                  "JOIN TBL_CONTEXTS AS c ON c.unitId = u.unitId " +
                  "JOIN TBL_SCHEMAS AS s ON s.contextId = c.contextId " +
                  "JOIN TBL_SCHEMAVERSIONS AS sv ON sv.schemaId = s.schemaId " +
                  "WHERE o.name = :organization AND u.name = :unit AND c.namespace = :context AND s.name = :schema";

  Tuple2<SchemaVersionData,SemanticVersion> tempCurrentVersion;

  private final Map<String,String> parameters;

  public SchemaVersionQueriesActor(final ObjectStore objectStore) {
    super(objectStore);
    this.parameters = new HashMap<>(5);
  }

  @Override
  public Completes<List<SchemaVersionData>> schemaVersionsByIds(final String organizationId, final String unitId, final String contextId, final String schemaId) {
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
  public Completes<List<SchemaVersionData>> schemaVersionsByNames(String organization, String unit, String context, String schema) {
    parameters.clear();
    parameters.put("organization", organization);
    parameters.put("unit", unit);
    parameters.put("context", context);
    parameters.put("schema", schema);

    final QueryExpression query = MapQueryExpression.using(SchemaVersionState.class, ByNamesWoVersion, parameters);
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

  private Completes<SchemaVersionData> noSchemaVersion() {
    final CompletesEventually completesEventually = completesEventually();
    completesEventually.with(null);
    return Completes.withSuccess(null);
  }

  @Override
  public Completes<SchemaVersionData> schemaVersion(String fullyQualifiedTypeName) {
    String[] parts = fullyQualifiedTypeName.split(Schemata.ReferenceSeparator);
    Completes<SchemaVersionData> schemaVersionData;

    if (parts.length < Schemata.MinReferenceParts) {
      schemaVersionData = noSchemaVersion();
    } else if (parts.length > Schemata.MinReferenceParts) {
      schemaVersionData = schemaVersionOf(parts[0], parts[1], parts[2], parts[3], parts[4]);
    } else {
      schemaVersionData = queryGreatestByNames(parts[0], parts[1], parts[2], parts[3]);
    }

    return schemaVersionData;
  }

  @Override
  public Completes<SchemaVersionData> schemaVersionOf(String organization, String unit, String context, String schema, String schemaVersion) {
    parameters.clear();
    parameters.put("organization", organization);
    parameters.put("unit", unit);
    parameters.put("context", context);
    parameters.put("schema", schema);
    parameters.put("currentVersion", schemaVersion);

    return queryOne(ByNames, parameters);
  }

  @Override
  public Completes<SchemaVersionData> schemaVersionOfVersion(final String organizationId, final String unitId, final String contextId, final String schemaId, final String version) {
    if (version.equals(GreatestVersion)) {
      return queryGreatest(organizationId, unitId, contextId, schemaId);
    }
    parameters.clear();
    parameters.put("organizationId", organizationId);
    parameters.put("unitId", unitId);
    parameters.put("contextId", contextId);
    parameters.put("schemaId", schemaId);
    parameters.put("currentVersion", version);

    return queryOne(ByCurrentVersion, parameters);
  }

  private final Completes<SchemaVersionData> queryGreatest(List<SchemaVersionData> versions) {
      final CompletesEventually completesEventually = completesEventually();

      versions.forEach(schemaVersion -> {
          final SemanticVersion semanticVersion = SemanticVersion.from(schemaVersion.currentVersion);
          if (semanticVersion.isGreaterThan(tempCurrentVersion._2)) {
              tempCurrentVersion = Tuple2.from(schemaVersion, semanticVersion);
          }
      });

      completesEventually.with(tempCurrentVersion._1);
      return Completes.withSuccess(tempCurrentVersion._1);
  }

  private Completes<SchemaVersionData> queryGreatest(final String organizationId, final String unitId, final String contextId, final String schemaId) {
    tempCurrentVersion = Tuple2.from(SchemaVersionData.none(), SemanticVersion.from(SchemaVersionData.none().currentVersion));

    schemaVersionsByIds(organizationId, unitId, contextId, schemaId)
      .andThenTo(versions -> queryGreatest(versions));

    return completes();
  }

  private Completes<SchemaVersionData> queryGreatestByNames(final String organization, final String unit, final String context, final String schema) {
    tempCurrentVersion = Tuple2.from(SchemaVersionData.none(), SemanticVersion.from(SchemaVersionData.none().currentVersion));

    schemaVersionsByNames(organization, unit, context, schema)
      .andThenTo(versions -> queryGreatest(versions));

    return completes();
  }

  private Completes<SchemaVersionData> queryOne(final String query, final Map<String,String> parameters) {
    final QueryExpression expression = MapQueryExpression.using(SchemaVersionState.class, query, parameters);

    return queryObject(SchemaVersionState.class, expression, (SchemaVersionState state) -> state == null
      ? SchemaVersionData.none()
      : SchemaVersionData.from(state));
  }
}
