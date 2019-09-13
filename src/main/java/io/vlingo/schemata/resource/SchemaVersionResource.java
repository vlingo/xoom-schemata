// Copyright © 2012-2018 Vaughn Vernon. All rights reserved.
//
// This Source Code Form is subject to the terms of the
// Mozilla Public License, v. 2.0. If a copy of the MPL
// was not distributed with this file, You can obtain
// one at https://mozilla.org/MPL/2.0/.

package io.vlingo.schemata.resource;

import static io.vlingo.common.serialization.JsonSerialization.serialized;
import static io.vlingo.http.Response.Status.Conflict;
import static io.vlingo.http.Response.Status.Created;
import static io.vlingo.http.Response.Status.Ok;
import static io.vlingo.http.ResponseHeader.*;
import static io.vlingo.http.resource.ResourceBuilder.get;
import static io.vlingo.http.resource.ResourceBuilder.patch;
import static io.vlingo.http.resource.ResourceBuilder.post;
import static io.vlingo.http.resource.ResourceBuilder.resource;
import static io.vlingo.schemata.Schemata.NoId;
import static io.vlingo.schemata.Schemata.SchemaVersionsPath;
import static io.vlingo.schemata.Schemata.StageName;

import io.vlingo.actors.Stage;
import io.vlingo.actors.World;
import io.vlingo.common.Completes;
import io.vlingo.http.Header.Headers;
import io.vlingo.http.Response;
import io.vlingo.http.ResponseHeader;
import io.vlingo.http.resource.Resource;
import io.vlingo.http.resource.ResourceHandler;
import io.vlingo.schemata.model.Id.SchemaId;
import io.vlingo.schemata.model.Id.SchemaVersionId;
import io.vlingo.schemata.model.SchemaVersion;
import io.vlingo.schemata.model.SchemaVersion.Specification;
import io.vlingo.schemata.model.SchemaVersion.Version;
import io.vlingo.schemata.model.SchemaVersionState;
import io.vlingo.schemata.query.SchemaVersionQueries;
import io.vlingo.schemata.resource.data.SchemaVersionData;

public class SchemaVersionResource extends ResourceHandler {
  private final SchemaVersionCommands commands;
  private final SchemaVersionQueries queries;
  private final Stage stage;

  public SchemaVersionResource(final World world, final SchemaVersionQueries queries) {
    this.stage = world.stageNamed(StageName);
    this.queries = queries;
    this.commands = new SchemaVersionCommands(this.stage, 10);
  }

  public Completes<Response> defineWith(
          final String organizationId,
          final String unitId,
          final String contextId,
          final String schemaId,
          final SchemaVersionData data) {

    return SchemaVersion.with(stage, SchemaId.existing(organizationId, unitId, contextId, schemaId), Specification.of(data.specification), data.description, Version.of(data.previousVersion), Version.of(data.currentVersion))
            .andThenTo(3000, state -> {
                final String location = schemaVersionLocation(state.schemaVersionId);
                final Headers<ResponseHeader> headers = Headers.of(
                        of(Location, location),
                        of(ContentType, "application/json; charset=UTF-8")
                );
                final String serialized = serialized(SchemaVersionData.from(state));

                return Completes.withSuccess(Response.of(Created, headers, serialized));
              })
            .otherwise(response -> Response.of(Conflict, serialized(SchemaVersionData.from(organizationId, unitId, contextId, schemaId, NoId, data.specification, data.description, "Draft", data.previousVersion, data.currentVersion))));
  }

  public Completes<Response> describeAs(final String organizationId, final String unitId, final String contextId, final String schemaId, final String schemaVersionId, final String description) {
    return commands
              .describeAs(SchemaVersionId.existing(organizationId, unitId, contextId, schemaId, schemaVersionId), description).answer()
              .andThenTo(state -> Completes.withSuccess(Response.of(Ok, serialized(SchemaVersionData.from(state)))));
  }

  public Completes<Response> specifyWith(final String organizationId, final String unitId, final String contextId, final String schemaId, final String schemaVersionId, final String specification) {
    return commands
            .specifyWith(SchemaVersionId.existing(organizationId, unitId, contextId, schemaId, schemaVersionId), Specification.of(specification)).answer()
            .andThenTo(state -> Completes.withSuccess(Response.of(Ok, serialized(SchemaVersionData.from(state)))));
  }

  public Completes<Response> statusOf(final String organizationId, final String unitId, final String contextId, final String schemaId, final String schemaVersionId, final String status) {
    final SchemaVersionId id = SchemaVersionId.existing(organizationId, unitId, contextId, schemaId, schemaVersionId);
    final Completes<SchemaVersionState> answer;

    switch (status.toLowerCase()) {
    case "published":
      answer = commands.publish(id).answer();
      break;
    case "deprecated":
      answer = commands.deprecate(id).answer();
      break;
    case "removed":
      answer = commands.remove(id).answer();
      break;
    default:
      return Completes.withSuccess(Response.of(Conflict, serialized(SchemaVersionData.from(organizationId, unitId, contextId, schemaId, schemaVersionId, "", "", status, "", ""))));
    }

    return answer.andThenTo(state -> Completes.withSuccess(Response.of(Ok, serialized(SchemaVersionData.from(state)))));
  }

  public Completes<Response> querySchemaVersions(final String organizationId, final String unitId, final String contextId, final String schemaId) {
    return queries
            .schemaVersions(organizationId, unitId, contextId, schemaId)
            .andThenTo(schemaVersions -> Completes.withSuccess(Response.of(Ok, serialized(schemaVersions))));
  }

  public Completes<Response> querySchemaVersion(final String organizationId, final String unitId, final String contextId, final String schemaId, final String schemaVersionId) {
    return queries
            .schemaVersion(organizationId, unitId, contextId, schemaId, schemaVersionId)
            .andThenTo(schemaVersion -> Completes.withSuccess(Response.of(Ok, serialized(schemaVersion))));
  }

  @Override
  public Resource<?> routes() {
    return resource("SchemaVersion Resource",
      post("/organizations/{organizationId}/units/{unitId}/contexts/{contextId}/schemas/{schemaId}/versions")
        .param(String.class)
        .param(String.class)
        .param(String.class)
        .param(String.class)
        .body(SchemaVersionData.class)
        .handle(this::defineWith),
      patch("/organizations/{organizationId}/units/{unitId}/contexts/{contextId}/schemas/{schemaId}/versions/{schemaVersionId}/description")
        .param(String.class)
        .param(String.class)
        .param(String.class)
        .param(String.class)
        .param(String.class)
        .body(String.class)
        .handle(this::describeAs),
      patch("/organizations/{organizationId}/units/{unitId}/contexts/{contextId}/schemas/{schemaId}/versions/{schemaVersionId}/specification")
        .param(String.class)
        .param(String.class)
        .param(String.class)
        .param(String.class)
        .param(String.class)
        .body(String.class)
        .handle(this::specifyWith),
      patch("/organizations/{organizationId}/units/{unitId}/contexts/{contextId}/schemas/{schemaId}/versions/{schemaVersionId}/status")
        .param(String.class)
        .param(String.class)
        .param(String.class)
        .param(String.class)
        .param(String.class)
        .body(String.class)
        .handle(this::statusOf),
      get("/organizations/{organizationId}/units/{unitId}/contexts/{contextId}/schemas/{schemaId}/versions")
        .param(String.class)
        .param(String.class)
        .param(String.class)
        .param(String.class)
        .handle(this::querySchemaVersions),
      get("/organizations/{organizationId}/units/{unitId}/contexts/{contextId}/schemas/{schemaId}/versions/{schemaVersionId}")
        .param(String.class)
        .param(String.class)
        .param(String.class)
        .param(String.class)
        .param(String.class)
        .handle(this::querySchemaVersion));
  }

  private String schemaVersionLocation(final SchemaVersionId schemaVersionId) {
    return String.format(SchemaVersionsPath, schemaVersionId.organizationId().value, schemaVersionId.unitId().value, schemaVersionId.contextId().value, schemaVersionId.schemaId.value, schemaVersionId.value);
  }
}
