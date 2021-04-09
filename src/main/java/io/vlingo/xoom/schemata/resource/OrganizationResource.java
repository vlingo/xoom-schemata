// Copyright © 2012-2020 VLINGO LABS. All rights reserved.
//
// This Source Code Form is subject to the terms of the
// Mozilla Public License, v. 2.0. If a copy of the MPL
// was not distributed with this file, You can obtain
// one at https://mozilla.org/MPL/2.0/.

package io.vlingo.xoom.schemata.resource;

import io.vlingo.xoom.actors.Grid;
import io.vlingo.xoom.common.Completes;
import io.vlingo.xoom.http.Body;
import io.vlingo.xoom.http.Header.Headers;
import io.vlingo.xoom.http.Response;
import io.vlingo.xoom.http.ResponseHeader;
import io.vlingo.xoom.http.resource.DynamicResourceHandler;
import io.vlingo.xoom.http.resource.Resource;
import io.vlingo.xoom.schemata.infra.persistence.StorageProvider;
import io.vlingo.xoom.schemata.model.Id.OrganizationId;
import io.vlingo.xoom.schemata.model.Naming;
import io.vlingo.xoom.schemata.model.Organization;
import io.vlingo.xoom.schemata.query.OrganizationQueries;
import io.vlingo.xoom.schemata.query.view.OrganizationsView;
import io.vlingo.xoom.schemata.resource.data.OrganizationData;

import static io.vlingo.xoom.common.serialization.JsonSerialization.serialized;
import static io.vlingo.xoom.http.Response.Status.*;
import static io.vlingo.xoom.http.ResponseHeader.*;
import static io.vlingo.xoom.http.resource.ResourceBuilder.*;
import static io.vlingo.xoom.schemata.Schemata.NoId;
import static io.vlingo.xoom.schemata.Schemata.OrganizationsPath;

public class OrganizationResource extends DynamicResourceHandler {
  private final Grid grid;
  private final OrganizationCommands commands;
  private final OrganizationQueries queries;

  public OrganizationResource(final Grid grid) {
    super(grid.world().stage());
    this.grid = grid;
    this.commands = new OrganizationCommands(grid, 10);
    this.queries = StorageProvider.instance().organizationQueries;
  }

  public Completes<Response> defineWith(final OrganizationData data) {
    if (!Naming.isValid(data.name)) {
      return Completes.withSuccess(Response.of(BadRequest, Naming.invalidNameMessage(data.name)));
    }

    return Organization.with(grid, data.name, data.description)
            .andThenTo(3000, state -> {
                final String location = organizationLocation(state.organizationId);
                final Headers<ResponseHeader> headers = Headers.of(
                        of(Location, location),
                        of(ContentType, "application/json; charset=UTF-8")
                );
                final String serialized = serialized(OrganizationData.from(state));

                return Completes.withSuccess(Response.of(Created, headers, Body.from(serialized.getBytes(), Body.Encoding.UTF8)));
              })
            .otherwise(response -> Response.of(Conflict, serialized(OrganizationData.from(NoId, data.name, data.description))));
  }

  public Completes<Response> describeAs(final String organizationId, final String description) {
    return commands
              .describeAs(OrganizationId.existing(organizationId), description).answer()
              .andThenTo(state -> Completes.withSuccess(Response.of(Ok, serialized(OrganizationData.from(state)))));
  }

  public Completes<Response> redefineWith(final String organizationId, final OrganizationData organizationData) {
    if (!Naming.isValid(organizationData.name)) {
      Completes.withSuccess(Response.of(BadRequest, Naming.invalidNameMessage(organizationData.name)));
    }

    return commands
            .redefineWith(OrganizationId.existing(organizationId), organizationData.name, organizationData.description).answer()
            .andThenTo(state -> Completes.withSuccess(Response.of(Ok, serialized(OrganizationData.from(state)))));
  }

  public Completes<Response> renameTo(final String organizationId, final String name) {
    if (!Naming.isValid(name)) {
      Completes.withSuccess(Response.of(BadRequest, Naming.invalidNameMessage(name)));
    }

    return commands
            .renameTo(OrganizationId.existing(organizationId), name).answer()
            .andThenTo(state -> Completes.withSuccess(Response.of(Ok, serialized(OrganizationData.from(state)))));
  }

  public Completes<Response> queryOrganizations() {
    return queries
            .organizations()
            .andThenTo(organizations -> organizations == null
                    ? Completes.withSuccess(Response.of(Ok, serialized(OrganizationsView.empty().all())))
                    : Completes.withSuccess(Response.of(Ok, serialized(organizations.all()))))
            .otherwise(response -> Response.of(Ok, serialized(OrganizationsView.empty().all()))) // no OrganizationsView state found in stateStore
            .recoverFrom(e -> Response.of(InternalServerError, serialized(e)));
  }

  public Completes<Response> queryOrganization(final String organizationId) {
    return queries
            .organization(organizationId)
            .andThenTo(organization -> organization == null
                    ? Completes.withSuccess(Response.of(NotFound, serialized("Organization not found!"))) // hit by unit tests
                    : Completes.withSuccess(Response.of(Ok, serialized(organization))))
            .otherwise(response -> Response.of(NotFound, serialized("Organization not found!"))) // hit in production
            .recoverFrom(e -> Response.of(InternalServerError, serialized(e)));
  }

  @Override
  public Resource<?> routes() {
    return resource("Organization Resource", 1,
      post("/api/organizations")
        .body(OrganizationData.class)
        .handle(this::defineWith),
      put("/api/organizations/{organizationId}")
        .param(String.class)
        .body(OrganizationData.class)
        .handle(this::redefineWith),
      patch("/api/organizations/{organizationId}/description")
        .param(String.class)
        .body(String.class)
        .handle(this::describeAs),
      patch("/api/organizations/{organizationId}/name")
        .param(String.class)
        .body(String.class)
        .handle(this::renameTo),
      get("/api/organizations")
        .handle(this::queryOrganizations),
      get("/api/organizations/{organizationId}")
        .param(String.class)
        .handle(this::queryOrganization));
  }

  private String organizationLocation(final OrganizationId organizationId) {
    return String.format(OrganizationsPath, organizationId.value);
  }
}
