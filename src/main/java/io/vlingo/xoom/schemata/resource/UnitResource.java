// Copyright © 2012-2022 VLINGO LABS. All rights reserved.
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
import static io.vlingo.xoom.schemata.Schemata.NoId;
import static io.vlingo.xoom.schemata.Schemata.UnitsPath;

import io.vlingo.xoom.common.Completes;
import io.vlingo.xoom.http.Header.Headers;
import io.vlingo.xoom.http.Response;
import io.vlingo.xoom.http.ResponseHeader;
import io.vlingo.xoom.http.resource.DynamicResourceHandler;
import io.vlingo.xoom.http.resource.Resource;
import io.vlingo.xoom.lattice.grid.Grid;
import io.vlingo.xoom.schemata.infra.persistence.StorageProvider;
import io.vlingo.xoom.schemata.model.Id.OrganizationId;
import io.vlingo.xoom.schemata.model.Id.UnitId;
import io.vlingo.xoom.schemata.model.Naming;
import io.vlingo.xoom.schemata.model.Unit;
import io.vlingo.xoom.schemata.query.UnitQueries;
import io.vlingo.xoom.schemata.resource.data.UnitData;

public class UnitResource extends DynamicResourceHandler {
  private final Grid grid;
  private final UnitCommands commands;
  private final UnitQueries queries;

  public UnitResource(final Grid grid) {
    super(grid.world().stage());
    this.grid = grid;
    this.commands = new UnitCommands(grid, 10);
    this.queries = StorageProvider.instance().unitQueries;
  }

  public Completes<Response> defineWith(final String organizationId, final UnitData data) {
    if (!Naming.isValid(data.name)) {
      return Completes.withSuccess(Response.of(BadRequest, Naming.invalidNameMessage(data.name)));
    }

    return Unit.with(grid, OrganizationId.existing(organizationId), data.name, data.description)
            .andThenTo(3000, state -> {
                final String location = unitLocation(state.unitId);
                final Headers<ResponseHeader> headers = Headers.of(
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
            .andThenTo(units -> units == null
                    ? Completes.withSuccess(Response.of(NotFound, serialized("Units not found!"))) // hit in unit tests
                    : Completes.withSuccess(Response.of(Ok, serialized(units.all()))))
            .otherwise(response -> Response.of(NotFound, serialized("Units not found!"))) // hit in production
            .recoverFrom(e -> Response.of(InternalServerError, serialized(e)));
  }

  public Completes<Response> queryUnit(final String organizationId, final String unitId) {
    return queries
            .unit(organizationId, unitId)
            .andThenTo(unit -> unit == null
                    ? Completes.withSuccess(Response.of(NotFound, serialized("Unit not found!"))) // hit in unit tests
                    : Completes.withSuccess(Response.of(Ok, serialized(unit))))
            .otherwise(response -> Response.of(NotFound, serialized("Unit not found!"))) // hit in production
            .recoverFrom(e -> Response.of(InternalServerError, serialized(e)));
  }

  @Override
  public Resource<?> routes() {
    return resource("Unit Resource", 1,
      post("/api/organizations/{organizationId}/units")
        .param(String.class)
        .body(UnitData.class)
        .handle(this::defineWith),
      put("/api/organizations/{organizationId}/units/{unitId}")
        .param(String.class)
        .param(String.class)
        .body(UnitData.class)
        .handle(this::redefineWith),
      patch("/api/organizations/{organizationId}/units/{unitId}/description")
        .param(String.class)
        .param(String.class)
        .body(String.class)
        .handle(this::describeAs),
      patch("/api/organizations/{organizationId}/units/{unitId}/name")
        .param(String.class)
        .param(String.class)
        .body(String.class)
        .handle(this::renameTo),
      get("/api/organizations/{organizationId}/units")
        .param(String.class)
        .handle(this::queryUnits),
      get("/api/organizations/{organizationId}/units/{unitId}")
        .param(String.class)
        .param(String.class)
        .handle(this::queryUnit));
  }

  private String unitLocation(final UnitId unitId) {
    return String.format(UnitsPath, unitId.organizationId.value, unitId.value);
  }
}
