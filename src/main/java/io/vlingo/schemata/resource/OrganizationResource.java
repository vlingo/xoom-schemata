// Copyright © 2012-2020 VLINGO LABS. All rights reserved.
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
import static io.vlingo.schemata.Schemata.NoId;
import static io.vlingo.schemata.Schemata.OrganizationsPath;
import static io.vlingo.schemata.Schemata.StageName;

import io.vlingo.actors.Stage;
import io.vlingo.actors.World;
import io.vlingo.common.Completes;
import io.vlingo.http.Body;
import io.vlingo.http.Header.Headers;
import io.vlingo.http.Response;
import io.vlingo.http.ResponseHeader;
import io.vlingo.http.resource.Resource;
import io.vlingo.http.resource.ResourceHandler;
import io.vlingo.schemata.model.Id.OrganizationId;
import io.vlingo.schemata.model.Naming;
import io.vlingo.schemata.model.Organization;
import io.vlingo.schemata.query.Queries;
import io.vlingo.schemata.resource.data.OrganizationData;

public class OrganizationResource extends ResourceHandler {
  private final OrganizationCommands commands;
  private final Stage stage;

  public OrganizationResource(final World world) {
    this.stage = world.stageNamed(StageName);
    this.commands = new OrganizationCommands(this.stage, 10);
  }

  public Completes<Response> defineWith(final OrganizationData data) {
    if (!Naming.isValid(data.name)) {
      return Completes.withSuccess(Response.of(BadRequest, Naming.invalidNameMessage(data.name)));
    }

    return Organization.with(stage, data.name, data.description)
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
    return Queries.forOrganizations()
            .organizations()
            .andThenTo(organizations -> Completes.withSuccess(Response.of(Ok, serialized(organizations))));
  }

  public Completes<Response> queryOrganization(final String organizationId) {
    return Queries.forOrganizations()
            .organization(organizationId)
            .andThenTo(organization -> Completes.withSuccess(Response.of(Ok, serialized(organization))));
  }

  @Override
  public Resource<?> routes() {
    return resource("Organization Resource", 1,
      post("/organizations")
        .body(OrganizationData.class)
        .handle(this::defineWith),
      put("/organizations/{organizationId}")
        .param(String.class)
        .body(OrganizationData.class)
        .handle(this::redefineWith),
      patch("/organizations/{organizationId}/description")
        .param(String.class)
        .body(String.class)
        .handle(this::describeAs),
      patch("/organizations/{organizationId}/name")
        .param(String.class)
        .body(String.class)
        .handle(this::renameTo),
      get("/organizations")
        .handle(this::queryOrganizations),
      get("/organizations/{organizationId}")
        .param(String.class)
        .handle(this::queryOrganization));
  }

  private String organizationLocation(final OrganizationId organizationId) {
    return String.format(OrganizationsPath, organizationId.value);
  }
}
