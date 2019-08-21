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
import static io.vlingo.http.ResponseHeader.Location;
import static io.vlingo.http.ResponseHeader.headers;
import static io.vlingo.http.ResponseHeader.of;
import static io.vlingo.schemata.Schemata.ContextsPath;
import static io.vlingo.schemata.Schemata.NoId;
import static io.vlingo.schemata.Schemata.StageName;

import io.vlingo.actors.Stage;
import io.vlingo.actors.World;
import io.vlingo.common.Completes;
import io.vlingo.http.Header.Headers;
import io.vlingo.http.Response;
import io.vlingo.http.ResponseHeader;
import io.vlingo.http.resource.ResourceHandler;
import io.vlingo.schemata.model.Context;
import io.vlingo.schemata.model.Id.ContextId;
import io.vlingo.schemata.model.Id.UnitId;
import io.vlingo.schemata.resource.data.ContextData;

public class ContextResource extends ResourceHandler {
  private final ContextCommands commands;
  private final Stage stage;

  public ContextResource(final World world) {
    this.stage = world.stageNamed(StageName);
    this.commands = new ContextCommands(this.stage, 10);
  }

  public Completes<Response> defineWith(final String organizationId, final String unitId, final String namespace, final String description) {
    return Context.with(stage, UnitId.existing(organizationId, unitId), namespace, description)
            .andThenTo(state -> {
                final String location = contextLocation(state.contextId);
                final Headers<ResponseHeader> headers = headers(of(Location, location));
                final String serialized = serialized(ContextData.from(state));

                return Completes.withSuccess(Response.of(Created, headers, serialized));
              })
            .otherwise(response -> Response.of(Conflict, serialized(ContextData.from(NoId, NoId, NoId, namespace, description))));
  }

  public Completes<Response> describeAs(final String organizationId, final String unitId, final String contextId, final String description) {
    return commands
              .describeAs(ContextId.existing(organizationId, unitId, contextId), description).answer()
              .andThenTo(state -> Completes.withSuccess(Response.of(Ok, serialized(ContextData.from(state)))));
  }

  public Completes<Response> moveToNamespace(final String organizationId, final String unitId, final String contextId, final String namespace) {
    return commands
            .moveToNamespace(ContextId.existing(organizationId, unitId, contextId), namespace).answer()
            .andThenTo(state -> Completes.withSuccess(Response.of(Ok, serialized(ContextData.from(state)))));
  }

  private String contextLocation(final ContextId contextId) {
    return String.format(ContextsPath, contextId.organizationId().value, contextId.unitId.value, contextId.value);
  }
}
