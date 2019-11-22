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
import static io.vlingo.http.ResponseHeader.ContentType;
import static io.vlingo.http.ResponseHeader.Location;
import static io.vlingo.http.ResponseHeader.of;
import static io.vlingo.http.resource.ResourceBuilder.get;
import static io.vlingo.http.resource.ResourceBuilder.patch;
import static io.vlingo.http.resource.ResourceBuilder.post;
import static io.vlingo.http.resource.ResourceBuilder.put;
import static io.vlingo.http.resource.ResourceBuilder.resource;
import static io.vlingo.schemata.Schemata.ContextsPath;
import static io.vlingo.schemata.Schemata.NoId;
import static io.vlingo.schemata.Schemata.StageName;

import io.vlingo.actors.Stage;
import io.vlingo.actors.World;
import io.vlingo.common.Completes;
import io.vlingo.http.Header.Headers;
import io.vlingo.http.Response;
import io.vlingo.http.ResponseHeader;
import io.vlingo.http.resource.Resource;
import io.vlingo.http.resource.ResourceHandler;
import io.vlingo.schemata.model.Context;
import io.vlingo.schemata.model.Id.ContextId;
import io.vlingo.schemata.model.Id.UnitId;
import io.vlingo.schemata.model.Naming;
import io.vlingo.schemata.query.Queries;
import io.vlingo.schemata.resource.data.ContextData;

public class ContextResource extends ResourceHandler {
  private final ContextCommands commands;
  private final Stage stage;

  public ContextResource(final World world) {
    this.stage = world.stageNamed(StageName);
    this.commands = new ContextCommands(this.stage, 10);
  }

  public Completes<Response> defineWith(final String organizationId, final String unitId, final ContextData data) {
    if (Naming.isValid(data.namespace)) {
      Completes.withSuccess(Response.of(BadRequest, Naming.invalidNameMessage(data.namespace)));
    }

    return Context.with(stage, UnitId.existing(organizationId, unitId), data.namespace, data.description)
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
    return Queries.forContexts()
            .contexts(organizationId, unitId)
            .andThenTo(contexts -> Completes.withSuccess(Response.of(Ok, serialized(contexts))));
  }

  public Completes<Response> queryContext(final String organizationId, final String unitId, final String contextId) {
    return Queries.forContexts()
            .context(organizationId, unitId, contextId)
            .andThenTo(context -> Completes.withSuccess(Response.of(Ok, serialized(context))));
  }

  @Override
  public Resource<?> routes() {
    return resource("Context Resource", 1,
      post("/organizations/{organizationId}/units/{unitId}/contexts")
        .param(String.class)
        .param(String.class)
        .body(ContextData.class)
        .handle(this::defineWith),
      patch("/organizations/{organizationId}/units/{unitId}/contexts/{contextId}/description")
        .param(String.class)
        .param(String.class)
        .param(String.class)
        .body(String.class)
        .handle(this::describeAs),
      patch("/organizations/{organizationId}/units/{unitId}/contexts/{contextId}/namespace")
        .param(String.class)
        .param(String.class)
        .param(String.class)
        .body(String.class)
        .handle(this::moveToNamespace),
      put("/organizations/{organizationId}/units/{unitId}/contexts/{contextId}")
        .param(String.class)
        .param(String.class)
        .param(String.class)
        .body(ContextData.class)
        .handle(this::redefineWith),
      get("/organizations/{organizationId}/units/{unitId}/contexts")
        .param(String.class)
        .param(String.class)
        .handle(this::queryContexts),
      get("/organizations/{organizationId}/units/{unitId}/contexts/{contextId}")
        .param(String.class)
        .param(String.class)
        .param(String.class)
        .handle(this::queryContext));
  }

  private String contextLocation(final ContextId contextId) {
    return String.format(ContextsPath, contextId.organizationId().value, contextId.unitId.value, contextId.value);
  }
}
