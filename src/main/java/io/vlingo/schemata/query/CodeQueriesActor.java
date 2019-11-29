// Copyright © 2012-2018 Vaughn Vernon. All rights reserved.
//
// This Source Code Form is subject to the terms of the
// Mozilla Public License, v. 2.0. If a copy of the MPL
// was not distributed with this file, You can obtain
// one at https://mozilla.org/MPL/2.0/.

package io.vlingo.schemata.query;

import java.util.Optional;

import io.vlingo.actors.Actor;
import io.vlingo.actors.CompletesEventually;
import io.vlingo.common.Completes;
import io.vlingo.common.version.SemanticVersion;
import io.vlingo.schemata.resource.data.AuthorizationData;
import io.vlingo.schemata.resource.data.PathData;
import io.vlingo.schemata.resource.data.SchemaVersionData;

public class CodeQueriesActor extends Actor implements CodeQueries {
  private final SchemaVersionQueries schemaVersionQueries;

  public CodeQueriesActor(final SchemaVersionQueries schemaVersionQueries) {
    this.schemaVersionQueries = schemaVersionQueries;
  }

  @Override
  public Completes<SchemaVersionData> schemaVersionFor(final AuthorizationData authorization, final PathData path) {
    final CompletesEventually completesEventually = completesEventually();

    schemaVersionQueries.schemaVersionOf(path.organization, path.unit, path.context, path.schema, path.version)
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

      @SuppressWarnings("unused")
      final String version = path.versionOrElse(SemanticVersion.greatest().toString());

      schemaVersionQueries.schemaVersionOf(path.organization, path.unit, path.context, path.schema, path.version)
        .andThenTo(schemaVersion -> { completesEventually.with(schemaVersion); return Completes.withSuccess(schemaVersion); })
        .otherwise(failure -> SchemaVersionData.none())
        .recoverFrom(exception -> SchemaVersionData.none());
    });

    return completes();
  }

  private Completes<SchemaVersionData> validate(final AuthorizationData authorization, final SchemaVersionData schemaVersion) {
    return authorization.sameSource(schemaVersion.organizationId) ? Completes.withSuccess(schemaVersion) : Completes.withFailure();
  }

  private Optional<PathData> pathFrom(final String fullQualifiedTypeName) {
    try {
      return Optional.of(PathData.versionOptional(fullQualifiedTypeName));
    } catch (Exception e) {
      return Optional.empty();
    }
  }
}
