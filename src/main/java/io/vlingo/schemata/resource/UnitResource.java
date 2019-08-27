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
import static io.vlingo.http.resource.ResourceBuilder.get;
import static io.vlingo.http.resource.ResourceBuilder.patch;
import static io.vlingo.http.resource.ResourceBuilder.post;
import static io.vlingo.http.resource.ResourceBuilder.resource;
import static io.vlingo.schemata.Schemata.NoId;
import static io.vlingo.schemata.Schemata.StageName;
import static io.vlingo.schemata.Schemata.UnitsPath;

import io.vlingo.actors.Stage;
import io.vlingo.actors.World;
import io.vlingo.common.Completes;
import io.vlingo.http.Header.Headers;
import io.vlingo.http.Response;
import io.vlingo.http.ResponseHeader;
import io.vlingo.http.resource.Resource;
import io.vlingo.http.resource.ResourceHandler;
import io.vlingo.schemata.model.Id.OrganizationId;
import io.vlingo.schemata.model.Id.UnitId;
import io.vlingo.schemata.model.Unit;
import io.vlingo.schemata.query.UnitQueries;
import io.vlingo.schemata.resource.data.UnitData;

public class UnitResource extends ResourceHandler {
  private final UnitCommands commands;
  private final UnitQueries queries;
  private final Stage stage;

  public UnitResource(final World world, final UnitQueries queries) {
    this.stage = world.stageNamed(StageName);
    this.queries = queries;
    this.commands = new UnitCommands(this.stage, 10);
  }

  public Completes<Response> defineWith(final String organizationId, final UnitData data) {
    return Unit.with(stage, OrganizationId.existing(organizationId), data.name, data.description)
            .andThenTo(3000, state -> {
                final String location = unitLocation(state.unitId);
                final Headers<ResponseHeader> headers = headers(of(Location, location));
                final String serialized = serialized(UnitData.from(state));

                return Completes.withSuccess(Response.of(Created, headers, serialized));
              })
            .otherwise(response -> Response.of(Conflict, serialized(UnitData.from(organizationId, NoId, data.name, data.description))));
  }

  public Completes<Response> describeAs(final String organizationId, final String unitId, final String description) {
    return commands
              .describeAs(UnitId.existing(organizationId, unitId), description).answer()
              .andThenTo(state -> Completes.withSuccess(Response.of(Ok, serialized(UnitData.from(state)))));
  }

  public Completes<Response> renameTo(final String organizationId, final String unitId, final String name) {
    return commands
            .renameTo(UnitId.existing(organizationId, unitId), name).answer()
            .andThenTo(state -> Completes.withSuccess(Response.of(Ok, serialized(UnitData.from(state)))));
  }

  public Completes<Response> queryUnits(final String organizationId) {
    return queries
            .units(organizationId)
            .andThenTo(units -> Completes.withSuccess(Response.of(Ok, serialized(units))));
  }

  public Completes<Response> queryUnit(final String organizationId, final String unitId) {
    return queries
            .unit(organizationId, unitId)
            .andThenTo(unit -> Completes.withSuccess(Response.of(Ok, serialized(unit))));
  }

  @Override
  public Resource<?> routes() {
    return resource("Unit Resource",
      post("/organizations/{organizationId}/units")
        .param(String.class)
        .body(UnitData.class)
        .handle(this::defineWith),
      patch("/organizations/{organizationId}/units/{unitId}/description")
        .param(String.class)
        .param(String.class)
        .body(String.class)
        .handle(this::describeAs),
      patch("/organizations/{organizationId}/units/{unitId}/name")
        .param(String.class)
        .param(String.class)
        .body(String.class)
        .handle(this::renameTo),
      get("/organizations/{organizationId}/units")
        .param(String.class)
        .handle(this::queryUnits),
      get("/organizations/{organizationId}/units/{unitId}")
        .param(String.class)
        .param(String.class)
        .handle(this::queryUnit));
  }

  private String unitLocation(final UnitId unitId) {
    return String.format(UnitsPath, unitId.organizationId.value, unitId.value);
  }
}
