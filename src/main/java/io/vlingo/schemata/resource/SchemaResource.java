// Copyright © 2012-2020 VLINGO LABS. All rights reserved.
//
// This Source Code Form is subject to the terms of the
// Mozilla Public License, v. 2.0. If a copy of the MPL
// was not distributed with this file, You can obtain
// one at https://mozilla.org/MPL/2.0/.

package io.vlingo.schemata.resource;

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
import io.vlingo.schemata.model.Scope;
import io.vlingo.schemata.query.SchemaQueries;
import io.vlingo.schemata.resource.data.SchemaData;

import static io.vlingo.common.serialization.JsonSerialization.serialized;
import static io.vlingo.http.Response.Status.*;
import static io.vlingo.http.ResponseHeader.*;
import static io.vlingo.http.resource.ResourceBuilder.*;
import static io.vlingo.schemata.Schemata.*;

public class SchemaResource extends ResourceHandler {
  private final SchemaCommands commands;
  private final SchemaQueries queries;
  private final Stage stage;

  public SchemaResource(final World world, SchemaQueries queries) {
    this.stage = world.stageNamed(StageName);
    this.commands = new SchemaCommands(this.stage, 10);
    this.queries = queries;
  }

  public Completes<Response> defineWith(final String organizationId, final String unitId, final String contextId, final SchemaData data) {
    if (!Naming.isValid(data.name)) {
      return Completes.withSuccess(Response.of(BadRequest, Naming.invalidNameMessage(data.name)));
    }

    return Schema.with(stage, ContextId.existing(organizationId, unitId, contextId), Category.valueOf(data.category),
            data.scope == null ? Scope.Private : Scope.valueOf(data.scope), data.name, data.description)
            .andThenTo(3000, state -> {
                final String location = schemaLocation(state.schemaId);
                final Headers<ResponseHeader> headers = Headers.of(
                        of(Location, location),
                        of(ContentType, "application/json; charset=UTF-8")
                );
                final String serialized = serialized(SchemaData.from(state));

                return Completes.withSuccess(Response.of(Created, headers, serialized));
              })
            .otherwise(response -> Response.of(Conflict, serialized(SchemaData.from(organizationId, unitId, contextId, NoId, data.category, data.scope, data.name, data.description))));
  }

  public Completes<Response> categorizeAs(final String organizationId, final String unitId, final String contextId, final String schemaId, final String category) {
    return commands
            .categorizeAs(SchemaId.existing(organizationId, unitId, contextId, schemaId), Category.valueOf(category)).answer()
            .andThenTo(state -> Completes.withSuccess(Response.of(Ok, serialized(SchemaData.from(state)))));
  }

  public Completes<Response> scopeAs(final String organizationId, final String unitId, final String contextId, final String schemaId, final String scope) {
    return commands
            .scopeAs(SchemaId.existing(organizationId, unitId, contextId, schemaId), Scope.valueOf(scope)).answer()
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

  public Completes<Response> redefineWith(final String organizationId, final String unitId, final String contextId, final String schemaId, final SchemaData data) {
    if (Naming.isValid(data.name)) {
      Completes.withSuccess(Response.of(BadRequest, Naming.invalidNameMessage(data.name)));
    }

    return commands
            .redefineWith(SchemaId.existing(organizationId, unitId, contextId, schemaId), Category.valueOf(data.category), Scope.valueOf(data.scope), data.name, data.description).answer()
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

  public Completes<Response> querySchemaCategories() {
    return Completes.withSuccess(Response.of(Ok, serialized(Category.values())));
  }

  public Completes<Response> querySchemaScopes() {
    return Completes.withSuccess(Response.of(Ok, serialized(Scope.values())));
  }

  @Override
  public Resource<?> routes() {
    return resource("Schema Resource", 1,
      post("/organizations/{organizationId}/units/{unitId}/contexts/{contextId}/schemas")
        .param(String.class)
        .param(String.class)
        .param(String.class)
        .body(SchemaData.class)
        .handle(this::defineWith),
      patch("/organizations/{organizationId}/units/{unitId}/contexts/{contextId}/schemas/{schemaId}/category")
        .param(String.class)
        .param(String.class)
        .param(String.class)
        .param(String.class)
        .body(String.class)
        .handle(this::categorizeAs),
      patch("/organizations/{organizationId}/units/{unitId}/contexts/{contextId}/schemas/{schemaId}/scope")
        .param(String.class)
        .param(String.class)
        .param(String.class)
        .param(String.class)
        .body(String.class)
        .handle(this::scopeAs),
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
      put("/organizations/{organizationId}/units/{unitId}/contexts/{contextId}/schemas/{schemaId}")
        .param(String.class)
        .param(String.class)
        .param(String.class)
        .param(String.class)
        .body(SchemaData.class)
        .handle(this::redefineWith),
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
        .handle(this::querySchema),
      get("/schema/categories")
        .handle(this::querySchemaCategories),
      get("/schema/scopes")
        .handle(this::querySchemaScopes));
  }

  private String schemaLocation(final SchemaId schemaId) {
    return String.format(SchemasPath, schemaId.organizationId().value, schemaId.unitId().value, schemaId.contextId.value, schemaId.value);
  }
}
