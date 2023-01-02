// Copyright © 2012-2023 VLINGO LABS. All rights reserved.
//
// This Source Code Form is subject to the terms of the
// Mozilla Public License, v. 2.0. If a copy of the MPL
// was not distributed with this file, You can obtain
// one at https://mozilla.org/MPL/2.0/.

package io.vlingo.xoom.schemata.resource;

import io.vlingo.xoom.actors.Logger;
import io.vlingo.xoom.common.Completes;
import io.vlingo.xoom.common.Outcome;
import io.vlingo.xoom.common.Tuple3;
import io.vlingo.xoom.http.Header;
import io.vlingo.xoom.http.Request;
import io.vlingo.xoom.http.Response;
import io.vlingo.xoom.http.ResponseHeader;
import io.vlingo.xoom.http.resource.DynamicResourceHandler;
import io.vlingo.xoom.http.resource.Resource;
import io.vlingo.xoom.lattice.grid.Grid;
import io.vlingo.xoom.schemata.Schemata;
import io.vlingo.xoom.schemata.errors.SchemataBusinessException;
import io.vlingo.xoom.schemata.infra.persistence.StorageProvider;
import io.vlingo.xoom.schemata.model.Path;
import io.vlingo.xoom.schemata.query.CodeQueries;
import io.vlingo.xoom.schemata.query.QueryResultsCollector;
import io.vlingo.xoom.schemata.resource.data.*;

import java.io.ByteArrayInputStream;
import java.io.InputStream;

import static io.vlingo.xoom.http.RequestHeader.Authorization;
import static io.vlingo.xoom.http.Response.Status.*;
import static io.vlingo.xoom.http.resource.ResourceBuilder.get;
import static io.vlingo.xoom.http.resource.ResourceBuilder.resource;
import static io.vlingo.xoom.schemata.codegen.TypeDefinitionCompiler.compilerFor;

//
// like this:
//    /code/Org:Unit:Context:Schema:Version/java
//    /code/Org:Unit:Context:Schema:Version/csharp
//    /code/Org:Unit:Context:Schema:Version/ruby
//    /code/Org:Unit:Context:Schema:Version/python
//    /code/Org:Unit:Context:Schema:Version/???
//
// header: Authorization: XOOM-SCHEMATA source=<some-hash-value> dependent=<some-hash-value>
//
public class CodeResource extends DynamicResourceHandler {
  private final Grid grid;
  private final Logger logger;
  private final CodeQueries queries;

  public CodeResource(final Grid grid) {
    super(grid.world().stage());
    this.grid = grid;
    this.logger = grid.world().defaultLogger();
    this.queries = StorageProvider.instance().codeQueries;
  }

  private boolean isReferenceValid(final String reference) {
    if (reference != null) {
      final String[] parts = reference.split(Schemata.ReferenceSeparator);
      if (parts.length >= Schemata.MinReferenceParts) {
        return true;
      }
    }

    return false;
  }

  public Completes<Response> queryCodeForLanguage(final String reference, final String language) {
    if (!isReferenceValid(reference)) {
      return Completes.withSuccess(Response.of(Response.Status.BadRequest, "Invalid reference parameter!"));
    }

    // TODO: this works as of #160, but the usecase might have vanished. All #55-FIXME's could be irrelevant now.
    // final Collector collector = given(context().request, reference);
    logger().debug(context().request.toString());

    final Path path = Path.with(reference, true);

    return queries.codeFor(path)
            .andThenTo(codeView -> {
              logger.debug("COMPILING: " + codeView.specification());
              return compile(codeView.reference(), codeView.specification(), codeView.currentVersion(), language);
            })
            .andThenTo(code -> {
              logger.debug("CODE: \n" + code.get());
              return recordDependency(code.get(), null); //collector
            })
            .andThenTo(code -> {
              logger.debug("SUCCESS: \n" + code);
              return Completes.withSuccess(Response.of(Ok, code));
            })
            .otherwise(failure -> {
              logger.error("FAILED: " + failure);
              return Response.of(
                      InternalServerError,
                      Header.Headers.of(ResponseHeader.contentLength(0))
              );
            })
            .recoverFrom(exception -> {
              logger.error(exception.getMessage(), exception);
              return Response.of(
                      BadRequest,
                      Header.Headers.of(ResponseHeader.contentLength(exception.getMessage().length())),
                      exception.getMessage()
              );
            });
  }

  @Override
  public Resource<?> routes() {
    return resource("Code Resource", this, 1,
            get("/api/code/{reference}/{language}")
                    .param(String.class)
                    .param(String.class)
                    .handle(this::queryCodeForLanguage));
  }

  //////////////////////////////////
  // Internal implementation
  //////////////////////////////////

  private Completes<Outcome<SchemataBusinessException, String>> compile(final String reference, final String specification, final String currentVersion, final String language) {
    final InputStream inputStream = new ByteArrayInputStream(specification.getBytes());
    return compilerFor(grid, language).compile(inputStream, reference, currentVersion);
  }

  @SuppressWarnings("unused")
  private Collector given(final Request request, final String reference) {
    AuthorizationData auth = null;
    PathData path = null;

    // FIXME: temporary workaround for missing context in handler, see #55
    String mockAuth = mockAuth(reference);
    final String header = request == null ? mockAuth : request.headerValueOr(Authorization, mockAuth);

    try {
      auth = AuthorizationData.with(header);
      path = PathData.withVersion(reference);
      final Tuple3<String, String, String> ids = auth.dependentAsIds();
      return Collector.startingWith(auth, path, ContextData.identity(ids._1, ids._2, ids._3));
    } catch (Exception e) {
      if (auth == null) {
        throw new IllegalArgumentException("Authorization is invalid: " + header);
      }
      throw new IllegalArgumentException("Reference path is invalid: " + reference);
    }
  }

  // FIXME: temporary workaround for missing context in handler, see #55
  private String mockAuth(String reference) {
    final String[] parts = reference.split(Schemata.ReferenceSeparator);

    return AuthorizationData.AuthorizationType +
            " source = " + parts[0] +
            "    dependent = " +
            parts[0] + Schemata.ReferenceSeparator +
            parts[1] + Schemata.ReferenceSeparator +
            parts[2];
  }

  @SuppressWarnings("unused")
  private Completes<Outcome<SchemataBusinessException,ContextData>> queryContextWith(final ContextData contextIds, final Collector collector) {
//    final Completes<Outcome<SchemataBusinessException,ContextData>> context =
//            Queries.forContexts().context(
//                    contextIds.organizationId,
//                    contextIds.unitId,
//                    contextIds.contextId,
//                    collector);
//
//    return context;
    // TODO Implement this method
    return null;
  }

  private Completes<String> recordDependency(final String code, final Collector collector) {
    // TODO: record dependency
    // return collector.expectCode(Completes.withSuccess(code));
    return Completes.withSuccess(code);
  }

  @SuppressWarnings("unused")
  private Completes<ContextData> validateContext(final ContextData context, final Collector collector) {
    return collector.authorization.sameDependent(context.organizationId, context.unitId, context.contextId) ?
            Completes.withSuccess(context) :
            Completes.withFailure();
  }

  private static class Collector implements QueryResultsCollector {
    public final AuthorizationData authorization;
    @SuppressWarnings("unused")
    public final ContextData contextIndentities;
    @SuppressWarnings("unused")
    public final PathData path;

    private Completes<Outcome<SchemataBusinessException,OrganizationData>> eventualOrganization;
    private Completes<Outcome<SchemataBusinessException,UnitData>> eventualUnit;
    private Completes<Outcome<SchemataBusinessException,ContextData>> eventualContext;
    private Completes<Outcome<SchemataBusinessException,SchemaData>> eventualSchema;
    private Completes<Outcome<SchemataBusinessException,SchemaVersionData>> eventualVersion;
    private Completes<Outcome<SchemataBusinessException,String>> eventualCode;

    static Collector startingWith(final AuthorizationData authorization, final PathData path, final ContextData contextIndentities) {
      return new Collector(authorization, path, contextIndentities);
    }

    @Override
    public Completes<Outcome<SchemataBusinessException,OrganizationData>> expectOrganization(final Completes<Outcome<SchemataBusinessException,OrganizationData>> organization) {
      eventualOrganization = organization;
      return organization;
    }

    @Override
    public Completes<Outcome<SchemataBusinessException,UnitData>> expectUnit(final Completes<Outcome<SchemataBusinessException,UnitData>> unit) {
      eventualUnit = unit;
      return unit;
    }

    @Override
    public Completes<Outcome<SchemataBusinessException,ContextData>> expectContext(final Completes<Outcome<SchemataBusinessException,ContextData>> context) {
      eventualContext = context;
      return context;
    }

    @Override
    public Completes<Outcome<SchemataBusinessException,SchemaData>> expectSchema(final Completes<Outcome<SchemataBusinessException,SchemaData>> schema) {
      eventualSchema = schema;
      return schema;
    }

    @Override
    public Completes<Outcome<SchemataBusinessException,SchemaVersionData>> expectSchemaVersion(final Completes<Outcome<SchemataBusinessException,SchemaVersionData>> version) {
      this.eventualVersion = version;
      return version;
    }

    @Override
    public Completes<Outcome<SchemataBusinessException,String>> expectCode(final Completes<Outcome<SchemataBusinessException,String>> code) {
      eventualCode = code;
      return code;
    }

    @Override
    public OrganizationData organization() {
      return eventualOrganization.outcome().getOrNull();
    }

    @Override
    public UnitData unit() {
      return eventualUnit.outcome().getOrNull();
    }

    @Override
    public ContextData context() {
      return eventualContext.outcome().getOrNull();
    }

    @Override
    public SchemaData schema() {
      return eventualSchema.outcome().getOrNull();
    }

    @Override
    public SchemaVersionData schemaVersion() {
      return eventualVersion.outcome().getOrNull();
    }

    @Override
    public String code() {
      return eventualCode.outcome().getOrNull();
    }

    private Collector(
            final AuthorizationData authorization,
            final PathData path,
            final ContextData contextIndentities) {

      this.authorization = authorization;
      this.path = path;
      this.contextIndentities = contextIndentities;
    }
  }
}
