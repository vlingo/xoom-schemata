// Copyright © 2012-2020 VLINGO LABS. All rights reserved.
//
// This Source Code Form is subject to the terms of the
// Mozilla Public License, v. 2.0. If a copy of the MPL
// was not distributed with this file, You can obtain
// one at https://mozilla.org/MPL/2.0/.

package io.vlingo.schemata.resource;

import io.vlingo.actors.Logger;
import io.vlingo.actors.Stage;
import io.vlingo.common.Completes;
import io.vlingo.common.serialization.JsonSerialization;
import io.vlingo.common.version.SemanticVersion;
import io.vlingo.http.Header.Headers;
import io.vlingo.http.Response;
import io.vlingo.http.ResponseHeader;
import io.vlingo.http.resource.DynamicResourceHandler;
import io.vlingo.http.resource.Resource;
import io.vlingo.schemata.infra.persistence.StorageProvider;
import io.vlingo.schemata.model.FullyQualifiedReference;
import io.vlingo.schemata.model.Id.SchemaId;
import io.vlingo.schemata.model.Id.SchemaVersionId;
import io.vlingo.schemata.model.Path;
import io.vlingo.schemata.model.SchemaVersion;
import io.vlingo.schemata.model.SchemaVersion.Specification;
import io.vlingo.schemata.model.SchemaVersion.Version;
import io.vlingo.schemata.model.SchemaVersionState;
import io.vlingo.schemata.query.CodeQueries;
import io.vlingo.schemata.query.SchemaQueries;
import io.vlingo.schemata.query.SchemaVersionQueries;
import io.vlingo.schemata.query.view.SchemaVersionView;
import io.vlingo.schemata.resource.data.SchemaVersionData;

import static io.vlingo.common.serialization.JsonSerialization.serialized;
import static io.vlingo.http.Response.Status.*;
import static io.vlingo.http.ResponseHeader.*;
import static io.vlingo.http.resource.ResourceBuilder.*;
import static io.vlingo.schemata.Schemata.NoId;
import static io.vlingo.schemata.Schemata.SchemaVersionsPath;
import static io.vlingo.schemata.query.SchemaVersionQueries.GreatestVersion;

public class SchemaVersionResource extends DynamicResourceHandler {
    private final SchemaVersionCommands commands;
    private final SchemaQueries schemaQueries;
    private final SchemaVersionQueries schemaVersionQueries;
    private final CodeQueries codeQueries;
    private final Stage stage;
    private final Logger logger;

  public SchemaVersionResource(final Stage stage) {
        super(stage);
        this.stage = stage;
        this.commands = new SchemaVersionCommands(this.stage, 10);
        this.schemaQueries = StorageProvider.instance().schemaQueries;
        this.schemaVersionQueries = StorageProvider.instance().schemaVersionQueries;
        this.codeQueries = StorageProvider.instance().codeQueries;
        this.logger = stage.world().defaultLogger();
    }

    /*
     * Since this has become a convoluted mess due to workarounds, framework changes and poorly understood APIs,
     * I'll outline what is done in here as a breadcrumb trail for future refactoring me:
     *
     * * Check whether the request contains a specification
     * * Check whether the version is syntactically valid (i.e. is an update to a existing version or 0.0.1)
     * * Load the previous version (i.e. the one that is updated); return an error if it does not exist
     * * If the new version is a patch or minor update, diff the previous version against the specification passed in
     *   ; return an error if there are incompatible changes
     * * Otherwise create the new specification persistently and return it
     */
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
        SchemaVersionView previousVersion  = schemaVersionQueries.schemaVersionsByIds(organizationId, unitId, contextId, schemaId)
                .andThen(view -> GreatestVersion.equals(data.previousVersion)
                  ? view.greatestVersion()
                  : view.withVersion(data.previousVersion))
                .await();

        if (
          (previousVersion == null || previousVersion.isNone())
          && !previousSemantic.equals(SemanticVersion.from(0,0,0))
        ) {
          return Completes.withSuccess(Response.of(NotFound, "Tried to update non-existing version " + previousSemantic.toString()));
        }

        if(previousVersion != null && !previousVersion.isNone() ) {
          String incompatibleDiffResult = commands
              .diffAgainst(
                  SchemaVersionId.existing(
                      organizationId, unitId, contextId, schemaId,
                      previousVersion.schemaVersionId()),
                  data)
              .answer()
              .andThen(o -> o.resolve(
                          JsonSerialization::serialized,
                          diff -> diff.isCompatible() ? null : serialized(diff)
                  ))
              .await();

          if (incompatibleDiffResult != null) {
            return Completes.withSuccess(
                    Response.of(
                            Conflict,
                            Headers.of(of(ContentType, "application/json; charset=UTF-8")),
                            serialized(incompatibleDiffResult)));
          }
        }
      }

      return SchemaVersion.with(stage, SchemaId.existing(organizationId, unitId, contextId, schemaId), Specification.of(data.specification), data.description,
              Version.of(data.previousVersion), Version.of(data.currentVersion))
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
        return schemaVersionQueries
                .schemaVersionsByIds(organizationId, unitId, contextId, schemaId)
                .andThenTo(schemaVersions -> schemaVersions == null
                        ? Completes.withSuccess(Response.of(NotFound, serialized("SchemaVersions not found!")))
                        : Completes.withSuccess(Response.of(Ok, serialized(schemaVersions.all()))))
                .otherwise(response -> Response.of(NotFound, serialized("SchemaVersions not found!"))) // hit in production
                .recoverFrom(e -> Response.of(InternalServerError, serialized(e)));
    }

    public Completes<Response> querySchemaVersionByIds(final String organizationId, final String unitId, final String contextId, final String schemaId, final String schemaVersionId) {
        return schemaVersionQueries
                .schemaVersion(organizationId, unitId, contextId, schemaId, schemaVersionId)
                .andThenTo(schemaVersion -> schemaVersion == null
                        ? Completes.withSuccess(Response.of(NotFound, serialized("SchemaVersion not found!")))
                        : Completes.withSuccess(Response.of(Ok, serialized(schemaVersion))))
                .otherwise(response -> Response.of(NotFound, serialized("SchemaVersion not found!"))) // hit in production
                .recoverFrom(e -> Response.of(InternalServerError, serialized(e)));
    }

    public Completes<Response> searchSchemaVersionsByNames(final String organization, final String unit, final String context, final String schema) {
        return schemaQueries
                .schemaByNames(organization, unit, context, schema)
                .andThenTo(namedSchemaView -> namedSchemaView == null
                        ? Completes.withSuccess(Response.of(NotFound, serialized("NamedSchema not found!")))
                        : Completes.withSuccess(Response.of(Ok, serialized(namedSchemaView.schemaVersions()))))
                .otherwise(response -> Response.of(NotFound, serialized("NamedSchema not found!"))) // hit in production
                .recoverFrom(e -> Response.of(InternalServerError, serialized(e)));
    }

    public Completes<Response> searchSchemaVersionByNames(final String organization, final String unit, final String context, final String schema, final String schemaVersion) {
        return schemaQueries
                .schemaByNames(organization, unit, context, schema)
                .andThenTo(namedSchemaView -> namedSchemaView == null
                        ? Completes.withSuccess(Response.of(NotFound, serialized("NamedSchema not found!")))
                        : Completes.withSuccess(Response.of(Ok, serialized(namedSchemaView.versionOf(schemaVersion)))))
                .otherwise(response -> Response.of(NotFound, serialized("NamedSchema not found!"))) // hit in production
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

        SchemaVersionData updatedSchemaVersionData = schemaQueries.schemaByNames(fqr.organization,
                    fqr.unit, fqr.context, fqr.schema)
                .andThenTo(namedSchemaView -> Completes.withSuccess(SchemaVersionData.from(
                        namedSchemaView.organizationId(),
                        namedSchemaView.unitId(),
                        namedSchemaView.contextId(),
                        namedSchemaView.schemaId(),
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
        if (Path.isValidReference(reference, false)) {
            Path path = Path.with(reference, false);
            return codeQueries.codeFor(path)
                    .andThenTo(codeView -> codeView == null
                            ? Completes.withSuccess(Response.of(NotFound, serialized("Code not found!")))
                            : Completes.withSuccess(Response.of(Ok, serialized(codeView.schemaVersionView()))))
                    .otherwise(response -> Response.of(NotFound, serialized("Code not found!"))) // hit in production
                    .recoverFrom(e -> Response.of(InternalServerError, serialized(e)));
        } else {
            String message = "Invalid reference. Please provide the reference in the form of 'org:unit:context:schema:version'!";
            return Completes.withSuccess(Response.of(
                    BadRequest,
                    Headers.of(of(ContentLength, message.length())),
                    message
            ));
        }
    }

    public Completes<Response> retrieveSchemaVersionStatus(final String reference) {
        if (Path.isValidReference(reference, false)) {
            Path path = Path.with(reference, false);
            return codeQueries.codeFor(path)
                    .andThenTo(view -> Completes.withSuccess(Response.of(Ok, Headers.of(of(ContentType, "text/plain; charset=UTF-8")), view.status())))
                    .otherwise(response -> Response.of(NotFound, Headers.of(of(ContentType, "text/plain; charset=UTF-8")), "SchemaVersion not found!"))
                    .recoverFrom(e -> Response.of(InternalServerError, serialized(e)));
        } else {
            String message = "Invalid reference. Please provide the reference in the form of 'org:unit:context:schema:version'!";
            return Completes.withSuccess(Response.of(
                    BadRequest,
                    Headers.of(of(ContentLength, message.length())),
                    message
            ));
        }
    }

    @Override
    public Resource<?> routes() {
        return resource("SchemaVersion Resource", 1,
                post("/api/organizations/{organizationId}/units/{unitId}/contexts/{contextId}/schemas/{schemaId}/versions")
                        .param(String.class)
                        .param(String.class)
                        .param(String.class)
                        .param(String.class)
                        .body(SchemaVersionData.class)
                        .handle(this::defineWith),
                patch("/api/organizations/{organizationId}/units/{unitId}/contexts/{contextId}/schemas/{schemaId}/versions/{schemaVersionId}/description")
                        .param(String.class)
                        .param(String.class)
                        .param(String.class)
                        .param(String.class)
                        .param(String.class)
                        .body(String.class)
                        .handle(this::describeAs),
                patch("/api/organizations/{organizationId}/units/{unitId}/contexts/{contextId}/schemas/{schemaId}/versions/{schemaVersionId}/specification")
                        .param(String.class)
                        .param(String.class)
                        .param(String.class)
                        .param(String.class)
                        .param(String.class)
                        .body(String.class)
                        .handle(this::specifyWith),
                patch("/api/organizations/{organizationId}/units/{unitId}/contexts/{contextId}/schemas/{schemaId}/versions/{schemaVersionId}/status")
                        .param(String.class)
                        .param(String.class)
                        .param(String.class)
                        .param(String.class)
                        .param(String.class)
                        .body(String.class)
                        .handle(this::statusOf),
                get("/api/organizations/{organizationId}/units/{unitId}/contexts/{contextId}/schemas/{schemaId}/versions")
                        .param(String.class)
                        .param(String.class)
                        .param(String.class)
                        .param(String.class)
                        .handle(this::querySchemaVersions),
                get("/api/organizations/{organizationId}/units/{unitId}/contexts/{contextId}/schemas/{schemaId}/versions/{schemaVersionId}")
                        .param(String.class)
                        .param(String.class)
                        .param(String.class)
                        .param(String.class)
                        .param(String.class)
                        .handle(this::querySchemaVersionByIds),
                get("/api/versions/search")
                        .query("version", String.class, null)
                        .query("organization", String.class)
                        .query("unit", String.class)
                        .query("context", String.class)
                        .query("schema", String.class)
                        .handle(this::searchSchemaVersions),
                post("/api/versions/{reference}")
                        .param(String.class)
                        .body(SchemaVersionData.class)
                        .handle(this::pushSchemaVersion),
                get("/api/versions/{reference}")
                  .param(String.class)
                  .handle(this::retrieveSchemaVersion),
                get("/api/versions/{reference}/status")
                  .param(String.class)
                  .handle(this::retrieveSchemaVersionStatus));
    }

    private String schemaVersionLocation(final SchemaVersionId schemaVersionId) {
        return String.format(SchemaVersionsPath, schemaVersionId.organizationId().value, schemaVersionId.unitId().value, schemaVersionId.contextId().value, schemaVersionId.schemaId.value, schemaVersionId.value);
    }
}
