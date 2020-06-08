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

import io.vlingo.actors.CompletesEventually;
import io.vlingo.common.*;
import io.vlingo.common.version.SemanticVersion;
import io.vlingo.lattice.query.StateObjectQueryActor;
import io.vlingo.lattice.query.StateStoreQueryActor;
import io.vlingo.schemata.Schemata;
import io.vlingo.schemata.errors.SchemataBusinessException;
import io.vlingo.schemata.model.SchemaVersionState;
import io.vlingo.schemata.query.view.SchemaVersionView;
import io.vlingo.schemata.query.view.SchemaVersionsView;
import io.vlingo.schemata.resource.data.SchemaVersionData;
import io.vlingo.symbio.store.MapQueryExpression;
import io.vlingo.symbio.store.QueryExpression;
import io.vlingo.symbio.store.object.ObjectStore;
import io.vlingo.symbio.store.state.StateStore;

public class SchemaVersionQueriesActor extends StateStoreQueryActor implements SchemaVersionQueries {
  public SchemaVersionQueriesActor(final StateStore stateStore) {
    super(stateStore);
  }

  @Override
  public Completes<SchemaVersionsView> schemaVersionsByIds(final String organizationId, final String unitId, final String contextId, final String schemaId) {
    return queryStateFor(schemaId, SchemaVersionsView.class);
//    parameters.clear();
//    parameters.put("organizationId", organizationId);
//    parameters.put("unitId", unitId);
//    parameters.put("contextId", contextId);
//    parameters.put("schemaId", schemaId);
//
//    final QueryExpression query =
//            MapQueryExpression.using(
//                    SchemaVersionState.class,
//                    "SELECT * FROM TBL_SCHEMAVERSIONS " +
//                    "WHERE organizationId = :organizationId " +
//                      "AND unitId = :unitId " +
//                      "AND contextId = :contextId " +
//                      "AND schemaId = :schemaId",
//                    parameters);
//
//    return queryAll(SchemaVersionState.class, query, (List<SchemaVersionState> states) -> SchemaVersionData.from(states));
  }

  @Override
  public Completes<SchemaVersionsView> schemaVersionsByNames(String organization, String unit, String context, String schema) {
    return null;
//    parameters.clear();
//    parameters.put("organization", organization);
//    parameters.put("unit", unit);
//    parameters.put("context", context);
//    parameters.put("schema", schema);
//
//    final QueryExpression query = MapQueryExpression.using(SchemaVersionState.class, ByNamesWoVersion, parameters);
//    return queryAll(SchemaVersionState.class, query, (List<SchemaVersionState> states) -> SchemaVersionData.from(states));
  }

  @Override
  public Completes<SchemaVersionView> schemaVersion(final String organizationId, final String unitId, final String contextId, final String schemaId, final String schemaVersionId) {
    final String id = dataIdFrom(":", schemaId, schemaVersionId);
    return queryStateFor(id, SchemaVersionView.class);
//    parameters.clear();
//    parameters.put("organizationId", organizationId);
//    parameters.put("unitId", unitId);
//    parameters.put("contextId", contextId);
//    parameters.put("schemaId", schemaId);
//    parameters.put("schemaVersionId", schemaVersionId);
//
//    return queryOne(ById, parameters);
  }

//  private Completes<SchemaVersionData> noSchemaVersion() {
//    final CompletesEventually completesEventually = completesEventually();
//    completesEventually.with(null);
//    return Completes.withSuccess(null);
//  }

  @Override
  public Completes<SchemaVersionView> schemaVersion(String fullyQualifiedTypeName) {
    return null;
//    String[] parts = fullyQualifiedTypeName.split(Schemata.ReferenceSeparator);
////
////    final CompletesEventually completesEventually = completesEventually();
////
////    if (parts.length < Schemata.MinReferenceParts) {
////      Outcome<SchemataBusinessException,SchemaVersionData> outcome = Failure.of(SchemataBusinessException.invalidReference(fullyQualifiedTypeName));
////      completesEventually.with(outcome);
////      return Completes.withSuccess(outcome);
////    } else if (parts.length > Schemata.MinReferenceParts) {
////      completesEventually.with(schemaVersionOf(parts[0], parts[1], parts[2], parts[3], parts[4]));
////    } else {
////      completesEventually.with(queryGreatestByNames(parts[0], parts[1], parts[2], parts[3]));
////    }
////
////    return completes();
  }

  @Override
  public Completes<SchemaVersionView> schemaVersionOf(String organization, String unit, String context, String schema, String schemaVersion) {
    return null;
//    parameters.clear();
//    parameters.put("organization", organization);
//    parameters.put("unit", unit);
//    parameters.put("context", context);
//    parameters.put("schema", schema);
//    parameters.put("currentVersion", schemaVersion);
//
//    return queryOne(ByNames, parameters);
  }

  @Override
  public Completes<SchemaVersionView> schemaVersionOfVersion(final String organizationId, final String unitId, final String contextId, final String schemaId, final String version) {
    return null;
//    if (version.equals(GreatestVersion)) {
//      return queryGreatest(organizationId, unitId, contextId, schemaId);
//    }
//    parameters.clear();
//    parameters.put("organizationId", organizationId);
//    parameters.put("unitId", unitId);
//    parameters.put("contextId", contextId);
//    parameters.put("schemaId", schemaId);
//    parameters.put("currentVersion", version);
//
//    return queryOne(ByCurrentVersion, parameters);
  }

//  private final Completes<SchemaVersionData> queryGreatest(List<SchemaVersionData> versions) {
//      final CompletesEventually completesEventually = completesEventually();
//
//      versions.forEach(schemaVersion -> {
//          final SemanticVersion semanticVersion = SemanticVersion.from(schemaVersion.currentVersion);
//          if (semanticVersion.isGreaterThan(tempCurrentVersion._2)) {
//              tempCurrentVersion = Tuple2.from(schemaVersion, semanticVersion);
//          }
//      });
//
//      completesEventually.with(tempCurrentVersion._1);
//      return Completes.withSuccess(tempCurrentVersion._1);
//  }
//
//  private Completes<Outcome<SchemataBusinessException,SchemaVersionData>> queryGreatest(final String organizationId, final String unitId, final String contextId, final String schemaId) {
//    tempCurrentVersion = Tuple2.from(SchemaVersionData.none(), SemanticVersion.from(SchemaVersionData.none().currentVersion));
//
//    schemaVersionsByIds(organizationId, unitId, contextId, schemaId)
//      .andThenTo(versions -> queryGreatest(versions));
//
//    return completes();
//  }
//
//  private Completes<Outcome<SchemataBusinessException,SchemaVersionData>> queryGreatestByNames(final String organization, final String unit, final String context, final String schema) {
//    tempCurrentVersion = Tuple2.from(SchemaVersionData.none(), SemanticVersion.from(SchemaVersionData.none().currentVersion));
//
//    schemaVersionsByNames(organization, unit, context, schema)
//      .andThenTo(versions -> queryGreatest(versions));
//
//    return completes();
//  }
//
//  private Completes<Outcome<SchemataBusinessException,SchemaVersionData>> queryOne(final String query, final Map<String,String> parameters) {
//    final QueryExpression expression = MapQueryExpression.using(SchemaVersionState.class, query, parameters);
//
//    return queryObject(SchemaVersionState.class, expression,
//            (SchemaVersionState state) -> state == null
//                    ? Failure.of(SchemataBusinessException.notFound("Schema Version", parameters))
//                    : Success.of(SchemaVersionData.from(state)));
//  }
}
