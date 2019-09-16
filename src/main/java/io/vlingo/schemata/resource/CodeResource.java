// Copyright © 2012-2018 Vaughn Vernon. All rights reserved.
//
// This Source Code Form is subject to the terms of the
// Mozilla Public License, v. 2.0. If a copy of the MPL
// was not distributed with this file, You can obtain
// one at https://mozilla.org/MPL/2.0/.

package io.vlingo.schemata.resource;

import static io.vlingo.http.RequestHeader.Authorization;
import static io.vlingo.http.Response.Status.BadRequest;
import static io.vlingo.http.Response.Status.InternalServerError;
import static io.vlingo.http.Response.Status.Ok;
import static io.vlingo.http.resource.ResourceBuilder.get;
import static io.vlingo.http.resource.ResourceBuilder.resource;
import static io.vlingo.schemata.Schemata.StageName;
import static io.vlingo.schemata.codegen.TypeDefinitionCompiler.compilerFor;

import java.io.ByteArrayInputStream;
import java.io.InputStream;

import io.vlingo.actors.Stage;
import io.vlingo.actors.World;
import io.vlingo.common.Completes;
import io.vlingo.common.Tuple3;
import io.vlingo.http.Request;
import io.vlingo.http.Response;
import io.vlingo.http.resource.Resource;
import io.vlingo.http.resource.ResourceHandler;
import io.vlingo.schemata.query.Queries;
import io.vlingo.schemata.query.QueryResultsCollector;
import io.vlingo.schemata.resource.data.AuthorizationData;
import io.vlingo.schemata.resource.data.ContextData;
import io.vlingo.schemata.resource.data.OrganizationData;
import io.vlingo.schemata.resource.data.PathData;
import io.vlingo.schemata.resource.data.SchemaData;
import io.vlingo.schemata.resource.data.SchemaVersionData;
import io.vlingo.schemata.resource.data.UnitData;

//
// like this:
//    /code/Org:Unit:Context:Schema:Version/java
//    /code/Org:Unit:Context:Schema:Version/csharp
//    /code/Org:Unit:Context:Schema:Version/ruby
//    /code/Org:Unit:Context:Schema:Version/python
//    /code/Org:Unit:Context:Schema:Version/???
//
// header: Authorization: VLINGO-SCHEMATA source=<some-hash-value> dependent=<some-hash-value>
//
public class CodeResource extends ResourceHandler {
  private final Stage stage;

  public CodeResource(final World world) {
    this.stage = world.stageNamed(StageName);
  }

  public Completes<Response> queryCodeForLanguage(final String reference, final String language) {
    final Collector collector = given(context().request, reference);

    return Queries.forCode()
            .schemaVersionFor(collector.authorization, collector.path, collector)
            .otherwise(failure -> {
              System.out.println("FAILED ON VERSION: " + failure);
              return failure;
            })
            .andThenTo(version -> {
              System.out.println("VERSION: " + version);
              return queryContextWith(collector.contextIndentities, collector);
            })
            .otherwise(failure -> {
              System.out.println("FAILED ON QUERY CONTEXT: " + failure);
              return failure;
            })
            .andThenTo(context -> {
              System.out.println("CONTEXT: " + context);
              return validateContext(context, collector);
            })
            .otherwise(failure -> {
              System.out.println("FAILED ON VALIDATE CONTEXT: " + failure);
              return failure;
            })
            .andThenTo(context -> {
              System.out.println("COMPILING: " + collector.schemaVersion().specification);
              return compile(collector.schemaVersion(), language);
            })
            .otherwise(failure -> {
              System.out.println("FAILED ON COMPILE: " + failure);
              return failure;
            })
            .andThenTo(code    -> {
              System.out.println("CODE: \n" + code);
              return recordDependency(code, collector);
            })
            .otherwise(failure -> {
              System.out.println("FAILED ON RECORD DEPENDENCY: " + failure);
              return failure;
            })
            .andThenTo(code    -> {
              System.out.println("SUCCESS: \n" + code);
              return Completes.withSuccess(Response.of(Ok, code));
            })
            .otherwise(failure -> {
              System.out.println("FAILED: " + failure);
              return Response.of(InternalServerError);
            })
            .recoverFrom(exception -> {
              System.out.println("EXCEPTION: " + exception);
              return Response.of(BadRequest, exception.getMessage());
            });
  }

  @Override
  public Resource<?> routes() {
    return resource("Code Resource", 1,
      get("/code/{reference}/{language}")
        .param(String.class)
        .param(String.class)
        .handle(this::queryCodeForLanguage));
  }

  //////////////////////////////////
  // Internal implementation
  //////////////////////////////////

  private Completes<String> compile(final SchemaVersionData version, final String language) {
    final InputStream inputStream = new ByteArrayInputStream(version.specification.getBytes());
    return compilerFor(stage, language).compile(inputStream, version.currentVersion);
  }

  private Collector given(final Request request, final String reference) {
    AuthorizationData auth = null;
    PathData path = null;
    final String header = request.headerValueOr(Authorization, "");

    try {
      auth = AuthorizationData.with(header);
      path = PathData.from(reference);
      final Tuple3<String,String,String> ids = auth.dependentAsIds();
      return Collector.startingWith(auth, path, ContextData.identity(ids._1, ids._2, ids._3));
    } catch (Exception e) {
      if (auth == null) {
        throw new IllegalArgumentException("Authorization is invalid: " + header);
      }
      throw new IllegalArgumentException("Reference path is invalid: " + reference);
    }
  }

  private Completes<ContextData> queryContextWith(final ContextData contextIds, final Collector collector) {
    final Completes<ContextData> context =
            Queries.forContexts().context(
                    contextIds.organizationId,
                    contextIds.unitId,
                    contextIds.contextId,
                    collector);

    return context;
  }

  private Completes<String> recordDependency(final String code, final Collector collector) {
    // TODO: record dependency
    // return collector.expectCode(Completes.withSuccess(code));
    return Completes.withSuccess(code);
  }

  private Completes<ContextData> validateContext(final ContextData context, final Collector collector) {
    return collector.authorization.sameDependent(context.organizationId, context.unitId, context.contextId) ?
            Completes.withSuccess(context) :
            Completes.withFailure();
  }

  private static class Collector implements QueryResultsCollector {
    public final AuthorizationData authorization;
    public final ContextData contextIndentities;
    public final PathData path;

    private Completes<OrganizationData> eventualOrganization;
    private Completes<UnitData> eventualUnit;
    private Completes<ContextData> eventualContext;
    private Completes<SchemaData> eventualSchema;
    private Completes<SchemaVersionData> eventualVersion;
    private Completes<String> eventualCode;

    static Collector startingWith(final AuthorizationData authorization, final PathData path, final ContextData contextIndentities) {
      return new Collector(authorization, path, contextIndentities);
    }

    @Override
    public Completes<OrganizationData> expectOrganization(final Completes<OrganizationData> organization) {
      eventualOrganization = organization;
      return organization;
    }

    @Override
    public Completes<UnitData> expectUnit(final Completes<UnitData> unit) {
      eventualUnit = unit;
      return unit;
    }

    @Override
    public Completes<ContextData> expectContext(final Completes<ContextData> context) {
      eventualContext = context;
      return context;
    }

    @Override
    public Completes<SchemaData> expectSchema(final Completes<SchemaData> schema) {
      eventualSchema = schema;
      return schema;
    }

    @Override
    public Completes<SchemaVersionData> expectSchemaVersion(final Completes<SchemaVersionData> version) {
      this.eventualVersion = version;
      return version;
    }

    @Override
    public Completes<String> expectCode(final Completes<String> code) {
      eventualCode = code;
      return code;
    }

    @Override
    public OrganizationData organization() {
      return eventualOrganization.outcome();
    }

    @Override
    public UnitData unit() {
      return eventualUnit.outcome();
    }

    @Override
    public ContextData context() {
      return eventualContext.outcome();
    }

    @Override
    public SchemaData schema() {
      return eventualSchema.outcome();
    }

    @Override
    public SchemaVersionData schemaVersion() {
      return eventualVersion.outcome();
    }

    @Override
    public String code() {
      return eventualCode.outcome();
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
