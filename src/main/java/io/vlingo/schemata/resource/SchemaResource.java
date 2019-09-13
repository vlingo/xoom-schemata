// Copyright © 2012-2018 Vaughn Vernon. All rights reserved.
//
// This Source Code Form is subject to the terms of the
// Mozilla Public License, v. 2.0. If a copy of the MPL
// was not distributed with this file, You can obtain
// one at https://mozilla.org/MPL/2.0/.

package io.vlingo.schemata.resource;

import static io.vlingo.common.serialization.JsonSerialization.serialized;
import static io.vlingo.http.Response.Status.BadRequest;
import static io.vlingo.http.Response.Status.Conflict;
import static io.vlingo.http.Response.Status.Created;
import static io.vlingo.http.Response.Status.Ok;
import static io.vlingo.http.ResponseHeader.*;
import static io.vlingo.http.resource.ResourceBuilder.get;
import static io.vlingo.http.resource.ResourceBuilder.patch;
import static io.vlingo.http.resource.ResourceBuilder.post;
import static io.vlingo.http.resource.ResourceBuilder.resource;
import static io.vlingo.schemata.Schemata.NoId;
import static io.vlingo.schemata.Schemata.SchemasPath;
import static io.vlingo.schemata.Schemata.StageName;

import io.vlingo.actors.Stage;
import io.vlingo.actors.World;
import io.vlingo.common.Completes;
import io.vlingo.http.Header.Headers;
import io.vlingo.http.Response;
import io.vlingo.http.ResponseHeader;
import io.vlingo.http.resource.Resource;
import io.vlingo.http.resource.ResourceHandler;
import io.vlingo.schemata.model.Category;
import io.vlingo.schemata.model.Id.ContextId;
import io.vlingo.schemata.model.Id.SchemaId;
import io.vlingo.schemata.model.Naming;
import io.vlingo.schemata.model.Schema;
import io.vlingo.schemata.query.SchemaQueries;
import io.vlingo.schemata.resource.data.SchemaData;

public class SchemaResource extends ResourceHandler {
  private final SchemaCommands commands;
  private final SchemaQueries queries;
  private final Stage stage;

  public SchemaResource(final World world, final SchemaQueries queries) {
    this.stage = world.stageNamed(StageName);
    this.queries = queries;
    this.commands = new SchemaCommands(this.stage, 10);
  }

  public Completes<Response> defineWith(final String organizationId, final String unitId, final String contextId, final SchemaData data) {
    if (Naming.isValid(data.name)) {
      Completes.withSuccess(Response.of(BadRequest, Naming.invalidNameMessage(data.name)));
    }

    return Schema.with(stage, ContextId.existing(organizationId, unitId, contextId), Category.valueOf(data.category), data.name, data.description)
            .andThenTo(3000, state -> {
                final String location = schemaLocation(state.schemaId);
                final Headers<ResponseHeader> headers = Headers.of(
                        of(Location, location),
                        of(ContentType, "application/json; charset=UTF-8")
                );
                final String serialized = serialized(SchemaData.from(state));

                return Completes.withSuccess(Response.of(Created, headers, serialized));
              })
            .otherwise(response -> Response.of(Conflict, serialized(SchemaData.from(organizationId, unitId, contextId, NoId, data.category, data.name, data.description))));
  }

  public Completes<Response> categorizeAs(final String organizationId, final String unitId, final String contextId, final String schemaId, final String category) {
    return commands
            .categorizeAs(SchemaId.existing(organizationId, unitId, contextId, schemaId), Category.valueOf(category)).answer()
            .andThenTo(state -> Completes.withSuccess(Response.of(Ok, serialized(SchemaData.from(state)))));
  }

  public Completes<Response> describeAs(final String organizationId, final String unitId, final String contextId, final String schemaId, final String description) {
    return commands
              .describeAs(SchemaId.existing(organizationId, unitId, contextId, schemaId), description).answer()
              .andThenTo(state -> Completes.withSuccess(Response.of(Ok, serialized(SchemaData.from(state)))));
  }

  public Completes<Response> renameTo(final String organizationId, final String unitId, final String contextId, final String schemaId, final String name) {
    if (Naming.isValid(name)) {
      Completes.withSuccess(Response.of(BadRequest, Naming.invalidNameMessage(name)));
    }

    return commands
            .renameTo(SchemaId.existing(organizationId, unitId, contextId, schemaId), name).answer()
            .andThenTo(state -> Completes.withSuccess(Response.of(Ok, serialized(SchemaData.from(state)))));
  }

  public Completes<Response> querySchemas(final String organizationId, final String unitId, final String contextId) {
    return queries
            .schemas(organizationId, unitId, contextId)
            .andThenTo(schemas -> Completes.withSuccess(Response.of(Ok, serialized(schemas))));
  }

  public Completes<Response> querySchema(final String organizationId, final String unitId, final String contextId, final String schemaId) {
    return queries
            .schema(organizationId, unitId, contextId, schemaId)
            .andThenTo(schema -> Completes.withSuccess(Response.of(Ok, serialized(schema))));
  }

  @Override
  public Resource<?> routes() {
    return resource("Schema Resource",
      post("/organizations/{organizationId}/units/{unitId}/contexts/{contextId}/schemas")
        .param(String.class)
        .param(String.class)
        .param(String.class)
        .body(SchemaData.class)
        .handle(this::defineWith),
      patch("/organizations/{organizationId}/units/{unitId}/contexts/{contextId}/schemas/{schemaId}/cateogry")
        .param(String.class)
        .param(String.class)
        .param(String.class)
        .param(String.class)
        .body(String.class)
        .handle(this::categorizeAs),
      patch("/organizations/{organizationId}/units/{unitId}/contexts/{contextId}/schemas/{schemaId}/description")
        .param(String.class)
        .param(String.class)
        .param(String.class)
        .param(String.class)
        .body(String.class)
        .handle(this::describeAs),
      patch("/organizations/{organizationId}/units/{unitId}/contexts/{contextId}/schemas/{schemaId}/name")
        .param(String.class)
        .param(String.class)
        .param(String.class)
        .param(String.class)
        .body(String.class)
        .handle(this::renameTo),
      get("/organizations/{organizationId}/units/{unitId}/contexts/{contextId}/schemas")
        .param(String.class)
        .param(String.class)
        .param(String.class)
        .handle(this::querySchemas),
      get("/organizations/{organizationId}/units/{unitId}/contexts/{contextId}/schemas/{schemaId}")
        .param(String.class)
        .param(String.class)
        .param(String.class)
        .param(String.class)
        .handle(this::querySchema));
  }

  private String schemaLocation(final SchemaId schemaId) {
    return String.format(SchemasPath, schemaId.organizationId().value, schemaId.unitId().value, schemaId.contextId.value, schemaId.value);
  }
}
