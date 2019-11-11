// Copyright © 2012-2018 Vaughn Vernon. All rights reserved.
//
// This Source Code Form is subject to the terms of the
// Mozilla Public License, v. 2.0. If a copy of the MPL
// was not distributed with this file, You can obtain
// one at https://mozilla.org/MPL/2.0/.

package io.vlingo.schemata.query;

import io.vlingo.actors.CompletesEventually;
import io.vlingo.common.Completes;
import io.vlingo.common.Tuple2;
import io.vlingo.common.version.SemanticVersion;
import io.vlingo.lattice.query.StateObjectQueryActor;
import io.vlingo.schemata.Schemata;
import io.vlingo.schemata.codegen.TypeDefinitionMiddleware;
import io.vlingo.schemata.codegen.ast.types.TypeDefinition;
import io.vlingo.schemata.codegen.processor.types.TypeResolver;
import io.vlingo.schemata.model.SchemaVersionState;
import io.vlingo.schemata.resource.data.SchemaVersionData;
import io.vlingo.symbio.store.object.MapQueryExpression;
import io.vlingo.symbio.store.object.ObjectStore;
import io.vlingo.symbio.store.object.QueryExpression;

import java.io.ByteArrayInputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class SchemaVersionQueriesActor extends StateObjectQueryActor implements SchemaVersionQueries, TypeResolver {
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

  Tuple2<SchemaVersionData,SemanticVersion> tempCurrentVersion;

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

  @Override
  public Completes<Optional<TypeDefinition>> resolve(final TypeDefinitionMiddleware middleware, final String fullQualifiedTypeName) {
    String[] parts = fullQualifiedTypeName.split(Schemata.ReferenceSeparator);
    parameters.clear();
    parameters.put("organizationId", parts[0]);
    parameters.put("unitId", parts[1]);
    parameters.put("contextId", parts[2]);
    parameters.put("schemaId", parts[3]);
    parameters.put("currentVersion", GreatestVersion);

    if (parts.length > 4) {
      parameters.put("currentVersion", parts[4]);
    }

    return queryOne(ByCurrentVersion, parameters)
            .andThen(data -> data.specification)
            .andThenTo(spec -> middleware.compileToAST(new ByteArrayInputStream(spec.getBytes()), fullQualifiedTypeName))
            .andThen(node -> Optional.of((TypeDefinition) node))
            .otherwise(ex -> Optional.empty());
  }

  private Completes<SchemaVersionData> queryGreatest(final String organizationId, final String unitId, final String contextId, final String schemaId) {
    final CompletesEventually completesEventually = completesEventually();
    tempCurrentVersion = Tuple2.from(SchemaVersionData.none(), SemanticVersion.from(SchemaVersionData.none().currentVersion));

    schemaVersions(organizationId, unitId, contextId, schemaId)
      .andThenTo(versions -> {
        versions.forEach(schemaVersion -> {
          final SemanticVersion semanticVersion = SemanticVersion.from(schemaVersion.currentVersion);
          if (semanticVersion.isGreaterThan(tempCurrentVersion._2)) {
            tempCurrentVersion = Tuple2.from(schemaVersion, semanticVersion);
          }
        });
        completesEventually.with(tempCurrentVersion._1);
        return Completes.withSuccess(tempCurrentVersion._1);
      });

    return completes();
  }

  private Completes<SchemaVersionData> queryOne(final String query, final Map<String,String> parameters) {
    final QueryExpression expression = MapQueryExpression.using(SchemaVersionState.class, query, parameters);

    return queryObject(SchemaVersionState.class, expression, (SchemaVersionState state) -> SchemaVersionData.from(state));
  }
}
