// Copyright © 2012-2020 VLINGO LABS. All rights reserved.
//
// This Source Code Form is subject to the terms of the
// Mozilla Public License, v. 2.0. If a copy of the MPL
// was not distributed with this file, You can obtain
// one at https://mozilla.org/MPL/2.0/.

package io.vlingo.schemata.resource;

import static io.vlingo.common.serialization.JsonSerialization.serialized;
import static io.vlingo.http.Response.Status.*;
import static io.vlingo.http.ResponseHeader.ContentLength;
import static io.vlingo.http.ResponseHeader.ContentType;
import static io.vlingo.http.ResponseHeader.Location;
import static io.vlingo.http.ResponseHeader.of;
import static io.vlingo.http.resource.ResourceBuilder.get;
import static io.vlingo.http.resource.ResourceBuilder.patch;
import static io.vlingo.http.resource.ResourceBuilder.post;
import static io.vlingo.http.resource.ResourceBuilder.resource;
import static io.vlingo.schemata.Schemata.NoId;
import static io.vlingo.schemata.Schemata.SchemaVersionsPath;
import static io.vlingo.schemata.Schemata.StageName;

import io.vlingo.actors.Logger;
import io.vlingo.actors.Stage;
import io.vlingo.actors.World;
import io.vlingo.common.Completes;
import io.vlingo.common.CompletesOutcomeT;
import io.vlingo.common.version.SemanticVersion;
import io.vlingo.http.Header.Headers;
import io.vlingo.http.Response;
import io.vlingo.http.ResponseHeader;
import io.vlingo.http.resource.Resource;
import io.vlingo.http.resource.ResourceHandler;
import io.vlingo.schemata.model.FullyQualifiedReference;
import io.vlingo.schemata.model.Id.SchemaId;
import io.vlingo.schemata.model.Id.SchemaVersionId;
import io.vlingo.schemata.model.SchemaVersion;
import io.vlingo.schemata.model.SchemaVersion.Specification;
import io.vlingo.schemata.model.SchemaVersion.Version;
import io.vlingo.schemata.model.SchemaVersionState;
import io.vlingo.schemata.model.SpecificationDiff;
import io.vlingo.schemata.query.Queries;
import io.vlingo.schemata.resource.data.SchemaVersionData;

public class SchemaVersionResource extends ResourceHandler {
    private final SchemaVersionCommands commands;
    private final Stage stage;
    private final Logger logger;

  public SchemaVersionResource(final World world) {
        this.stage = world.stageNamed(StageName);
        this.commands = new SchemaVersionCommands(this.stage, 10);
        this.logger = world.defaultLogger();
    }

    public Completes<Response> defineWith(
            final String organizationId,
            final String unitId,
            final String contextId,
            final String schemaId,
            final SchemaVersionData data) {

        if (!data.hasSpecification()) {
            return Completes.withSuccess(Response.of(BadRequest, "Missing specification"));
        }
        if (!data.validVersions()) {
            return Completes.withSuccess(Response.of(BadRequest, "Conflicting versions"));
        }

      final SemanticVersion previousSemantic = SemanticVersion.from(data.previousVersion);
      final SemanticVersion currentSemantic = SemanticVersion.from(data.currentVersion);

        // FIXME: Refactor into one reactive pipeline without awaiting
      if(currentSemantic.equals(previousSemantic.nextPatch()) || currentSemantic.equals(previousSemantic.nextMinor())) {
        SchemaVersionData previousVersion = Queries.forSchemaVersions().schemaVersionOfVersion(
            organizationId,
            unitId,
            contextId,
            schemaId,
            data.previousVersion)
                .andThen(o -> o.resolve(
                        e -> Completes.withSuccess(Response.of(NotFound, "Tried to update non-existing version " + previousSemantic.toString())),
                        svd -> svd
                ))
            .await();

        if(
          (previousVersion == null || previousVersion.isNone())
          && !previousSemantic.equals(SemanticVersion.from(0,0,0))
        ) {
          return Completes.withSuccess(Response.of(NotFound, "Tried to update non-existing version " + previousSemantic.toString()));
        }

        if(previousVersion != null && !previousVersion.isNone() ) {
          SpecificationDiff diff = commands
              .diffAgainst(
                  SchemaVersionId.existing(
                      organizationId, unitId, contextId, schemaId,
                      previousVersion.schemaVersionId),
                  data)
              .answer()
              .await();

          if (!diff.isCompatible()) {
            return Completes.withSuccess(Response.of(Conflict, serialized(diff)));
          }
        }
      }

      return SchemaVersion.with(stage, SchemaId.existing(organizationId, unitId, contextId, schemaId), Specification.of(data.specification), data.description, Version.of(data.previousVersion), Version.of(data.currentVersion))
              .andThenTo(3000, state -> {
                  final String location = schemaVersionLocation(state.schemaVersionId);
                  final Headers<ResponseHeader> headers = Headers.of(
                          of(Location, location),
                          of(ContentType, "application/json; charset=UTF-8")
                  );
                  final String serialized = serialized(SchemaVersionData.from(state));

                  return Completes.withSuccess(Response.of(Created, headers, serialized));
              })
              .otherwise(response -> Response.of(Conflict, serialized(SchemaVersionData.from(organizationId, unitId, contextId, schemaId, NoId, data.specification, data.description, "Draft", data.previousVersion, data.currentVersion))))
          .recoverFrom(ex -> {
            logger.error("{}",ex);
            return Response.of(Conflict, serialized(SchemaVersionData.from(organizationId, unitId, contextId, schemaId, NoId, data.specification, data.description, "Draft", data.previousVersion, data.currentVersion)));

          });
    }

    public Completes<Response> describeAs(final String organizationId, final String unitId, final String contextId, final String schemaId, final String schemaVersionId, final String description) {
        return commands
                .describeAs(SchemaVersionId.existing(organizationId, unitId, contextId, schemaId, schemaVersionId), description).answer()
                .andThenTo(state -> Completes.withSuccess(Response.of(Ok, serialized(SchemaVersionData.from(state)))));
    }

    public Completes<Response> specifyWith(final String organizationId, final String unitId, final String contextId, final String schemaId, final String schemaVersionId, final String specification) {
        if (!SchemaVersionData.hasSpecification(specification)) {
            return Completes.withSuccess(Response.of(BadRequest, "Missing specification"));
        }

        //FIXME: add diff/validation as like in define with

        return commands
                .specifyWith(SchemaVersionId.existing(organizationId, unitId, contextId, schemaId, schemaVersionId), Specification.of(specification)).answer()
                .andThenTo(state -> Completes.withSuccess(Response.of(Ok, serialized(SchemaVersionData.from(state)))));
    }

    public Completes<Response> statusOf(final String organizationId, final String unitId, final String contextId, final String schemaId, final String schemaVersionId, final String status) {
        final SchemaVersionId id = SchemaVersionId.existing(organizationId, unitId, contextId, schemaId, schemaVersionId);
        final Completes<SchemaVersionState> answer;

        switch (status.toLowerCase()) {
            case "published":
                answer = commands.publish(id).answer();
                break;
            case "deprecated":
                answer = commands.deprecate(id).answer();
                break;
            case "removed":
                answer = commands.remove(id).answer();
                break;
            default:
                return Completes.withSuccess(Response.of(Conflict, serialized(SchemaVersionData.from(organizationId, unitId, contextId, schemaId, schemaVersionId, "", "", status, "", ""))));
        }

        return answer.andThenTo(state -> {
            if (status.toLowerCase().equals(state.status.value.toLowerCase()))
                return Completes.withSuccess(Response.of(Ok, serialized(SchemaVersionData.from(state))));
            else
                return Completes.withSuccess(Response.of(
                        Conflict,
                        "Status '" + status + "' cannot be reached from current status '" + state.status.value + "'"));
        });
    }

    public Completes<Response> querySchemaVersions(final String organizationId, final String unitId, final String contextId, final String schemaId) {
        return Queries.forSchemaVersions()
                .schemaVersionsByIds(organizationId, unitId, contextId, schemaId)
                .andThenTo(schemaVersions -> Completes.withSuccess(Response.of(Ok, serialized(schemaVersions))));
    }

    public Completes<Response> querySchemaVersionByIds(final String organizationId, final String unitId, final String contextId, final String schemaId, final String schemaVersionId) {
        return Queries.forSchemaVersions()
                .schemaVersion(organizationId, unitId, contextId, schemaId, schemaVersionId)
                .andThen(o -> o.resolve(
                        e -> Response.of(NotFound, serialized(e)),
                        sv -> Response.of(
                                Ok,
                                Headers.of(of(ContentType, "application/json; charset=UTF-8")),
                                serialized(sv))
                ))
                .recoverFrom(e -> Response.of(InternalServerError, serialized(e)));
    }

    public Completes<Response> searchSchemaVersionsByNames(final String organization, final String unit, final String context, final String schema) {
        return Queries.forSchemaVersions()
                .schemaVersionsByNames(organization, unit, context, schema)
                .andThenTo(schemaVersionData -> Completes.withSuccess(Response.of(Ok, serialized(schemaVersionData))));
    }

    public Completes<Response> searchSchemaVersionByNames(final String organization, final String unit, final String context, final String schema, final String schemaVersion) {
        return Queries.forSchemaVersions()
                .schemaVersionOf(organization, unit, context, schema, schemaVersion)
                .andThen(o -> o.resolve(
                        e -> Response.of(NotFound, serialized(e)),
                        sv -> Response.of(
                                Ok,
                                Headers.of(of(ContentType, "application/json; charset=UTF-8")),
                                serialized(sv))
                ))
                .recoverFrom(e -> Response.of(InternalServerError, serialized(e)));
  }

    public Completes<Response> searchSchemaVersions(final String schemaVersion, final String organization, final String unit, final String context, final String schema) {
        if (schemaVersion == null) {
            return searchSchemaVersionsByNames(organization, unit, context, schema);
        } else {
            return searchSchemaVersionByNames(organization, unit, context, schema, schemaVersion);
        }
    }

    public Completes<Response> pushSchemaVersion(final String reference, final SchemaVersionData data) {

        FullyQualifiedReference fqr;
        try {
            fqr = FullyQualifiedReference.from(reference);
        } catch (IllegalArgumentException ex) {
            return Completes.withSuccess(Response.of(
                    BadRequest,
                    Headers.of(of(ContentLength, ex.getMessage().length())),
                    ex.getMessage()));
        }

        if (!fqr.isSchemaVersionReference()) {
            final String msg = "Include the version of the schema to push";
            return Completes.withSuccess(Response.of(
                    BadRequest,
                    Headers.of(of(ContentLength, msg.length())),
                    msg));
        }

        SchemaVersionData updatedSchemaVersionData = Queries.forSchemas().schemaVersionByNames(
                fqr.organization,
                fqr.unit,
                fqr.context,
                fqr.schema
        )
                .andThen(outcome -> outcome.get()) // FIXME: Use transformer
                .andThenTo(schemaData -> Completes.withSuccess(SchemaVersionData.from(
                schemaData.organizationId,
                schemaData.unitId,
                schemaData.contextId,
                schemaData.schemaId,
                null,
                data.specification,
                data.description,
                SchemaVersion.Status.Draft.value,
                data.previousVersion,
                fqr.schemaVersion))
        ).await();

        return this.defineWith(
          updatedSchemaVersionData.organizationId,
          updatedSchemaVersionData.unitId,
          updatedSchemaVersionData.contextId,
          updatedSchemaVersionData.schemaId,
          updatedSchemaVersionData
      );
    }

    public Completes<Response> retrieveSchemaVersion(final String reference) {
        FullyQualifiedReference fqr;
        try {
            fqr = FullyQualifiedReference.from(reference);
        } catch (IllegalArgumentException ex) {
            return Completes.withSuccess(Response.of(
              BadRequest,
              Headers.of(of(ContentLength, ex.getMessage().length())),
              ex.getMessage()));
        }

        if (!fqr.isSchemaVersionReference()) {
            final String msg = "Include the version of the schema to retrieve";
            return Completes.withSuccess(Response.of(
              BadRequest,
              Headers.of(of(ContentLength, msg.length())),
              msg));
        }

        return Queries.forSchemaVersions().schemaVersionOf(
          fqr.organization,
          fqr.unit,
          fqr.context,
          fqr.schema,
          fqr.schemaVersion)
          .andThen(o -> o.resolve(
                  e -> Response.of(NotFound, serialized(e)),
                  sv -> Response.of(
                          Ok,
                          Headers.of(of(ContentType, "application/json; charset=UTF-8")),
                          serialized(sv))
          ))
          .recoverFrom(e -> Response.of(InternalServerError, serialized(e)));

    }

    public Completes<Response> retrieveSchemaVersionStatus(final String reference) {
        FullyQualifiedReference fqr;
        try {
            fqr = FullyQualifiedReference.from(reference);
        } catch (IllegalArgumentException ex) {
            return Completes.withSuccess(Response.of(
              BadRequest,
              Headers.of(of(ContentLength, ex.getMessage().length())),
              ex.getMessage()));
        }

        if (!fqr.isSchemaVersionReference()) {
            final String msg = "Include the version of the schema to retrieve";
            return Completes.withSuccess(Response.of(
              BadRequest,
              Headers.of(of(ContentLength, msg.length())),
              msg));
        }

        return Queries.forSchemaVersions().schemaVersionOf(
          fqr.organization,
          fqr.unit,
          fqr.context,
          fqr.schema,
          fqr.schemaVersion)
          .andThen(o -> o.resolve(
                  e -> Response.of(NotFound, serialized(e)),
                  sv -> Response.of(
                          Ok,
                          Headers.of(of(ContentType, "text/plain; charset=UTF-8")),
                          sv.status)
          ))
          .recoverFrom(e -> Response.of(InternalServerError, serialized(e)));
    }

    @Override
    public Resource<?> routes() {
        return resource("SchemaVersion Resource", 1,
                post("/organizations/{organizationId}/units/{unitId}/contexts/{contextId}/schemas/{schemaId}/versions")
                        .param(String.class)
                        .param(String.class)
                        .param(String.class)
                        .param(String.class)
                        .body(SchemaVersionData.class)
                        .handle(this::defineWith),
                patch("/organizations/{organizationId}/units/{unitId}/contexts/{contextId}/schemas/{schemaId}/versions/{schemaVersionId}/description")
                        .param(String.class)
                        .param(String.class)
                        .param(String.class)
                        .param(String.class)
                        .param(String.class)
                        .body(String.class)
                        .handle(this::describeAs),
                patch("/organizations/{organizationId}/units/{unitId}/contexts/{contextId}/schemas/{schemaId}/versions/{schemaVersionId}/specification")
                        .param(String.class)
                        .param(String.class)
                        .param(String.class)
                        .param(String.class)
                        .param(String.class)
                        .body(String.class)
                        .handle(this::specifyWith),
                patch("/organizations/{organizationId}/units/{unitId}/contexts/{contextId}/schemas/{schemaId}/versions/{schemaVersionId}/status")
                        .param(String.class)
                        .param(String.class)
                        .param(String.class)
                        .param(String.class)
                        .param(String.class)
                        .body(String.class)
                        .handle(this::statusOf),
                get("/organizations/{organizationId}/units/{unitId}/contexts/{contextId}/schemas/{schemaId}/versions")
                        .param(String.class)
                        .param(String.class)
                        .param(String.class)
                        .param(String.class)
                        .handle(this::querySchemaVersions),
                get("/organizations/{organizationId}/units/{unitId}/contexts/{contextId}/schemas/{schemaId}/versions/{schemaVersionId}")
                        .param(String.class)
                        .param(String.class)
                        .param(String.class)
                        .param(String.class)
                        .param(String.class)
                        .handle(this::querySchemaVersionByIds),
                get("/versions/search")
                        .query("version", String.class, null)
                        .query("organization", String.class)
                        .query("unit", String.class)
                        .query("context", String.class)
                        .query("schema", String.class)
                        .handle(this::searchSchemaVersions),
                post("/versions/{reference}")
                        .param(String.class)
                        .body(SchemaVersionData.class)
                        .handle(this::pushSchemaVersion),
                get("/versions/{reference}")
                  .param(String.class)
                  .handle(this::retrieveSchemaVersion),
                get("/versions/{reference}/status")
                  .param(String.class)
                  .handle(this::retrieveSchemaVersionStatus));
    }

    private String schemaVersionLocation(final SchemaVersionId schemaVersionId) {
        return String.format(SchemaVersionsPath, schemaVersionId.organizationId().value, schemaVersionId.unitId().value, schemaVersionId.contextId().value, schemaVersionId.schemaId.value, schemaVersionId.value);
    }
}
