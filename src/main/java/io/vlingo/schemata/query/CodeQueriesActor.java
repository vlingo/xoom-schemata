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
import io.vlingo.schemata.resource.data.OrganizationData;
import io.vlingo.schemata.resource.data.PathData;
import io.vlingo.schemata.resource.data.SchemaVersionData;

public class CodeQueriesActor extends Actor implements CodeQueries {
  private final OrganizationQueries organizationQueries;
  private final UnitQueries unitQueries;
  private final ContextQueries contextQueries;
  private final SchemaQueries schemaQueries;
  private final SchemaVersionQueries schemaVersionQueries;

  public CodeQueriesActor(
          final OrganizationQueries organizationQueries,
          final UnitQueries unitQueries,
          final ContextQueries contextQueries,
          final SchemaQueries schemaQueries,
          final SchemaVersionQueries schemaVersionQueries) {

    this.organizationQueries = organizationQueries;
    this.unitQueries = unitQueries;
    this.contextQueries = contextQueries;
    this.schemaQueries = schemaQueries;
    this.schemaVersionQueries = schemaVersionQueries;
  }

  @Override
  public Completes<SchemaVersionData> schemaVersionFor(final AuthorizationData authorization, final PathData path) {
    final CompletesEventually completesEventually = completesEventually();

    organizationQueries.organizationNamed(path.organization)
      .andThenTo(organization -> validate(authorization, organization))
      .andThenTo(organization -> unitQueries.unitNamed(organization.organizationId, path.unit))
      .andThenTo(unit -> contextQueries.contextOfNamespace(unit.organizationId, unit.unitId, path.context))
      .andThenTo(context -> schemaQueries.schemaNamed(context.organizationId, context.unitId, context.contextId, path.schema))
      .andThenTo(schema -> schemaVersionQueries.schemaVersionOfVersion(schema.organizationId, schema.unitId, schema.contextId, schema.schemaId, path.version))
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

      organizationQueries.organizationNamed(path.organization)
        .andThenTo(organization -> unitQueries.unitNamed(organization.organizationId, path.unit))
        .andThenTo(unit -> contextQueries.contextOfNamespace(unit.organizationId, unit.unitId, path.context))
        .andThenTo(context -> schemaQueries.schemaNamed(context.organizationId, context.unitId, context.contextId, path.schema))
        .andThenTo(schema -> schemaVersionQueries.schemaVersionOfVersion(schema.organizationId, schema.unitId, schema.contextId, schema.schemaId, version))
        .andThenTo(schemaVersion -> { completesEventually.with(schemaVersion); return Completes.withSuccess(schemaVersion); })
        .otherwise(failure -> SchemaVersionData.none())
        .recoverFrom(exception -> SchemaVersionData.none());
    });

    return completes();
  }

  private Completes<OrganizationData> validate(final AuthorizationData authorization, final OrganizationData organization) {
    return authorization.sameSource(organization.organizationId) ?
            Completes.withSuccess(organization) :
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
