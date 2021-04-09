// Copyright © 2012-2021 VLINGO LABS. All rights reserved.
//
// This Source Code Form is subject to the terms of the
// Mozilla Public License, v. 2.0. If a copy of the MPL
// was not distributed with this file, You can obtain
// one at https://mozilla.org/MPL/2.0/.

package io.vlingo.xoom.schemata.resource;

import io.vlingo.xoom.actors.Grid;
import io.vlingo.xoom.common.Completes;
import io.vlingo.xoom.http.Header.Headers;
import io.vlingo.xoom.http.Response;
import io.vlingo.xoom.http.ResponseHeader;
import io.vlingo.xoom.http.resource.DynamicResourceHandler;
import io.vlingo.xoom.http.resource.Resource;
import io.vlingo.xoom.schemata.infra.persistence.StorageProvider;
import io.vlingo.xoom.schemata.model.Category;
import io.vlingo.xoom.schemata.model.Id.ContextId;
import io.vlingo.xoom.schemata.model.Id.SchemaId;
import io.vlingo.xoom.schemata.model.Naming;
import io.vlingo.xoom.schemata.model.Schema;
import io.vlingo.xoom.schemata.model.Scope;
import io.vlingo.xoom.schemata.query.SchemaQueries;
import io.vlingo.xoom.schemata.resource.data.SchemaData;

import static io.vlingo.xoom.common.serialization.JsonSerialization.serialized;
import static io.vlingo.xoom.http.Response.Status.*;
import static io.vlingo.xoom.http.ResponseHeader.*;
import static io.vlingo.xoom.http.resource.ResourceBuilder.*;
import static io.vlingo.xoom.schemata.Schemata.NoId;
import static io.vlingo.xoom.schemata.Schemata.SchemasPath;

public class SchemaResource extends DynamicResourceHandler {
  private final Grid grid;
  private final SchemaCommands commands;
  private final SchemaQueries queries;

  public SchemaResource(final Grid grid) {
    super(grid.world().stage());
    this.grid = grid;
    this.commands = new SchemaCommands(grid, 10);
    this.queries = StorageProvider.instance().schemaQueries;
  }

  public Completes<Response> defineWith(final String organizationId, final String unitId, final String contextId, final SchemaData data) {
    if (!Naming.isValid(data.name)) {
      return Completes.withSuccess(Response.of(BadRequest, Naming.invalidNameMessage(data.name)));
    }

    return Schema.with(grid, ContextId.existing(organizationId, unitId, contextId), Category.valueOf(data.category),
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
            .andThenTo(schemas -> schemas == null
                    ? Completes.withSuccess(Response.of(NotFound, serialized("Schemas not found!"))) // hit in unit tests
                    : Completes.withSuccess(Response.of(Ok, serialized(schemas.all()))))
            .otherwise(response -> Response.of(NotFound, serialized("Schemas not found!"))) // hit in production
            .recoverFrom(e -> Response.of(InternalServerError, serialized(e)));
  }

  public Completes<Response> querySchema(final String organizationId, final String unitId, final String contextId, final String schemaId) {
    return queries
            .schema(organizationId, unitId, contextId, schemaId)
            .andThenTo(schema -> schema == null
                    ? Completes.withSuccess(Response.of(NotFound, serialized("Schema not found!"))) // hit in unit tests
                    : Completes.withSuccess(Response.of(Ok, serialized(schema))))
            .otherwise(response -> Response.of(NotFound, serialized("Schema not found!"))) // hit in production
            .recoverFrom(e -> Response.of(InternalServerError, serialized(e)));
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
      post("/api/organizations/{organizationId}/units/{unitId}/contexts/{contextId}/schemas")
        .param(String.class)
        .param(String.class)
        .param(String.class)
        .body(SchemaData.class)
        .handle(this::defineWith),
      patch("/api/organizations/{organizationId}/units/{unitId}/contexts/{contextId}/schemas/{schemaId}/category")
        .param(String.class)
        .param(String.class)
        .param(String.class)
        .param(String.class)
        .body(String.class)
        .handle(this::categorizeAs),
      patch("/api/organizations/{organizationId}/units/{unitId}/contexts/{contextId}/schemas/{schemaId}/scope")
        .param(String.class)
        .param(String.class)
        .param(String.class)
        .param(String.class)
        .body(String.class)
        .handle(this::scopeAs),
      patch("/api/organizations/{organizationId}/units/{unitId}/contexts/{contextId}/schemas/{schemaId}/description")
        .param(String.class)
        .param(String.class)
        .param(String.class)
        .param(String.class)
        .body(String.class)
        .handle(this::describeAs),
      patch("/api/organizations/{organizationId}/units/{unitId}/contexts/{contextId}/schemas/{schemaId}/name")
        .param(String.class)
        .param(String.class)
        .param(String.class)
        .param(String.class)
        .body(String.class)
        .handle(this::renameTo),
      put("/api/organizations/{organizationId}/units/{unitId}/contexts/{contextId}/schemas/{schemaId}")
        .param(String.class)
        .param(String.class)
        .param(String.class)
        .param(String.class)
        .body(SchemaData.class)
        .handle(this::redefineWith),
      get("/api/organizations/{organizationId}/units/{unitId}/contexts/{contextId}/schemas")
        .param(String.class)
        .param(String.class)
        .param(String.class)
        .handle(this::querySchemas),
      get("/api/organizations/{organizationId}/units/{unitId}/contexts/{contextId}/schemas/{schemaId}")
        .param(String.class)
        .param(String.class)
        .param(String.class)
        .param(String.class)
        .handle(this::querySchema),
      get("/api/schema/categories")
        .handle(this::querySchemaCategories),
      get("/api/schema/scopes")
        .handle(this::querySchemaScopes));
  }

  private String schemaLocation(final SchemaId schemaId) {
    return String.format(SchemasPath, schemaId.organizationId().value, schemaId.unitId().value, schemaId.contextId.value, schemaId.value);
  }
}
