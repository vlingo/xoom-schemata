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
import io.vlingo.schemata.model.Id.OrganizationId;
import io.vlingo.schemata.model.Id.UnitId;
import io.vlingo.schemata.model.Naming;
import io.vlingo.schemata.model.Unit;
import io.vlingo.schemata.query.UnitQueries;
import io.vlingo.schemata.resource.data.UnitData;

import static io.vlingo.common.serialization.JsonSerialization.serialized;
import static io.vlingo.http.Response.Status.*;
import static io.vlingo.http.ResponseHeader.*;
import static io.vlingo.http.resource.ResourceBuilder.*;
import static io.vlingo.schemata.Schemata.*;

public class UnitResource extends ResourceHandler {
  private final UnitCommands commands;
  private final UnitQueries queries;
  private final Stage stage;

  public UnitResource(final World world, UnitQueries queries) {
    this.stage = world.stageNamed(StageName);
    this.commands = new UnitCommands(this.stage, 10);
    this.queries = queries;
  }

  public Completes<Response> defineWith(final String organizationId, final UnitData data) {
    if (!Naming.isValid(data.name)) {
      return Completes.withSuccess(Response.of(BadRequest, Naming.invalidNameMessage(data.name)));
    }

    return Unit.with(stage, OrganizationId.existing(organizationId), data.name, data.description)
            .andThenTo(3000, state -> {
                final String location = unitLocation(state.unitId);
                final Headers<ResponseHeader> headers =Headers.of(
                        of(Location, location),
                        of(ContentType, "application/json; charset=UTF-8")
                );
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

  public Completes<Response> redefineWith(final String organizationId, final String unitId, final UnitData data) {
    if (Naming.isValid(data.name)) {
      Completes.withSuccess(Response.of(BadRequest, Naming.invalidNameMessage(data.name)));
    }

    return commands
            .redefineWith(UnitId.existing(organizationId, unitId), data.name, data.description).answer()
            .andThenTo(state -> Completes.withSuccess(Response.of(Ok, serialized(UnitData.from(state)))));
  }

  public Completes<Response> renameTo(final String organizationId, final String unitId, final String name) {
    if (Naming.isValid(name)) {
      Completes.withSuccess(Response.of(BadRequest, Naming.invalidNameMessage(name)));
    }

    return commands
            .renameTo(UnitId.existing(organizationId, unitId), name).answer()
            .andThenTo(state -> Completes.withSuccess(Response.of(Ok, serialized(UnitData.from(state)))));
  }

  public Completes<Response> queryUnits(final String organizationId) {
    return queries
            .units(organizationId)
            .andThenTo(units -> Completes.withSuccess(Response.of(Ok, serialized(units))))
            .recoverFrom(e -> Response.of(InternalServerError, serialized(e)));
  }

  public Completes<Response> queryUnit(final String organizationId, final String unitId) {
    return queries
            .unit(organizationId, unitId)
            .andThenTo(unit -> unit == null
                    ? Completes.withSuccess(Response.of(NotFound, serialized("Unit not found!")))
                    : Completes.withSuccess(Response.of(Ok, serialized(unit))))
            .recoverFrom(e -> Response.of(InternalServerError, serialized(e)));
  }

  @Override
  public Resource<?> routes() {
    return resource("Unit Resource", 1,
      post("/organizations/{organizationId}/units")
        .param(String.class)
        .body(UnitData.class)
        .handle(this::defineWith),
      put("/organizations/{organizationId}/units/{unitId}")
        .param(String.class)
        .param(String.class)
        .body(UnitData.class)
        .handle(this::redefineWith),
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
