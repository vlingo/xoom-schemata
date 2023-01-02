// Copyright © 2012-2023 VLINGO LABS. All rights reserved.
//
// This Source Code Form is subject to the terms of the
// Mozilla Public License, v. 2.0. If a copy of the MPL
// was not distributed with this file, You can obtain
// one at https://mozilla.org/MPL/2.0/.

package io.vlingo.xoom.schemata.resource;

import static io.vlingo.xoom.common.serialization.JsonSerialization.serialized;
import static io.vlingo.xoom.http.Response.Status.BadRequest;
import static io.vlingo.xoom.http.Response.Status.Conflict;
import static io.vlingo.xoom.http.Response.Status.Created;
import static io.vlingo.xoom.http.Response.Status.InternalServerError;
import static io.vlingo.xoom.http.Response.Status.NotFound;
import static io.vlingo.xoom.http.Response.Status.Ok;
import static io.vlingo.xoom.http.ResponseHeader.ContentType;
import static io.vlingo.xoom.http.ResponseHeader.Location;
import static io.vlingo.xoom.http.ResponseHeader.of;
import static io.vlingo.xoom.http.resource.ResourceBuilder.get;
import static io.vlingo.xoom.http.resource.ResourceBuilder.patch;
import static io.vlingo.xoom.http.resource.ResourceBuilder.post;
import static io.vlingo.xoom.http.resource.ResourceBuilder.put;
import static io.vlingo.xoom.http.resource.ResourceBuilder.resource;
import static io.vlingo.xoom.schemata.Schemata.ContextsPath;
import static io.vlingo.xoom.schemata.Schemata.NoId;

import io.vlingo.xoom.common.Completes;
import io.vlingo.xoom.http.Header.Headers;
import io.vlingo.xoom.http.Response;
import io.vlingo.xoom.http.ResponseHeader;
import io.vlingo.xoom.http.resource.DynamicResourceHandler;
import io.vlingo.xoom.http.resource.Resource;
import io.vlingo.xoom.lattice.grid.Grid;
import io.vlingo.xoom.schemata.infra.persistence.StorageProvider;
import io.vlingo.xoom.schemata.model.Context;
import io.vlingo.xoom.schemata.model.Id.ContextId;
import io.vlingo.xoom.schemata.model.Id.UnitId;
import io.vlingo.xoom.schemata.model.Naming;
import io.vlingo.xoom.schemata.query.ContextQueries;
import io.vlingo.xoom.schemata.resource.data.ContextData;

public class ContextResource extends DynamicResourceHandler {
  private final Grid grid;
  private final ContextCommands commands;
  private final ContextQueries queries;

  public ContextResource(final Grid grid) {
    super(grid.world().stage());
    this.grid = grid;
    this.commands = new ContextCommands(grid, 10);
    this.queries = StorageProvider.instance().contextQueries;
  }

  public Completes<Response> defineWith(final String organizationId, final String unitId, final ContextData data) {
    if (Naming.isValid(data.namespace)) {
      Completes.withSuccess(Response.of(BadRequest, Naming.invalidNameMessage(data.namespace)));
    }

    return Context.with(grid, UnitId.existing(organizationId, unitId), data.namespace, data.description)
            .andThenTo(3000, state -> {
                final String location = contextLocation(state.contextId);
                final Headers<ResponseHeader> headers = Headers.of(
                        of(Location, location),
                        of(ContentType, "application/json; charset=UTF-8")
                );
                final String serialized = serialized(ContextData.from(state));

                return Completes.withSuccess(Response.of(Created, headers, serialized));
              })
            .otherwise(response -> Response.of(Conflict, serialized(ContextData.from(organizationId, unitId, NoId, data.namespace, data.description))));
  }

  public Completes<Response> describeAs(final String organizationId, final String unitId, final String contextId, final String description) {
    return commands
              .describeAs(ContextId.existing(organizationId, unitId, contextId), description).answer()
              .andThenTo(state -> Completes.withSuccess(Response.of(Ok, serialized(ContextData.from(state)))));
  }

  public Completes<Response> moveToNamespace(final String organizationId, final String unitId, final String contextId, final String namespace) {
    if (Naming.isValid(namespace)) {
      Completes.withSuccess(Response.of(BadRequest, Naming.invalidNameMessage(namespace)));
    }

    return commands
            .moveToNamespace(ContextId.existing(organizationId, unitId, contextId), namespace).answer()
            .andThenTo(state -> Completes.withSuccess(Response.of(Ok, serialized(ContextData.from(state)))));
  }

  public Completes<Response> redefineWith(final String organizationId, final String unitId, final String contextId, final ContextData data) {
    if (Naming.isValid(data.namespace)) {
      Completes.withSuccess(Response.of(BadRequest, Naming.invalidNameMessage(data.namespace)));
    }

    return commands
            .redefineWith(ContextId.existing(organizationId, unitId, contextId), data.namespace, data.description).answer()
            .andThenTo(state -> Completes.withSuccess(Response.of(Ok, serialized(ContextData.from(state)))));
  }

  public Completes<Response> queryContexts(final String organizationId, final String unitId) {
    return queries
            .contexts(organizationId, unitId)
            .andThenTo(contexts -> contexts == null
                    ? Completes.withSuccess(Response.of(NotFound, serialized("Contexts not found!")))
                    : Completes.withSuccess(Response.of(Ok, serialized(contexts.all()))))
            .otherwise(response -> Response.of(NotFound, serialized("Contexts not found!"))) // hit in production
            .recoverFrom(e -> Response.of(InternalServerError, serialized(e)));
  }

  public Completes<Response> queryContext(final String organizationId, final String unitId, final String contextId) {
    return queries
            .context(organizationId, unitId, contextId)
            .andThenTo(context -> context == null
                    ? Completes.withSuccess(Response.of(NotFound, serialized("Context not found!")))
                    : Completes.withSuccess(Response.of(Ok, serialized(context))))
            .otherwise(response -> Response.of(NotFound, serialized("Context not found!"))) // hit in production
            .recoverFrom(e -> Response.of(InternalServerError, serialized(e)));
  }

  @Override
  public Resource<?> routes() {
    return resource("Context Resource", 1,
      post("/api/organizations/{organizationId}/units/{unitId}/contexts")
        .param(String.class)
        .param(String.class)
        .body(ContextData.class)
        .handle(this::defineWith),
      patch("/api/organizations/{organizationId}/units/{unitId}/contexts/{contextId}/description")
        .param(String.class)
        .param(String.class)
        .param(String.class)
        .body(String.class)
        .handle(this::describeAs),
      patch("/api/organizations/{organizationId}/units/{unitId}/contexts/{contextId}/namespace")
        .param(String.class)
        .param(String.class)
        .param(String.class)
        .body(String.class)
        .handle(this::moveToNamespace),
      put("/api/organizations/{organizationId}/units/{unitId}/contexts/{contextId}")
        .param(String.class)
        .param(String.class)
        .param(String.class)
        .body(ContextData.class)
        .handle(this::redefineWith),
      get("/api/organizations/{organizationId}/units/{unitId}/contexts")
        .param(String.class)
        .param(String.class)
        .handle(this::queryContexts),
      get("/api/organizations/{organizationId}/units/{unitId}/contexts/{contextId}")
        .param(String.class)
        .param(String.class)
        .param(String.class)
        .handle(this::queryContext));
  }

  private String contextLocation(final ContextId contextId) {
    return String.format(ContextsPath, contextId.organizationId().value, contextId.unitId.value, contextId.value);
  }
}
